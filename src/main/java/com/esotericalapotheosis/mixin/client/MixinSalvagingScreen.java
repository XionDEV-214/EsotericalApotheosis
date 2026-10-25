package com.esotericalapotheosis.mixin.client;

import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingMenu;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingRecipe.OutputData;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingScreen;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = SalvagingScreen.class, remap = false)
public abstract class MixinSalvagingScreen {

    @Redirect(method = "computeResults", at = @At(value = "INVOKE",
            target = "Ldev/shadowsoffire/apotheosis/adventure/affix/salvaging/SalvagingMenu;getSalvageCounts(Ldev/shadowsoffire/apotheosis/adventure/affix/salvaging/SalvagingRecipe$OutputData;Lnet/minecraft/world/item/ItemStack;)[I"))
    private int[] esotericalapotheosis$multiplyByCount(OutputData data, ItemStack stack) {
        int[] counts = SalvagingMenu.getSalvageCounts(data, stack);
        int n = Math.max(1, stack.getCount());
        return new int[]{counts[0] * n, counts[1] * n};
    }
}
