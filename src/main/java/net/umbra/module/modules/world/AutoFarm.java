/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.world;

import net.umbra.Umbra;
import net.umbra.event.events.TickEvent.Post;
import net.umbra.event.events.TickEvent.Pre;
import net.umbra.event.listeners.TickListener;
import net.umbra.managers.InteractionManager;
import net.umbra.module.AntiCheat;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.settings.types.FloatSetting;
import net.umbra.utils.ModuleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AutoFarm extends Module implements TickListener {

	private final FloatSetting radius = FloatSetting.builder().id("autofarm_radius").displayName("Radius")
			.description("Radius").defaultValue(5f).minValue(0f).maxValue(15f).step(1f).build();

	public AutoFarm() {
		super("AutoFarm");
		setCategory(Category.of("World"));
		setDescription("Automatically plants, fertilizes, and harvests crops.");
		addSetting(radius);

		setDetectable(
		    AntiCheat.NoCheatPlus,
		    AntiCheat.Vulcan,
		    AntiCheat.AdvancedAntiCheat,
		    AntiCheat.Verus,
		    AntiCheat.Grim,
		    AntiCheat.Matrix,
		    AntiCheat.Negativity,
		    AntiCheat.Karhu
		);
	}

	public void setRadius(int radius) {
		this.radius.setValue((float) radius);
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
		int rad = radius.getValue().intValue();
		BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

		for (int x = -rad; x < rad; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -rad; z < rad; z++) {
					mutableBlockPos.set(x, y, z);
					Block block = MC.level.getBlockState(mutableBlockPos).getBlock();
					BlockState blockState = MC.level.getBlockState(mutableBlockPos);

					if (block instanceof CropBlock crop) {
						if (!crop.isBonemealSuccess(MC.level, null, mutableBlockPos, blockState)) {
							InteractionManager.destroyBlock(mutableBlockPos);
						} else {
							fertilizeCrops(mutableBlockPos);
						}
					} else if (block instanceof FarmlandBlock) {
						handleFarmland(mutableBlockPos);
					}
				}
			}
		}
	}

	private void fertilizeCrops(BlockPos.MutableBlockPos mutableBlockPos) {
		if (InteractionManager.selectItem(stack -> stack.getItem() == Items.BONE_MEAL)) {
			InteractionManager.useItemOnBlock(mutableBlockPos, InteractionHand.MAIN_HAND);
		}
	}

	private void handleFarmland(BlockPos.MutableBlockPos mutableBlockPos) {
		BlockPos blockAbovePos = mutableBlockPos.above();
		Block blockAbove = MC.level.getBlockState(blockAbovePos).getBlock();
		if (blockAbove == Blocks.AIR) {
			if (InteractionManager.selectItem(ModuleUtils::isPlantable)) {
				InteractionManager.useItemOnBlock(mutableBlockPos, InteractionHand.MAIN_HAND);
			}
		}
	}

	@Override
	public void onTick(Post event) {

	}
}