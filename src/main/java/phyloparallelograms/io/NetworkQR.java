/*
 * NetworkQR.java Copyright (C) 2026 Daniel H. Huson
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

package phyloparallelograms.io;

import jloda.fx.qr.QRViewUtils;
import jloda.phylo.NewickIO;
import jloda.phylo.PhyloTree;

import java.io.IOException;
import java.io.StringWriter;

/**
 * the text that represents a network in a QR code
 * Daniel Huson, 11.2026
 */
public class NetworkQR {
	/**
	 * the network in Newick format, for display as a QR code. The annotations of the network (the tree traces and
	 * other data attached to its nodes and edges) are included, if the result fits into a QR code. Otherwise, the
	 * network is written without them, with its edge weights only.
	 *
	 * @param network the network
	 * @return the Newick string, or null, if there is no network or if it is too large for a QR code either way
	 */
	public static String apply(PhyloTree network) {
		if (network == null || network.getNumberOfNodes() == 0)
			return null;
		try {
			var annotated = ExportNewick.apply(network).trim();
			if (QRViewUtils.canEncode(annotated))
				return annotated;

			var w = new StringWriter();
			new NewickIO().write(network, w, true, false);
			var plain = w + ";";
			if (QRViewUtils.canEncode(plain))
				return plain;
		} catch (IOException ignored) {
		}
		return null;
	}
}
