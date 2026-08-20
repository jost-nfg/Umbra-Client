/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.movement;

import net.umbra.Umbra;
import net.umbra.event.events.TickEvent.Post;
import net.umbra.event.events.TickEvent.Pre;
import net.umbra.event.listeners.TickListener;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.minecraft.client.player.LocalPlayer;

public class Sneak extends Module implements TickListener {
	public Sneak() {
		super("Sneak");
		setCategory(Category.of("Movement"));
		setDescription("Makes the player appear like they're sneaking.");
	}

	@Override
	public void onDisable() {
		LocalPlayer player = MC.player;
		if (player != null) {
			MC.options.keyShift.setDown(false);
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

	@Override
	public void onTick(Pre event) {
		LocalPlayer player = MC.player;
		if (player != null) {
			MC.options.keyShift.setDown(true);
		}
	}

	@Override
	public void onTick(Post event) {

	}
}