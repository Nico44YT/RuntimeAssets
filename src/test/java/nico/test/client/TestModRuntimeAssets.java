package nico.test.client;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import nico.runtimeassets.client.api.texture.BakedTexture;
import nico.runtimeassets.client.api.RuntimeAssetsInitializer;
import nico.runtimeassets.client.api.texture.Texture;
import nico.runtimeassets.client.api.texture.TextureLayer;
import nico.test.TestMod;

import java.util.HashMap;
import java.util.Map;

public class TestModRuntimeAssets implements RuntimeAssetsInitializer {
    @Override
    public Map<Identifier, Resource> apply(ResourcePack resourcePack) {
        Map<Identifier, Resource> map = new HashMap<>();

        TextureLayer layer0 = TextureLayer.of(Identifier.of("minecraft", "block/end_stone")).invert();
        //TextureLayer layer1 = TextureLayer.of(Identifier.of("test", "block/red")).setType(LayerType.MULTIPLICATIVE);
        BakedTexture baked = new Texture(16, 16, layer0).bake();

        map.put(TestMod.id("textures/block/test.png"), baked.asResource().apply(resourcePack));

        return map;
    }

    @Override
    public String getId() {
        return TestMod.MOD_ID;
    }

    private int fromFloats(float[] floats) {
        int red = (int) (floats[0] * 255f);
        int green = (int) (floats[1] * 255f);
        int blue = (int) (floats[2] * 255f);

        return red << 16 | green << 8 | blue;
    }
}
