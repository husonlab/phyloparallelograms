/*
 * SetupRubberBandSelection.java Copyright (C) 2026 Daniel H. Huson
 *
 * (Some files contain contributions from other authors, who are then mentioned separately.)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package phyloparallelograms.view;

import javafx.animation.PauseTransition;
import javafx.collections.ObservableSet;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import jloda.fx.util.ProgramProperties;
import jloda.fx.util.SelectionEffectBlue;
import jloda.phylo.PhyloTree;
import phyloparallelograms.model.Document;
import splitstree6.data.parts.Taxon;

/**
 * rubber-band (box) selection of taxa: all taxa whose labels lie inside the dragged rectangle are toggled.
 * <p>
 * With a mouse, a drag on the background opens the rectangle and, unless the shift key is down, the press clears
 * the selection first.
 * <p>
 * On a touch screen, a drag on the background pans the drawing, so the rectangle is opened by a long press followed
 * by a drag, as in the other apps. The selection is additive, as there is no shift key, and a tap on the background
 * clears it.
 * <p>
 * Daniel Huson, 2025, 11.2026
 */
public class SetupRubberBandSelection {
	private static final double LONG_PRESS_SECONDS = 0.7;
	private static final double TAP_SLOP = 4.0;

	public static void apply(Document document, NetworkView networkView, ObservableSet<Taxon> selectedTaxa) {
		var pane = networkView.getCenterPane();

		var rect = new Rectangle();
		rect.setManaged(false);
		rect.setFill(Color.TRANSPARENT);
		rect.setStroke(SelectionEffectBlue.getInstance().getColor());
		rect.setVisible(false);

		pane.getChildren().add(rect);

		if (ProgramProperties.isDesktop())
			setupForMouse(document, networkView, selectedTaxa, pane, rect);
		else
			setupForTouch(document, networkView, selectedTaxa, pane, rect);
	}

	private static void setupForMouse(Document document, NetworkView networkView, ObservableSet<Taxon> selectedTaxa, Pane pane, Rectangle rect) {
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
			update(rect, start[0], start[1], e.getX(), e.getY());
			e.consume();
		});

		pane.setOnMouseReleased(e -> {
			if (!rect.isVisible())
				return;

			if (!e.isStillSincePress())
				toggleTaxaInside(document, networkView, selectedTaxa, rect);
			rect.setVisible(false);
			e.consume();
		});
	}

	private static void setupForTouch(Document document, NetworkView networkView, ObservableSet<Taxon> selectedTaxa, Pane pane, Rectangle rect) {
		var state = new Object() {
			Point2D press; // where the finger went down, in pane coordinates; null if no gesture is in progress
			boolean selecting; // has the rectangle been opened?
			boolean swallowClick; // the click that follows a completed rectangle must not clear the selection
			boolean suppressScrolling; // set when the rectangle opens and cleared on the next press
			PauseTransition longPress;
		};

		Runnable stopTimer = () -> {
			if (state.longPress != null) {
				state.longPress.stop();
				state.longPress = null;
			}
		};

		Runnable finish = () -> {
			stopTimer.run();
			if (state.selecting) {
				if (rect.getWidth() > TAP_SLOP || rect.getHeight() > TAP_SLOP)
					toggleTaxaInside(document, networkView, selectedTaxa, rect);
				state.swallowClick = true;
			}
			state.selecting = false;
			state.press = null;
			rect.setVisible(false);
		};

		pane.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
			if (!e.isPrimaryButtonDown())
				return;
			// taxon labels consume their press, so any press that arrives here is on the background or the drawing
			state.swallowClick = false;
			state.suppressScrolling = false;
			state.press = new Point2D(e.getX(), e.getY());
			stopTimer.run();
			state.longPress = new PauseTransition(Duration.seconds(LONG_PRESS_SECONDS));
			state.longPress.setOnFinished(a -> {
				if (state.press != null) {
					state.selecting = true;
					state.suppressScrolling = true;
					rect.setX(state.press.getX());
					rect.setY(state.press.getY());
					rect.setWidth(0);
					rect.setHeight(0);
					rect.setVisible(true);
					rect.toFront();
				}
			});
			state.longPress.playFromStart();
		});

		// Once the rectangle is open, drags and the release belong to it, whatever lies under the finger, so they are
		// handled in the capturing phase. The rectangle also follows the touch events, which are always delivered to
		// the node on which the finger went down.
		pane.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
			if (state.press == null)
				return;
			if (state.selecting) {
				var local = pane.sceneToLocal(e.getSceneX(), e.getSceneY());
				update(rect, state.press.getX(), state.press.getY(), local.getX(), local.getY());
				e.consume();
			} else {
				var local = pane.sceneToLocal(e.getSceneX(), e.getSceneY());
				if (state.press.distance(local) > TAP_SLOP)
					stopTimer.run(); // an ordinary drag: the drawing is panned
			}
		});
		pane.addEventFilter(TouchEvent.TOUCH_MOVED, e -> {
			if (state.selecting && state.press != null) {
				var local = pane.sceneToLocal(e.getTouchPoint().getSceneX(), e.getTouchPoint().getSceneY());
				update(rect, state.press.getX(), state.press.getY(), local.getX(), local.getY());
			} else if (state.press != null) {
				var local = pane.sceneToLocal(e.getTouchPoint().getSceneX(), e.getTouchPoint().getSceneY());
				if (state.press.distance(local) > TAP_SLOP)
					stopTimer.run();
			}
		});
		pane.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
			var wasSelecting = state.selecting;
			finish.run();
			if (wasSelecting)
				e.consume();
		});
		// not consumed: the enclosing scroll pane has to see the release, or it keeps its scroll bars showing
		pane.addEventFilter(TouchEvent.TOUCH_RELEASED, e -> finish.run());

		// a drag on a touch screen also arrives as a scroll gesture, which would pan the drawing under the rectangle
		pane.addEventFilter(ScrollEvent.ANY, e -> {
			if (state.selecting || state.suppressScrolling)
				e.consume();
		});

		// a tap on the background clears the selection
		pane.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			if (state.swallowClick) {
				state.swallowClick = false;
				e.consume();
			} else if (e.isStillSincePress()) { // taxon labels consume their click, so this is not a tap on a label
				selectedTaxa.clear();
			}
		});
	}

	private static void update(Rectangle rect, double x0, double y0, double x1, double y1) {
		rect.setX(Math.min(x0, x1));
		rect.setY(Math.min(y0, y1));
		rect.setWidth(Math.abs(x1 - x0));
		rect.setHeight(Math.abs(y1 - y0));
	}

	/**
	 * toggles the selection state of all taxa whose labels lie inside the rectangle
	 */
	private static void toggleTaxaInside(Document document, NetworkView networkView, ObservableSet<Taxon> selectedTaxa, Rectangle rect) {
		var selectionBounds = rect.localToScene(rect.getBoundsInLocal());
		for (var entry : networkView.getNodeLabeledNodeShapeMap().entrySet()) {
			var v = entry.getKey();
			var label = entry.getValue().getLabel();
			if (label != null) {
				var b = label.localToScene(label.getBoundsInLocal());
				if (selectionBounds.contains(b)) {
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
	}
}
