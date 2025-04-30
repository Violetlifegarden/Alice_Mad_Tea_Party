package com.huashanlunjian.alice_mad_tea_party.item;

import com.huashanlunjian.alice_mad_tea_party.entity.DollEntity;
import com.huashanlunjian.alice_mad_tea_party.network.ChangeCameraPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class MagicDoll extends Item {
    public MagicDoll(Properties properties) {
        super(properties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide){
            PacketDistributor.sendToServer(new ChangeCameraPacket(true));

        }


//        if (minecraft.getCameraEntity()==player){
//
//            minecraft.setCameraEntity(doll);
//        } else if (minecraft.getCameraEntity()!=player) {
//            minecraft.cameraEntity = player;
//        }

        ItemStack itemstack = player.getItemInHand(hand);
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
}
