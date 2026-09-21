package nico.runtimeassets.client.internal;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GeneratedModResourcePack implements ModResourcePack {

    private final ModMetadata modMetadata;
    private final Map<Identifier, Resource> resourceMap;

    public GeneratedModResourcePack(ModMetadata modMetadata, Function<GeneratedModResourcePack, Map<Identifier, Resource>> resourceMap) {
        this.modMetadata = modMetadata;
        this.resourceMap = resourceMap.apply(this);
    }

    @Override
    public ModMetadata getFabricModMetadata() {
        return modMetadata;
    }

    @Override
    public @Nullable InputSupplier<InputStream> openRoot(String... pathSegments) {
        String fileName = String.join("/", pathSegments);

        return null;
    }

    @Override
    public @Nullable InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        if(!resourceMap.containsKey(id)) return null;
        return () -> resourceMap.get(id).getInputStream();
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
        resourceMap.forEach((identifier, resource) -> {
            if (!identifier.getNamespace().equals(namespace)) return;
            if (!identifier.getPath().startsWith(prefix)) return;

            consumer.accept(identifier, resource::getInputStream);
        });

    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        return switch (type) {
            case CLIENT_RESOURCES ->
                    resourceMap.keySet().stream().map(Identifier::getNamespace).collect(Collectors.toSet());
            case SERVER_DATA ->
                    Set.of();
        };
    }

    @Override
    public @Nullable <T> T parseMetadata(ResourceMetadataReader<T> metaReader) {
        return null;
    }

    @Override
    public String getName() {
        return modMetadata.getName();
    }

    @Override
    public void close() {

    }
}
