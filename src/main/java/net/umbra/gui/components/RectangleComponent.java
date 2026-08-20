/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.components;

import net.umbra.gui.GuiManager;
import net.umbra.rendering.Renderer2D;
import net.umbra.rendering.shaders.Shader;

public class RectangleComponent extends Component {

	public RectangleComponent() {
		bindProperty(BorderThicknessProperty, GuiManager.lineThickness);
		bindProperty(CornerRadiusProperty, GuiManager.roundingRadius);
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		Shader bgEffect = getProperty(BackgroundProperty);
		Shader bdEffect = getProperty(BorderProperty);
		Float cornerRadius = getProperty(CornerRadiusProperty);
		Float borderThickness = getProperty(BorderThicknessProperty);
		float radius = cornerRadius != null ? cornerRadius : 0f;
		float thickness = borderThickness != null ? borderThickness : 0f;

		if (bgEffect != null || bdEffect != null) {
			float actualX = getActualSize().x();
			float actualY = getActualSize().y();
			float actualWidth = getActualSize().width();
			float actualHeight = getActualSize().height();

			if (bgEffect != null) {
				renderer.drawRoundedBox(actualX, actualY, actualWidth, actualHeight, radius, bgEffect);
			}

			if (bdEffect != null) {
				renderer.drawRoundedBoxOutline(actualX, actualY, actualWidth, actualHeight, radius, thickness,
						bdEffect);
			}
		}

		super.draw(renderer, partialTicks);
	}
}
