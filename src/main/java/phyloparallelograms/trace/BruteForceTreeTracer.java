/*
 * BruteForceTreeTracer.java Copyright (C) 2026 Daniel H. Huson
 *
 *  (Some files contain contributions from other authors, who are then mentioned separately.)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package phyloparallelograms.trace;

import jloda.graph.DAGTraversals;
import jloda.graph.Edge;
import jloda.graph.Node;
import jloda.graph.NodeArray;
import jloda.phylo.CommentData;
import jloda.phylo.NewickIO;
import jloda.phylo.PhyloTree;
import jloda.phylo.algorithms.ClusterPoppingAlgorithm;
import jloda.util.*;
import jloda.util.progress.ProgressListener;
import jloda.util.progress.ProgressSilent;
import phyloparallelograms.algorithm.FilterTrees;
import phyloparallelograms.window.TreeRecord;
import splitstree6.data.TaxaBlock;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static phyloparallelograms.trace.TreeTrace.getTT;
import static phyloparallelograms.trace.TreeTrace.setTT;

/**
 * traces input trees through a rooted phylogenetic network by brute force.
 * <p>
 * The network carries <em>tree-trace</em> ({@code TT}) annotations: every node and edge stores a {@link BitSet}
 * of the ids of the input trees that pass through it (see {@link TreeTrace}; these are written out as
 * {@code [&TT=..]} Newick comments and used downstream to highlight each tree). This class fills in those
 * annotations for any tree that is marked <em>shown</em> but is not yet recorded on the network, i.e. whose id is
 * absent from the root's {@code TT} set.
 * <p>
 * A tree displayed by the network is obtained by choosing, at each reticulation node (in-degree &gt; 1), exactly
 * one of its incoming edges. The algorithm enumerates the full Cartesian product of these choices -- every
 * embedded tree -- and marks an untraced input tree as soon as some embedded tree matches it. Trees are compared
 * by their hardwired clusters (the set of taxa below each node), not by topology directly.
 * <ol>
 *     <li>collect the shown rows whose id is missing from the root's {@code TT} set;</li>
 *     <li>if the network has no {@code TT} annotation yet, initialise the root, the leaves and the reticulate
 *     edges with empty sets and clear all other node/edge data;</li>
 *     <li>enumerate one incoming-edge choice per reticulation ({@link #allChoicesOfReticulateEdges}) and process
 *     the choices in parallel;</li>
 *     <li>for each choice, extract the clusters of the embedded tree (following only tree edges plus the chosen
 *     reticulate edges) and, for each untraced tree (optionally pre-filtered by confidence/concordance), test
 *     whether the tree's clusters are all compatible -- pairwise disjoint or nested -- with those clusters,
 *     restricting the network's clusters to the tree's taxa when the tree spans fewer taxa. A match means the
 *     tree is displayed under this choice, so its id is set on the root, on the leaves it contains and on the
 *     chosen reticulate edges, and then pushed up through the internal nodes;</li>
 *     <li>finally {@link CompleteTreeTrace} propagates the leaf and edge sets to every remaining node and edge.</li>
 * </ol>
 * The number of embedded trees is the product of the reticulation in-degrees, so the cost is exponential in the
 * number of reticulations -- hence "brute force"; the work is spread across cores over the choices.
 * <p>
 * Daniel Huson, 3.2026
 */
public class BruteForceTreeTracer {

	/**
	 * for each tree row for which 'show' is true and no trace is available,
	 * determine the reticulate edges that it uses
	 *
	 * @param network     the rooted network
	 * @param treeRecords the tree rows
	 */
	public static void apply(TaxaBlock taxaBlock, PhyloTree network, List<TreeRecord> treeRecords, double minConfidence, double minConcordance, ProgressListener progress) {
		var requireComputation = new ArrayList<TreeRecord>();

		var set = getTT(network.getRoot());
		for (var row : treeRecords) {
			if (row.isShow() && (set == null || !set.get(row.getId())))
				requireComputation.add(row);
		}

		if (set == null) {
			// 1. setup comment data for root, leaves and reticulate edges
			for (var v : network.nodes()) {
				if ((v.isLeaf() || v == network.getRoot())) {
					TreeTrace.setTT(v);
				} else v.setData(null);
			}
			for (var e : network.edges()) {
				if (network.isReticulateEdge(e)) {
					TreeTrace.setTT(e);
				} else e.setData(null);
			}
		}

		if (!requireComputation.isEmpty()) {
			// generate all combinations of choices of reticulation edges:
			var reticulateNodes = network.nodeStream().filter(v -> v.getInDegree() > 1).toList();
			var choices = allChoicesOfReticulateEdges(reticulateNodes);

			try {
				ExecuteInParallel.apply(choices, choice -> {
					traceTrees(taxaBlock, network, choice, requireComputation, minConfidence, minConcordance, progress);
				}, ProgramExecutorService.getNumberOfCoresToUse(), progress);
				CompleteTreeTrace.apply(network);
			} catch (InterruptedException ignored) {
			} catch (Exception e) {
				Basic.caught(e);
			}
		}
	}

	/**
	 * performs the tree-tracing analysis for a given choice of reticulate edges
	 *
	 * @param network               the network
	 * @param chosenReticulateEdges one edge per reticulation
	 * @param treeRecords           the tree rows
	 */
	private static void traceTrees(TaxaBlock taxaBlock, PhyloTree network, Set<Edge> chosenReticulateEdges, List<TreeRecord> treeRecords, double minConfidence, double minConcordance, ProgressListener progress) throws CanceledException {
		//System.err.println("Reticulate edges: "+StringUtils.toString(chosenReticulateEdges,", "));
		var treeInNetworkClusters = extractAllClusters(network, chosenReticulateEdges);
		//System.err.println("Network clusters: "+StringUtils.toString(treeInNetworkClusters,", "));
		if (false) {
			var treeInNetwork = new PhyloTree();
			ClusterPoppingAlgorithm.apply(treeInNetworkClusters, treeInNetwork);
			for (var v : treeInNetwork.nodes()) {
				if (treeInNetwork.hasTaxa(v)) {
					treeInNetwork.setLabel(v, taxaBlock.getLabel(treeInNetwork.getTaxon(v)));
				}
			}
			System.err.println("TreeInNetwork " + treeInNetwork.toBracketString(false) + ";");
		}
		var networkTaxa = BitSetUtils.union(treeInNetworkClusters);
		var rootBitSet = getTT(network.getRoot());

		for (var record : treeRecords) {
			if (record.isShow() && !rootBitSet.get(record.getId())) {
				var tree = record.getTree();
				if (minConfidence > 0.0 || minConcordance > 0.0) {
					tree = FilterTrees.apply(taxaBlock, List.of(tree), minConfidence, minConcordance, new ProgressSilent()).get(0);
				} else
					tree = record.getTree();

				if (false)
					System.err.println("Tree " + record.getId() + ": " + tree.toBracketString(false) + ";");

				var treeClusters = extractAllClusters(tree, Collections.emptySet());

				var treeTaxa = BitSetUtils.union(treeClusters);
				if (treeTaxa.cardinality() == networkTaxa.cardinality()) {
					if (!allCompatible(treeInNetworkClusters, treeClusters))
						continue;
				} else {
					var inducedNetworkClusters = treeInNetworkClusters.stream()
							.map(c -> BitSetUtils.intersection(c, treeTaxa))
							.filter(c -> c.cardinality() > 0).collect(Collectors.toSet());
					if (!allCompatible(inducedNetworkClusters, treeClusters))
						continue;
				}
				// tree is contained in network using selected reticulate edges
				for (var v : network.nodes()) {
					if (v == network.getRoot() || (v.isLeaf() && treeTaxa.get(network.getTaxon(v)))) {
						getTT(v).set(record.getId());
					}
				}
				for (var e : chosenReticulateEdges) {
					getTT(e).set(record.getId());
				}
				network.postorderTraversal(v -> {
					if (getTT(v) == null)
						setTT(v);
					for (var e : v.outEdges()) {
						if (v.getInDegree() <= 1) {
							var w = e.getTarget();
							setTT(v, BitSetUtils.union(getTT(v), getTT(w)));
						} else if (getTT(e) != null) {
							setTT(v, BitSetUtils.union(getTT(v), getTT(e)));
						}
					}
				});
				if (false) {
					var newickIO = new NewickIO();
					newickIO.allowMultiLabeledNodes = false;
					newickIO.setNewickNodeCommentSupplier(CommentData.createDataNodeSupplier());
					newickIO.setNewickEdgeCommentSupplier(CommentData.createDataEdgeSupplier());
					System.err.println("Network " + newickIO.toBracketString(network, true) + ";");
				}
			}
			progress.checkForCancel();
		}
	}

	private static Set<BitSet> extractAllClusters(PhyloTree phylogeny, Set<Edge> chosenReticulateEdges) {
		try (NodeArray<BitSet> nodeClusterMap = phylogeny.newNodeArray()) {
			Function<Node, List<Node>> nodeListFunction = u -> u.outEdgesStream(false)
					.filter(f -> f.getTarget().getInDegree() < 2 || chosenReticulateEdges.contains(f)).map(Edge::getTarget)
					.toList();
			Consumer<Node> consumer = v -> {
				if (v.isLeaf()) {
					nodeClusterMap.put(v, BitSetUtils.asBitSet(phylogeny.getTaxon(v)));
				} else {
					var cluster = new BitSet();
					for (var u : nodeListFunction.apply(v)) {
						cluster.or(nodeClusterMap.get(u));
					}
					nodeClusterMap.put(v, cluster);
				}
			};
			DAGTraversals.postOrderTraversal(phylogeny.getRoot(), nodeListFunction, consumer);
			return new HashSet<>(nodeClusterMap.values());
		}
	}

	public static List<Set<Edge>> allChoicesOfReticulateEdges(List<Node> nodes) {
		var choices = new ArrayList<Set<Edge>>(nodes.size());
		for (var n : nodes) {
			var set = IteratorUtils.asSet(n.inEdges());
			if (set.isEmpty()) {
				// If any node has no choices, there are no complete selections
				return List.of();
			}
			choices.add(set);
		}

		var result = new ArrayList<Set<Edge>>();
		allChoicesOfReticulateEdgesRec(choices, 0, new ArrayList<>(nodes.size()), result);
		return result;
	}

	private static <Edge> void allChoicesOfReticulateEdgesRec(List<Set<Edge>> choices, int i, List<Edge> current, List<Set<Edge>> out) {
		if (i == choices.size()) {
			out.add(new HashSet<>(current));
			return;
		}
		for (var e : choices.get(i)) {
			current.add(e);
			allChoicesOfReticulateEdgesRec(choices, i + 1, current, out);
			current.remove(current.size() - 1);
		}
	}

	private static boolean allCompatible(Collection<BitSet> clusters1, Collection<BitSet> clusters2) {
		for (var c1 : clusters1) {
			for (var c2 : clusters2) {
				var intersectionSize = BitSetUtils.intersection(c1, c2).cardinality();
				if (intersectionSize != 0 && intersectionSize != c1.cardinality() && intersectionSize != c2.cardinality())
					return false;
			}
		}
		return true;
	}


	public static boolean requireTracing(List<PhyloTree> networks, List<TreeRecord> treeRecords) {
		for (var network : networks) {
			var set = getTT(network.getRoot());
			for (var row : treeRecords) {
				if (row.isShow() && (set == null || !set.get(row.getId())))
					return true;
			}
		}
		return false;
	}
}
