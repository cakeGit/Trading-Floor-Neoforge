package com.cake.trading_floor.registry;

import com.cake.trading_floor.TradingFloor;
import com.cake.trading_floor.content.trading_depot.TradingDepotBlock;
import com.cake.trading_floor.content.trading_depot.TradingDepotBlockEntity;
import com.cake.trading_floor.content.trading_depot.TradingDepotRenderer;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static com.cake.trading_floor.TradingFloor.REGISTRATE;
import static com.simibubi.create.api.behaviour.display.DisplaySource.displaySource;

public class TFRegistry {

    public static final BlockEntry<TradingDepotBlock> TRADING_DEPOT = REGISTRATE
        .block("trading_depot", TradingDepotBlock::new)
        .properties(BlockBehaviour.Properties::noOcclusion)
        .blockstate(BlockStateGen.horizontalBlockProvider(false))
        .transform(displaySource(TFDisplaySources.TRADE_COMPLETED_COUNT))
        .transform(displaySource(TFDisplaySources.TRADE_PRODUCT_SUM))
        .simpleItem()
        .register();
    
    public static final BlockEntityEntry<TradingDepotBlockEntity> TRADING_DEPOT_BLOCK_ENTITY = REGISTRATE
        .blockEntity("trading_depot", TradingDepotBlockEntity::new)
        .validBlocks(TRADING_DEPOT)
        .renderer(() -> TradingDepotRenderer::new)
        .register();
    
    public static void init() {
        TradingFloor.LOGGER.info("Registering all " + TradingFloor.NAME + " entries");
    }
    
}
