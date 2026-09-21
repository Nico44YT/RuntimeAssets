package nico.test.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import nico.test.TestMod;

public class TestModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            context.drawTexture(TestMod.id("textures/block/test.png"), 32, 32, 0, 0, 64, 64, 64, 64);
        });
    }
}
