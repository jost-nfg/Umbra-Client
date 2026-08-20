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
import net.umbra.mixin.interfaces.ILivingEntity;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.settings.types.FloatSetting;

public class NoJumpDelay extends Module implements TickListener {

	private final FloatSetting delay = FloatSetting.builder().id("nojumpdelay_delay").displayName("Delay")
			.description("NoJumpDelay Delay.").defaultValue(1f).minValue(0f).maxValue(20f).step(1f).build();

	public NoJumpDelay() {
		super("NoJumpDelay");
		setCategory(Category.of("Movement"));
		setDescription("Makes it so the user can jump very quickly.");

		addSetting(delay);
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
		ILivingEntity ent = (ILivingEntity) MC.player;
		if (ent.getJumpCooldown() > delay.getValue()) {
			ent.setJumpCooldown(delay.getValue().intValue());
		}
	}

	@Override
	public void onTick(Post event) {

	}
}