/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.navigation.huds;

import net.umbra.gui.UIElement;
import net.umbra.gui.components.GridComponent;
import net.umbra.gui.components.ItemPreviewComponent;
import net.umbra.gui.navigation.HudWindow;
import net.umbra.gui.types.GridDefinition;
import net.umbra.gui.types.GridDefinition.RelativeUnit;
import net.umbra.gui.types.SizeToContent;
import net.umbra.gui.types.Thickness;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ArmorHud extends HudWindow {

	private static final EquipmentSlot[] ARMOR_SLOTS = {
			EquipmentSlot.HEAD,
			EquipmentSlot.CHEST,
			EquipmentSlot.LEGS,
			EquipmentSlot.FEET
	};

	private final ItemPreviewComponent[] slots = new ItemPreviewComponent[ARMOR_SLOTS.length];

	public ArmorHud(int x, int y) {
		super("ArmorHud", x, y);
		setProperty(UIElement.MinWidthProperty, 16f);
		setProperty(UIElement.MinHeightProperty, 64f);
		setProperty(UIElement.PaddingProperty, new Thickness(0f));

		GridComponent grid = new GridComponent();
		for (int i = 0; i < ARMOR_SLOTS.length; i++) {
			grid.addRowDefinition(new GridDefinition(1f, RelativeUnit.Relative));
		}
		for (int i = 0; i < ARMOR_SLOTS.length; i++) {
			slots[i] = new ItemPreviewComponent();
			grid.addChild(slots[i]);
		}
		setContent(grid);
		setSizeToContent(SizeToContent.None);
	}

	@Override
	public void update() {
		super.update();
		if (MC.player == null)
			return;

		for (int i = 0; i < ARMOR_SLOTS.length; i++) {
			ItemStack stack = MC.player.getItemBySlot(ARMOR_SLOTS[i]);
			slots[i].setProperty(ItemPreviewComponent.ItemStackProperty, stack);
		}
	}
}
