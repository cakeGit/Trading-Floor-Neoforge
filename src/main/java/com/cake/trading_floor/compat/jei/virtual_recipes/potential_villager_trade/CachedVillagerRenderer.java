package com.cake.trading_floor.compat.jei.virtual_recipes.potential_villager_trade;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerType;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class CachedVillagerRenderer {
    
    private static Villager him = null;
    public static final List<VillagerType> ALL_BASE_VILLAGER_TYPES = List.of(
        VillagerType.DESERT,
        VillagerType.JUNGLE,
        VillagerType.PLAINS,
        VillagerType.SAVANNA,
        VillagerType.SNOW,
        VillagerType.SWAMP,
        VillagerType.TAIGA
    );
    
    public static void renderVillagerForRecipe(GuiGraphics guiGraphics, int x, int y, int scale, float targetX, float targetY, PotentialVillagerTrade recipe) {
        if (him == null && Minecraft.getInstance().level != null)
            him = new Villager(EntityType.VILLAGER, Minecraft.getInstance().level);
        
        if (him == null) return;
        
        VillagerData recipeSpecificData = him.getVillagerData()
            .setProfession(recipe.getProfession())
            .setLevel(recipe.getVillagerLevel());
        
        if (recipe.offer.isNoteVillagerTypeSpecific())
            recipeSpecificData = setCycleOfVillagerType(recipeSpecificData);
        else
            recipeSpecificData = recipeSpecificData.setType(VillagerType.PLAINS);
        
        him.setVillagerData(recipeSpecificData);
        
        renderEntityInInventoryFollowsMouse(guiGraphics, x, y, scale, 0.325f, targetX, targetY, him);
    }

    public static void renderEntityInInventoryFollowsMouse(GuiGraphics guiGraphics, int x1, int y1, int scale, float yOffset, float mouseX, float mouseY, LivingEntity entity) {
        float f = (float)(x1) / 2.0F;
        float f1 = (float)(y1) / 2.0F;
        float f2 = (float)Math.atan((f - mouseX) / 40.0F);
        float f3 = (float)Math.atan((f1 - mouseY) / 40.0F);
        renderEntityInInventoryFollowsAngle(guiGraphics, x1, y1, x1+50, y1, scale, yOffset, f2, f3, entity);
    }

    public static void renderEntityInInventoryFollowsAngle(GuiGraphics p_282802_, int p_275688_, int p_275245_, int p_275535_, int p_294406_, int p_294663_, float p_275604_, float angleXComponent, float angleYComponent, LivingEntity p_275689_) {
        float f = (float)(p_275688_);
        float f1 = (float)(p_275245_);
        Quaternionf quaternionf = (new Quaternionf()).rotateZ((float)Math.PI);
        Quaternionf quaternionf1 = (new Quaternionf()).rotateX(angleYComponent * 20.0F * ((float)Math.PI / 180F));
        quaternionf.mul(quaternionf1);
        float f4 = p_275689_.yBodyRot;
        float f5 = p_275689_.getYRot();
        float f6 = p_275689_.getXRot();
        float f7 = p_275689_.yHeadRotO;
        float f8 = p_275689_.yHeadRot;
        p_275689_.yBodyRot = 180.0F + angleXComponent * 20.0F;
        p_275689_.setYRot(180.0F + angleXComponent * 40.0F);
        p_275689_.setXRot(-angleYComponent * 20.0F);
        p_275689_.yHeadRot = p_275689_.getYRot();
        p_275689_.yHeadRotO = p_275689_.getYRot();
        float f9 = p_275689_.getScale();
        Vector3f vector3f = new Vector3f(0.0F, p_275689_.getBbHeight() / 2.0F + p_275604_ * f9, 0.0F);
        float f10 = (float)p_294663_ / f9;
        InventoryScreen.renderEntityInInventory(p_282802_, f, f1, f10, vector3f, quaternionf, quaternionf1, p_275689_);
        p_275689_.yBodyRot = f4;
        p_275689_.setYRot(f5);
        p_275689_.setXRot(f6);
        p_275689_.yHeadRotO = f7;
        p_275689_.yHeadRot = f8;
    }

    private static VillagerData setCycleOfVillagerType(VillagerData recipeSpecificData) {
        int villagerTypeIndex = (AnimationTickHolder.getTicks() / 20) % ALL_BASE_VILLAGER_TYPES.size();
        return recipeSpecificData.setType(ALL_BASE_VILLAGER_TYPES.get(villagerTypeIndex));
    }
    
}
