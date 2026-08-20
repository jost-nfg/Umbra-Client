/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.event.listeners;

import net.umbra.event.events.SendMovementPacketEvent;

public interface SendMovementPacketListener extends AbstractListener {
    void onSendMovementPacket(SendMovementPacketEvent.Pre event);
    void onSendMovementPacket(SendMovementPacketEvent.Post event);
}
