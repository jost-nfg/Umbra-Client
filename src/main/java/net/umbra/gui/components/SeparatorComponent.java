/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.components;

import net.umbra.gui.GuiManager;
import net.umbra.gui.UIElement;
import net.umbra.rendering.Renderer2D;

public class SeparatorComponent extends Component {

	public SeparatorComponent() {
		setProperty(UIElement.HeightProperty, 1.0f);
		bindProperty(BorderProperty, GuiManager.componentBorderColor);
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		float actualX = getActualSize().x();
		float actualY = getActualSize().y();
		float actualWidth = getActualSize().width();

		renderer.drawLine(actualX, actualY, actualX + actualWidth, actualY, getProperty(BorderProperty));
	}
}
