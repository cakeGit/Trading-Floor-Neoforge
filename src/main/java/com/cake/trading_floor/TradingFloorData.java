package com.cake.trading_floor;

import com.cake.trading_floor.foundation.advancement.TFAdvancements;
import com.cake.trading_floor.registry.TFRegistry;
import com.simibubi.create.Create;
import com.tterrag.registrate.providers.RegistrateDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class TradingFloorData {
    
    public static void gatherData(GatherDataEvent event) {
        if (!event.getMods().contains(Create.ID))
            return;

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        generator.addProvider(true, new TFAdvancements(output, lookupProvider));
//        event.getGenerator().addProvider(true, TradingFloor.REGISTRATE.setDataProvider(new RegistrateDataProvider(TradingFloor.REGISTRATE, TradingFloor.MOD_ID, event)));
    }
    
}
