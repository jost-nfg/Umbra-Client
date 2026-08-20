/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.settings.types;

import java.util.function.Consumer;

import net.umbra.gui.colors.Color;
import net.umbra.settings.Setting;

public class ColorSetting extends Setting<Color> {

    protected ColorSetting(String ID, String displayName, String description, Color default_value, Consumer<Color> onUpdate) {
        super(ID, displayName, description, default_value);
        type = TYPE.COLOR;
    }

    @Override
    protected boolean isValueValid(Color value) {
        return value != null;
    }

    public static ColorSetting.BUILDER builder() {
    	return new ColorSetting.BUILDER();
    }

    public static class BUILDER extends Setting.BUILDER<ColorSetting.BUILDER, ColorSetting, Color> {
		protected BUILDER() {
        }

		@Override
		public ColorSetting build() {
			return new ColorSetting(id, displayName, description, defaultValue, onUpdate);
		}
	}
}
