/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TextUtils {
    public static String idToName(String ID) {
        return Arrays.stream(ID.split("_")).map(TextUtils::capitalize).collect(Collectors.joining(" "));
    }

    public static String capitalize(String str) {
        if (str.length() > 1) {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return str;
    }
    
	public static float ptToFontSize(float fontSizePt) {
		// 1 pt = 96/72 DIPs
		// MC font defaults to 8 px height
		// 8 / (96/72 = 6
		return fontSizePt / 6;
	}
}
