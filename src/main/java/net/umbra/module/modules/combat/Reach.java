/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.combat;

import net.umbra.module.AntiCheat;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.settings.types.FloatSetting;

public class Reach extends Module {

	private final FloatSetting distance = FloatSetting.builder().id("reach_distance").displayName("Distance")
			.description("Distance, in blocks, that you can reach.").defaultValue(3.1f).minValue(3f).maxValue(6f)
			.step(0.05f).build();

	private final FloatSetting chance = FloatSetting.builder().id("reach_chance").displayName("Chance")
			.description("Chance (0-1) that the extra reach applies.").defaultValue(1f).minValue(0f).maxValue(1f)
			.step(0.05f).build();

	private long lastChanceRollMs;
	private float lastEffectiveReach;

    public Reach() {
		super("Reach");

		setCategory(Category.of("Combat"));
		setDescription("Allows you to reach further.");

		addSetting(distance);
		addSetting(chance);

		setDetectable(
				AntiCheat.NoCheatPlus,
				AntiCheat.AdvancedAntiCheat,
				AntiCheat.Grim,
				AntiCheat.Buzz
		);
	}

	public float getReach() {
		return distance.getValue().floatValue();
	}

	/**
	 * Returns the reach that should apply right now, re-rolling the chance at most
	 * every 150 ms so the result stays consistent within a hit.
	 *
	 * @return The reach distance, or 0 when the chance roll fails.
	 */
	public float getEffectiveReach() {
		long now = System.currentTimeMillis();
		if (now - lastChanceRollMs >= 150L) {
			lastChanceRollMs = now;
			lastEffectiveReach = Math.random() <= chance.getValue() ? distance.getValue() : 0f;
		}
		return lastEffectiveReach;
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

	public void setReachLength(float reach) {
		distance.setValue(reach);
	}
}