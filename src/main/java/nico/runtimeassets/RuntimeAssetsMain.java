package nico.runtimeassets;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApiStatus.Internal
public class RuntimeAssetsMain implements ModInitializer {

    public static final String MOD_ID = "runtime_textures";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[RuntimeTextures] Initializing");
    }

    public static Identifier id(String name) {
        return Identifier.of(MOD_ID, name);
    }
}
