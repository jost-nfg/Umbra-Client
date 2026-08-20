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
import net.umbra.gui.colors.Color;
import net.umbra.rendering.shaders.Shader;
import net.umbra.settings.Setting;

public class ShaderSetting extends Setting<Shader> {

    protected ShaderSetting(String ID, String displayName, String description, Shader default_value, Consumer<Shader> onUpdate) {
        super(ID, displayName, description, default_value);
        type = TYPE.SHADER;
    }

    @Override
    protected boolean isValueValid(Shader value) {
        return value != null;
    }

    public String getShaderId() {
        Shader v = getValue();
        return v != null ? v.id() : "solid";
    }

    public void setShaderId(String shaderId) {
        Shader template = Umbra.getInstance().shaderManager.getShader(shaderId);
        if (template != null) {
            Shader newShader = template.copy();
            setValue(newShader);
        }
    }

    public static ShaderSetting.BUILDER builder() {
    	return new ShaderSetting.BUILDER();
    }

    public static class BUILDER extends Setting.BUILDER<ShaderSetting.BUILDER, ShaderSetting, Shader> {
		protected BUILDER() {
        }

		@Override
		public ShaderSetting build() {
		    Shader def = defaultValue != null ? defaultValue : Shader.solid(new Color(255, 255, 255));
			return new ShaderSetting(id, displayName, description, def, onUpdate);
		}
	}
}
