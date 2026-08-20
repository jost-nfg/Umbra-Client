/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.settings.types;

import java.util.function.Consumer;

import net.umbra.Umbra;
import net.umbra.gui.font.FontManager;
import net.umbra.gui.font.UIFont;
import net.umbra.settings.Setting;

public class FontSetting extends Setting<UIFont> {
	private String fontName;

	protected FontSetting(String ID, String displayName, String description, String defaultFontName, Consumer<UIFont> onUpdate) {
		super(ID, displayName, description, Umbra.getInstance().fontManager.getDefaultFont(), onUpdate);
		this.fontName = defaultFontName;
		type = TYPE.FONT;
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String name) {
		this.fontName = name;
		UIFont resolved = Umbra.getInstance().fontManager.getFont(name);
		super.setValue(resolved);
	}

	@Override
	protected boolean isValueValid(UIFont value) {
		return value != null;
	}

	public static FontSetting.BUILDER builder() {
		return new FontSetting.BUILDER();
	}

	public static class BUILDER extends Setting.BUILDER<FontSetting.BUILDER, FontSetting, UIFont> {
		private String fontName = FontManager.DEFAULT_FONT;

		protected BUILDER() {
		}

		public FontSetting.BUILDER fontName(String name) {
			this.fontName = name;
			return this;
		}

		@Override
		public FontSetting build() {
			return new FontSetting(id, displayName, description, fontName, onUpdate);
		}
	}
}
