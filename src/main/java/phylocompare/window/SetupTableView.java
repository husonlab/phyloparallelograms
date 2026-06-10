

/*
 * SetupTableView.java Copyright (C) 2026 Daniel H. Huson
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

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.converter.DefaultStringConverter;
import jloda.fx.util.BasicFX;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

class SetupTableView {

	public static void apply(TableView<TreeRecord> treeTableView,
							 TableColumn<TreeRecord, String> treeColumn, TableColumn<TreeRecord, Boolean> runColumn,
							 TableColumn<TreeRecord, Boolean> showColumn, BooleanProperty disableAllRun, BooleanProperty disableAllShow,
							 MainWindowController controller) {
		treeTableView.setEditable(true);
		treeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		var treeNameEditRequested = new AtomicBoolean(false);

		// Tree name
		treeColumn.setEditable(true);
		treeColumn.setCellValueFactory(data -> data.getValue().nameProperty());
		treeColumn.setCellFactory(col -> new TextFieldTableCell<TreeRecord, String>(new DefaultStringConverter()) {
			@Override
			public void startEdit() {
				if (treeNameEditRequested.get())
					super.startEdit();
			}
		});
		treeColumn.setOnEditCommit(e -> {
			var record = e.getRowValue();
			record.setName(e.getNewValue());
		});

		// Run checkbox

		runColumn.setEditable(true);
		runColumn.setCellValueFactory(cd -> cd.getValue().runLayoutProperty());
		runColumn.setCellFactory(col -> {
			var cell = new CheckBoxTableCell<TreeRecord, Boolean>();
			cell.setEditable(true);
			cell.disableProperty().bind(disableAllRun);
			return cell;
		});

		// Show checkbox
		showColumn.setEditable(true);

		showColumn.setCellValueFactory(cd -> cd.getValue().showProperty());
		showColumn.setCellFactory(col -> {
			var cell = new CheckBoxTableCell<TreeRecord, Boolean>();
			cell.setEditable(true);
			cell.disableProperty().bind(disableAllShow);
			return cell;
		});

		installRowDragAndDrop(treeTableView, treeColumn, treeNameEditRequested, controller);
	}

	private static void installRowDragAndDrop(TableView<TreeRecord> tableView, TableColumn<TreeRecord, String> treeColumn,
											  AtomicBoolean treeNameEditRequested, MainWindowController controller) {
		tableView.setRowFactory(tv -> {
			var row = new TableRow<TreeRecord>();

			var editNameItem = new MenuItem("Edit tree name");
			editNameItem.setOnAction(e -> {
				if (!row.isEmpty()) {
					var index = row.getIndex();
					tableView.getSelectionModel().clearAndSelect(index);
					treeNameEditRequested.set(true);
					tableView.edit(index, treeColumn);
					treeNameEditRequested.set(false);
				}
			});

			var menu = new ContextMenu();
			menu.getItems().add(editNameItem);
			menu.getItems().add(new SeparatorMenuItem());
			menu.getItems().addAll(BasicFX.copyMenu(List.of(controller.getUseAllMenuItem(), controller.getUseNoneMenuItem(),
					new SeparatorMenuItem(), controller.getShowAllMenuItem(), controller.getShowNoneMenuItem()), false));

			// Only show menu for non-empty rows:
			row.contextMenuProperty().bind(
					Bindings.when(row.emptyProperty())
							.then((ContextMenu) null)
							.otherwise(menu)
			);

			// Ensure right-click selects the row (without nuking multi-selection unless desired)
			row.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
				if (evt.getButton() == MouseButton.SECONDARY && !row.isEmpty()) {
					MultipleSelectionModel<TreeRecord> sm = tv.getSelectionModel();

					int index = row.getIndex();
					if (!sm.getSelectedIndices().contains(index)) {
						// common behavior: right-click focuses/selects only that row
						// If you'd rather ADD to selection, use sm.select(index) instead.
						sm.clearAndSelect(index);
					}
				}
			});

			row.setOnDragDetected(event -> {
				if (!row.isEmpty()) {
					var index = row.getIndex();
					var dragboard = row.startDragAndDrop(TransferMode.MOVE);
					var content = new ClipboardContent();
					content.putString(String.valueOf(index));
					dragboard.setContent(content);
					event.consume();
				}
			});

			row.setOnDragOver(event -> {
				var dragboard = event.getDragboard();
				if (dragboard.hasString()) {
					var draggedIndex = Integer.parseInt(dragboard.getString());
					if (row.getIndex() != draggedIndex) {
						event.acceptTransferModes(TransferMode.MOVE);
						event.consume();
					}
				}
			});

			row.setOnDragDropped(event -> {
				var dragboard = event.getDragboard();
				if (dragboard.hasString()) {
					var draggedIndex = Integer.parseInt(dragboard.getString());
					var items = tableView.getItems();
					var draggedRecord = items.remove(draggedIndex);
					int dropIndex;
					if (row.isEmpty()) {
						dropIndex = items.size();
					} else {
						dropIndex = row.getIndex();
					}
					items.add(dropIndex, draggedRecord);
					tableView.getSelectionModel().clearAndSelect(dropIndex);
					event.setDropCompleted(true);
					event.consume();
				}
			});

			return row;
		});
	}

}
