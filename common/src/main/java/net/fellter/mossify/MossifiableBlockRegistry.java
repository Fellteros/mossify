package net.fellter.mossify;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.block.Block;

import static net.minecraft.block.Blocks.*;

public final class MossifiableBlockRegistry {
	public static final BiMap<Block, Block> BLOCK_MAP = HashBiMap.create();
	public static final Logger LOGGER = LoggerFactory.getLogger(MossifiableBlockRegistry.class);

	public static void register(Block clean, Block mossy) {
		Block old = BLOCK_MAP.put(clean, mossy);

		if (old != null) {
			LOGGER.info("Replaced old mossifying mapping from {} to {} with {}", clean, old, mossy);
		}
	}

	static {
		register(COBBLESTONE, MOSSY_COBBLESTONE);
		register(COBBLESTONE_SLAB, MOSSY_COBBLESTONE_SLAB);
		register(COBBLESTONE_STAIRS, MOSSY_COBBLESTONE_STAIRS);
		register(COBBLESTONE_WALL, MOSSY_COBBLESTONE_WALL);
		register(STONE_BRICKS, MOSSY_STONE_BRICKS);
		register(STONE_BRICK_SLAB, MOSSY_STONE_BRICK_SLAB);
		register(STONE_BRICK_STAIRS, MOSSY_STONE_BRICK_STAIRS);
		register(STONE_BRICK_WALL, MOSSY_STONE_BRICK_WALL);
		register(INFESTED_STONE_BRICKS, INFESTED_MOSSY_STONE_BRICKS);
	}
}
