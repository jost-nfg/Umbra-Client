/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.misc;

import net.umbra.Umbra;
import net.umbra.event.events.MouseClickEvent;
import net.umbra.event.listeners.MouseClickListener;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.settings.types.EnumSetting;
import net.umbra.utils.FindItemResult;
import net.umbra.utils.types.MouseAction;
import net.umbra.utils.types.MouseButton;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class MCA extends Module implements MouseClickListener {
	public enum Mode {
		FRIEND, PEARL, FIREWORK
	}

	private final EnumSetting<Mode> mode = EnumSetting.<Mode>builder().id("mca_mode").displayName("Mode")
			.description("The mode for the action to run when the middle mouse button is clicked.")
			.defaultValue(Mode.FRIEND).build();

	private int previousSlot = -1;

	public MCA() {
		super("MCA");

		setCategory(Category.of("misc"));
		setDescription("Middle Click Action");

		addSetting(mode);
	}

	@Override
	public void onDisable() {
		Umbra.getInstance().eventManager.RemoveListener(MouseClickListener.class, this);
	}

	@Override
	public void onEnable() {
		Umbra.getInstance().eventManager.AddListener(MouseClickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onMouseClick(MouseClickEvent mouseClickEvent) {
		if (mouseClickEvent.button != MouseButton.MIDDLE) return;

		if (mouseClickEvent.action == MouseAction.DOWN) {
			switch (mode.getValue()) {
				case FRIEND -> handleFriend(mouseClickEvent);
				case PEARL -> handleItem(mouseClickEvent, Items.ENDER_PEARL);
				case FIREWORK -> handleItem(mouseClickEvent, Items.FIREWORK_ROCKET);
			}
		} else if (mouseClickEvent.action == MouseAction.UP) {
			if ((mode.getValue() == Mode.PEARL || mode.getValue() == Mode.FIREWORK) && previousSlot != -1) {
				swap(previousSlot, false);
				previousSlot = -1;
				mouseClickEvent.cancel();
			}
		}
	}

	private void handleFriend(MouseClickEvent event) {
		if (!(MC.crosshairPickEntity instanceof Player player)) return;

		String playerName = player.getName().getString();

		if (Umbra.getInstance().friendsList.contains(player)) {
			Umbra.getInstance().friendsList.removeFriend(player.getUUID());
			sendChatMessage("Removed " + playerName + " from friends list.");
		} else {
			Umbra.getInstance().friendsList.addFriend(player);
			sendChatMessage("Added " + playerName + " to friends list.");
		}

		event.cancel();
	}

	private void handleItem(MouseClickEvent event, Item item) {
		FindItemResult itemResult = find(item);
		if (!itemResult.found() || !itemResult.isHotbar()) return;

		previousSlot = MC.player.getInventory().getSelectedSlot();

		if (!itemResult.isMainHand()) {
			swap(itemResult.slot(), false);
			MC.gameMode.useItem(MC.player, InteractionHand.MAIN_HAND);
			event.cancel();
		}
	}
}
