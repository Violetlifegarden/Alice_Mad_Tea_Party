package com.huashanlunjian.alice_mad_tea_party.network;

import com.huashanlunjian.alice_mad_tea_party.Alice_mad_tea_party;
import com.huashanlunjian.alice_mad_tea_party.entity.DollEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record ChangeCameraPacket(boolean doll) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChangeCameraPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Alice_mad_tea_party.MOD_ID, "change_camera"));
    public static final StreamCodec<ByteBuf,ChangeCameraPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            ChangeCameraPacket::doll,
            ChangeCameraPacket::new
    );
    public static void handle(ChangeCameraPacket packet, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            DollEntity doll = new DollEntity(context.player());

            ServerPlayer player = (ServerPlayer) context.player();
            player.level().addFreshEntity(doll);
            player.connection.send(new ClientboundSetCameraPacket(doll));
            //player.setCamera(doll);
        }

    }


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
