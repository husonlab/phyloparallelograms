/*
 * ExportNewick.java Copyright (C) 2025 Daniel H. Huson
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

package phylofusion.io;

import javafx.stage.FileChooser;
import jloda.fx.util.ProgramProperties;
import jloda.fx.window.NotificationManager;
import jloda.phylo.CommentData;
import jloda.phylo.NewickIO;
import jloda.phylo.PhyloTree;
import jloda.util.FileUtils;
import phylofusion.window.MainWindow;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * save in Newick format
 * Daniel Huson, 9.2024
 */
public class ExportNewick {
	public static void apply(MainWindow mainWindow) {
		final var fileChooser = new FileChooser();
		fileChooser.setTitle("Export Newick Format");

		final var previousFile = new File(ProgramProperties.get("NewickExport", ""));
		if (previousFile.isFile()) {
			fileChooser.setInitialDirectory(previousFile.getParentFile());
			fileChooser.setInitialFileName(mainWindow.getName());
		}
		fileChooser.setSelectedExtensionFilter(ExtensionFilters.newick());
		fileChooser.getExtensionFilters().addAll(ExtensionFilters.newick(), ExtensionFilters.createText());
		var file = fileChooser.showSaveDialog(mainWindow.getStage());
		if (file != null) {
			var newickIO = new NewickIO();
			newickIO.setNewickNodeCommentSupplier(CommentData.createDataNodeSupplier());
			newickIO.setNewickEdgeCommentSupplier(CommentData.createDataEdgeSupplier());
			try {
				apply(file.getPath(), mainWindow.getDocument().getNetworks());
				ProgramProperties.put("NewickExport", file.getPath());
			} catch (IOException ex) {
				NotificationManager.showError("Export failed: " + ex);
			}
		}
	}

	public static void apply(String filename, List<PhyloTree> networks) throws IOException {
		var newickIO = new NewickIO();
		newickIO.setNewickNodeCommentSupplier(CommentData.createDataNodeSupplier());
		newickIO.setNewickEdgeCommentSupplier(CommentData.createDataEdgeSupplier());
		try (var w = FileUtils.getOutputWriterPossiblyZIPorGZIP(filename)) {
			for (var network : networks) {
				newickIO.write(network, w, false, false);
				w.write(";\n");
			}
		}
	}
}

