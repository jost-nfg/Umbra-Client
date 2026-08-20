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
import net.minecraft.world.entity.player.Player;

public class SpeedHud extends HudWindow {
	private String speedText = null;

	public SpeedHud(int x, int y) {
		super("SpeedHud", x, y, 50, 24);
		setProperty(UIElement.MinWidthProperty, 50f);
		setProperty(UIElement.MinHeightProperty, 20f);
		setProperty(UIElement.MaxHeightProperty, 20f);
		resizeMode = ResizeMode.None;
	}

	@Override
	public void update() {
		super.update();

		Player player = MC.player;
		if (player != null) {
			double dx = player.getX() - player.xo;
			double dz = player.getZ() - player.zo;
			double dy = player.getY() - player.yo;

			double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

			double speed = distance * 20 * 3.6;

			speedText = String.format("Speed: %.2f km/h", speed);
		} else
			speedText = null;
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		boolean isVisible = getProperty(UIElement.IsVisibleProperty);
		if (speedText != null && isVisible) {
			Rectangle pos = position.getValue();
			renderer.drawString(speedText, pos.x(), pos.y(),
					GuiManager.foregroundColor.getValue(), GuiManager.fontSetting.getValue().getRenderer());
		}

		super.draw(renderer, partialTicks);
	}
}
