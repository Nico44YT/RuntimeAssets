package nico.runtimeassets.client;

import net.fabricmc.api.ClientModInitializer;
import nico.runtimeassets.client.api.RuntimeAssetsInitializer;

import java.util.LinkedHashSet;
import java.util.Set;

public class RuntimeAssetsClient implements ClientModInitializer {

    public static final Set<RuntimeAssetsInitializer> INITIALIZERS = new LinkedHashSet<>();

    @Override
    public void onInitializeClient() {

    }
}
