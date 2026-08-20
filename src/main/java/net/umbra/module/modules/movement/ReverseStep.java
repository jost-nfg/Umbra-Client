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
import net.umbra.module.AntiCheat;
import net.umbra.module.Category;
import net.umbra.module.Module;

public class ReverseStep extends Module implements TickListener {
	public ReverseStep() {
		super("ReverseStep");
		setCategory(Category.of("Movement"));
		setDescription("Steps. But in reverse...");

		setDetectable(
		    AntiCheat.NoCheatPlus,
		    AntiCheat.Vulcan,
		    AntiCheat.AdvancedAntiCheat,
		    AntiCheat.Grim,
		    AntiCheat.Matrix,
		    AntiCheat.Karhu
		);
	}

	@Override
	public void onDisable() {
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
		if (MC.player.onGround()) {
			MC.player.setDeltaMovement(MC.player.getDeltaMovement().x, MC.player.getDeltaMovement().y - 1.0,
					MC.player.getDeltaMovement().z);
		}
	}

	@Override
	public void onTick(Post event) {

	}
}
