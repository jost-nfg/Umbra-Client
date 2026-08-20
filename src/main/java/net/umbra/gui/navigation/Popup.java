/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.navigation;

import net.umbra.Umbra;
import net.umbra.event.events.MouseClickEvent;
import net.umbra.gui.GuiManager;
import net.umbra.gui.UIElement;
import net.umbra.gui.UIProperty;
import net.umbra.gui.components.Component;
import net.umbra.gui.types.Rectangle;
import net.umbra.gui.types.Size;
import net.umbra.gui.types.Thickness;
import net.umbra.rendering.Renderer2D;
import net.umbra.rendering.shaders.Shader;
import net.umbra.utils.types.MouseAction;

public class Popup extends Component {

	public enum PlacementMode {
		Bottom, Top, Right, Left
	}

	private UIElement placementTarget;

	public static final UIProperty<PlacementMode> PlacementModeProperty = new UIProperty<>("PlacementMode", PlacementMode.Bottom, false, true);
	
	public Popup() {
		setProperty(UIElement.PaddingProperty, new Thickness(4f));
		bindProperty(ForegroundProperty, GuiManager.foregroundColor);
		bindProperty(BackgroundProperty, GuiManager.windowBackgroundColor);
		bindProperty(BorderProperty, GuiManager.windowBorderColor);
		bindProperty(CornerRadiusProperty, GuiManager.roundingRadius);
		bindProperty(FontProperty, GuiManager.fontSetting);
	}

	public UIElement getPlacementTarget() {
		return placementTarget;
	}

	public void setPlacementTarget(UIElement target) {
		this.placementTarget = target;
	}


	public void open(UIElement target, UIElement content) {
		open(target, content, PlacementMode.Bottom);
	}

	public void open(UIElement target, UIElement content, PlacementMode mode) {
		this.placementTarget = target;
		
		setProperty(PlacementModeProperty, mode);
		setContent(content);

		if (!initialized)
			initialize();

		invalidateMeasure();
	}

	public void close() {
		this.placementTarget = null;
		UIElement oldContent = getContent();
		setContent(null);
		if (oldContent != null)
			oldContent.dispose();
	}

	@Override
	public void invalidateMeasure() {
		if (!initialized || placementTarget == null)
			return;

		measureDirty = true;

		Rectangle targetSize = placementTarget.getActualSize();
		if (targetSize == null)
			return;

		float targetX = targetSize.x();
		float targetY = targetSize.y();
		float targetWidth = targetSize.width();
		float targetHeight = targetSize.height();

		measureCore(new Size(targetWidth, Float.MAX_VALUE));
		Size preferred = getPreferredSize();

		float w = Math.max(preferred.width(), targetWidth);
		float h = preferred.height();

		Thickness padding = getProperty(UIElement.PaddingProperty);
		if (padding != null) {
			w += padding.horizontalSum();
			h += padding.verticalSum();
		}

		float x;
		float y;
		PlacementMode placementMode = getProperty(PlacementModeProperty);
		switch (placementMode) {
		case Top -> {
			x = targetX;
			y = targetY - h;
		}
		case Right -> {
			x = targetX + targetWidth;
			y = targetY;
		}
		case Left -> {
			x = targetX - w;
			y = targetY;
		}
		case Bottom -> {
			x = targetX;
			y = targetY + targetHeight;
		}
		default -> {
			x = 0;
			y = 0;
		}
		}

		arrange(new Rectangle(x, y, w, h));
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		Rectangle size = getActualSize();
		if (size == null) 
			return;

		float actualX = size.x();
		float actualY = size.y();
		float actualWidth = size.width();
		float actualHeight = size.height();

		Shader bgEffect = getProperty(BackgroundProperty);
		Shader bdEffect = getProperty(BorderProperty);
		Float radius = getProperty(CornerRadiusProperty);

		if (bgEffect != null || bdEffect != null) {
			renderer.drawOutlinedRoundedBox(actualX, actualY, actualWidth, actualHeight, radius != null ? radius : 0f,
					bdEffect, bgEffect);
		}

		super.draw(renderer, partialTicks);
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);

		// Check if the mouse was clicked outside of the popup.
		if (!event.isCancelled() && event.action == MouseAction.DOWN) {
			Rectangle size = getActualSize();
			if (size != null && !size.intersects((float) event.mouseX, (float) event.mouseY)) {
				Umbra.getInstance().guiManager.closePopup();
				event.cancel();
			}
		}
	}
}
