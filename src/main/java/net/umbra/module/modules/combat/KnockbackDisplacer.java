package net.umbra.module.modules.combat;

import java.util.Random;

import net.umbra.Umbra;
import net.umbra.event.events.StartAttackEvent;
import net.umbra.event.listeners.StartAttackListener;
import net.umbra.mixin.interfaces.ILocalPlayer;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.settings.types.FloatSetting;
import net.minecraft.client.Minecraft;

public class KnockbackDisplacer extends Module implements StartAttackListener {
	private static final Random rnd = new Random();
	
	private final FloatSetting angle = FloatSetting.builder().id("displacement_angle").displayName("Angle")
			.description("The angle by which the knockback is displaced").defaultValue(180.0f).minValue(-180.0f)
			.maxValue(180.0f).step(1.0f).build();

	private final FloatSetting random = FloatSetting.builder().id("displacement_angle_random").displayName("Angle randomization")
			.description("Displacement angle randomization").defaultValue(0.0f).minValue(0.0f)
			.maxValue(180.0f).step(1.0f).build();
	
	public KnockbackDisplacer() {
		super("KBDisplacer");
		setCategory(Category.of("Combat"));
		
		addSettings(angle, random);
	}

	@Override
	public void onStartAttack(StartAttackEvent event) {
		if(MC.player == null || MC.level == null) return;
		
		if(!MC.player.isSprinting()) return; // TODO: Player can still displace KB if they are holding a KB weapon
		
		float displacement = angle.getValue();
		float random = this.random.getValue();
		
		if(random != 0)
			displacement += rnd.nextFloat(-random, +random);
		
    	MC.player.setYRot(MC.player.getYRot() + displacement);
    	((ILocalPlayer)Minecraft.getInstance().player).invokeSendPosition();
    	MC.player.setYRot(MC.player.getYRot() - displacement);
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
}