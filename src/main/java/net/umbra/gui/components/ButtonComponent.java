/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.components;

import net.umbra.event.events.MouseClickEvent;
import net.umbra.gui.GuiManager;
import net.umbra.gui.UIElement;
import net.umbra.gui.types.Thickness;
import net.umbra.utils.input.CursorStyle;
import net.umbra.rendering.Renderer2D;
import net.umbra.rendering.shaders.Shader;
import net.umbra.utils.types.MouseAction;
import net.umbra.utils.types.MouseButton;

public class ButtonComponent extends Component {
	private Runnable onClick;

	/**
	 * Constructor for button component.
	 *
	 * @param onClick OnClick delegate that will run when the button is pressed.
	 */
	public ButtonComponent(Runnable onClick) {
		this.onClick = onClick;
		this.setProperty(UIElement.CursorProperty, CursorStyle.Click);
		this.setProperty(UIElement.PaddingProperty, new Thickness(4f));
		this.bindProperty(UIElement.BackgroundProperty, GuiManager.buttonBackgroundColor);
		this.bindProperty(UIElement.BorderProperty, GuiManager.buttonBorderColor);
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		boolean hovered = getProperty(UIElement.IsHoveredProperty);
		Shader bgEffect = hovered ? GuiManager.buttonHoverBackgroundColor.getValue()
				: getProperty(BackgroundProperty);
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

	/**
	 * Sets the OnClick delegate of the button.
	 *
	 * @param onClick Delegate to set.
	 */
	public void setOnClick(Runnable onClick) {
		this.onClick = onClick;
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);
		if (event.button == MouseButton.LEFT && event.action == MouseAction.DOWN) {
			boolean hovered = getProperty(UIElement.IsHoveredProperty);
			if (hovered) {
				if (onClick != null)
					onClick.run();
				event.cancel();
			}
		}
	}
}
