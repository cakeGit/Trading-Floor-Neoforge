package com.cake.trading_floor.network;

import com.cake.trading_floor.TradingFloor;
import com.cake.trading_floor.network.packets.EmitParticlesFromInstancePacket;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Locale;

/**
 * THANK GOD TO SIMI FOR YOUR WONDERFUL MIT LISCENCE I LOVE YOU
 */
public enum TFPackets implements BasePacketPayload.PacketTypeProvider{
    EMIT_PARTICLES_FROM_INSTANCE(EmitParticlesFromInstancePacket.class, EmitParticlesFromInstancePacket.STREAM_CODEC);

    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> TFPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        String name = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
            new CustomPacketPayload.Type<>(TradingFloor.asResource(name)),
            clazz, codec
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>) this.type.type();
    }

    public static void register() {
        CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(TradingFloor.MOD_ID, 1);
        for (TFPackets packet : TFPackets.values()) {
            packetRegistry.registerPacket(packet.type);
        }
        packetRegistry.registerAllPackets();
    }
    
}
