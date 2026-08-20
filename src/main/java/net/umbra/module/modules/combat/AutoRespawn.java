/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.combat;

import net.umbra.Umbra;
import net.umbra.event.events.PlayerDeathEvent;
import net.umbra.event.events.TickEvent;
import net.umbra.event.listeners.PlayerDeathListener;
import net.umbra.event.listeners.TickListener;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.settings.types.FloatSetting;

public class AutoRespawn extends Module implements PlayerDeathListener, TickListener {

	private final FloatSetting respawnDelay = FloatSetting.builder().id("autorespawn_delay").displayName("Delay")
			.description("The delay between dying and automatically respawning.").defaultValue(0.0f).minValue(0.0f)
			.maxValue(100.0f).step(1.0f).build();

    private int tick;

	public AutoRespawn() {
		super("AutoRespawn");

		setCategory(Category.of("Combat"));
		setDescription("Automatically respawns when you die.");

		addSetting(respawnDelay);
	}

	@Override
	public void onDisable() {
		Umbra.getInstance().eventManager.RemoveListener(TickListener.class, this);
		Umbra.getInstance().eventManager.RemoveListener(PlayerDeathListener.class, this);
	}

	@Override
	public void onEnable() {
		Umbra.getInstance().eventManager.AddListener(PlayerDeathListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onPlayerDeath(PlayerDeathEvent readPacketEvent) {
		if (respawnDelay.getValue() == 0.0f) {
			respawn();
		} else {
			tick = 0;
			Umbra.getInstance().eventManager.AddListener(TickListener.class, this);
		}
		readPacketEvent.cancel();
	}

	@Override
	public void onTick(TickEvent.Pre event) {

	}

	@Override
	public void onTick(TickEvent.Post event) {
		if (tick < respawnDelay.getValue()) {
			tick++;
		} else {
			respawn();
		}
	}

	private void respawn() {
		MC.player.respawn();
		MC.gui.setScreen(null);
		Umbra.getInstance().eventManager.RemoveListener(TickListener.class, this);
	}
}
