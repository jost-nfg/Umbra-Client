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

public class Fullbright extends Module {

	// private double previousValue = 0.0;
	public Fullbright() {
		super("Fullbright");
		setCategory(Category.of("Render"));
		setDescription("Maxes out the brightness.");
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
