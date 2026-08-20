/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.navigation.windows;

import java.util.function.Function;
import java.util.function.Supplier;
import com.mojang.blaze3d.platform.InputConstants;
import net.umbra.Umbra;
import net.umbra.gui.GuiManager;
import net.umbra.gui.UIElement;
import net.umbra.gui.components.ButtonComponent;
import net.umbra.gui.components.CheckboxComponent;
import net.umbra.gui.components.EllipseComponent;
import net.umbra.gui.components.GridComponent;
import net.umbra.gui.components.ItemsComponent;
import net.umbra.gui.components.KeybindComponent;
import net.umbra.gui.components.PanelComponent;
import net.umbra.gui.components.PolygonComponent;
import net.umbra.gui.components.ScrollComponent;
import net.umbra.gui.components.SeparatorComponent;
import net.umbra.gui.components.StackPanelComponent;
import net.umbra.gui.components.StringComponent;
import net.umbra.gui.components.TextBoxComponent;
import net.umbra.gui.components.StackPanelComponent.StackType;
import net.umbra.gui.font.FontManager;
import net.umbra.gui.navigation.Window;
import net.umbra.gui.types.GridDefinition;
import net.umbra.gui.types.HorizontalAlignment;
import net.umbra.gui.types.SizeToContent;
import net.umbra.gui.types.TextAlign;
import net.umbra.gui.types.Thickness;
import net.umbra.gui.types.VerticalAlignment;
import net.umbra.gui.types.GridDefinition.RelativeUnit;
import net.umbra.managers.macros.Macro;
import net.umbra.rendering.shaders.Shader;
import net.umbra.rendering.utils.PolygonBank;
import net.umbra.gui.colors.Color;
public class MacroWindow extends Window {
	private ButtonComponent recordButton;
	private EllipseComponent recordIcon;
	private PolygonComponent recordStopIcon;
	private ButtonComponent playPausePlaybackButton;
	private PolygonComponent playPauseIcon;
	private ButtonComponent stopPlaybackButton;
	private TextBoxComponent filenameText;
	private ItemsComponent<Macro> macrosList;
	private ButtonComponent saveButton;
	private CheckboxComponent loopCheckbox;
	private KeybindComponent keybindComponent;
	private Macro currentMacro;
	
	private Runnable startRecordingRunnable;
	private Runnable stopRecordingRunnable;
	private Runnable playMacroRunnable;
	private Runnable pauseMacroRunnable;
	private Runnable stopMacroRunnable;
	public MacroWindow() {
		super("Macro", 895, 150);
		sizeToContent = SizeToContent.Both;
		setProperty(UIElement.MinWidthProperty, 350f);

		StackPanelComponent stackPanel = new StackPanelComponent();
		stackPanel.setSpacing(8f);
		
		StringComponent header = new StringComponent("Macros");
		header.setProperty(UIElement.FontWeightProperty, FontManager.WEIGHT_BOLD);
		header.bindProperty(UIElement.ForegroundProperty, GuiManager.foregroundHeaderColor);
		stackPanel.addChild(header);

		stackPanel.addChild(new SeparatorComponent());

		StringComponent label = new StringComponent("Records your inputs and plays them back.");
		stackPanel.addChild(label);

		// Record button uses a red circle (idle) / red square (recording).
		Shader recordRed = Shader.solid(new Color(220, 40, 40));

		recordIcon = new EllipseComponent();
		recordIcon.setProperty(UIElement.WidthProperty, 10f);
		recordIcon.setProperty(UIElement.HeightProperty, 10f);
		recordIcon.setProperty(UIElement.HorizontalAlignmentProperty, HorizontalAlignment.Center);
		recordIcon.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		recordIcon.setProperty(UIElement.BackgroundProperty, recordRed);
		recordIcon.setProperty(UIElement.IsHitTestVisibleProperty, false);

		recordStopIcon = new PolygonComponent(PolygonBank.STOP);
		recordStopIcon.setProperty(UIElement.WidthProperty, 14f);
		recordStopIcon.setProperty(UIElement.HeightProperty, 14f);
		recordStopIcon.setProperty(UIElement.HorizontalAlignmentProperty, HorizontalAlignment.Center);
		recordStopIcon.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		recordStopIcon.setProperty(UIElement.ForegroundProperty, recordRed);
		recordStopIcon.setProperty(UIElement.IsHitTestVisibleProperty, false);

		startRecordingRunnable = () -> {
			Umbra.getInstance().guiManager.setClickGuiOpen(false);
			Umbra.getInstance().macroManager.getRecorder().reset();
			Umbra.getInstance().macroManager.getRecorder().startRecording();
			recordButton.setContent(recordStopIcon);
			recordButton.setOnClick(stopRecordingRunnable);
		};

		stopRecordingRunnable = () -> {
			Umbra.getInstance().macroManager.getRecorder().stopRecording();
			Macro macro = UMBRA.macroManager.getRecorder().constructMacro();
			this.currentMacro = macro;
			recordButton.setContent(recordIcon);
			recordButton.setOnClick(startRecordingRunnable);
		};

		recordButton = new ButtonComponent(startRecordingRunnable);
		recordButton.setProperty(UIElement.WidthProperty, 26f);
		recordButton.setProperty(UIElement.HeightProperty, 26f);
		recordButton.setContent(recordIcon);

		// Play / Pause Button — toggles the PolygonProperty between PLAY and PAUSE.
		playPauseIcon = new PolygonComponent(PolygonBank.PLAY);
		playPauseIcon.setProperty(UIElement.WidthProperty, 14f);
		playPauseIcon.setProperty(UIElement.HeightProperty, 14f);
		playPauseIcon.setProperty(UIElement.HorizontalAlignmentProperty, HorizontalAlignment.Center);
		playPauseIcon.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		playPauseIcon.setProperty(UIElement.IsHitTestVisibleProperty, false);

		playMacroRunnable = () -> {
			playPauseIcon.setProperty(PolygonComponent.PolygonProperty, PolygonBank.PAUSE);
			UMBRA.macroManager.getPlayer().play(currentMacro, () -> {
				playPauseIcon.setProperty(PolygonComponent.PolygonProperty, PolygonBank.PLAY);
				playPausePlaybackButton.setOnClick(playMacroRunnable);
			});
			playPausePlaybackButton.setOnClick(pauseMacroRunnable);
		};

		pauseMacroRunnable = () -> {
			playPauseIcon.setProperty(PolygonComponent.PolygonProperty, PolygonBank.PLAY);
			UMBRA.macroManager.getPlayer().stop();
			playPausePlaybackButton.setOnClick(playMacroRunnable);
		};

		playPausePlaybackButton = new ButtonComponent(playMacroRunnable);
		playPausePlaybackButton.setProperty(UIElement.WidthProperty, 26f);
		playPausePlaybackButton.setProperty(UIElement.HeightProperty, 26f);
		playPausePlaybackButton.setContent(playPauseIcon);

		// Stop Button
		stopMacroRunnable = () -> {
			UMBRA.macroManager.getPlayer().stop();
			playPauseIcon.setProperty(PolygonComponent.PolygonProperty, PolygonBank.PLAY);
			playPausePlaybackButton.setOnClick(playMacroRunnable);
		};
		stopPlaybackButton = new ButtonComponent(stopMacroRunnable);
		stopPlaybackButton.setProperty(UIElement.WidthProperty, 26f);
		stopPlaybackButton.setProperty(UIElement.HeightProperty, 26f);
		PolygonComponent stopIcon = new PolygonComponent(PolygonBank.STOP);
		stopIcon.setProperty(UIElement.WidthProperty, 14f);
		stopIcon.setProperty(UIElement.HeightProperty, 14f);
		stopIcon.setProperty(UIElement.HorizontalAlignmentProperty, HorizontalAlignment.Center);
		stopIcon.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		stopIcon.setProperty(UIElement.IsHitTestVisibleProperty, false);
		stopPlaybackButton.setContent(stopIcon);
		
		StackPanelComponent controlsPanel = new StackPanelComponent();
		controlsPanel.setDirection(StackType.Horizontal);
		controlsPanel.setSpacing(6f);
		controlsPanel.addChild(recordButton);
		controlsPanel.addChild(playPausePlaybackButton);
		controlsPanel.addChild(stopPlaybackButton);

		stackPanel.addChild(controlsPanel);

		// Loop Checkbox
		loopCheckbox = new CheckboxComponent();
		loopCheckbox.setProperty(CheckboxComponent.HeaderProperty, "Loop");
		loopCheckbox.setOnChanged((looping) -> {
			if (currentMacro != null)
				currentMacro.setLooping(looping);
		});
		stackPanel.addChild(loopCheckbox);

		// Keybind Text
		keybindComponent = new KeybindComponent();
		keybindComponent.setProperty(KeybindComponent.SelectedKeyProperty, InputConstants.UNKNOWN);
		keybindComponent.setProperty(KeybindComponent.HeaderProperty, "Macro Keybind");
		keybindComponent.setOnChanged((key) -> {
			if(currentMacro != null) 
				currentMacro.setKeybind(key);
		});
		stackPanel.addChild(keybindComponent);
		
		// Filename + Save
		stackPanel.addChild(new StringComponent("Filename:"));

		GridComponent fileNameGrid = new GridComponent();
		fileNameGrid.setProperty(GridComponent.HorizontalSpacingProperty, 8f);
		fileNameGrid.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative));
		fileNameGrid.addColumnDefinition(new GridDefinition(75, RelativeUnit.Absolute));
		
		filenameText = new TextBoxComponent();
		fileNameGrid.addChild(filenameText);

		saveButton = new ButtonComponent(() -> {
			String filename = filenameText.getProperty(TextBoxComponent.TextProperty);
			Macro clone = new Macro(currentMacro);
			clone.setName(filename);
			UMBRA.macroManager.addMacro(clone);
			currentMacro = clone;
			filenameText.setProperty(TextBoxComponent.TextProperty, "");
			loopCheckbox.setProperty(CheckboxComponent.IsCheckedProperty, false);
		});

		StringComponent saveText = new StringComponent("Save");
		saveText.setProperty(StringComponent.TextAlignmentProperty, TextAlign.Center);
		saveText.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
		saveButton.setContent(saveText);
		fileNameGrid.addChild(saveButton);
		stackPanel.addChild(fileNameGrid);

		// Macros List
		stackPanel.addChild(new StringComponent("Saved Macros"));
		stackPanel.addChild(new SeparatorComponent());

		Function<Macro, UIElement> macroItemFactory = (macro -> {
			
			GridComponent grid = new GridComponent();
			grid.addColumnDefinition(new GridDefinition(1, RelativeUnit.Relative));
			grid.addColumnDefinition(new GridDefinition(1, RelativeUnit.Auto));
			grid.setProperty(GridComponent.HorizontalSpacingProperty, 8f);
			
			// Macro Name
			String macroNameText = macro.getName();
			if(macro.isLooping())
				macroNameText += " (Loop)";
			
			if(macro.getKeybind() != InputConstants.UNKNOWN)
				macroNameText += " [" + macro.getKeybind().getDisplayName().getString() + "]";
			
			StringComponent text = new StringComponent(macroNameText);
			text.setProperty(UIElement.IsHitTestVisibleProperty, true);
			text.setOnClicked((_) -> {
				this.currentMacro = new Macro(macro);
				this.filenameText.setProperty(TextBoxComponent.TextProperty, macro.getName());
				this.keybindComponent.setProperty(KeybindComponent.SelectedKeyProperty, macro.getKeybind());
				this.loopCheckbox.setProperty(CheckboxComponent.IsCheckedProperty, macro.isLooping());
			});
			grid.addChild(text);
			
			// Delete Button 
			ButtonComponent deleteButton = new ButtonComponent(() -> {
				// TODO: We need to add some kind of confirmation....
				// Let's add a GuiManager POPUP
				UMBRA.macroManager.removeMacro(macro);
			});
			
			StringComponent deleteString = new StringComponent("🗑");
			deleteString.setProperty(StringComponent.TextAlignmentProperty, TextAlign.Center);
			deleteString.setProperty(UIElement.VerticalAlignmentProperty, VerticalAlignment.Center);
			deleteButton.setContent(deleteString);
			grid.addChild(deleteButton);
			return grid;
		});

		Supplier<PanelComponent> macroListParentSupplier = () -> {
			StackPanelComponent panel = new StackPanelComponent();
			panel.setSpacing(4f);
			return panel;
		};

		macrosList = new ItemsComponent<Macro>(Umbra.getInstance().macroManager.getMacros(), macroListParentSupplier, macroItemFactory);

		ScrollComponent macroScroll = new ScrollComponent();
		macroScroll.setProperty(UIElement.MaxHeightProperty, 300f);
		macroScroll.setProperty(UIElement.MarginProperty, new Thickness(4f));
		macroScroll.setContent(macrosList);
		stackPanel.addChild(macroScroll);

		setContent(stackPanel);
	}
}