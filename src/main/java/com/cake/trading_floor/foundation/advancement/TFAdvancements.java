package com.cake.trading_floor.foundation.advancement;

import com.cake.trading_floor.TradingFloor;
import com.cake.trading_floor.registry.TFRegistry;
import com.google.common.collect.Sets;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Package-private class avoidance, Name isn't shortened to make it clear the ownership
 */
public class TFAdvancements implements DataProvider {

    public static final List<TFAdvancement> ENTRIES = new ArrayList<>();

    public static final TFAdvancement
        MONEY_MONEY_MONEY = createTF("money_money_money", b -> b.icon(TFRegistry.TRADING_DEPOT)
        .title("Money Money Money,")
        .description("Make a trade automatically using a trading depot")
        .afterCreateRoot()
    ),
        BUDDING_CAPITALIST = createTF("drop_shipper", b -> b.icon(Items.EMERALD)
            .title("Intro to drop-shipping")
            .description("Generate 64 emeralds from a single trading depot")
            .after(MONEY_MONEY_MONEY)
            .special(TFAdvancement.TaskType.NOISY)
        ),
        HAPPY_JEFF = createTF("happy_jeff", b -> b.icon(Items.EMERALD_BLOCK)
            .title("Jeff Bezos would be proud")
            .description("Generate 1000 emeralds from a single trading depot")
            .after(BUDDING_CAPITALIST)
            .special(TFAdvancement.TaskType.NOISY)
        );

    protected static TFAdvancement createTF(String id, UnaryOperator<TFAdvancement.Builder> b) {
        return new TFAdvancement(id, b);
    }

    // Datagen

    private final PackOutput output;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public TFAdvancements(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.output = output;
        this.registries = registries;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return this.registries.thenCompose(provider -> {
            PackOutput.PathProvider pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "advancement");
            List<CompletableFuture<?>> futures = new ArrayList<>();

            Set<ResourceLocation> set = Sets.newHashSet();
            Consumer<AdvancementHolder> consumer = (advancement) -> {
                ResourceLocation id = advancement.id();
                if (!set.add(id))
                    throw new IllegalStateException("Duplicate advancement " + id);
                Path path = pathProvider.json(id);
                LOGGER.info("Saving advancement {}", id);
                futures.add(DataProvider.saveStable(cache, provider, Advancement.CODEC, advancement.value(), path));
            };

            for (TFAdvancement advancement : ENTRIES)
                advancement.save(consumer, provider);

            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "Trading Floor's Advancements";
    }

    public static void provideLang(BiConsumer<String, String> consumer) {
        for (TFAdvancement advancement : ENTRIES)
            advancement.provideLang(consumer);
    }

    public static void register() {
        TradingFloor.LOGGER.info("Registering Advancements for: " + TradingFloor.NAME);
    }

}
