/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.event.events;

import java.util.ArrayList;
import java.util.List;

import net.umbra.event.listeners.AbstractListener;
import net.umbra.event.listeners.SubtickListener;

public class SubtickEvent extends AbstractEvent {
	private final float delta;

	public SubtickEvent(float delta) {
		this.delta = delta;
	}

	public float getDelta() {
		return delta;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : List.copyOf(listeners)) {
			SubtickListener subtickListener = (SubtickListener) listener;
			subtickListener.onSubtick(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<SubtickListener> GetListenerClassType() {
		return SubtickListener.class;
	}
}
