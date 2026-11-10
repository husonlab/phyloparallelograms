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
import jloda.phylo.PhyloTree;

import java.io.IOException;

/**
 * the text that represents a network in a QR code
 * Daniel Huson, 11.2026
 */
public class NetworkQR {
	/**
	 * the network in Newick format, for display as a QR code. The annotations of the network (the tree traces and
	 * other data attached to its nodes and edges, written as comments in square brackets) are included only if the
	 * string then fits into a QR code. Otherwise, the annotations are removed from the string.
	 *
	 * @param network the network
	 * @return the Newick string, or null, if there is no network or if it is too large for a QR code even without
	 * its annotations
	 */
	public static String apply(PhyloTree network) {
		if (network == null || network.getNumberOfNodes() == 0)
			return null;
		try {
			return choose(ExportNewick.apply(network).trim());
		} catch (IOException ignored) {
			return null;
		}
	}

	/**
	 * chooses the string to show: the annotated Newick string if it fits into a QR code, otherwise the same string
	 * with its annotations removed
	 *
	 * @return the string to show, or null, if it does not fit even without annotations
	 */
	public static String choose(String annotatedNewick) {
		if (QRViewUtils.canEncode(annotatedNewick))
			return annotatedNewick;
		var plain = removeAnnotations(annotatedNewick);
		return QRViewUtils.canEncode(plain) ? plain : null;
	}

	/**
	 * removes all annotations, that is, all comments in square brackets, from a Newick string. Square brackets
	 * inside a quoted label are part of the label and are kept
	 */
	public static String removeAnnotations(String newick) {
		var buf = new StringBuilder(newick.length());
		var depth = 0; // nesting depth of square brackets
		var inQuotes = false;
		for (var i = 0; i < newick.length(); i++) {
			var ch = newick.charAt(i);
			if (depth == 0 && ch == '\'')
				inQuotes = !inQuotes; // a doubled quote inside a label toggles twice, which is harmless
			if (!inQuotes && ch == '[') {
				depth++;
			} else if (!inQuotes && ch == ']' && depth > 0) {
				depth--;
			} else if (depth == 0) {
				buf.append(ch);
			}
		}
		return buf.toString();
	}
}
