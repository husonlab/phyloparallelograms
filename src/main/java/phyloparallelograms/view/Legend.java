/*
 * Legend.java Copyright (C) 2026 Daniel H. Huson
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

package phyloparallelograms.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import jloda.fx.undo.UndoManager;
import jloda.fx.util.BasicFX;
import phyloparallelograms.window.MainWindow;
import phyloparallelograms.window.TreeRecord;

public class Legend {
	private final MainWindow window;
	private final VBox vbox;

	public Legend(MainWindow window, VBox vbox) {
		this.window = window;
		this.vbox = vbox;

		var document = window.getDocument();
		vbox.getChildren().addListener((ListChangeListener<Node>) c -> {
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
			if (vbox.getChildren().size() > 25)
				vbox.setVisible(false);
		});

		vbox.managedProperty().bind(vbox.visibleProperty());
		window.getController().getShowLegendCheckMenuItem().selectedProperty().bindBidirectional(visibleProperty());
	}

	public void clear() {
		vbox.getChildren().clear();
	}

	public LegendItem add(Integer treeId, TreeRecord record, Color color) {
		if (vbox.getChildren().isEmpty())
			vbox.getChildren().add(new Label("Trees"));

		var treeName = (record != null ? record.getName() : "???");
		var label = new Label(treeName);
		label.setUserData(treeId);
		if (record == null) {
			label.setText(("[ %s ]").formatted(label.getText()));
		}

		var patch = new Rectangle(14, 2);
		patch.setStrokeWidth(0);
		patch.setStroke(Color.TRANSPARENT);
		patch.setFill(color);

		var selected = new SimpleBooleanProperty(this, "selected", false);
		var highlighted = new SimpleBooleanProperty(this, "highlighted", false);
		highlighted.addListener((v, o, n) -> patch.setHeight(n ? 10 : 2));

		selected.addListener((v, o, n) -> {
			highlighted.set(n);
		});
		var hbox = new HBox(patch, label);
		hbox.setAlignment(Pos.CENTER_LEFT);
		hbox.setSpacing(5);
		hbox.setOnMouseClicked(e -> selected.set(!selected.get()));

		vbox.getChildren().add(hbox);

		hbox.setOnMouseEntered(e -> {
			highlighted.set(true);
		});

		hbox.setOnMouseExited(e -> {
			if (!selected.get()) {
				highlighted.set(false);
			}
		});

		setupColorContextMenu(hbox, record, window.getUndoManager());
		return new LegendItem(selected, highlighted);
	}

	public BooleanProperty visibleProperty() {
		return vbox.visibleProperty();
	}

	public boolean isVisible() {
		return vbox.isVisible();
	}

	public void setVisible(boolean visible) {
		vbox.setVisible(visible);
	}

	private void setupColorContextMenu(Node node, TreeRecord treeRecord, UndoManager undoManager) {
		var colorPicker = new ColorPicker(Color.BLACK);

		var colorItem = new CustomMenuItem(colorPicker);
		colorItem.setHideOnClick(false);

		var contextMenu = new ContextMenu(colorItem);
		node.setOnContextMenuRequested(e -> {
			var current = treeRecord.getColor();
			if (current != null)
				colorPicker.setValue(current);
			contextMenu.show(node, e.getScreenX(), e.getScreenY());
			e.consume();
		});
		colorPicker.setOnAction(e -> {
			var color = colorPicker.getValue();
			if (color != treeRecord.getColor()) {
				var oldColor = treeRecord.getColor();
				undoManager.doAndAdd("color", () -> {
					treeRecord.setColor(oldColor);
					window.getPresenter().runUpdateTreesDrawing();
				}, () -> {
					treeRecord.setColor(color);
					window.getPresenter().runUpdateTreesDrawing();
				});
			}
			contextMenu.hide();
		});
	}

	public record LegendItem(BooleanProperty selectedProperty, BooleanProperty highlightedProperty) {
	}


}
