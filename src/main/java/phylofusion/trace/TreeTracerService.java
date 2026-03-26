/*
 * TreeTracerService.java Copyright (C) 2026 Daniel H. Huson
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

package phylofusion.trace;

import javafx.scene.layout.Pane;
import jloda.fx.util.AService;
import jloda.phylo.CommentData;
import jloda.phylo.NewickIO;
import jloda.phylo.PhyloTree;
import phylofusion.window.TreeRecord;

import java.util.BitSet;
import java.util.List;

@Deprecated
public class TreeTracerService extends AService<Integer> {
	public TreeTracerService(Pane bottomPane) {
		super(bottomPane);
	}

	public void setupCalculation(PhyloTree network, List<TreeRecord> treeRecords, double minConfidence) {
		setCallable(() -> {
			BruteForceTreeTracer.apply(network, treeRecords, minConfidence);
			getProgressListener().setTasks("Tree tracing", "");
			if (true) {
				var newickIO = new NewickIO();
				newickIO.setNewickNodeCommentSupplier(u -> u.getData() == null ? null : u.getData().toString());
				newickIO.setNewickEdgeCommentSupplier(u -> u.getData() == null ? null : u.getData().toString());
				if (false) {
					System.err.println(newickIO.toBracketString(network, false) + ";");
					for (var v : network.nodes()) {
						System.err.println(v + " " + v.getData());
					}
					for (var e : network.edgeStream().filter(e -> e.getTarget().getInDegree() > 1).toList()) {
						System.err.println(e + " " + e.getData());
					}
				}
			}
			return ((CommentData) network.getRoot().getData()).getIntSetValue(TreeTrace.KEY).orElse(new BitSet()).cardinality();
		});
	}
}
