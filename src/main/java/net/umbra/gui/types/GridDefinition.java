/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.types;

/**
 * A definition for a column or row in a Grid component. 
 * Defines the size and the unit.
 */
public class GridDefinition {
	public enum RelativeUnit {
		Absolute, Relative, Auto
	}

	public final float value;
	public final RelativeUnit unit;

	public GridDefinition(RelativeUnit unit) {
		this(0, unit);
	}
	
	public GridDefinition(float value) {
		this(value, RelativeUnit.Absolute);
	}

	public GridDefinition(float value, RelativeUnit unit) {
		this.value = value;
		this.unit = unit;
	}
}
