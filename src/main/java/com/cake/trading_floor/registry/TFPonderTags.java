package com.cake.trading_floor.registry;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class TFPonderTags {

    public static final ResourceLocation ALL_TRADING_FLOOR_PONDERS = Create.asResource("base");

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        helper.registerTag(ALL_TRADING_FLOOR_PONDERS)
            .addToIndex()
            .item(AllBlocks.COGWHEEL.get(), true, false)
            .title("Create: Trading Floor")
            .description("Special trading depot to automatically trade with villagers")
            .register();


    }
    
}
