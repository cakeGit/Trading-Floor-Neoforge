package com.cake.trading_floor.foundation.advancement;

import com.cake.trading_floor.TradingFloor;
import com.cake.trading_floor.foundation.access.TFAdvancementBuilderAccess;
import com.cake.trading_floor.registry.TFTriggers;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.advancement.SimpleCreateTrigger;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class TFAdvancement {
    
    static final String LANG = "advancement." + TradingFloor.MOD_ID + ".";

    private SimpleCreateTrigger builtinTrigger;
    private final Advancement.Builder builder;

    AdvancementHolder datagenResult;

    public @Nullable AdvancementHolder parent;
    public @Nullable ResourceLocation parentId;
    
    protected final String id;
    protected String title;
    protected String description;
    
    public TFAdvancement(String id, UnaryOperator<Builder> b) {
        this.builder = Advancement.Builder.advancement();
        this.id = id;
        
        Builder t = new Builder();
        b.apply(t);

        builtinTrigger = TFTriggers.addSimple(id + "_builtin");
        builder.addCriterion("0", builtinTrigger.createCriterion(builtinTrigger.instance()));
        
        builder.display(t.icon, Component.translatable(titleKey()),
            Component.translatable(descriptionKey()).withStyle(s -> s.withColor(0xDBA213)),
            null, t.type.frame, t.type.toast, t.type.announce, t.type.hide);
        
        TFAdvancements.ENTRIES.add(this);
    }
    
    private String titleKey() {
        return LANG + id;
    }
    
    private String descriptionKey() {
        return titleKey() + ".desc";
    }
    
    public boolean isAlreadyAwardedTo(Player player) {
        if (!(player instanceof ServerPlayer sp) || sp.getServer() == null)
            return true;
        AdvancementHolder advancement = sp.getServer()
            .getAdvancements()
            .get(TradingFloor.asResource(id));
        if (advancement == null)
            return true;
        return sp.getAdvancements()
            .getOrStartProgress(advancement)
            .isDone();
    }
    
    public void awardTo(Player player) {
        if (!(player instanceof ServerPlayer sp))
            return;
        if (builtinTrigger == null)
            throw new UnsupportedOperationException("Advancement " + id + " uses external Triggers, it cannot be awarded directly");
        builtinTrigger.trigger(sp);
    }
    
    void save(Consumer<AdvancementHolder> t, HolderLookup.Provider registries) {
        if (parent != null) {
            builder.parent(parent);
        }
        if (parentId != null) {
            ((TFAdvancementBuilderAccess) builder).create_trading_floor$setParent(parentId);
        }
        datagenResult = builder.save(t, TradingFloor.asResource(id)
            .toString());
    }
    
    void provideLang(BiConsumer<String, String> consumer) {
        consumer.accept(titleKey(), title);
        consumer.accept(descriptionKey(), description);
    }

    public enum TaskType {
        
        SILENT(AdvancementType.TASK, false, false, false),
        NORMAL(AdvancementType.TASK, true, false, false),
        NOISY(AdvancementType.TASK, true, true, false),
        EXPERT(AdvancementType.GOAL, true, true, false),
        SECRET(AdvancementType.GOAL, true, true, true),
        
        ;
        
        final AdvancementType frame;
        final boolean toast;
        final boolean announce;
        final boolean hide;
        
        TaskType(AdvancementType frame, boolean toast, boolean announce, boolean hide) {
            this.frame = frame;
            this.toast = toast;
            this.announce = announce;
            this.hide = hide;
        }
    }
    
    public class Builder {
        
        private TaskType type = TaskType.NORMAL;
        private ItemStack icon;
        
        Builder special(TaskType type) {
            this.type = type;
            return this;
        }
        
        Builder afterCreateRoot() {
            TFAdvancement.this.parentId = Create.asResource("root");
            return this;
        }
        
        Builder after(TFAdvancement other) {
            parentId = TradingFloor.asResource(other.id);
            return this;
        }

        Builder icon(ItemProviderEntry<?, ?> item) {
            return icon(item.asStack());
        }

        Builder icon(ItemLike item) {
            return icon(new ItemStack(item));
        }
        
        Builder icon(ItemStack stack) {
            icon = stack;
            return this;
        }
        
        Builder title(String title) {
            TFAdvancement.this.title = title;
            return this;
        }
        
        Builder description(String description) {
            TFAdvancement.this.description = description;
            return this;
        }
        
    }
    
}
