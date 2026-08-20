/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.components;

import net.umbra.gui.UIElement;
import net.umbra.gui.types.Size;
import net.umbra.rendering.Renderer2D;
import net.umbra.rendering.shaders.Shader;

public class EllipseComponent extends Component {
	public EllipseComponent() {
	}

	@Override
	public Size measure(Size availableSize) {
		Float width = getProperty(UIElement.WidthProperty);
		Float height = getProperty(UIElement.HeightProperty);
		float w = width != null ? width : availableSize.width();
		float h = height != null ? height : availableSize.height();
		return new Size(w, h);
	}
	
	public void draw(Renderer2D renderer, float partialTicks) {
		float actualX = getActualSize().x();
		float actualY = getActualSize().y();
		float actualWidth = getActualSize().width();
		float actualHeight = getActualSize().height();

		float radiusX = actualWidth / 2f;
		float radiusY = actualHeight / 2f;
		float centerX = actualX + radiusX;
		float centerY = actualY + radiusY;

		Shader bgEffect = getProperty(UIElement.BackgroundProperty);
		if (bgEffect != null) {
			renderer.drawEllipse(centerX, centerY, radiusX, radiusY, bgEffect);
		}

		super.draw(renderer, partialTicks);
	}
}
