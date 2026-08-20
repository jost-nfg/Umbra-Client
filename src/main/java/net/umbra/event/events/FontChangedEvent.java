/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.event.events;

import java.util.ArrayList;

import net.umbra.event.listeners.AbstractListener;
import net.umbra.event.listeners.FontChangedListener;

public class FontChangedEvent extends AbstractEvent {
	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			FontChangedListener fontChangeListener = (FontChangedListener) listener;
			fontChangeListener.onFontChanged(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<FontChangedListener> GetListenerClassType() {
		return FontChangedListener.class;
	}
}