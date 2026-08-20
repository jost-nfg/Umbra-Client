/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.components;

import net.umbra.gui.UIProperty;

public class TabItemComponent extends Component {
	public static final UIProperty<String> HeaderProperty = new UIProperty<>(
			"Header", "", false, true);

	public TabItemComponent() {
	}

	public TabItemComponent(String header) {
		setProperty(HeaderProperty, header);
	}
}
