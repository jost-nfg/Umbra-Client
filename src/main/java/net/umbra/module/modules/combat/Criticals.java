/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.combat;

import net.umbra.Umbra;
import net.umbra.event.events.SendPacketEvent;
import net.umbra.event.listeners.SendPacketListener;
import net.umbra.module.AntiCheat;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.settings.types.BooleanSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public class Criticals extends Module implements SendPacketListener {

	private final BooleanSetting legit = BooleanSetting.builder().id("criticals_legit").displayName("Legit")
			.description("Whether or not we will use the 'legit' mode.").defaultValue(false).build();

	public Criticals() {
		super("Criticals");

		setCategory(Category.of("Combat"));
		setDescription("Makes all attacks into critical strikes.");

		addSetting(legit);

		setDetectable(
				AntiCheat.NoCheatPlus,
				AntiCheat.Vulcan,
				AntiCheat.AdvancedAntiCheat,
				AntiCheat.Verus,
				AntiCheat.Grim,
				AntiCheat.Matrix,
				AntiCheat.Karhu
		);
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
		if (packet instanceof ServerboundAttackPacket) {
			Minecraft mc = Minecraft.getInstance();
			LocalPlayer player = mc.player;
			if (player.onGround() && !player.isInLava() && !player.isUnderWater()) {
				if (legit.getValue()) {
					player.jumpFromGround();
				} else {
					ClientPacketListener networkHandler = mc.getConnection();
					networkHandler.send(new ServerboundMovePlayerPacket.Pos(mc.player.getX(),
							mc.player.getY() + 0.03125D, mc.player.getZ(), false, false));
					networkHandler.send(new ServerboundMovePlayerPacket.Pos(mc.player.getX(),
							mc.player.getY() + 0.0625D, mc.player.getZ(), false, false));
					networkHandler.send(new ServerboundMovePlayerPacket.Pos(mc.player.getX(),
							mc.player.getY(), mc.player.getZ(), false, false));
				}
			}
		}
	}
}
