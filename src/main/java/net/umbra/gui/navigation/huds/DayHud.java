/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.navigation.huds;

import net.umbra.gui.GuiManager;
import net.umbra.gui.UIElement;
import net.umbra.gui.navigation.HudWindow;
import net.umbra.gui.types.Rectangle;
import net.umbra.gui.types.ResizeMode;
import net.umbra.rendering.Renderer2D;

public class DayHud extends HudWindow {
	private String timeText = null;

	public DayHud(int x, int y) {
		super("DayHud", x, y, 50, 24);
		setProperty(UIElement.MinWidthProperty, 50f);
		setProperty(UIElement.MinHeightProperty, 24f);
		setProperty(UIElement.MaxHeightProperty, 24f);
		resizeMode = ResizeMode.None;
	}

	@Override
	public void update() {
		super.update();
		timeText = "Day: " + (int) (MC.level.getGameTime() / 24000);
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		boolean isVisible = getProperty(UIElement.IsVisibleProperty);
		if (timeText != null && isVisible) {
			Rectangle pos = position.getValue();
			renderer.drawString(timeText, pos.x(), pos.y(),
					GuiManager.foregroundColor.getValue(), GuiManager.fontSetting.getValue().getRenderer());
		}

		super.draw(renderer, partialTicks);
	}
}