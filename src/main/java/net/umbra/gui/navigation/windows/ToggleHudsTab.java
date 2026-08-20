/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.navigation.windows;

import java.util.ArrayList;
import net.umbra.gui.UIElement;
import net.umbra.gui.components.HudComponent;
import net.umbra.gui.components.SeparatorComponent;
import net.umbra.gui.components.StackPanelComponent;
import net.umbra.gui.components.StringComponent;
import net.umbra.gui.font.FontManager;
import net.umbra.gui.navigation.HudWindow;
import net.umbra.gui.navigation.Window;
import net.umbra.gui.types.SizeToContent;
import net.umbra.gui.GuiManager;

public class ToggleHudsTab extends Window {
	public ToggleHudsTab(ArrayList<HudWindow> huds) {
		super("Toggle HUDs", 0, 0);
		sizeToContent = SizeToContent.Both;
		StackPanelComponent stackPanel = new StackPanelComponent();
		stackPanel.setSpacing(8f);
		
		StringComponent header = new StringComponent("Toggle HUDs");
		header.setProperty(UIElement.FontWeightProperty, FontManager.WEIGHT_BOLD);
		header.bindProperty(UIElement.ForegroundProperty, GuiManager.foregroundHeaderColor);
		stackPanel.addChild(header);
		
		stackPanel.addChild(new SeparatorComponent());

		for (HudWindow hud : huds) {
			HudComponent hudComponent = new HudComponent(hud.getID(), hud);
			stackPanel.addChild(hudComponent);
		}

		setContent(stackPanel);
		setProperty(UIElement.MinWidthProperty, 300f);
	}
}
