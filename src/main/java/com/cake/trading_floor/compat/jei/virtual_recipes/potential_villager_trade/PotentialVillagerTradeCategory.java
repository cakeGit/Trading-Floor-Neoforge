package com.cake.trading_floor.compat.jei.virtual_recipes.potential_villager_trade;

import com.cake.trading_floor.TradingFloor;
import com.cake.trading_floor.compat.jei.TradingFloorJei;
import com.cake.trading_floor.foundation.TFLang;
import com.cake.trading_floor.registry.TFRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.ItemIcon;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class PotentialVillagerTradeCategory implements IRecipeCategory<PotentialVillagerTrade> {
    
    static final ResourceLocation TEXTURES = TradingFloor.asResource("textures/gui/jei.png");
    
    @Override
    public RecipeType<PotentialVillagerTrade> getRecipeType() {
        return TradingFloorJei.POTENTIAL_TRADE_TYPE;
    }
    
    @Override
    public Component getTitle() {
        return TFLang.translate("jei.potential_trade_type.title").component();
    }
    
    @Override
    public IDrawable getBackground() {
        return new EmptyBackground(177, 80);
    }
    
    @Override
    public @Nullable IDrawable getIcon() {
        return new ItemIcon(TFRegistry.TRADING_DEPOT::asStack);
    }
    
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PotentialVillagerTrade recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 11, 32)
            .addItemStack(recipe.getOffer().getCostA());
        if (recipe.getOffer().getCostB().isEmpty())
            builder.addSlot(RecipeIngredientRole.INPUT, 37, 32)
                .addItemStack(recipe.getOffer().getCostB());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 137, 32)
            .addItemStacks(recipe.getOffer().getPossibleResults());
    }
    
    @Override
    public void draw(PotentialVillagerTrade recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        guiGraphics.blit(TEXTURES, 160, 64, 0, 0, 16, 16);

        if (Minecraft.getInstance().level != null) {
            PoseStack stack = guiGraphics.pose();

            stack.pushPose();
//            stack.translate(0, 0, 400);
            CachedVillagerRenderer.renderVillagerForRecipe(guiGraphics, 92, 30, 30, (float) mouseX - 45, (float) mouseY, recipe);
            stack.popPose();
        }

        Component tradeNoteTooltip = getTooltipOfTradeNote(recipe.offer);
        if (tradeNoteTooltip != null && mouseX > 132 && mouseX < 140 && mouseY > 53 && mouseY < 61)
            guiGraphics.renderTooltip(Minecraft.getInstance().font, tradeNoteTooltip, (int) mouseX, (int) mouseY);
        
        if (mouseX > 170 && mouseX < 176 && mouseY > 65 && mouseY < 81) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, TFLang.translate("jei.missing_disclaimer").component(), (int) mouseX, (int) mouseY);
        }
        
        guiGraphics.blit(TEXTURES, 10, 31, 16, 0, 44, 18);
        guiGraphics.blit(TEXTURES, 132, 27, 60, 0, 26, 26);
        
        Integer tradeNoteUV = getUVXOffsetOfTradeNote(recipe.offer);
        if (tradeNoteUV != null)
            guiGraphics.blit(TEXTURES, 132, 53, tradeNoteUV, 32, 8, 8);

    }
    
    public static @Nullable Integer getUVXOffsetOfTradeNote(PotentialMerchantOfferInfo merchantOfferInfo) {
        if (merchantOfferInfo.isNoteVillagerTypeSpecific())
            return 0;
        else if (merchantOfferInfo.isNoteRandomisedEmeraldCost())
            return 8;
        else if (merchantOfferInfo.isNoteRandomisedDyeColor())
            return 16;
        else if (merchantOfferInfo.isImplyEnchantedVariants())
            return 24;
        return null;
    }
    
    public static @Nullable MutableComponent getTooltipOfTradeNote(PotentialMerchantOfferInfo merchantOfferInfo) {
        if (merchantOfferInfo.isNoteVillagerTypeSpecific())
            return TFLang.translate("jei.note.type_specific").component();
        else if (merchantOfferInfo.isNoteRandomisedEmeraldCost())
            return TFLang.translate("jei.note.randomised_cost").component();
        else if (merchantOfferInfo.isNoteRandomisedDyeColor())
            return TFLang.translate("jei.note.randomised_dye").component();
        else if (merchantOfferInfo.isImplyEnchantedVariants())
            return TFLang.translate("jei.note.enchanted_variants").component();
        return null;
    }
    
}
