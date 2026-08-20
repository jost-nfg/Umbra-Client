/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.utils.discord.callbacks;

import com.sun.jna.Callback;

public interface ErroredCallback extends Callback {
    void apply(int p0, String p1);
}
