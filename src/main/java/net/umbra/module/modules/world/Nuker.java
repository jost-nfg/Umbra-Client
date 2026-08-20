/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.world;

import java.util.HashSet;

import net.umbra.Umbra;
import net.umbra.event.events.BlockStateEvent;
import net.umbra.event.events.Render3DEvent;
import net.umbra.event.events.TickEvent.Post;
import net.umbra.event.events.TickEvent.Pre;
import net.umbra.event.listeners.BlockStateListener;
import net.umbra.event.listeners.Render3DListener;
import net.umbra.event.listeners.TickListener;
import net.umbra.gui.colors.Color;
import net.umbra.module.AntiCheat;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.rendering.shaders.Shader;
import net.umbra.settings.types.BlocksSetting;
import net.umbra.settings.types.BooleanSetting;
import net.umbra.settings.types.ShaderSetting;
import net.umbra.settings.types.FloatSetting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class Nuker extends Module implements Render3DListener, TickListener, BlockStateListener {

	private final BooleanSetting creative = BooleanSetting.builder().id("nuker_creative").displayName("Creative")
			.description("Creative").defaultValue(false).build();

	private final ShaderSetting color = ShaderSetting.builder().id("nuker_color").displayName("Color")
			.description("Color").defaultValue(Shader.solid(new Color(0f, 1f, 1f, 1f))).build();

	private final FloatSetting radius = FloatSetting.builder().id("nuker_radius").displayName("Radius")
			.description("Radius").defaultValue(5f).minValue(0f).maxValue(15f).step(1f).build();

	private final BlocksSetting blacklist = BlocksSetting.builder().id("nuker_blacklist").displayName("Blacklist")
			.description("Blocks that will not be broken by Nuker.").defaultValue(new HashSet<Block>()).build();

	private BlockPos currentBlockToBreak = null;

	public Nuker() {
		super("Nuker");
		setCategory(Category.of("World"));
		setDescription("Destroys blocks around the player.");

		addSetting(creative);
		addSetting(radius);
		addSetting(color);
		addSetting(blacklist);

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
		Umbra.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
		Umbra.getInstance().eventManager.RemoveListener(TickListener.class, this);
		Umbra.getInstance().eventManager.RemoveListener(BlockStateListener.class, this);
	}

	@Override
	public void onEnable() {
		Umbra.getInstance().eventManager.AddListener(Render3DListener.class, this);
		Umbra.getInstance().eventManager.AddListener(TickListener.class, this);
		Umbra.getInstance().eventManager.AddListener(BlockStateListener.class, this);
	}

	@Override
	public void onToggle() {
	}

	@Override
	public void onRender(Render3DEvent event) {
		if (currentBlockToBreak != null) {
			event.getRenderer().drawBox(new AABB(currentBlockToBreak), color.getValue(),
					1.0f);
		}
	}

	@Override
	public void onBlockStateChanged(BlockStateEvent event) {
		if (currentBlockToBreak != null) {
			BlockPos blockPos = event.getBlockPos();
			BlockState oldBlockState = event.getPreviousBlockState();
			if (blockPos.equals(currentBlockToBreak) && (oldBlockState.isAir())) {
				currentBlockToBreak = null;
			}
		}
	}

	private BlockPos getNextBlock() {
		// Scan to find next block to begin breaking.
		int rad = radius.getValue().intValue();
		for (int y = rad; y > -rad; y--) {
			for (int x = -rad; x < rad; x++) {
				for (int z = -rad; z < rad; z++) {
					BlockPos blockpos = new BlockPos(MC.player.getBlockX() + x, MC.player.getBlockY() + y,
							MC.player.getBlockZ() + z);
					Block block = MC.level.getBlockState(blockpos).getBlock();
					if (block == Blocks.AIR || blacklist.getValue().contains(block))
						continue;

					return blockpos;
				}
			}
		}
		return null;
	}

	@Override
	public void onTick(Pre event) {
		if (creative.getValue()) {
			int range = (int) (Math.floor(radius.getValue()) + 1);
			Iterable<BlockPos> blocks = BlockPos
					.withinManhattan(BlockPos.containing(MC.player.position()).above(), range, range, range);
			for (BlockPos blockPos : blocks) {
				Block block = MC.level.getBlockState(blockPos).getBlock();
				if (block == Blocks.AIR || blacklist.getValue().contains(block))
					continue;

				MC.player.connection
						.send(new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, blockPos, Direction.NORTH));
				MC.player.connection
						.send(new ServerboundPlayerActionPacket(Action.STOP_DESTROY_BLOCK, blockPos, Direction.NORTH));
				MC.player.swing(InteractionHand.MAIN_HAND);
			}
		} else {
			if (currentBlockToBreak == null) {
				currentBlockToBreak = getNextBlock();
			}

			if (currentBlockToBreak != null) {

				// Check to ensure that the block is not further than we can reach.
				int range = (int) (Math.floor(radius.getValue()) + 1);
				int rangeSqr = range ^ 2;
				if (Vec3.atCenterOf(MC.player.blockPosition()).distanceTo(Vec3.atCenterOf(currentBlockToBreak)) > rangeSqr) {
					currentBlockToBreak = null;
				} else {
					MC.player.connection.send(new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK,
							currentBlockToBreak, Direction.NORTH));
					MC.player.connection.send(
							new ServerboundPlayerActionPacket(Action.STOP_DESTROY_BLOCK, currentBlockToBreak, Direction.NORTH));
					MC.player.swing(InteractionHand.MAIN_HAND);
				}
			}
		}
	}

	@Override
	public void onTick(Post event) {

	}
}
