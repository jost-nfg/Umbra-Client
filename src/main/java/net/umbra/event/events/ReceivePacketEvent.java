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
import net.umbra.event.listeners.ReceivePacketListener;
import net.minecraft.network.protocol.Packet;

public class ReceivePacketEvent extends AbstractEvent {

	private final Packet<?> packet;

	public Packet<?> GetPacket() {
		return packet;
	}

	public ReceivePacketEvent(Packet<?> packet) {
		this.packet = packet;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			ReceivePacketListener readPacketListener = (ReceivePacketListener) listener;
			readPacketListener.onReceivePacket(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<ReceivePacketListener> GetListenerClassType() {
		return ReceivePacketListener.class;
	}
}
