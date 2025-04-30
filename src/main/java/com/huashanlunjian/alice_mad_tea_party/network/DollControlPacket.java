package com.huashanlunjian.alice_mad_tea_party.network;

import com.huashanlunjian.alice_mad_tea_party.Alice_mad_tea_party;
import com.huashanlunjian.alice_mad_tea_party.entity.DollEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DollControlPacket(int id, float x, float y, float z, float yaw, float pitch) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<DollControlPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Alice_mad_tea_party.MOD_ID, "change_dimension"));
    public static final StreamCodec<ByteBuf,DollControlPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            DollControlPacket::id,
            ByteBufCodecs.FLOAT,
            DollControlPacket::x,
            ByteBufCodecs.FLOAT,
            DollControlPacket::y,
            ByteBufCodecs.FLOAT,
            DollControlPacket::z,
            ByteBufCodecs.FLOAT,
            DollControlPacket::yaw,
            ByteBufCodecs.FLOAT,
            DollControlPacket::pitch,
            DollControlPacket::new
    );
    public static void handle(DollControlPacket packet, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            Player player = context.player();
            DollEntity doll = DollEntity.getDoll(player, packet.id());
            if (doll!=null) {
                Minecraft.getInstance().getTutorial().onMouse(packet.yaw(), packet.pitch());
                doll.turn(packet.yaw(), packet.pitch());
                doll.yHeadRot = doll.getYRot();
                doll.move(MoverType.SELF, new Vec3(packet.x()* Mth.cos(doll.getYRot()/180*Mth.PI)-packet.z()* Mth.sin(doll.getYRot()/180*Mth.PI), packet.y(), packet.z()* Mth.cos(doll.getYRot()/180*Mth.PI)+packet.x()* Mth.sin(doll.getYRot()/180*Mth.PI)));//;move(MoverType.SELF, vec3);
            }
        }

    }
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
