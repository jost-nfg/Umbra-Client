/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.components;

import net.umbra.gui.UIElement;
import net.umbra.gui.UIProperty;
import net.umbra.rendering.Renderer2D;
import net.umbra.rendering.shaders.Shader;

public class PolygonComponent extends Component {

	public static final UIProperty<float[]> PolygonProperty = new UIProperty<>("Polygon", null, false, true);

	public PolygonComponent() {
	}

	public PolygonComponent(float[] polygon) {
		setProperty(PolygonProperty, polygon);
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		Shader fgEffect = getProperty(UIElement.ForegroundProperty);
		float[] polygon = getProperty(PolygonProperty);

		if (fgEffect != null && polygon != null) {
			float actualX = getActualSize().x();
			float actualY = getActualSize().y();
			float actualWidth = getActualSize().width();
			float actualHeight = getActualSize().height();
			renderer.drawPolygon(actualX, actualY, actualWidth, actualHeight, polygon, fgEffect);
		}

		super.draw(renderer, partialTicks);
	}
}
