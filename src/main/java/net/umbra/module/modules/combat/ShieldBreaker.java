package net.umbra.module.modules.combat;

import net.umbra.Umbra;
import net.umbra.event.events.StartAttackEvent;
import net.umbra.event.listeners.StartAttackListener;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.utils.FindItemResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;

public class ShieldBreaker extends Module implements StartAttackListener {
	public ShieldBreaker() {
		super("ShieldBreaker");
		
		setCategory(Category.of("Combat"));
		setDescription("Renders an opponents shield useless. Requires an axe to be in your hotbar.");
	}

	@Override
	public void onDisable() {
		Umbra.getInstance().eventManager.RemoveListener(StartAttackListener.class, this);
	}

	@Override
	public void onEnable() {
		Umbra.getInstance().eventManager.AddListener(StartAttackListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onStartAttack(StartAttackEvent event) {

		Entity target = event.getTarget();
		if (target == null)
			return;
		
		// Ensure that target HAS a shield and is currently blocking.
		if(target instanceof LivingEntity livingTarget) {
			if(livingTarget.isBlocking()) {
				FindItemResult findItemResult = findInHotbar(s -> s.getItem() instanceof AxeItem);
				if (findItemResult.found() && swap(findItemResult.slot(), true)) {
					event.cancel();

					// Note: we do not want to use InteractionUtils.attack() here
					// Doing so will infinitely bubble the onStartAttack event.
					MC.gameMode.attack(MC.player, target);
					MC.player.swing(InteractionHand.MAIN_HAND);

					swapBack();
				}
			}
		}
	}
}