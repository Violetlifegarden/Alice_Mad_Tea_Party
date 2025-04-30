package com.huashanlunjian.alice_mad_tea_party;

import com.huashanlunjian.alice_mad_tea_party.client.model.EntityFairyModel;
import com.huashanlunjian.alice_mad_tea_party.client.renderer.DollRenderer;
import com.huashanlunjian.alice_mad_tea_party.entity.DollEntity;
import com.huashanlunjian.alice_mad_tea_party.entity.ModEntities;
import com.huashanlunjian.alice_mad_tea_party.item.CreativeModTab;
import com.huashanlunjian.alice_mad_tea_party.network.NetworkHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.Locale;

import static com.huashanlunjian.alice_mad_tea_party.item.Moditems.ITEMS;

@Mod(Alice_mad_tea_party.MOD_ID)
public class Alice_mad_tea_party {
    public static final String MOD_ID = "alice_mad_tea_party";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);


    public Alice_mad_tea_party(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        ITEMS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        CreativeModTab.register(modEventBus);
        modEventBus.addListener(NetworkHandler::registerPacket);

    }
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
    public static ResourceLocation prefix(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name.toLowerCase(Locale.ROOT));
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event){
            event.registerEntityRenderer(ModEntities.DEMOSONG.get(), DollRenderer::new);

        }
        @SubscribeEvent
        public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(EntityFairyModel.LAYER, EntityFairyModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void addEntityAttributeEvent(EntityAttributeCreationEvent event) {
            event.put(ModEntities.DEMOSONG.get(), DollEntity.createMobAttributes().build());
        }
    }

}
