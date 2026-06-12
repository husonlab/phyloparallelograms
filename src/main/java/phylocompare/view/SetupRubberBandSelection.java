/*
 * SetupRubberBandSelection.java Copyright (C) 2026 Daniel H. Huson
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

package phylocompare.view;

import javafx.collections.ObservableSet;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import jloda.phylo.PhyloTree;
import phylocompare.model.Document;
import splitstree6.data.parts.Taxon;

public class SetupRubberBandSelection {
	public static void apply(Document document, NetworkView networkView, ObservableSet<Taxon> selectedTaxa) {
		var pane = networkView.getCenterPane();
		var nodeLabeledNodeShapeMap = networkView.getNodeLabeledNodeShapeMap();

		var rect = new Rectangle();
		rect.setManaged(false);
		rect.setFill(Color.TRANSPARENT);
		rect.setStroke(Color.color(0.2, 0.4, 1.0, 0.8));
		rect.setVisible(false);

		pane.getChildren().add(rect);

		final double[] start = new double[2];

		pane.setOnMousePressed(e -> {
			if (!e.isPrimaryButtonDown())
				return;

			start[0] = e.getX();
			start[1] = e.getY();

			rect.setX(start[0]);
			rect.setY(start[1]);
			rect.setWidth(0);
			rect.setHeight(0);
			rect.setVisible(true);
			if (!e.isShiftDown())
				selectedTaxa.clear();
			e.consume();
		});

		pane.setOnMouseDragged(e -> {
			if (!rect.isVisible())
				return;
			var x = Math.min(start[0], e.getX());
			var y = Math.min(start[1], e.getY());
			var w = Math.abs(e.getX() - start[0]);
			var h = Math.abs(e.getY() - start[1]);

			rect.setX(x);
			rect.setY(y);
			rect.setWidth(w);
			rect.setHeight(h);
			e.consume();
		});

		pane.setOnMouseReleased(e -> {
			if (!rect.isVisible() || e.isStillSincePress())
				return;

			var selectionBounds = rect.localToScene(rect.getBoundsInLocal());
			for (var entry : nodeLabeledNodeShapeMap.entrySet()) {
				var v = entry.getKey();
				var labeledNode = entry.getValue();
				var label = labeledNode.getLabel();
				if (label != null) {
					var b = label.localToScene(label.getBoundsInLocal());
					if (selectionBounds.intersects(b)) {
						var network = (PhyloTree) v.getOwner();
						if (network.hasTaxa(v)) {
							var taxId = network.getTaxon(v);
							if (taxId != -1) {
								var taxon = document.getTaxaBlock().get(taxId);
								if (taxon != null) {
									if (selectedTaxa.contains(taxon)) {
										selectedTaxa.remove(taxon);
									} else {
										selectedTaxa.add(taxon);
									}
								}
							}
						}
					}
				}
			}
			rect.setVisible(false);
			e.consume();
		});
	}
}
