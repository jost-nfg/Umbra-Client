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
import net.umbra.event.listeners.SendPacketListener;
import net.minecraft.network.protocol.Packet;

public class SendPacketEvent extends AbstractEvent {

	private final Packet<?> packet;

	public SendPacketEvent(Packet<?> packet) {
		this.packet = packet;
	}

	public Packet<?> GetPacket() {
		return packet;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			SendPacketListener sendPacketListener = (SendPacketListener) listener;
			sendPacketListener.onSendPacket(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<SendPacketListener> GetListenerClassType() {
		return SendPacketListener.class;
	}
}
