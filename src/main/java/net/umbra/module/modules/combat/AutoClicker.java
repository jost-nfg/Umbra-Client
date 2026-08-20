/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.combat;

import java.util.concurrent.ThreadLocalRandom;

import net.umbra.Umbra;
import net.umbra.event.events.SubtickEvent;
import net.umbra.event.listeners.SubtickListener;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.settings.types.BooleanSetting;
import net.umbra.settings.types.FloatSetting;
import net.minecraft.world.phys.HitResult;

public class AutoClicker extends Module implements SubtickListener {
	private final FloatSetting minCps = FloatSetting.builder().id("autoclicker_min_cps").displayName("Min CPS")
			.description("Minimum clicks per second.").defaultValue(9f).minValue(1f).maxValue(25f).step(1f).build();

	private final FloatSetting maxCps = FloatSetting.builder().id("autoclicker_max_cps").displayName("Max CPS")
			.description("Maximum clicks per second.").defaultValue(13f).minValue(1f).maxValue(25f).step(1f).build();

	private final BooleanSetting onlyWhileHolding = BooleanSetting.builder().id("autoclicker_only_while_holding")
			.displayName("Only While Holding").description("Only clicks while the attack key is held.")
			.defaultValue(true).build();

	private final BooleanSetting onlyWhenTargeted = BooleanSetting.builder().id("autoclicker_only_when_targeted")
			.displayName("Only When Targeted")
			.description("Only clicks when the crosshair is on an entity or air, so it never looks like mining.")
			.defaultValue(false).build();

	private final FloatSetting jitterMs = FloatSetting.builder().id("autoclicker_jitter").displayName("Jitter")
			.description("Extra random delay in milliseconds added between clicks.").defaultValue(25f).minValue(0f)
			.maxValue(100f).step(1f).build();

	private float timeSinceClickMs;
	private float nextClickDelayMs;

	public AutoClicker() {
		super("AutoClicker");

		setCategory(Category.of("Combat"));
		setDescription("Automatically left clicks with humanized timing.");

		addSetting(minCps);
		addSetting(maxCps);
		addSetting(onlyWhileHolding);
		addSetting(onlyWhenTargeted);
		addSetting(jitterMs);
	}

	@Override
	public void onDisable() {
		Umbra.getInstance().eventManager.RemoveListener(SubtickListener.class, this);
	}

	@Override
	public void onEnable() {
		Umbra.getInstance().eventManager.AddListener(SubtickListener.class, this);
		timeSinceClickMs = 0f;
		nextClickDelayMs = sampleNextClickDelay();
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onSubtick(SubtickEvent event) {
		timeSinceClickMs += event.getDelta();

		if (MC.player == null || MC.level == null)
			return;
		if (onlyWhileHolding.getValue() && !MC.options.keyAttack.isDown())
			return;
		if (onlyWhenTargeted.getValue() && MC.hitResult != null && MC.hitResult.getType() == HitResult.Type.BLOCK)
			return;
		if (timeSinceClickMs < nextClickDelayMs)
			return;

		timeSinceClickMs = 0f;
		nextClickDelayMs = sampleNextClickDelay();

		IMC.startAttack();
	}

	/**
	 * Samples the delay until the next click from the configured CPS range and
	 * jitter.
	 *
	 * @return Delay in milliseconds until the next click.
	 */
	private float sampleNextClickDelay() {
		float min = Math.min(minCps.getValue(), maxCps.getValue());
		float max = Math.max(minCps.getValue(), maxCps.getValue());
		float cps = min + ThreadLocalRandom.current().nextFloat() * (max - min);
		return 1000f / cps + ThreadLocalRandom.current().nextFloat() * jitterMs.getValue();
	}
}
