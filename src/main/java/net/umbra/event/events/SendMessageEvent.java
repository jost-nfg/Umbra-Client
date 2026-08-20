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
import net.umbra.event.listeners.SendMessageListener;

public class SendMessageEvent extends AbstractEvent {
	private String message;

	public SendMessageEvent(String message) {
		this.message = message;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			SendMessageListener sendMessageListener = (SendMessageListener) listener;
			sendMessageListener.onSendMessage(this);
		}
	}

	@Override
	public Class<SendMessageListener> GetListenerClassType() {
		return SendMessageListener.class;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
