/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.render;

import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.settings.types.FloatSetting;

public class FocusFps extends Module {
	private final FloatSetting fps = FloatSetting.builder().id("focusfps_fps").displayName("FPS")
			.description("The FPS for when the window is not in focus.").defaultValue(30f).minValue(1f).maxValue(45f)
			.step(1f).build();

	public FocusFps() {
		super("FocusFPS");
		setCategory(Category.of("Render"));
		setDescription("Limits the FPS of the game when it is not focused.");
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

	public Float getFps() {
		return fps.getValue();
	}
}
