/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.settings.types;

import java.util.function.Consumer;

import net.umbra.settings.Setting;

public class StringSetting extends Setting<String> {
	protected StringSetting(String ID, String displayName, String description, String default_value,
			Consumer<String> onUpdate) {
		super(ID, description, default_value, onUpdate);
		type = TYPE.STRING;
	}

	/**
	 * Checks whether or not a value is with this setting's valid range.
	 */
	@Override
	protected boolean isValueValid(String value) {
		return true;
	}

	public static StringSetting.BUILDER builder() {
		return new StringSetting.BUILDER();
	}

	public static class BUILDER extends Setting.BUILDER<StringSetting.BUILDER, StringSetting, String> {
		protected BUILDER() {
        }

		@Override
		public StringSetting build() {
			return new StringSetting(id, displayName, description, defaultValue, onUpdate);
		}
	}
}
