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
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Step extends Module {

	private final FloatSetting stepHeight = FloatSetting.builder().id("step_height").displayName("Height")
			.description("Height that the player will step up.").defaultValue(1f).minValue(0f).maxValue(2f).step(0.5f)
			.build();

	public Step() {
		super("Step");
		setCategory(Category.of("Movement"));
		setDescription("Steps up blocks.");

		stepHeight.addOnUpdate((i) -> {
			if (state.getValue()) {
				AttributeInstance attribute = MC.player.getAttribute(Attributes.STEP_HEIGHT);
				attribute.setBaseValue(stepHeight.getValue());
			}
		});

		addSetting(stepHeight);

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
		if (MC.player != null) {
			AttributeInstance attribute = MC.player.getAttribute(Attributes.STEP_HEIGHT);
			attribute.setBaseValue(0.5f);
		}
	}

	@Override
	public void onEnable() {
		AttributeInstance attribute = MC.player.getAttribute(Attributes.STEP_HEIGHT);
		attribute.setBaseValue(stepHeight.getValue());
	}

	@Override
	public void onToggle() {

	}

	public float getStepHeight() {
		return stepHeight.getValue();
	}

	public void setStepHeight(float height) {
		stepHeight.setValue(height);
	}
}
