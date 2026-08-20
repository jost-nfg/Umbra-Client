/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.utils.input;

import static net.umbra.UmbraClient.MC;

import org.lwjgl.glfw.GLFW;

public class Input {
	private static CursorStyle lastCursorStyle = CursorStyle.Default;

	public static void setCursorStyle(CursorStyle style) {

		if (lastCursorStyle != style) {
			GLFW.glfwSetCursor(MC.getWindow().handle(), style.getGlfwCursor());
			lastCursorStyle = style;
		}
	}
}
