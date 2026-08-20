/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.misc;

import net.umbra.Umbra;
import net.umbra.event.events.SendMovementPacketEvent;
import net.umbra.event.events.SendPacketEvent;
import net.umbra.event.listeners.SendMovementPacketListener;
import net.umbra.event.listeners.SendPacketListener;
import net.umbra.mixin.interfaces.IServerboundMovePlayerPacket;
import net.umbra.module.AntiCheat;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.settings.types.BooleanSetting;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;

public class AntiHunger extends Module implements SendPacketListener, SendMovementPacketListener {
	private boolean lastOnGround, ignorePacket;

	private final BooleanSetting sprint = BooleanSetting.builder().id("antihunger_sprint").displayName("Sprint")
			.description("Change sprint packets.").defaultValue(true).build();

	private final BooleanSetting onGround = BooleanSetting.builder().id("antihunger_onground").displayName("On Ground")
			.description("Fakes onGround.").defaultValue(true).build();

	public AntiHunger() {
		super("AntiHunger");
		setCategory(Category.of("Misc"));
		setDescription("Reduces the amount of hunger that is consumed.");

		addSetting(sprint);
		addSetting(onGround);

		setDetectable(
				AntiCheat.Vulcan,
				AntiCheat.AdvancedAntiCheat,
				AntiCheat.Verus,
				AntiCheat.Grim,
				AntiCheat.Matrix,
				AntiCheat.Negativity,
				AntiCheat.Karhu
		);
	}

	@Override
	public void onDisable() {
		Umbra.getInstance().eventManager.RemoveListener(SendPacketListener.class, this);
		Umbra.getInstance().eventManager.RemoveListener(SendMovementPacketListener.class, this);
	}

	@Override
	public void onEnable() {
		Umbra.getInstance().eventManager.AddListener(SendPacketListener.class, this);
		Umbra.getInstance().eventManager.AddListener(SendMovementPacketListener.class, this);

		if(MC.player != null) {
			lastOnGround = MC.player.onGround();
		}else {
			lastOnGround = false;
		}
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onSendPacket(SendPacketEvent event) {
		if (ignorePacket && event.GetPacket() instanceof ServerboundMovePlayerPacket) {
			ignorePacket = false;
			return;
		}

		if (MC.player != null) {
			if (MC.player.isPassenger() || MC.player.isInWater() || MC.player.isUnderWater())
				return;

			if (event.GetPacket() instanceof ServerboundMovePlayerPacket packet && onGround.getValue() && MC.player.onGround()
					&& MC.player.fallDistance <= 0.0 && !MC.gameMode.isDestroying()) {
				((IServerboundMovePlayerPacket) packet).setOnGround(false);
			}
		}

		if (event.GetPacket() instanceof ServerboundPlayerCommandPacket packet && sprint.getValue()) {
			if (packet.getAction() == ServerboundPlayerCommandPacket.Action.START_SPRINTING)
				event.cancel();
		}
	}

	@Override
	public void onSendMovementPacket(SendMovementPacketEvent.Pre event) {
		if (MC.player.onGround() && !lastOnGround && onGround.getValue()) {
			ignorePacket = true;
		}

		lastOnGround = MC.player.onGround();
	}

	@Override
	public void onSendMovementPacket(SendMovementPacketEvent.Post event) {

	}
}
