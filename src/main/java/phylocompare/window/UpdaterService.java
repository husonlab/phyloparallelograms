/*
 * UpdaterService.java Copyright (C) 2026 Daniel H. Huson
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

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Window;

import java.util.ServiceLoader;

public interface UpdaterService {
	SimpleBooleanProperty DISABLED = new SimpleBooleanProperty(true);

	default void checkForUpdates(Window owner) {
	}

	default ReadOnlyBooleanProperty disabledProperty() {
		return DISABLED;
	}

	static UpdaterService get() {
		return ServiceLoader.load(UpdaterService.class)
				.findFirst()
				.orElse(new UpdaterService() {
				});
	}
}