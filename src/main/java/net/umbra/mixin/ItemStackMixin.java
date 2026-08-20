package net.umbra.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.umbra.Umbra;
import net.umbra.UmbraClient;
import net.umbra.event.events.ItemUsedEvent;
import net.umbra.event.events.ItemUsedEvent.Post;
import net.umbra.event.events.ItemUsedEvent.Pre;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Inject(method = "finishUsingItem", at = @At("HEAD"))
	private void onFinishUsingPre(Level world, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
		if (entity == UmbraClient.MC.player) {
			Pre event = new ItemUsedEvent.Pre((ItemStack) (Object) this);
			Umbra.getInstance().eventManager.Fire(event);
		}
	}

	@Inject(method = "finishUsingItem", at = @At("TAIL"))
	private void onFinishUsingPost(Level world, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
		if (entity == UmbraClient.MC.player) {
			Post event = new ItemUsedEvent.Post((ItemStack) (Object) this);
			Umbra.getInstance().eventManager.Fire(event);
		}
	}
}
