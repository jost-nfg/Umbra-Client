/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.misc;

import net.umbra.module.AntiCheat;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.settings.types.FloatSetting;

public class Timer extends Module {
	private final FloatSetting multiplier = FloatSetting.builder().id("timer_multiplier").displayName("Multiplier")
			.description("The multiplier that will affect the game speed.").defaultValue(1f).minValue(0.1f)
			.maxValue(15.0f).step(0.1f).build();

	public Timer() {
		super("Timer");

		setCategory(Category.of("Misc"));
		setDescription("Increases the speed of Minecraft.");
		addSetting(multiplier);

		setDetectable(
				AntiCheat.NoCheatPlus,
				AntiCheat.Vulcan,
				AntiCheat.AdvancedAntiCheat,
				AntiCheat.Verus,
				AntiCheat.Grim,
				AntiCheat.Matrix,
				AntiCheat.Negativity,
				AntiCheat.Karhu
		);
	}

	public float getMultiplier() {
		return multiplier.getValue().floatValue();
	}

	public void setMultipler(float multiplier) {
		this.multiplier.setValue(multiplier);
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onToggle() {
	}
}