package nico.runtimeassets.client.api;

import com.google.gson.JsonObject;
import io.netty.util.internal.UnstableApi;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public interface RuntimeAssetsInitializer {
    Map<Identifier, Resource> apply(ResourcePack resourcePack);

    String getId();

    @UnstableApi
    default Resource createAnimationMetaFile(ResourcePack resourcePack, Boolean interpolate, @Nullable Integer frameTime) {
        JsonObject root = new JsonObject();
        JsonObject animation = new JsonObject();

        if (interpolate != null) animation.addProperty("interpolate", interpolate);
        if (frameTime != null) animation.addProperty("frametime", frameTime);

        root.add("animation", animation);

        return new Resource(resourcePack, () -> new ByteArrayInputStream(root.toString().getBytes(StandardCharsets.UTF_8)));
    }
}
