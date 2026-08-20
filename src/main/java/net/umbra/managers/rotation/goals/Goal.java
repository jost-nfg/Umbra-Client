/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.managers.rotation.goals;

import java.util.Objects;

import net.umbra.managers.rotation.Rotation;
import net.umbra.managers.rotation.RotationMode;

/**
 * A class that represents a desire to rotate the players camera to a certain
 * rotation.
 */
public abstract class Goal<T> {

	protected RotationMode rotationMode = RotationMode.SMOOTH;
	protected T rotationGoal;
	protected float maxRotation = 10.0f;
	protected float yawRandomness = 0f;
	protected float pitchRandomness = 0f;
	protected boolean fakeRotation = false;
	protected boolean moveFix = false;
	protected EasingFunction easingFunction = EasingFunction.SineEaseInOut;

	/**
	 * Getter for rotationGoal
	 * 
	 * @return Rotation goal.
	 */
	public T getGoal() {
		return rotationGoal;
	}

	/**
	 * Getter for rotationMode
	 * 
	 * @return Rotation Mode of the goal.
	 */
	public RotationMode getRotationMode() {
		return rotationMode;
	}

	/**
	 * Getter for rotationGoal
	 * 
	 * @return Rotation goal.
	 */
	public abstract Rotation getGoalRotation(float tickDelta);

	/**
	 * Getter for maxRotation
	 * 
	 * @return Max Rotation of the goal.
	 */
	public float getMaxRotation() {
		return maxRotation;
	}

	/**
	 * Getter for yawRandomness
	 * 
	 * @return Yaw Randomness of the goal.
	 */
	public float getYawRandomness() {
		return yawRandomness;
	}

	/**
	 * Getter for pitchRandomness
	 * 
	 * @return Pitch Randomness of the goal.
	 */
	public float getPitchRandomness() {
		return pitchRandomness;
	}

	public boolean isFakeRotation() {
		return fakeRotation;
	}

	public boolean isMoveFix() {
		return moveFix;
	}

	public EasingFunction getEasingFunction() {
		return easingFunction;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		if(obj instanceof Goal<?> goal) {
			return Objects.equals(rotationGoal, goal.rotationGoal);
		}
		return false;
		
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(rotationGoal);
	}
}
