/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.movement;

import net.umbra.module.AntiCheat;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.settings.types.FloatSetting;

public class HighJump extends Module {

	private final FloatSetting multiplier = FloatSetting.builder().id("highjump_jumpmultiplier")
			.displayName("Jump Multiplier").description("The height that the player will jump.").defaultValue(1.5f)
			.minValue(0.1f).maxValue(10f).step(0.1f).build();

	public HighJump() {
		super("HighJump");
		setCategory(Category.of("Movement"));
		setDescription("Allows the player to jump super high!");

		addSetting(multiplier);

		setDetectable(
		    AntiCheat.NoCheatPlus,
		    AntiCheat.Vulcan,
		    AntiCheat.AdvancedAntiCheat,
		    AntiCheat.Verus,
		    AntiCheat.Grim,
		    AntiCheat.Matrix,
		    AntiCheat.Negativity,
		    AntiCheat.Karhu,
		    AntiCheat.Buzz
		);
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

	public float getJumpHeightMultiplier() {
		return multiplier.getValue();
	}
}