/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.movement;

import net.umbra.Umbra;
import net.umbra.event.events.MouseClickEvent;
import net.umbra.event.listeners.MouseClickListener;
import net.umbra.module.AntiCheat;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.settings.types.FloatSetting;
import net.umbra.utils.types.MouseAction;
import net.umbra.utils.types.MouseButton;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ClickTP extends Module implements MouseClickListener {

	private final FloatSetting distance = FloatSetting.builder().id("clicktp_distance").displayName("Max Distance")
			.description("Max Distance to teleport.").defaultValue(10f).minValue(1.0f).maxValue(200f).step(1.0f)
			.build();

	public ClickTP() {
		super("ClickTP");
		setCategory(Category.of("Movement"));
		setDescription("Allows the user to teleport where they are looking.");

		addSetting(distance);

		setDetectable(
				AntiCheat.NoCheatPlus,
				AntiCheat.Vulcan,
				AntiCheat.AdvancedAntiCheat,
				AntiCheat.Verus,
				AntiCheat.Grim,
				AntiCheat.Matrix,
				AntiCheat.Karhu
		);
	}

	@Override
	public void onDisable() {
		Umbra.getInstance().eventManager.RemoveListener(MouseClickListener.class, this);
	}

	@Override
	public void onEnable() {
		Umbra.getInstance().eventManager.AddListener(MouseClickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onMouseClick(MouseClickEvent event) {

		if (event.button == MouseButton.RIGHT && event.action == MouseAction.DOWN) {
			Camera camera = MC.gameRenderer.mainCamera();

			if (camera != null) {
				Vec3 direction = Vec3.directionFromRotation(camera.xRot(), camera.yRot()).scale(210);
				Vec3 targetPos = camera.position().add(direction);

				ClipContext context = new ClipContext(camera.position(), targetPos,
						ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, MC.player);

				HitResult raycast = MC.level.clip(context);

				if (raycast.getType() == HitResult.Type.BLOCK) {
					BlockHitResult raycastBlock = (BlockHitResult) raycast;
					BlockPos pos = raycastBlock.getBlockPos();
					Direction side = raycastBlock.getDirection();

					Vec3 newPos = new Vec3(pos.getX() + 0.5 + side.getStepX(), pos.getY() + 1,
							pos.getZ() + 0.5 + side.getStepZ());
					int packetsRequired = (int) Math.ceil(MC.player.position().distanceTo(newPos) / 10) - 1;

					for (int i = 0; i < packetsRequired; i++) {
						MC.player.connection.send(new ServerboundMovePlayerPacket.StatusOnly(true, false));
					}

					MC.player.connection.send(
							new ServerboundMovePlayerPacket.Pos(newPos.x, newPos.y, newPos.z, true, false));
					MC.player.setPos(newPos);
				}
			}
		}
	}
}
