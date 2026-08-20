/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.navigation.windows;

import java.util.Arrays;
import net.umbra.Umbra;
import net.umbra.gui.components.ComboBoxComponent;
import net.umbra.gui.components.SeparatorComponent;
import net.umbra.gui.components.StackPanelComponent;
import net.umbra.gui.components.StringComponent;
import net.umbra.gui.font.FontManager;
import net.umbra.gui.navigation.Window;
import net.umbra.gui.types.BindingMode;
import net.umbra.gui.types.SizeToContent;
import net.umbra.module.AntiCheat;
import net.umbra.gui.GuiManager;
import net.umbra.gui.UIElement;

/**
 * Represents the AntiCheat Window that allows the user to select their
 * anticheat.
 */
public class AntiCheatWindow extends Window {
	public AntiCheatWindow() {
		super("AntiCheat", 50, 990);
		sizeToContent = SizeToContent.Both;
		StackPanelComponent stackPanel = new StackPanelComponent();
		stackPanel.setSpacing(8f);
		
		StringComponent headerText = new StringComponent("AntiCheat Settings");
		headerText.setProperty(UIElement.FontWeightProperty, FontManager.WEIGHT_BOLD);
		headerText.bindProperty(ForegroundProperty, GuiManager.foregroundHeaderColor);
		stackPanel.addChild(headerText);
	
		stackPanel.addChild(new SeparatorComponent());
		
		ComboBoxComponent comboBox = new ComboBoxComponent();
		comboBox.setProperty(ComboBoxComponent.ItemsSourceProperty, Arrays.asList(AntiCheat.values()));
		comboBox.bindProperty(ComboBoxComponent.SelectedItemProperty, Umbra.getInstance().moduleManager.antiCheat, BindingMode.TwoWay);
		stackPanel.addChild(comboBox);
		
		StringComponent detailText = new StringComponent("The selected AC will disable any features that are KNOWN detectable by that AC.");
		detailText.bindProperty(UIElement.ForegroundProperty, GuiManager.foregroundAccentColor);
		stackPanel.addChild(detailText);
		setContent(stackPanel);
		
		setProperty(UIElement.MinWidthProperty, 300f);
	}
}
