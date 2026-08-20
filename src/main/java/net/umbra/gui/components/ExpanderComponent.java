/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.components;

import net.umbra.gui.GuiManager;
import net.umbra.gui.UIElement;
import net.umbra.gui.UIProperty;
import net.umbra.gui.colors.Colors;
import net.umbra.gui.types.GridDefinition;
import net.umbra.gui.types.Thickness;
import net.umbra.gui.types.VerticalAlignment;
import net.umbra.gui.types.GridDefinition.RelativeUnit;
import net.umbra.rendering.shaders.Shader;
import net.umbra.utils.input.CursorStyle;
import net.umbra.utils.types.MouseAction;
import net.umbra.utils.types.MouseButton;

public class ExpanderComponent extends Component {
	private final StackPanelComponent layout;
	private final StringComponent toggleLabel;

	public static UIProperty<Boolean> IsExpandedProperty = new UIProperty<>("IsExpanded", false, false, true, ExpanderComponent::onIsExpandedPropertyChanged);
	public static UIProperty<UIElement> ExpanderContentProperty = new UIProperty<>("ExpanderContent", null, false, true, ExpanderComponent::onExpanderContentPropertyChanged);
	
	private static void onIsExpandedPropertyChanged(UIElement sender, Boolean oldValue, Boolean newValue) {
		if(sender instanceof ExpanderComponent expander) {
			expander.toggleLabel.setProperty(StringComponent.TextProperty, newValue ? "<<" : ">>");
			
			UIElement expanderContent = expander.getProperty(ExpanderContentProperty);
			if (expanderContent != null) {
				expanderContent.setProperty(UIElement.IsVisibleProperty, newValue);
			}
		}
	}
	
	private static void onExpanderContentPropertyChanged(UIElement sender, UIElement oldValue, UIElement newValue) {
		if(sender instanceof ExpanderComponent expander) {
			if (oldValue != null) {
				expander.layout.removeChild(oldValue);
			}

			if (newValue != null) {
				boolean isExpanded = expander.getProperty(IsExpandedProperty);
				newValue.setProperty(UIElement.IsVisibleProperty, isExpanded);
				expander.layout.addChild(newValue);
			}
		}
	}
	
	public ExpanderComponent(String headerText) {
		
		setProperty(UIElement.BorderProperty, Shader.solid(Colors.Transparent));
		setProperty(UIElement.BorderThicknessProperty, 0f);
		
		RectangleComponent container = new RectangleComponent();
		container.bindProperty(UIElement.BackgroundProperty, GuiManager.panelBackgroundColor);
		container.setProperty(UIElement.CornerRadiusProperty, 6f);
		container.setProperty(UIElement.PaddingProperty, new Thickness(8f));
		
		layout = new StackPanelComponent();
		layout.setSpacing(4f);
		
		GridComponent headerGrid = new GridComponent();
		headerGrid.addColumnDefinition(new GridDefinition(1f, RelativeUnit.Relative));
		headerGrid.addColumnDefinition(new GridDefinition(RelativeUnit.Auto));
		headerGrid.setProperty(UIElement.CursorProperty, CursorStyle.Click);
		
		StringComponent headerLabel = new StringComponent(headerText);
		headerLabel.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		headerGrid.addChild(headerLabel);

		toggleLabel = new StringComponent(">>");
		toggleLabel.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		toggleLabel.bindProperty(UIElement.ForegroundProperty, GuiManager.foregroundColor);
		headerGrid.addChild(toggleLabel);

		headerGrid.setOnClicked(e -> {
			if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
				boolean isExpanded = getProperty(IsExpandedProperty);
				setProperty(IsExpandedProperty, !isExpanded);
				e.cancel();
			}
		});

		layout.addChild(headerGrid);
		container.setContent(layout);
		super.setContent(container);
	}

	@Override
	public void setContent(UIElement content) {
		setProperty(ExpanderContentProperty, content);
	}
}
