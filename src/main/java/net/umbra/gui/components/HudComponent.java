/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.components;

import net.umbra.Umbra;
import net.umbra.gui.UIElement;
import net.umbra.gui.colors.Color;
import net.umbra.gui.colors.Colors;
import net.umbra.gui.navigation.HudWindow;
import net.umbra.gui.types.GridDefinition;
import net.umbra.gui.types.VerticalAlignment;
import net.umbra.gui.types.GridDefinition.RelativeUnit;
import net.umbra.rendering.shaders.Shader;
import net.umbra.utils.types.MouseAction;
import net.umbra.utils.types.MouseButton;

public class HudComponent extends Component {
	private final HudWindow hud;
	private final StringComponent statusComponent;

	public HudComponent(String text, HudWindow hud) {
		this.hud = hud;
		GridComponent grid = new GridComponent();
		grid.addColumnDefinition(new GridDefinition(1f, RelativeUnit.Relative));
		grid.addColumnDefinition(new GridDefinition(RelativeUnit.Auto));

		StringComponent nameComponent = new StringComponent(text);
		nameComponent.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		grid.addChild(nameComponent);

		statusComponent = new StringComponent(hud.activated.getValue() ? "-" : "+");
		statusComponent.setProperty(ForegroundProperty, Shader.solid(hud.activated.getValue() ? new Color(255, 0, 0) : new Color(0, 255, 0)));
		statusComponent.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		grid.addChild(statusComponent);

		setContent(grid);

		setOnClicked(e -> {
			if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
				boolean visibility = hud.activated.getValue();
				Umbra.getInstance().guiManager.setHudActive(hud, !visibility);
				e.cancel();
			}
		});
	}

	@Override
	public void update() {
		super.update();

		if (hud.activated.getValue()) {
			statusComponent.setProperty(StringComponent.TextProperty, "-");
			statusComponent.setProperty(ForegroundProperty, Shader.solid(Colors.Red));
		} else {
			statusComponent.setProperty(StringComponent.TextProperty, "+");
			statusComponent.setProperty(ForegroundProperty, Shader.solid(Colors.Green));
		}
	}
}
