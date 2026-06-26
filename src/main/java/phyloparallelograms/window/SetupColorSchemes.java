/*
 * SetupColorSchemes.java Copyright (C) 2026 Daniel H. Huson
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

import javafx.collections.FXCollections;
import javafx.scene.paint.Color;
import jloda.fx.util.ColorSchemeManager;
import jloda.fx.window.MainWindowManager;

import java.util.HashMap;

class SetupColorSchemes {
	public static void apply(MainWindow window) {
		var cbox = window.getController().getColorSchemeCBox();
		cbox.setPrefWidth(48);
		cbox.setMinWidth(48);
		cbox.setMaxWidth(48);

		cbox.setCellFactory(cb -> new javafx.scene.control.ListCell<>() {
			private final javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();

			@Override
			protected void updateItem(String name, boolean empty) {
				super.updateItem(name, empty);

				if (empty || name == null) {
					setGraphic(null);
					setText(null);
				} else {
					imageView.setImage(ColorSchemeManager.getInstance().createIcon(name));
					imageView.setFitWidth(16);
					imageView.setFitHeight(16);

					setGraphic(imageView);
					setText(null);
				}
			}
		});

		cbox.setButtonCell(new javafx.scene.control.ListCell<>() {
			private final javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();

			@Override
			protected void updateItem(String name, boolean empty) {
				super.updateItem(name, empty);

				if (empty || name == null) {
					setGraphic(null);
					setText(null);
				} else {
					imageView.setImage(ColorSchemeManager.getInstance().createIcon(name));
					imageView.setFitWidth(16);
					imageView.setFitHeight(16);

					setGraphic(imageView);
					setText(null); // <- critical: no text here
				}
			}
		});
		cbox.valueProperty().addListener((v, o, n) -> {
			if (n != null && ColorSchemeManager.getInstance().getNames().contains(n)) {
				window.getDocument().setColorSchemeName(n);
			}
		});

		var blackTheme = FXCollections.observableArrayList(MainWindowManager.isUseDarkTheme() ? Color.WHITE : Color.BLACK);
		MainWindowManager.useDarkThemeProperty().addListener((v, o, n) -> {
			blackTheme.setAll(n ? Color.WHITE : Color.BLACK);
		});

		ColorSchemeManager.getInstance().setColorScheme("black", blackTheme);

		cbox.getItems().addAll("Retro29", "Glasbey29", "Twenty", "Pairs12", "Fews8", "Caspian8", "black");
		cbox.setValue(window.getDocument().getColorSchemeName());

		cbox.getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
			if (n != null) {
				System.err.println(n);
				var colorScheme = ColorSchemeManager.getInstance().getColorScheme(n);
				if (colorScheme != null) {
					var oldColors = new HashMap<TreeRecord, Color>();
					window.getDocument().getTreeRecords().forEach(r -> oldColors.put(r, r.getColor()));
					var newColors = new HashMap<TreeRecord, Color>();
					window.getDocument().getTreeRecords().forEach(r -> newColors.put(r, colorScheme.get(r.getId() % colorScheme.size())));
					window.getUndoManager().doAndAdd("colors", () -> {
						oldColors.forEach(TreeRecord::setColor);
						window.getPresenter().runUpdateTreesDrawing();
					}, () -> {
						newColors.forEach(TreeRecord::setColor);
						window.getPresenter().runUpdateTreesDrawing();
					});
				}
			}
		});
	}
}
