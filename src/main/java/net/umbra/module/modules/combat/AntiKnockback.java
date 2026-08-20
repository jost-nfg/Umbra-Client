/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.combat;

import java.util.Optional;

import net.umbra.Umbra;
import net.umbra.event.events.ReceivePacketEvent;
import net.umbra.event.listeners.ReceivePacketListener;
import net.umbra.mixin.interfaces.IClientboundSetEntityMotionPacket;
import net.umbra.mixin.interfaces.IClientboundExplodePacket;
import net.umbra.module.AntiCheat;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.settings.types.BooleanSetting;
import net.umbra.settings.types.FloatSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec3;

public class AntiKnockback extends Module implements ReceivePacketListener {

	private final FloatSetting horizontal = FloatSetting.builder().id("antiknockback_horizontal")
			.displayName("Horizontal").description("Horizontal Velocity").defaultValue(0f).minValue(0f).maxValue(1f)
			.step(0.01f).build();

	private final FloatSetting vertical = FloatSetting.builder().id("antiknockback_vertical").displayName("Vertical")
			.description("Vertical Velocity").defaultValue(0f).minValue(0f).maxValue(1f).step(0.01f).build();

	private final BooleanSetting noPushEntites = BooleanSetting.builder().id("antiknockback_no_push_entities")
			.displayName("No Push Entities").description("Prevents being pushed by entites.").defaultValue(true)
			.build();

	private final BooleanSetting noPushBlocks = BooleanSetting.builder().id("antiknockback_no_push_blocks")
			.displayName("No Push Blocks").description("Prevents being pushed by blocks.").defaultValue(true).build();

	private final BooleanSetting noPushLiquids = BooleanSetting.builder().id("antiknockback_no_push_liquids")
			.displayName("No Push Liquids").description("Prevents being pushed by liquids.").defaultValue(true).build();

	private final BooleanSetting noPushFishhook = BooleanSetting.builder().id("antiknockback_no_push_fishhook")
			.displayName("No Push Fishhook").description("Prevents being pulled by the fishhook.").defaultValue(true)
			.build();

	private final FloatSetting chance = FloatSetting.builder().id("antiknockback_chance").displayName("Chance")
			.description("Chance (0-1) that knockback reduction applies per hit.").defaultValue(1f).minValue(0f)
			.maxValue(1f).step(0.05f).build();

	public AntiKnockback() {
		super("AntiKnockback");

		setCategory(Category.of("Combat"));
		setDescription("Prevents knockback.");

		addSetting(horizontal);
		addSetting(vertical);
		addSetting(noPushEntites);
		addSetting(noPushBlocks);
		addSetting(noPushLiquids);
		addSetting(noPushFishhook);
		addSetting(chance);

		// Vulcan, AACV5, Grim Matrix, and Karhu detectable.
		setDetectable(AntiCheat.Vulcan,
				AntiCheat.AdvancedAntiCheat,
				AntiCheat.Grim,
				AntiCheat.Matrix,
				AntiCheat.Karhu);
	}

	public boolean getNoPushEntities() {
		return noPushEntites.getValue();
	}

	public boolean getNoPushBlocks() {
		return noPushBlocks.getValue();
	}

	public boolean getNoPushLiquids() {
		return noPushLiquids.getValue();
	}

	@Override
	public void onDisable() {
		Umbra.getInstance().eventManager.RemoveListener(ReceivePacketListener.class, this);
	}

	@Override
	public void onEnable() {
		Umbra.getInstance().eventManager.AddListener(ReceivePacketListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onReceivePacket(ReceivePacketEvent readPacketEvent) {
		Minecraft mc = Minecraft.getInstance();
		Packet<?> packet = readPacketEvent.GetPacket();

		// Roll once per packet so velocity and explosion handling stay consistent.
		boolean applyReduction = Math.random() <= chance.getValue();

		if (applyReduction && packet instanceof ClientboundSetEntityMotionPacket velocityUpdatePacket) {
			if (mc.player != null) {
				if (velocityUpdatePacket.id() == mc.player.getId()) {
					Vec3 movement = ((IClientboundSetEntityMotionPacket) packet).getMovement();
					((IClientboundSetEntityMotionPacket) packet).setMovement(new Vec3(
							movement.x * horizontal.getValue(),
							movement.y * vertical.getValue(),
							movement.z * horizontal.getValue()));
				}
			}
		}

		// Cancel any explosions.
		if (applyReduction && packet instanceof ClientboundExplodePacket explosionS2CPacket) {
			Optional<Vec3> knockbackOptional = explosionS2CPacket.playerKnockback();
			if (!knockbackOptional.isEmpty()) {
				Vec3 knockback = knockbackOptional.get();
				((IClientboundExplodePacket) packet)
						.setPlayerKnockback(Optional.of(knockback.scale(horizontal.getValue())));
			}
		}

		// Cancel being launched with a fishing rod.
		if (packet instanceof ClientboundEntityEventPacket entityStatusS2CPacket) {
			if (entityStatusS2CPacket.getEventId() == EntityEvent.FISHING_ROD_REEL_IN && noPushFishhook.getValue()) {
				Entity entity = entityStatusS2CPacket.getEntity(mc.level);

				if (entity instanceof FishingHook fishingBobberEntity
						&& fishingBobberEntity.getHookedIn() == mc.player) {
					readPacketEvent.cancel();
				}
			}
		}
	}
}