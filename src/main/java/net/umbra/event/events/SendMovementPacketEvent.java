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
import net.umbra.event.listeners.SendMovementPacketListener;

public class SendMovementPacketEvent {
	public static class Pre extends AbstractEvent {
		@Override
		public void Fire(ArrayList<? extends AbstractListener> listeners) {
			for (AbstractListener listener : List.copyOf(listeners)) {
				SendMovementPacketListener sendMovementPacketListener = (SendMovementPacketListener) listener;
				sendMovementPacketListener.onSendMovementPacket(this);
			}
		}

		@Override
		public Class<SendMovementPacketListener> GetListenerClassType() {
			return SendMovementPacketListener.class;
		}
	}

	public static class Post extends AbstractEvent {
		@Override
		public void Fire(ArrayList<? extends AbstractListener> listeners) {
			for (AbstractListener listener : listeners) {
				SendMovementPacketListener sendMovementPacketListener = (SendMovementPacketListener) listener;
				sendMovementPacketListener.onSendMovementPacket(this);
			}
		}

		@Override
		public Class<SendMovementPacketListener> GetListenerClassType() {
			return SendMovementPacketListener.class;
		}

	}
}
