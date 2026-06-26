/*
 * BBox.java Copyright (C) 2026 Daniel H. Huson
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

import javafx.scene.Node;

import java.util.Collection;

public record BBox(double xMin, double yMin, double xMax, double yMax) {

	public static BBox compute(Collection<? extends Node> nodes) {
		var xMin = nodes.stream().mapToDouble(Node::getTranslateX).min().orElse(0.0);
		var xMax = nodes.stream().mapToDouble(Node::getTranslateX).max().orElse(0.0);
		var yMin = nodes.stream().mapToDouble(Node::getTranslateY).min().orElse(0.0);
		var yMax = nodes.stream().mapToDouble(Node::getTranslateY).max().orElse(0.0);
		return new BBox(xMin, yMin, xMax, yMax);
	}

	public double width() {
		return xMax - xMin;
	}

	public double height() {
		return yMax - yMin;
	}
}
