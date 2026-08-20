/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.event.listeners;

import net.umbra.event.events.TotemPopEvent;

public interface TotemPopListener extends AbstractListener {
    void onTotemPop(TotemPopEvent event);
}
