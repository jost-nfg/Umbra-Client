/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.navigation.huds;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import net.umbra.event.events.MouseClickEvent;
import net.umbra.gui.GuiManager;
import net.umbra.gui.UIElement;
import net.umbra.gui.navigation.HudWindow;
import net.umbra.gui.types.Rectangle;
import net.umbra.gui.types.ResizeMode;
import net.umbra.gui.types.TextAlign;
import net.umbra.module.Module;
import net.umbra.managers.SettingManager;
import net.umbra.settings.types.EnumSetting;
import net.umbra.rendering.Renderer2D;
import net.minecraft.client.gui.Font;
import net.umbra.utils.types.MouseAction;
import net.umbra.utils.types.MouseButton;

public class ModuleArrayListHud extends HudWindow {
	private final EnumSetting<TextAlign> textAlign = EnumSetting.<TextAlign>builder().id("ModuleArrayListHudText_TextAlign")
			.displayName("Text Align").description("Text Alignment").defaultValue(TextAlign.Left).build();

	public ModuleArrayListHud(int x, int y) {
		super("ModuleArrayListHud", x, y);
		resizeMode = ResizeMode.None;

		SettingManager.registerSetting(textAlign);

		// Calculate max possible width.
		float newWidth = 0;
		for (Module mod : UMBRA.moduleManager.modules) {
			float nameWidth = Renderer2D.getStringWidth(mod.getName(), GuiManager.fontSetting.getValue().getRenderer());
			if (nameWidth > newWidth)
				newWidth = nameWidth;
		}
		
		setProperty(UIElement.WidthProperty, newWidth);
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		super.onMouseClick(event);

		if (getProperty(UIElement.IsHoveredProperty) && event.button == MouseButton.RIGHT && event.action == MouseAction.DOWN) {
			TextAlign currentValue = textAlign.getValue();
			TextAlign[] enumConstants = currentValue.getDeclaringClass().getEnumConstants();
			int currentIndex = Arrays.asList(enumConstants).indexOf(currentValue);
			int enumCount = enumConstants.length;
			currentIndex = (currentIndex + 1) % enumCount;

			textAlign.setValue(enumConstants[currentIndex]);
		}
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		boolean isVisible = getProperty(UIElement.IsVisibleProperty);
		if (isVisible) {
			Rectangle pos = position.getValue();

			AtomicInteger iteration = new AtomicInteger(0);
				Font font = GuiManager.fontSetting.getValue().getRenderer();
				Stream<Module> moduleStream = UMBRA.moduleManager.modules.stream().filter(s -> s.state.getValue())
						.sorted(Comparator.comparing((mod) -> mod.getName()));

				switch (textAlign.getValue()) {
				case Left:
					moduleStream.forEachOrdered(mod -> {
						float yPosition = pos.y() + 10 + (iteration.get() * 20);
						renderer.drawString(mod.getName(), pos.x(), yPosition,
								GuiManager.foregroundColor.getValue(), font);
						iteration.incrementAndGet();
					});
					break;
				case Center:
					moduleStream.forEachOrdered(mod -> {
						float yPosition = pos.y() + 10 + (iteration.get() * 20);
						float centerTextWidth = Renderer2D.getStringWidth(mod.getName(), font) / 2.0f;
						renderer.drawString(mod.getName(),
								pos.x() + (pos.width() / 2.0f) - centerTextWidth, yPosition,
								GuiManager.foregroundColor.getValue(), font);
						iteration.incrementAndGet();
					});
					break;
				case Right:
					moduleStream.forEachOrdered(mod -> {
						float yPosition = pos.y() + 10 + (iteration.get() * 20);
						float rightTextWidth = Renderer2D.getStringWidth(mod.getName(), font);
						renderer.drawString(mod.getName(), pos.x() + pos.width() - rightTextWidth,
								yPosition, GuiManager.foregroundColor.getValue(), font);
						iteration.incrementAndGet();
					});
					break;
				}
		}

		super.draw(renderer, partialTicks);
	}
}
