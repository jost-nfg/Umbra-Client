/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.utils.types;

public record Range(float min, float max) {
	public Range {
		if (max < min) {
			float tmp = min;
			min = max;
			max = tmp;
		}
	}

	public Range withMin(float newMin) {
		return new Range(Math.min(newMin, max), max);
	}

	public Range withMax(float newMax) {
		return new Range(min, Math.max(newMax, min));
	}

	public float span() {
		return max - min;
	}

	public float clamp(float value) {
		if (value < min) {
			return min;
		}
		else if (value > max) {
			return max;
		}
		return value;
	}

	public boolean contains(float value) {
		return value >= min && value <= max;
	}
}
