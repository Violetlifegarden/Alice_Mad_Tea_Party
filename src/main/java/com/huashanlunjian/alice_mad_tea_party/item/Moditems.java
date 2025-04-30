package com.huashanlunjian.alice_mad_tea_party.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.huashanlunjian.alice_mad_tea_party.Alice_mad_tea_party.MOD_ID;

public class Moditems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredItem<Item> MAGICDOLL = ITEMS.register("magicdoll",
            ()->new MagicDoll(new Item.Properties()));
    public static final DeferredItem<Item> MAGICLINE = ITEMS.register("magicline",
            ()->new MagicLine(new Item.Properties()));
}
