/*
 * SetupContextMenuTaxonLabels.java Copyright (C) 2026 Daniel H. Huson
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

import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import jloda.fx.control.RichTextLabel;
import splitstree6.data.parts.Taxon;

public class SetupContextMenuTaxonLabels {

	public static void apply(RichTextLabel label, Taxon taxon, Group parent) {
		var editItem = new MenuItem("Edit Label");
		var copyItem = new MenuItem("Copy Selected Labels");

		var menu = new ContextMenu(editItem, new SeparatorMenuItem(), copyItem);

		label.setOnContextMenuRequested(e -> {
			menu.show(label, e.getScreenX(), e.getScreenY());
			e.consume();
		});

		editItem.setOnAction(e -> {
			TextField editor = new TextField(label.getText());

			editor.setTranslateX(label.getTranslateX());
			editor.setTranslateY(label.getTranslateY());

			editor.setLayoutX(label.getLayoutX());
			editor.setLayoutY(label.getLayoutY());
			editor.setPrefWidth(Math.max(80, label.getWidth() + 30));

			parent.getChildren().add(editor);
			label.setVisible(false);

			editor.requestFocus();
			editor.selectAll();

			Runnable commit = () -> {
				taxon.setDisplayLabel(editor.getText());
				label.setVisible(true);
				parent.getChildren().remove(editor);
			};

			Runnable cancel = () -> {
				label.setVisible(true);
				parent.getChildren().remove(editor);
			};

			editor.setOnAction(a -> commit.run());

			editor.setOnKeyPressed(k -> {
				switch (k.getCode()) {
					case ESCAPE -> cancel.run();
					case ENTER -> commit.run();
				}
			});

			editor.focusedProperty().addListener((obs, old, focused) -> {
				if (!focused && parent.getChildren().contains(editor)) {
					commit.run();
				}
			});
		});
	}
}
