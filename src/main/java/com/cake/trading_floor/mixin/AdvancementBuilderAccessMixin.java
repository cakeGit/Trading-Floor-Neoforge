package com.cake.trading_floor.mixin;

import com.cake.trading_floor.foundation.access.TFAdvancementBuilderAccess;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(targets = "net.minecraft.advancements.Advancement$Builder")
public class AdvancementBuilderAccessMixin implements TFAdvancementBuilderAccess {
    @Shadow private Optional<ResourceLocation> parent;

    @Override
    public void create_trading_floor$setParent(ResourceLocation location) {
        parent = Optional.of(location);
    }
}
