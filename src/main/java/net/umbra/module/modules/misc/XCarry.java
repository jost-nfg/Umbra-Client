/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.misc;

import net.umbra.Umbra;
import net.umbra.event.events.SendPacketEvent;
import net.umbra.event.listeners.SendPacketListener;
import net.umbra.module.AntiCheat;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;

public class XCarry extends Module implements SendPacketListener {
	public XCarry() {
		super("XCarry");
		setCategory(Category.of("Misc"));
		setDescription("Allows you to store items in your crafting slot..");

		isDetectable(AntiCheat.Negativity);
	}

	@Override
	public void onDisable() {
		Umbra.getInstance().eventManager.RemoveListener(SendPacketListener.class, this);
	}

	@Override
	public void onEnable() {
		Umbra.getInstance().eventManager.AddListener(SendPacketListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onSendPacket(SendPacketEvent event) {
		Packet<?> packet = event.GetPacket();
		if (packet instanceof ServerboundContainerClosePacket closeScreenPacket) {
            if (closeScreenPacket.getContainerId() == MC.player.inventoryMenu.containerId)
				event.cancel();
		}
	}
}
