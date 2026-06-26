/*
 * SetupReroot.java Copyright (C) 2026 Daniel H. Huson
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

package phyloparallelograms.window;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import phyloparallelograms.algorithm.RerootService;
import splitstree6.algorithms.trees.trees2trees.RerootOrReorderTrees;
import splitstree6.data.parts.Taxon;

class SetupReroot {
	public static void apply(MainWindow window, RerootService rerootService, ReadOnlyBooleanProperty serviceRunning) {
		var controller = window.getController();
		var document = window.getDocument();
		var taxaBlock = document.getTaxaBlock();
		var taxaSelectionModel = window.getTaxaSelectionModel();

		controller.getRerootByOutgroupMenuItem().setOnAction(e -> {
			var outGroup = window.getTaxaSelectionModel().getSelectedItems().stream().map(Taxon::getName).toArray(String[]::new);
			if (outGroup.length > 0 && outGroup.length < taxaBlock.size()) {
				rerootService.setupCalculation(window, RerootOrReorderTrees.RootBy.OutGroup, outGroup);
				rerootService.restart();
			}
		});
		controller.getRerootByOutgroupMenuItem().disableProperty().bind(serviceRunning.or(document.hasTreesProperty().not())
				.or(Bindings.createBooleanBinding(() -> taxaSelectionModel.size() == 0 || taxaSelectionModel.size() == taxaBlock.getNtax(), taxaSelectionModel.getSelectedItems()))
		);

		controller.getRerootByMidpointMenuItem().setOnAction(e -> {
			rerootService.setupCalculation(window, RerootOrReorderTrees.RootBy.MidPoint, new String[0]);
			rerootService.restart();
		});
		controller.getRerootByMidpointMenuItem().disableProperty().bind(serviceRunning.or(document.hasTreesProperty().not()));
	}
}
