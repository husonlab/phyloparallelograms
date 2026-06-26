/*
 * RerootService.java Copyright (C) 2026 Daniel H. Huson
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

package phyloparallelograms.algorithm;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import jloda.fx.util.AService;
import jloda.phylo.PhyloTree;
import jloda.util.progress.ProgressSilent;
import phyloparallelograms.utils.NexusBlocksUtils;
import phyloparallelograms.window.MainWindow;
import phyloparallelograms.window.TreeRecord;
import splitstree6.algorithms.trees.trees2trees.RerootOrReorderTrees;
import splitstree6.data.TreesBlock;

public class RerootService extends AService<TreePair> {

	public RerootService(Pane bottomPane) {
		super(bottomPane);
	}

	public void setupCalculation(MainWindow window, RerootOrReorderTrees.RootBy rootBy, String[] outgroup) {
		var document = window.getDocument();
		var taxaBlock = document.getTaxaBlock();
		var blocks = NexusBlocksUtils.setupBlocks(taxaBlock, document.getTreeRecords().stream().map(TreeRecord::getTree).toList());

		var algorithm = new RerootOrReorderTrees();
		algorithm.setOptionRootBy(rootBy);

		if (rootBy == RerootOrReorderTrees.RootBy.OutGroup)
			algorithm.setOptionOutGroupTaxa(outgroup);

		var originalTrees = blocks.treesBlock();
		originalTrees.getTrees().replaceAll(PhyloTree::new); // need copies for undo

		setCallable(() -> {
			var rerootedTrees = new TreesBlock();
			algorithm.compute(new ProgressSilent(), blocks.taxaBlock(), blocks.treesBlock(), rerootedTrees);
			return new TreePair(originalTrees, rerootedTrees);
		});

		setOnSucceeded(e -> {
			var originals = getValue().originalTrees();
			var rerooted = getValue().updatedTrees();
			window.getUndoManager().doAndAdd(rootBy.name().toLowerCase() + " rooting", () -> {
				for (var record : document.getTreeRecords()) {
					record.getTree().copy(originals.getTree(record.getId()));
				}
				Platform.runLater(() -> window.getPresenter().runRecomputeNetwork());
			}, () -> {
				for (var record : document.getTreeRecords()) {
					record.getTree().copy(rerooted.getTree(record.getId()));
				}
				Platform.runLater(() -> window.getPresenter().runRecomputeNetwork());
			});
		});
	}
}

record TreePair(TreesBlock originalTrees, TreesBlock updatedTrees) {
}

