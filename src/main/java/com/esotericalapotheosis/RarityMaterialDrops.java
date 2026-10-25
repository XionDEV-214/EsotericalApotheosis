package com.esotericalapotheosis;

import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

/**
 * Apotheosis 장비 드롭을 레어리티 재료 드롭으로 교체한다.
 * <p>
 * 대상 (Apotheosis 7.4.8 이 직접 붙이는 NBT 태그로 판별):
 * <ul>
 *     <li>{@code apoth_boss} - Apothic Boss / Miniboss 의 확정 드롭 장비 ({@code ApothBoss#modifyBossItem})</li>
 *     <li>{@code apoth_rspawn} - 자연 스폰 몹에게 확률로 쥐어주는 랜덤 어픽스 장비 ({@code AdventureEvents#special})</li>
 * </ul>
 * 드롭 개수: 기본 1~4개 + 약탈 보너스 0~(약탈 레벨)개.
 */
public final class RarityMaterialDrops {

    public static final int BASE_MIN = 1;
    public static final int BASE_MAX = 4;

    private RarityMaterialDrops() {}

    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        // 플레이어 사망 시 인벤토리 드롭은 건드리지 않는다.
        if (entity instanceof Player || entity.level().isClientSide) return;

        RandomSource rand = entity.getRandom();
        int looting = Math.max(0, event.getLootingLevel());

        for (ItemEntity drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            if (!isApothEquipmentDrop(stack)) continue;

            DynamicHolder<LootRarity> rarity = AffixHelper.getRarity(stack);
            if (!rarity.isBound()) continue;

            Item material = rarity.get().getMaterial();
            if (material == null || material == Items.AIR) continue;

            int count = BASE_MIN + rand.nextInt(BASE_MAX - BASE_MIN + 1);
            if (looting > 0) count += rand.nextInt(looting + 1);

            ItemStack result = new ItemStack(material);
            result.setCount(Math.min(count, result.getMaxStackSize()));
            drop.setItem(result);
        }
    }

    private static boolean isApothEquipmentDrop(ItemStack stack) {
        if (stack.isEmpty()) return false;
        CompoundTag tag = stack.getTag();
        if (tag == null) return false;
        return tag.getBoolean("apoth_boss") || tag.getBoolean("apoth_rspawn");
    }
}
