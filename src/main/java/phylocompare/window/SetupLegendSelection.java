/*
 * SetupLegendSelection.java Copyright (C) 2026 Daniel H. Huson
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

package phylocompare.window;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import jloda.fx.util.BasicFX;
import phylocompare.view.NetworkView;

public class SetupLegendSelection {
	public static void apply(MainWindow window, NetworkView networkView) {
		var document = window.getDocument();

		networkView.getLegend().getChildren().addListener((ListChangeListener<Node>) c -> {
			while (c.next()) {
				for (var node : c.getAddedSubList()) {
					for (var toggleButton : BasicFX.getAllRecursively(node, ToggleButton.class)) {
						if (toggleButton.getUserData() instanceof Integer id) {
							toggleButton.selectedProperty().addListener((v, o, n) -> {
								var record = document.getTreeRecords().stream().filter(r -> r.getId() == id).findAny();
								record.ifPresent(treeRecord -> {
									if (n) {
										window.getController().getTreeTable().getSelectionModel().select(treeRecord);
									} else {
										var pos = window.getController().getTreeTable().getItems().indexOf(treeRecord);
										window.getController().getTreeTable().getSelectionModel().clearSelection(pos);
									}

								});
							});
						}
					}
				}
			}
			networkView.getLegend().setVisible(networkView.getLegend().getChildren().size() < 100);
		});

		networkView.getLegend().managedProperty().bind(networkView.getLegend().visibleProperty());
		window.getController().getShowLegendCheckMenuItem().selectedProperty().bindBidirectional(networkView.getLegend().visibleProperty());
	}
}
