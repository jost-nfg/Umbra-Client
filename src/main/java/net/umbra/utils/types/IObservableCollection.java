/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.utils.types;

import java.util.function.Consumer;

public interface IObservableCollection<T> {
	void addListener(Consumer<IObservableCollection<T>> listener);
	void removeListener(Consumer<IObservableCollection<T>> listener);
}
