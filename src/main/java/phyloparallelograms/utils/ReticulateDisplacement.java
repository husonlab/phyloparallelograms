/*
 * ReticulateDisplacement.java Copyright (C) 2026 Daniel H. Huson
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

package phyloparallelograms.utils;

import jloda.graph.Node;
import jloda.phylo.PhyloTree;
import phyloparallelograms.trace.TreeTrace;
import splitstree6.layout.tree.LabeledNodeShape;

import java.util.Map;
import java.util.function.Function;

/**
 * computes the total reticulate displacement of a laid-out network, taking reticulate-edge multiplicities
 * into account. This is the quantity the displacement-optimized (DO) layout minimizes, so it can be used to
 * report the effect of using (versus not using) reticulate-edge multiplicities during layout: lower is better.
 * <p>
 * The displacement is the sum, over all reticulate (non-transfer-acceptor) edges, of the vertical distance
 * between the edge's two endpoints, each weighted by the edge's multiplicity (the number of trees traced
 * through it, i.e. the cardinality of its TT annotation, at least 1).
 * <p>
 * Daniel Huson, 10.2026
 */
public class ReticulateDisplacement {

	/**
	 * the total multiplicity-weighted reticulate displacement of the current layout
	 *
	 * @param network           the (laid-out) network
	 * @param nodeYCoordinate   the vertical coordinate of each node in the current layout, or null for a node
	 *                          that has no position (such a reticulate edge is skipped)
	 * @return the total displacement; lower means reticulate edges are drawn more compactly
	 */
	public static double compute(PhyloTree network, Function<Node, Double> nodeYCoordinate) {
		var total = 0.0;
		for (var e : network.edges()) {
			if (network.isReticulateEdge(e) && !network.isTransferAcceptorEdge(e)) {
				var ySource = nodeYCoordinate.apply(e.getSource());
				var yTarget = nodeYCoordinate.apply(e.getTarget());
				if (ySource == null || yTarget == null)
					continue;
				var tt = TreeTrace.getTT(e);
				var multiplicity = Math.max(1, tt == null ? 1 : tt.cardinality());
				total += Math.abs(ySource - yTarget) * multiplicity;
			}
		}
		return total;
	}

	/**
	 * reading node positions from the node-to-shape map produced by the layout
	 */
	public static double compute(PhyloTree network, Map<Node, LabeledNodeShape> nodeShapeMap) {
		return compute(network, v -> {
			var shape = nodeShapeMap.get(v);
			return shape == null ? null : shape.getTranslateY();
		});
	}

	/**
	 * computes the total multiplicity-weighted reticulate displacement and prints it to the console, returning it
	 *
	 * @param label a short label identifying the setting being reported (e.g. "multiplicities on"), may be null
	 */
	public static double report(String label, PhyloTree network, Map<Node, LabeledNodeShape> nodeShapeMap) {
		var displacement = compute(network, nodeShapeMap);
		System.err.printf("Reticulate displacement (multiplicity-weighted): %.3f%s%n",
				displacement, (label == null || label.isBlank()) ? "" : "  [" + label + "]");
		return displacement;
	}
}
