package nico.runtimeassets.client.api.texture;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;

import java.io.ByteArrayInputStream;
import java.util.function.Function;

public record BakedTexture(int width, int height, byte[] bytes) {

    public Function<ResourcePack, Resource> asResource() {
        return pack -> new Resource(pack, () -> new ByteArrayInputStream(bytes));
    }
}
