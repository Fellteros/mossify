package net.fellter.mossify.fabric;

import net.fellter.mossify.Mossify;

import net.fabricmc.api.ModInitializer;

public final class MossifyFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		Mossify.init();
		Mossify.LOGGER.info("Initialized Mossify on Fabric");

	}
}
