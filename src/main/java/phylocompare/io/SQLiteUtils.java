/*
 * SQLiteUtils.java Copyright (C) 2026 Daniel H. Huson
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

package phylocompare.io;

import java.io.File;
import java.io.RandomAccessFile;
import java.sql.DriverManager;

public class SQLiteUtils {

	public static boolean isSQLiteWithTreesOrNetworksTable(String fileName) {
		if (fileName == null)
			return false;

		var file = new File(fileName);
		if (!file.isFile() || file.length() < 100)
			return false;

		// First check the SQLite file header
		try (var raf = new RandomAccessFile(file, "r")) {
			byte[] header = new byte[16];
			raf.readFully(header);
			String magic = new String(header);
			if (!magic.startsWith("SQLite format 3"))
				return false;
		} catch (Exception ex) {
			return false;
		}

		// Then open as SQLite and inspect sqlite_master
		var url = "jdbc:sqlite:" + fileName;
		var sql = """
				SELECT 1
				FROM sqlite_master
				WHERE type='table' AND (name='trees' OR name='networks')
				LIMIT 1
				""";

		try (var conn = DriverManager.getConnection(url);
			 var pstmt = conn.prepareStatement(sql);
			 var rs = pstmt.executeQuery()) {
			return rs.next();
		} catch (Exception ex) {
			return false;
		}
	}
}