package nico.runtimeassets.mixin.client;

import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import nico.runtimeassets.client.internal.RuntimeAssetsResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Arrays;

@Mixin(ResourcePackManager.class)
public abstract class ResourcePackManagerMixin {
    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
    private static ResourcePackProvider[] modifyProvider(ResourcePackProvider[] providers) {
        ResourcePackProvider[] newProviders = Arrays.copyOf(providers, providers.length + 1);
        newProviders[providers.length] = RuntimeAssetsResourcePack.create();
        return newProviders;
    }
}
