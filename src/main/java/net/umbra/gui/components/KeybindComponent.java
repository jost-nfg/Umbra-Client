/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.components;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import net.umbra.event.events.KeyDownEvent;
import net.umbra.event.listeners.KeyDownListener;
import net.umbra.gui.GuiManager;
import net.umbra.gui.UIElement;
import net.umbra.gui.UIProperty;
import net.umbra.gui.types.GridDefinition;
import net.umbra.gui.types.HorizontalAlignment;
import net.umbra.gui.types.TextWrapping;
import net.umbra.gui.types.Thickness;
import net.umbra.gui.types.VerticalAlignment;
import net.umbra.gui.types.GridDefinition.RelativeUnit;
import net.umbra.utils.input.CursorStyle;
import net.umbra.utils.types.MouseAction;
import net.umbra.utils.types.MouseButton;

public class KeybindComponent extends Component implements KeyDownListener {
	private boolean listeningForKey;
	private final StringComponent label;
	private final StringComponent keyTextComponent;

	public static UIProperty<Key> SelectedKeyProperty = new UIProperty<>("SelectedKey", InputConstants.UNKNOWN, false, true, KeybindComponent::OnSelectedKeyPropertyChanged);
	public static UIProperty<String> HeaderProperty = new UIProperty<>("Header", "", false, true, KeybindComponent::OnHeaderPropertyChanged);
	
	private Consumer<Key> onChanged = null;
	private static void OnSelectedKeyPropertyChanged(UIElement sender, Key oldValue, Key newValue) {
		if(sender instanceof KeybindComponent keybind) {
			keybind.keyTextComponent.setProperty(StringComponent.TextProperty, keybind.getKeyDisplayText());
			
			if(keybind.onChanged != null)
				keybind.onChanged.accept(newValue);
		}
	}
	
	private static void OnHeaderPropertyChanged(UIElement sender, String oldValue, String newValue) {
		if(sender instanceof KeybindComponent keybind) {
			keybind.label.setProperty(StringComponent.TextProperty, newValue);
		}
	}
	
	public KeybindComponent() {
		setProperty(UIElement.CursorProperty, CursorStyle.Type);
		
		GridComponent grid = new GridComponent();
		grid.addColumnDefinition(new GridDefinition(1f, RelativeUnit.Relative));
		grid.addColumnDefinition(new GridDefinition(RelativeUnit.Auto));

		label = new StringComponent();
		label.setProperty(StringComponent.TextProperty, getProperty(KeybindComponent.HeaderProperty));
		label.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		grid.addChild(label);

		RectangleComponent keyButton = new RectangleComponent();
		keyButton.bindProperty(UIElement.BackgroundProperty, GuiManager.componentBackgroundColor);
		keyButton.bindProperty(UIElement.BorderProperty, GuiManager.componentBorderColor);
		keyButton.bindProperty(UIElement.CornerRadiusProperty, GuiManager.roundingRadius);
		keyButton.setProperty(UIElement.PaddingProperty, new Thickness(10f, 4f, 10f, 4f));
		keyButton.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);

		keyTextComponent = new StringComponent("N/A");
		keyTextComponent.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		keyTextComponent.setProperty(UIElement.HorizontalAlignmentProperty, HorizontalAlignment.Center);
		keyTextComponent.setProperty(UIElement.IsHitTestVisibleProperty, false);
		keyTextComponent.setProperty(StringComponent.TextWrappingProperty, TextWrapping.NoWrap);
		keyButton.setContent(keyTextComponent);

		keyButton.setOnClicked(e -> {
			if (e.button == MouseButton.LEFT && e.action == MouseAction.DOWN) {
				setListeningForKey(true);
				e.cancel();
			}
		});

		grid.addChild(keyButton);
		setContent(grid);
	}
	
	private String getKeyDisplayText() {
		Key keybind = getProperty(SelectedKeyProperty);
		String text = keybind.getDisplayName().getString();
		if (text.equals("scancode.0") || text.equals("key.keyboard.0"))
			return "N/A";
		return text;
	}


	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (listeningForKey) {
			Key key = event.GetKey() == GLFW.GLFW_KEY_ESCAPE
					? InputConstants.UNKNOWN
					: InputConstants.Type.KEYSYM.getOrCreate(event.GetKey());

			setProperty(SelectedKeyProperty, key);
			setListeningForKey(false);
			event.cancel();
		}
	}

	private void setListeningForKey(boolean state) {
		if (listeningForKey == state)
			return;

		listeningForKey = state;
		if (listeningForKey) {
			GuiManager.requestFocus(this);
			UMBRA.eventManager.AddListener(KeyDownListener.class, this);
		} else {
			GuiManager.clearFocus(this);
			UMBRA.eventManager.RemoveListener(KeyDownListener.class, this);
		}
	}

	@Override
	protected void onLostFocus() {
		if (listeningForKey) {
			setListeningForKey(false);
			keyTextComponent.setProperty(StringComponent.TextProperty, getKeyDisplayText());
		}
	}
	
	public void setOnChanged(Consumer<Key> consumer) {
		this.onChanged = consumer;
	}
}
