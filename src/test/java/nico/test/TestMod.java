package nico.test;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMod implements ModInitializer {

    public static final String MOD_ID = "test_mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[TestMod] Initializing");
    }

    public static Identifier id(String name) {
        return Identifier.of(MOD_ID, name);
    }
}
