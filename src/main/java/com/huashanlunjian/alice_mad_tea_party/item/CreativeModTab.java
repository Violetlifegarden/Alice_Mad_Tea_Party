package com.huashanlunjian.alice_mad_tea_party.item;

import com.huashanlunjian.alice_mad_tea_party.Alice_mad_tea_party;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CreativeModTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Alice_mad_tea_party.MOD_ID);

    public static final Supplier<CreativeModeTab> AMARA_ITEMS_TAB = CREATIVE_MODE_TAB.register("alice_doll",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(Moditems.MAGICDOLL.get()))
                    .title(Component.translatable("itemgroup.alice_doll"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(Moditems.MAGICDOLL.get());
                    }).build());
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }

}
