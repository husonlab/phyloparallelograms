/*
 * Version.java Copyright (C) 2026 Daniel H. Huson
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

package phylocompare.main;

public class Version {
	static public final String NAME = "PhyloCompare";

	/**
	 * Single source of truth: the {@code <version>} element of pom.xml.
	 * Maven copies it into the built JAR's manifest as
	 * {@code Implementation-Version} — provided the maven-jar-plugin
	 * config has {@code <addDefaultImplementationEntries>true} —
	 * and we read it back here at class-load time.
	 * <p>
	 * Falls back to "dev" when running outside a packaged JAR (for
	 * example from an IDE), so neither tagged releases nor IDE runs
	 * ever require this file to be edited by hand.
	 */
	public static final String VERSION = resolveVersion();

	public static final String SHORT_DESCRIPTION = NAME + " (version " + VERSION + ") - License GPL v3";

	public static final String UPDATE_MANIFEST_URL = "https://github.com/husonlab/phylocompare/releases/latest/download/manifest.json";

	static public final String WEBSITE_URL = "https://husonlab.github.io/phylocompare/manual.html";
	public static final String GITHUB_PAGE = "https://github.com/husonlab/phylcompare";

	private static String resolveVersion() {
		var pkg = Version.class.getPackage();
		var v = (pkg != null) ? pkg.getImplementationVersion() : null;
		return (v != null && !v.isBlank()) ? v : "dev";
	}



}
