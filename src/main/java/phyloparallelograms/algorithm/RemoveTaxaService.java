/*
 * RerootService Copyright (C) 2026 Daniel H. Huson
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
import splitstree6.algorithms.trees.trees2trees.TreesTaxaFilter;
import splitstree6.data.TaxaBlock;
import splitstree6.data.TreesBlock;
import splitstree6.data.parts.Taxon;

import java.util.Collection;

public class RemoveTaxaService extends AService<TreePair> {

	public RemoveTaxaService(Pane bottomPane) {
		super(bottomPane);
	}

	public void setupCalculation(MainWindow window, Collection<Taxon> toRemove) {
		var document = window.getDocument();
		var taxaBlock = document.getTaxaBlock();
		var blocks = NexusBlocksUtils.setupBlocks(taxaBlock, document.getTreeRecords().stream().map(TreeRecord::getTree).toList());
		var reducedTaxaBlock = new TaxaBlock();
		for (var taxon : taxaBlock.getTaxa()) {
			if (!toRemove.contains(taxon)) {
				reducedTaxaBlock.add(taxon);
			}
		}

		var algorithm = new TreesTaxaFilter();

		var originalTrees = blocks.treesBlock();
		originalTrees.getTrees().replaceAll(PhyloTree::new); // need copies for undo

		setCallable(() -> {
			var modifiedTrees = new TreesBlock();
			algorithm.filter(new ProgressSilent(), blocks.taxaBlock(), reducedTaxaBlock, blocks.treesBlock(), modifiedTrees);
			return new TreePair(originalTrees, modifiedTrees);
		});

		setOnSucceeded(e -> {
			var originals = getValue().originalTrees();
			var rerooted = getValue().updatedTrees();
			window.getUndoManager().doAndAdd("remove taxa", () -> {
				for (var record : document.getTreeRecords()) {
					record.getTree().copy(originals.getTree(record.getId()));
				}
				document.setTaxa(originals.getTrees(), true);
				Platform.runLater(() -> window.getPresenter().runRecomputeNetwork());
			}, () -> {
				for (var record : document.getTreeRecords()) {
					record.getTree().copy(rerooted.getTree(record.getId()));
				}
				document.setTaxa(rerooted.getTrees(), true);
				Platform.runLater(() -> window.getPresenter().runRecomputeNetwork());
			});
		});
	}
}

