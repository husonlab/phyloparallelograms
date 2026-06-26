/*
 * ScaleDrawing.java Copyright (C) 2026 Daniel H. Huson
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

import javafx.scene.Group;
import javafx.scene.shape.Path;
import jloda.fx.util.BasicFX;
import splitstree6.layout.tree.LabeledNodeShape;

public class ScaleDrawing {
	public static void apply(Group group, double dx, double dy) {
		var nodes = BasicFX.getAllRecursively(group, LabeledNodeShape.class);
		var edges = BasicFX.getAllRecursively(group, Path.class);

		if (dx != 0 || dy != 0) {
			var oldBBox = BBox.compute(nodes);
			var newBBox = new BBox(dx * oldBBox.xMin(), dy * oldBBox.yMin(), dx * oldBBox.xMax() + dx, dy * oldBBox.yMax());

			var xFactor = (oldBBox.width() > 0 ? newBBox.width() / oldBBox.width() : 1.0);
			var yFactor = (oldBBox.height() > 0 ? newBBox.height() / oldBBox.height() : 1.0);

			for (var n : nodes) {
				var x = n.getTranslateX();
				var y = n.getTranslateY();
				var newX = oldBBox.width() <= 0 ? x : (x - oldBBox.xMin()) * xFactor + newBBox.xMin();
				var newY = oldBBox.height() <= 0 ? y : (y - oldBBox.yMax()) * yFactor + newBBox.yMax();
				n.setTranslateX(newX);
				n.setTranslateY(newY);
			}
			for (var path : edges) {
				var eId = path.getId();
				var elements = PathTransforms.fitToBounds(path, oldBBox, newBBox).getElements();
				path.getElements().setAll(elements);
			}
		}
	}
}
