package net.fellter.mossify.neoforge;

import net.neoforged.fml.common.Mod;

import net.fellter.mossify.Mossify;

@Mod(Mossify.MOD_ID)
public final class MossifyNeoForge {
	public MossifyNeoForge() {
		Mossify.init();
		Mossify.LOGGER.info("Initialized Mossify on NeoForge");
	}
}
