package com.cake.trading_floor.registry;

import com.cake.trading_floor.TradingFloor;
import com.simibubi.create.foundation.advancement.CriterionTriggerBase;
import com.simibubi.create.foundation.advancement.SimpleCreateTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedList;
import java.util.List;

public class TFTriggers {
    private static final List<CriterionTriggerBase<?>> triggers = new LinkedList<>();

    public static SimpleTFTrigger addSimple(String id) {
        return add(new SimpleTFTrigger(id));
    }

    private static <T extends CriterionTriggerBase<?>> T add(T instance) {
        triggers.add(instance);
        return instance;
    }

    public static void register() {
        triggers.forEach(trigger -> {
            Registry.register(BuiltInRegistries.TRIGGER_TYPES, trigger.getId(), trigger);
        });
    }

    public static class SimpleTFTrigger extends SimpleCreateTrigger {
        public SimpleTFTrigger(String id) {
            super(id);
        }

        @Override
        public ResourceLocation getId() {
            return ResourceLocation.fromNamespaceAndPath(TradingFloor.MOD_ID, super.getId().getPath());
        }
    }
}
