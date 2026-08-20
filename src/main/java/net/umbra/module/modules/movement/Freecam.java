/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.movement;

import net.umbra.Umbra;
import net.umbra.event.events.PlayerHealthEvent;
import net.umbra.event.events.Render3DEvent;
import net.umbra.event.events.TickEvent.Post;
import net.umbra.event.events.TickEvent.Pre;
import net.umbra.event.listeners.PlayerHealthListener;
import net.umbra.event.listeners.Render3DListener;
import net.umbra.event.listeners.TickListener;
import net.umbra.mixin.interfaces.ICamera;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.settings.types.BooleanSetting;
import net.umbra.settings.types.FloatSetting;
import net.minecraft.client.Camera;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class Freecam extends Module implements TickListener, Render3DListener, PlayerHealthListener {
	private final FloatSetting flySpeed = FloatSetting.builder().id("freecam_speed").displayName("Speed")
			.description("Speed of the Freecam.").defaultValue(2f).minValue(0.1f).maxValue(15f).step(0.5f).build();

	private final BooleanSetting disableOnDamage = BooleanSetting.builder().id("freecam_disable_on_damage")
			.displayName("Disable On Damage")
			.description("Automatically disables Freecam when the player takes damage.").defaultValue(true).build();

	public final BooleanSetting disableCulling = BooleanSetting.builder().id("freecam_disable_culling")
			.displayName("Disable Culling")
			.description("Disables culling of chunks, which always displays them.")
			.defaultValue(true).build();

	private float lastHealth;
	private ClientInput savedInput;
	private Vec3 prevPos;
	private Vec3 pos;

	public Freecam() {
		super("Freecam");
		setCategory(Category.of("Movement"));
		setDescription("Lets the camera fly free while the player stands still server-side.");
		addSetting(flySpeed);
		addSetting(disableOnDamage);
		addSetting(disableCulling);

		disableCulling.addOnUpdate(_ -> {
			if (state.getValue() && MC.levelRenderer != null) {
				MC.levelRenderer.invalidateCompiledGeometry(MC.level, MC.options, MC.gameRenderer.mainCamera(), MC.getBlockColors());
			}
		});
	}

	public void setSpeed(float speed) {
		flySpeed.setValue(speed);
	}

	public double getSpeed() {
		return flySpeed.getValue();
	}

	@Override
	public void onDisable() {
		LocalPlayer player = MC.player;
		if (player != null && savedInput != null) {
			player.input = savedInput;
		}

		savedInput = null;

		if (disableCulling.getValue() && MC.levelRenderer != null) {
			MC.levelRenderer.invalidateCompiledGeometry(MC.level, MC.options, MC.gameRenderer.mainCamera(), MC.getBlockColors());
		}

		Umbra.getInstance().eventManager.RemoveListener(TickListener.class, this);
		Umbra.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
		Umbra.getInstance().eventManager.RemoveListener(PlayerHealthListener.class, this);
	}

	@Override
	public void onEnable() {
		Umbra.getInstance().eventManager.AddListener(TickListener.class, this);
		Umbra.getInstance().eventManager.AddListener(Render3DListener.class, this);
		Umbra.getInstance().eventManager.AddListener(PlayerHealthListener.class, this);

		Camera camera = MC.gameRenderer.mainCamera();
		ICamera iCamera = (ICamera) camera;
		LocalPlayer player = MC.player;
		
		Vec3 startPos;
		if (player != null) {
			startPos = player.position().add(0, player.getEyeHeight(), 0);
		
			// Create a mock input to prevent inputs from getting to the client player.
			savedInput = player.input;
			player.input = new ClientInput();
			lastHealth = player.getHealth();
		}else {
			startPos = camera.position();
		}
			
		prevPos = startPos;
		pos = startPos;
		iCamera.setCameraPos(pos);

		if (disableCulling.getValue() && MC.levelRenderer != null) {
			MC.levelRenderer.invalidateCompiledGeometry(MC.level, MC.options, MC.gameRenderer.mainCamera(), MC.getBlockColors());
		}
	}

	@Override
	public void onToggle() {
	}

	@Override
	public void onHealthChanged(PlayerHealthEvent event) {
		float newHealth = event.getHealth();
		if (disableOnDamage.getValue() && newHealth < lastHealth) {
			state.setValue(false);
			return;
		}
		lastHealth = newHealth;
	}

	@Override
	public void onRender(Render3DEvent event) {
		if (MC.player == null)
			return;

		Camera camera = MC.gameRenderer.mainCamera();
		ICamera iCamera = (ICamera) camera;

		float tickDelta = event.getRenderer().getDeltaTracker().getGameTimeDeltaPartialTick(true);

		Vec3 interpolatedPos = new Vec3(Mth.lerp(tickDelta, prevPos.x, pos.x),
				Mth.lerp(tickDelta, prevPos.y, pos.y), Mth.lerp(tickDelta, prevPos.z, pos.z));
		iCamera.setCameraPos(interpolatedPos);
	}

	@Override
	public void onTick(Pre event) {
		LocalPlayer player = MC.player;
		if (player == null)
			return;

		if (player.input instanceof KeyboardInput) {
			savedInput = player.input;
			player.input = new ClientInput();
		}
	}

	@Override
	public void onTick(Post event) {
		if (MC.player == null)
			return;

		Camera camera = MC.gameRenderer.mainCamera();
		Vec3 cameraPos = camera.position();
		prevPos = cameraPos;

		Vec3 forward = Vec3.directionFromRotation(0, camera.yRot());
		Vec3 right = Vec3.directionFromRotation(0, camera.yRot() + 90);

		Vec3 velocity = new Vec3(0, 0, 0);

		if (MC.options.keyUp.isDown()) {
			velocity = velocity.add(forward.scale(flySpeed.getValue()));
		} else if (MC.options.keyDown.isDown()) {
			velocity = velocity.subtract(forward.scale(flySpeed.getValue()));
		}

		if (MC.options.keyRight.isDown()) {
			velocity = velocity.add(right.scale(flySpeed.getValue()));
		} else if (MC.options.keyLeft.isDown()) {
			velocity = velocity.subtract(right.scale(flySpeed.getValue()));
		}

		if (MC.options.keyJump.isDown()) {
			velocity = velocity.add(0, flySpeed.getValue(), 0);
		} else if (MC.options.keyShift.isDown())
			velocity = velocity.add(0, -flySpeed.getValue(), 0);

		pos = cameraPos.add(velocity);
	}
}
