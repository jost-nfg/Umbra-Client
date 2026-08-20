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
import net.umbra.settings.types.BooleanSetting;

public class Jesus extends Module implements TickListener {

	private final BooleanSetting legit = BooleanSetting.builder().id("jesus_legit").displayName("Legit")
			.description("Whether or not the player will swim as close to the surface as possible.").defaultValue(true)
			.build();

	public Jesus() {
		super("Jesus");
		setCategory(Category.of("Movement"));
		setDescription("Allows the player to walk on water.");
		addSetting(legit);

		setDetectable(
		    AntiCheat.NoCheatPlus,
		    AntiCheat.Vulcan,
		    AntiCheat.AdvancedAntiCheat,
		    AntiCheat.Verus,
		    AntiCheat.Grim,
		    AntiCheat.Matrix,
		    AntiCheat.Karhu,
		    AntiCheat.Buzz
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

	public boolean getLegit() {
		return legit.getValue();
	}

	@Override
	public void onTick(Pre event) {
		// If Legit is enabled, simply swim.
		if (legit.getValue()) {
			if (MC.player.isInLava() || MC.player.isInWater()) {
				MC.options.keyJump.setDown(true);
			}
		}
	}

	@Override
	public void onTick(Post event) {

	}
}
