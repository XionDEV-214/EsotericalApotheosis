package com.esotericalapotheosis;

import com.mojang.logging.LogUtils;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.apotheosis.adventure.AdventureConfig;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvageItem;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityClamp;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.placebo.color.GradientColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Mod(EsotericalApotheosis.MODID)
public class EsotericalApotheosis {
    public static final String MODID = "esotericalapotheosis";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> ARTIFACT_MATERIAL = rarityMat("artifact");
    public static final RegistryObject<Item> HEIRLOOM_MATERIAL = rarityMat("heirloom");
    public static final RegistryObject<Item> ESOTERIC_MATERIAL = rarityMat("esoteric");
    public static final RegistryObject<Item> ABSOLUTION_MATERIAL = rarityMat("absolution");

    public static final Map<ResourceLocation, GradientColor> GRADIENT_COLORS = new HashMap<>();

    static {
        GRADIENT_COLORS.put(res("artifact"),
                new SyncSafeGradientColor(generateGradient(100, 0xFFB22222, 0xFFFF4500, 0xFFFFD700), "artifact_gradient", 0.8f));
        GRADIENT_COLORS.put(res("heirloom"),
                new SyncSafeGradientColor(generateGradient(100, 0xFF98FF98, 0xFF00BFFF, 0xFFFFFFFF), "heirloom_gradient", 0.8f));
        GRADIENT_COLORS.put(res("esoteric"),
                new SyncSafeGradientColor(generateGradient(100, 0xFF800080, 0xFFFF69B4, 0xFFFFA07A), "esoteric_gradient", 0.8f));
        GRADIENT_COLORS.put(res("absolution"),
                new SyncSafeGradientColor(generateGradient(120, 0xFF0047AB, 0xFFFFFFFF, 0xFFFFD700), "absolution_gradient", 0.8f));
    }

    public EsotericalApotheosis() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = net.minecraftforge.common.MinecraftForge.EVENT_BUS;
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onCreativeTab);
        forgeBus.addListener(EventPriority.LOWEST, this::onApotheosisReload);
        // HIGH: The Avarice 의 LivingDropsEvent 핸들러(NORMAL)보다 먼저 장비→재료 교체를 끝내서,
        // 교체된 레어리티 재료(max stack 64)가 Avarice 배수를 받도록 한다.
        forgeBus.addListener(EventPriority.HIGH, RarityMaterialDrops::onLivingDrops);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Esoterical Apotheosis loaded. Breaking through the Ancient ceiling!");
    }

    private void onApotheosisReload(Apotheosis.ApotheosisReloadEvent event) {
        AdventureConfig.GEM_DIM_RARITIES.put(
                new ResourceLocation("minecraft", "the_end"),
                new RarityClamp.Simple(
                        RarityRegistry.byLegacyId("apotheosis:rare"),
                        RarityRegistry.byLegacyId("esotericalapotheosis:artifact")
                )
        );
        AdventureConfig.GEM_DIM_RARITIES.put(
                new ResourceLocation("aether", "the_aether"),
                new RarityClamp.Simple(
                        RarityRegistry.byLegacyId("apotheosis:epic"),
                        RarityRegistry.byLegacyId("esotericalapotheosis:heirloom")
                )
        );
    }

    private void onCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == Adventure.Tabs.ADVENTURE.get()) {
            event.accept(ARTIFACT_MATERIAL.get());
            event.accept(HEIRLOOM_MATERIAL.get());
            event.accept(ESOTERIC_MATERIAL.get());
            event.accept(ABSOLUTION_MATERIAL.get());
        }
    }

    private static RegistryObject<Item> rarityMat(String id) {
        return ITEMS.register(id + "_material",
                () -> new SalvageItem(RarityRegistry.INSTANCE.holder(res(id)), new Item.Properties()));
    }

    public static ResourceLocation res(String path) {
        return new ResourceLocation(MODID, path);
    }

    private static int[] generateGradient(int steps, int... colors) {
        int[] result = new int[steps];
        int segments = colors.length;
        int stepsPerSegment = steps / segments;
        int idx = 0;
        for (int i = 0; i < segments; i++) {
            int c1 = colors[i];
            int c2 = colors[(i + 1) % colors.length];
            int r1 = (c1 >> 16) & 0xFF, g1 = (c1 >> 8) & 0xFF, b1 = c1 & 0xFF;
            int r2 = (c2 >> 16) & 0xFF, g2 = (c2 >> 8) & 0xFF, b2 = c2 & 0xFF;
            for (int j = 0; j < stepsPerSegment && idx < steps; j++) {
                float t = (float) j / stepsPerSegment;
                int r = (int) (r1 + (r2 - r1) * t);
                int g = (int) (g1 + (g2 - g1) * t);
                int b = (int) (b1 + (b2 - b1) * t);
                result[idx++] = 0xFF000000 | (r << 16) | (g << 8) | b;
            }
        }
        while (idx < steps) {
            result[idx++] = colors[0];
        }
        return result;
    }
}
