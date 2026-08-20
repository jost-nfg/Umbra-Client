package net.umbra.module.modules.combat;

import net.umbra.Umbra;
import net.umbra.event.events.StartAttackEvent;
import net.umbra.event.listeners.StartAttackListener;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.minecraft.world.phys.HitResult;

public class NoMissDelay extends Module implements StartAttackListener {
	public NoMissDelay() {
		super("NoMissDelay");
		
		setCategory(Category.of("Combat"));
		setDescription("Prevents you from swinging while looking at air");
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
		if(MC.hitResult != null && MC.hitResult.getType() == HitResult.Type.MISS) {
			event.cancel();
		}
	}
}
