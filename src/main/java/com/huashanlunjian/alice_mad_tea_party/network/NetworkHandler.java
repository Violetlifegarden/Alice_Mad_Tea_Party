package com.huashanlunjian.alice_mad_tea_party.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {
    private static final String VERSION = "1.0.0";
    public static void registerPacket(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(VERSION).optional();

        registrar.executesOn(HandlerThread.MAIN).playToServer(DollControlPacket.TYPE, DollControlPacket.CODEC, DollControlPacket::handle);
        registrar.executesOn(HandlerThread.MAIN).playToServer(ChangeCameraPacket.TYPE, ChangeCameraPacket.CODEC, ChangeCameraPacket::handle);

    }

}

