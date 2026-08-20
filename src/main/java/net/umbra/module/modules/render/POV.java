/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.render;

import net.umbra.Umbra;
import net.umbra.event.events.TickEvent.Post;
import net.umbra.event.events.TickEvent.Pre;
import net.umbra.event.listeners.TickListener;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.utils.entity.FakePlayerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class POV extends Module implements TickListener {
	private FakePlayerEntity fakePlayer;
	private String povString = null;
	private Entity povEntity = null;

	private final boolean fakePlayerSpawned = false;

	public POV() {
		super("POV");
		setCategory(Category.of("Render"));
		setDescription("Allows the player to see someone else's point-of-view.");
	}

	@Override
	public void onDisable() {
		Minecraft.getInstance().setCameraEntity(MC.player);
		if (fakePlayer != null) {
			fakePlayer.despawn();
			MC.level.removeEntity(-3, null);
		}
		Umbra.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}

	@Override
	public void onEnable() {
		Umbra.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	public void setEntityPOV(String entity) {
		povString = entity;
	}

	public Entity getEntity() {
		return povEntity;
	}

	public Player getEntityAsPlayer() {
		if (povEntity instanceof Player) {
			return (Player) povEntity;
		} else {
			return null;
		}
	}

	@Override
	public void onTick(Pre event) {
		LocalPlayer player = MC.player;
		povEntity = null;
		for (Entity entity : MC.level.players()) {
			if (entity.getName().getString().equals(povString)) {
				povEntity = entity;
			}
		}
		if (Minecraft.getInstance().getCameraEntity() == povEntity) {
			if (!fakePlayerSpawned) {
				fakePlayer = new FakePlayerEntity();
				fakePlayer.restoreFrom(player);
				fakePlayer.yHeadRot = player.yHeadRot;
				MC.level.addEntity(fakePlayer);
			}
			fakePlayer.restoreFrom(player);
			fakePlayer.yHeadRot = player.yHeadRot;
		} else {
			if (fakePlayer != null) {
				fakePlayer.despawn();
				MC.level.removeEntity(-3, null);
			}

			if (povEntity == null) {
				Minecraft.getInstance().setCameraEntity(MC.player);
			} else {
				Minecraft.getInstance().setCameraEntity(povEntity);
			}
		}
	}

	@Override
	public void onTick(Post event) {

	}
}