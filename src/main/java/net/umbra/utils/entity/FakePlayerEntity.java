/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.utils.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;

public class FakePlayerEntity extends AbstractClientPlayer {

	public FakePlayerEntity() {
		super(Minecraft.getInstance().level, Minecraft.getInstance().player.getGameProfile());
		LocalPlayer player = Minecraft.getInstance().player;

		float tickDelta = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
		setPosRaw(player.position().x, player.position().y, player.position().z);
		setRot(player.getViewYRot(tickDelta), player.getViewXRot(tickDelta));
		// this.inventory = player.getInventory();
	}

	public void despawn() {
		remove(RemovalReason.DISCARDED);
	}
}
