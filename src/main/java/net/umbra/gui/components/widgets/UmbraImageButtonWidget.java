/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.components.widgets;

import static net.umbra.UmbraClient.MC;

import java.util.function.Consumer;

import net.umbra.Umbra;
import net.umbra.gui.GuiManager;
import net.umbra.gui.colors.Colors;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.umbra.rendering.Renderer2D;
import net.umbra.rendering.shaders.Shader;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class UmbraImageButtonWidget extends AbstractButton {
	private Consumer<UmbraImageButtonWidget> pressAction;
	private final Identifier image;
	private boolean background = true;
	private static final Shader IMAGE_SHADER = Shader.solid(Colors.White);

	public UmbraImageButtonWidget(int x, int y, int width, int height, Identifier image) {
		super(x, y, width, height, Component.empty());

		this.image = image;
	}

	public UmbraImageButtonWidget(int x, int y, int width, int height, Identifier image,
			boolean background) {
		super(x, y, width, height, Component.empty());

		this.image = image;
		this.background = background;
	}

	public void setPressAction(Consumer<UmbraImageButtonWidget> pressAction) {
		this.pressAction = pressAction;
	}

	@Override
	public void onPress(InputWithModifiers input) {
		if (pressAction != null) {
			pressAction.accept(this);
		}
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		if (background) {
			Shader bgEffect = isHovered() ? GuiManager.buttonHoverBackgroundColor.getValue()
					: GuiManager.buttonBackgroundColor.getValue();
			Shader bdEffect = GuiManager.buttonBorderColor.getValue();

			Renderer2D renderer = Umbra.getInstance().render2D;
			renderer.beginFrame(graphics, MC.getDeltaTracker());
			renderer.drawOutlinedRoundedBox(getX(), getY(), width, height,
					GuiManager.roundingRadius.getValue(), bdEffect, bgEffect);
			renderer.drawTexturedQuad(image, getX(), getY(), width, height, IMAGE_SHADER);
		}
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		
	}
}