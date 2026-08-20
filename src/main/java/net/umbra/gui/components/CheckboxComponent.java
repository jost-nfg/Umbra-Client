/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.components;

import java.util.function.Consumer;
import net.umbra.gui.GuiManager;
import net.umbra.gui.UIElement;
import net.umbra.gui.UIProperty;
import net.umbra.gui.colors.Color;
import net.umbra.gui.types.GridDefinition;
import net.umbra.gui.types.VerticalAlignment;
import net.umbra.gui.types.GridDefinition.RelativeUnit;
import net.umbra.rendering.shaders.Shader;
import net.umbra.utils.input.CursorStyle;
import net.umbra.utils.types.MouseAction;
import net.umbra.utils.types.MouseButton;

public class CheckboxComponent extends Component {
	private static final Shader COLOR_ON = Shader.gradient(new Color(2, 212, 2), new Color(0, 154, 0, 200), 90);
	private static final Shader COLOR_OFF = Shader.gradient(new Color(189, 0, 0),new Color(154, 0, 0, 200), 90);		

	private Consumer<Boolean> onChanged;
	private final StringComponent headerComponent;
	private final RectangleComponent checkRectangle;

	public static final UIProperty<String> HeaderProperty  = new UIProperty<>("Header", "", false, true, CheckboxComponent::onHeaderChanged);
	public static final UIProperty<Boolean> IsCheckedProperty = new UIProperty<>("IsChecked", false, false, false, CheckboxComponent::onIsCheckedChanged);
	
	private static void onIsCheckedChanged(UIElement sender, Boolean oldValue, Boolean newValue) {
		if(sender instanceof CheckboxComponent checkbox) {
			checkbox.checkRectangle.setProperty(BackgroundProperty, newValue ? COLOR_ON : COLOR_OFF);
			
			if(checkbox.onChanged != null)
				checkbox.onChanged.accept(newValue);
		}
	}
	
	private static void onHeaderChanged(UIElement sender, String oldValue, String newValue) {
		if(sender instanceof CheckboxComponent checkbox) {
			checkbox.headerComponent.setProperty(StringComponent.TextProperty, newValue);
		}
	}
	
	public CheckboxComponent() {
		GridComponent grid = new GridComponent();
		grid.addColumnDefinition(new GridDefinition(RelativeUnit.Auto));
		grid.addColumnDefinition(new GridDefinition(1f, RelativeUnit.Relative));
		grid.setProperty(GridComponent.HorizontalSpacingProperty, 8f);
		
		checkRectangle = new RectangleComponent();
		checkRectangle.setProperty(BackgroundProperty,COLOR_OFF);
		checkRectangle.bindProperty(BorderProperty, GuiManager.componentBorderColor);
		checkRectangle.setProperty(CornerRadiusProperty, 3f);
		checkRectangle.setProperty(UIElement.CursorProperty, CursorStyle.Click);
		checkRectangle.setProperty(UIElement.WidthProperty, 20f);
		checkRectangle.setProperty(UIElement.HeightProperty, 20f);
		checkRectangle.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		grid.addChild(checkRectangle);
		
		headerComponent = new StringComponent("");
		headerComponent.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		grid.addChild(headerComponent);

		setContent(grid);

		setOnClicked(e -> {
			if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
				toggle();
				e.cancel();
			}
		});
	}

	public void toggle() {
		Boolean isChecked = getProperty(IsCheckedProperty);
		setProperty(IsCheckedProperty, !Boolean.TRUE.equals(isChecked));
	}
	
	public void setOnChanged(Consumer<Boolean> onChanged) {
		this.onChanged = onChanged;
	}
}
