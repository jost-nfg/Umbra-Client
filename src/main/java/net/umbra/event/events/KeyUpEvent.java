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
import net.umbra.event.listeners.KeyUpListener;

public class KeyUpEvent extends AbstractEvent {
	private final long window;
	private final int key;
	private final int scancode;
	private final int action;
	private final int modifiers;

	public KeyUpEvent(long window, int key, int scancode, int action, int modifiers) {
        this.window = window;
		this.key = key;
		this.scancode = scancode;
		this.action = action;
		this.modifiers = modifiers;
	}

	public long GetWindow() {
		return window;
	}

	public int GetKey() {
		return key;
	}

	public int GetScanCode() {
		return scancode;
	}

	public int GetAction() {
		return action;
	}

	public int GetModifiers() {
		return modifiers;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			KeyUpListener keyDownListener = (KeyUpListener) listener;
			keyDownListener.onKeyUp(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<KeyUpListener> GetListenerClassType() {
		return KeyUpListener.class;
	}
}