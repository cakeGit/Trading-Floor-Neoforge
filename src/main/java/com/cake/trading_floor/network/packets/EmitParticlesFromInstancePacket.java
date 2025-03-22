package com.cake.trading_floor.network.packets;

import com.cake.trading_floor.network.TFPackets;
import com.cake.trading_floor.foundation.ParticleEmitter;
import com.cake.trading_floor.registry.TFParticleEmitters;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public record EmitParticlesFromInstancePacket(
    Vec3 origin,
    int count,
    int emitterHash
) implements ClientboundPacketPayload {
    public static final StreamCodec<ByteBuf, EmitParticlesFromInstancePacket> STREAM_CODEC = StreamCodec.composite(
        CatnipStreamCodecs.VEC3, EmitParticlesFromInstancePacket::origin,
        ByteBufCodecs.INT, EmitParticlesFromInstancePacket::count,
        ByteBufCodecs.INT, EmitParticlesFromInstancePacket::emitterHash,
        EmitParticlesFromInstancePacket::new
    );

//        buffer.writeInt(emitter.hashCode());
//        buffer.writeInt(count);
//        buffer.writeDouble(origin.x);
//        buffer.writeDouble(origin.y);
//        buffer.writeDouble(origin.z);

//    final ParticleEmitter emitter;
//    final Vec3 origin;
//    final int count;
//
//    int emitterHash = -1;
//
//    public EmitParticlesFromInstancePacket(ParticleEmitter emitter, Vec3 origin, int count) {
//        this.emitter = emitter;
//        this.origin = origin;
//        this.count = count;
//    }
//
//    public EmitParticlesFromInstancePacket(FriendlyByteBuf byteBuf) {
//        emitterHash = byteBuf.readInt();
//        emitter = TFParticleEmitters.INSTANCES_BY_HASH.get(emitterHash);
//
//        count = byteBuf.readInt();
//
//        origin = new Vec3(
//            byteBuf.readDouble(),
//            byteBuf.readDouble(),
//            byteBuf.readDouble()
//        );
//    }

    @Override
    public void handle(LocalPlayer player) {
        ParticleEmitter emitter = TFParticleEmitters.INSTANCES_BY_HASH.get(emitterHash);
        if (emitter == null)
            throw new RuntimeException("Couldn't resolve local emitter instance, expected " + emitterHash);

        emitter.emitParticles(Minecraft.getInstance().level, origin, count);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return TFPackets.EMIT_PARTICLES_FROM_INSTANCE;
    }

}
