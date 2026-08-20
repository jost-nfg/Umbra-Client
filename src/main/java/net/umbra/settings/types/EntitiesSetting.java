/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.settings.types;

import net.umbra.settings.Setting;
import net.minecraft.world.entity.EntityType;
import java.util.Set;
import java.util.function.Consumer;

public class EntitiesSetting extends Setting<Set<EntityType<?>>> {
    protected EntitiesSetting(String ID, String displayName, String description, Set<EntityType<?>> default_value, Consumer<Set<EntityType<?>>> onUpdate) {
        super(ID, displayName, description, default_value, onUpdate);
        type = TYPE.ENTITIES;
    }

    @Override
    protected boolean isValueValid(Set<EntityType<?>> value) {
        return true;
    }

    public static EntitiesSetting.BUILDER builder() {
    	return new EntitiesSetting.BUILDER();
    }

    public static class BUILDER extends Setting.BUILDER<EntitiesSetting.BUILDER, EntitiesSetting, Set<EntityType<?>>> {
		protected BUILDER() {
        }

		@Override
		public EntitiesSetting build() {
			return new EntitiesSetting(id, displayName, description, defaultValue, onUpdate);
		}
	}
}
