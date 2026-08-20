/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.settings.types;

import net.umbra.settings.Setting;
import net.minecraft.world.level.block.Block;
import java.util.HashSet;
import java.util.function.Consumer;

public class BlocksSetting extends Setting<HashSet<Block>> {
    protected BlocksSetting(String ID, String displayName, String description, HashSet<Block> default_value, Consumer<HashSet<Block>> onUpdate) {
        super(ID, displayName, description, default_value, onUpdate);
        type = TYPE.BLOCKS;
    }

    @Override
    protected boolean isValueValid(HashSet<Block> value) {
        return true;
    }
    
    public static BlocksSetting.BUILDER builder() {
    	return new BlocksSetting.BUILDER();
    }
    
    public static class BUILDER extends Setting.BUILDER<BlocksSetting.BUILDER, BlocksSetting, HashSet<Block>> {
		protected BUILDER() {
        }

		@Override
		public BlocksSetting build() {
			return new BlocksSetting(id, displayName, description, defaultValue, onUpdate);
		}
	}
}
