package nico.runtimeassets.client.internal;

import net.fabricmc.fabric.impl.resource.loader.GroupResourcePack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.resource.*;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.metadata.PackFeatureSetMetadata;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nico.runtimeassets.RuntimeAssetsMain;
import nico.runtimeassets.client.RuntimeAssetsClient;
import nico.runtimeassets.client.api.RuntimeAssetsInitializer;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class RuntimeAssetsResourcePack extends GroupResourcePack implements ResourcePackProvider {
    public static List<ResourcePack> PACKS = new LinkedList<>();

    private static final Identifier ID = RuntimeAssetsMain.id("generated_assets");
    private static final ResourcePackSource SOURCE = new ResourcePackSource() {

        @Override
        public Text decorate(Text packName) {
            return Text.translatable("pack.nameAndSource", packName, Text.translatable("pack.source.runtime_textures"));
        }

        @Override
        public boolean canBeEnabledLater() {
            return false;
        }
    };

    private final ResourcePackProfile profile;
    private final PackResourceMetadata metadata;
    private final PackFeatureSetMetadata featureMetadata;

    public RuntimeAssetsResourcePack(List<? extends ResourcePack> packs) {
        super(ResourceType.CLIENT_RESOURCES, packs);

        this.metadata = new PackResourceMetadata(Text.translatable(ID.toTranslationKey("pack", "description")), SharedConstants.getGameVersion().getResourceVersion(ResourceType.CLIENT_RESOURCES));
        this.featureMetadata = new PackFeatureSetMetadata(FeatureSet.empty());

        this.profile = ResourcePackProfile.create(
                ID.toString(),
                Text.translatable(ID.toTranslationKey("pack", "name")),
                true,
                $ -> this,
                ResourceType.CLIENT_RESOURCES,
                ResourcePackProfile.InsertionPosition.TOP,
                SOURCE
        );
    }

    public static ResourcePackProvider create() {
        FabricLoader.getInstance().invokeEntrypoints(
                "runtime_textures",
                RuntimeAssetsInitializer.class,
                RuntimeAssetsClient.INITIALIZERS::add
        );

        RuntimeAssetsClient.INITIALIZERS.forEach(invoker -> {
            GeneratedModResourcePack resourcePack = new GeneratedModResourcePack(
                    FabricLoader.getInstance().getModContainer(invoker.getId()).get().getMetadata(),
                    invoker::apply
            );
            addPack(resourcePack);
        });

        return new RuntimeAssetsResourcePack(PACKS);
    }

    public static void addPack(ResourcePack... packs) {
        PACKS.addAll(Arrays.asList(packs));
    }

    @Override
    public @Nullable InputSupplier<InputStream> openRoot(String... pathSegments) {
        String fileName = String.join("/", pathSegments);

        if ("pack.png".equals(fileName)) {
            return FabricLoader.getInstance().getModContainer(RuntimeAssetsMain.MOD_ID)
                    .flatMap(container -> container.getMetadata().getIconPath(512).flatMap(container::findPath))
                    .map(path -> (InputSupplier<InputStream>) (() -> Files.newInputStream(path)))
                    .orElse(null);
        }

        return null;
    }

    @Override
    public @Nullable <T> T parseMetadata(ResourceMetadataReader<T> metaReader) {
        return (T) switch (metaReader.getKey()) {
            case "pack" -> this.metadata;
            case "features" -> this.featureMetadata;
            default -> null;
        };
    }

    @Override
    public String getName() {
        return ID.getNamespace();
    }

    @Override
    public boolean isAlwaysStable() {
        return true;
    }

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder) {
        profileAdder.accept(this.profile);
    }
}
