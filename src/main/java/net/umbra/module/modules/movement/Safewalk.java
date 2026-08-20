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
import net.minecraft.world.phys.Vec3;

public class Safewalk extends Module implements TickListener {

	public Safewalk() {
		super("Safewalk");
		setCategory(Category.of("Movement"));
		setDescription("Permanently keeps player in sneaking mode.");
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
		double x = MC.player.getDeltaMovement().x;
		double y = MC.player.getDeltaMovement().y;
		double z = MC.player.getDeltaMovement().z;
		if (MC.player.onGround()) {
			double increment;
			for (increment = 0.05D; x != 0.0D;) {
				if (x < increment && x >= -increment) {
					x = 0.0D;
				} else if (x > 0.0D) {
					x -= increment;
				} else {
					x += increment;
				}
			}
            while (z != 0.0D)
            {
                if (z < increment && z >= -increment) {
                    z = 0.0D;
                } else if (z > 0.0D) {
                    z -= increment;
                } else {
                    z += increment;
                }
            }
            while (x != 0.0D && z != 0.0D)
            {
                if (x < increment && x >= -increment) {
                    x = 0.0D;
                } else if (x > 0.0D) {
                    x -= increment;
                } else {
                    x += increment;
                }
                if (z < increment && z >= -increment) {
                    z = 0.0D;
                } else if (z > 0.0D) {
                    z -= increment;
                } else {
                    z += increment;
                }
            }
        }
		MC.player.setDeltaMovement(new Vec3(x, y, z));
	}

	@Override
	public void onTick(Post event) {

	}
}
