package com.cake.trading_floor.compat.jei;

import com.cake.trading_floor.TradingFloor;
import com.cake.trading_floor.compat.jei.virtual_recipes.potential_villager_trade.PotentialVillagerTrade;
import com.cake.trading_floor.compat.jei.virtual_recipes.potential_villager_trade.PotentialVillagerTradeCategory;
import com.cake.trading_floor.registry.TFRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

@SuppressWarnings("unused")
@JeiPlugin
public class TradingFloorJei implements IModPlugin {
    
    private static final ResourceLocation ID = TradingFloor.asResource("jei_plugin");
    
    public static RecipeType<PotentialVillagerTrade> POTENTIAL_TRADE_TYPE = new RecipeType<>(TradingFloor.asResource("potential_villager_trade"), PotentialVillagerTrade.class);
    
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (Minecraft.getInstance().level == null) {
            TradingFloor.LOGGER.info("Failed to get trading floor recipes, level is null!");
            return;
        }
        Level levelForProviders = Minecraft.getInstance().level;
        registration.addRecipes(POTENTIAL_TRADE_TYPE, PotentialVillagerTrade.buildPotentialTrades(levelForProviders.registryAccess(), levelForProviders));
    }
    
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new PotentialVillagerTradeCategory());
    }
    
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(TFRegistry.TRADING_DEPOT.asStack(), POTENTIAL_TRADE_TYPE);
    }
    
    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }
    
}
