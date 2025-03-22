package com.cake.trading_floor.foundation;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.FakePlayer;

public class TFPlatformPredicates {

    public static boolean isFakePlayer(LivingEntity player) {
        return player instanceof FakePlayer;
    }

}
