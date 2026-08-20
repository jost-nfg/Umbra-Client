/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui;

import java.util.function.Consumer;

import net.umbra.gui.types.BindingMode;
import net.umbra.settings.Setting;

@SuppressWarnings("rawtypes")
public class PropertyBinding<T> {
	private final UIProperty<T> property;

	private final Setting setting;
	private final Consumer<T> listener;
	private final BindingMode mode;

	public PropertyBinding(UIProperty<T> property, Setting setting, Consumer<T> listener, BindingMode mode) {
		this.property = property;
		this.setting = setting;
		this.listener = listener;
		this.mode = mode;
	}

	public UIProperty<T> getProperty() {
		return property;
	}

	public BindingMode getMode() {
		return mode;
	}

	@SuppressWarnings("unchecked")
	public void pushToSetting(Object value) {
		setting.setValue((T) value);
	}

	@SuppressWarnings("unchecked")
	public void unbind() {
		setting.removeOnUpdate(listener);
	}
}
