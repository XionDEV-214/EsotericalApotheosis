package com.esotericalapotheosis.mixin;

import com.esotericalapotheosis.salvage.StackingSalvageSlot;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingMenu;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = SalvagingMenu.class, remap = false)
public abstract class MixinSalvagingMenu {

    private static final int INPUT_SLOTS = 12;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void esotericalapotheosis$replaceInputSlots(int id, Inventory inv, BlockPos pos, CallbackInfo ci) {
        AbstractContainerMenu self = (AbstractContainerMenu) (Object) this;
        Level level = inv.player.level();
        for (int i = 0; i < INPUT_SLOTS; i++) {
            Slot old = self.slots.get(i);
            if (!(old instanceof SlotItemHandler sih) || !(sih.getItemHandler() instanceof InternalItemHandler handler)) continue;
            Slot replacement = new StackingSalvageSlot(self, handler, old.getContainerSlot(), old.x, old.y,
                    s -> SalvagingMenu.findMatch(level, s) != null);
            replacement.index = old.index;
            self.slots.set(i, replacement);
        }
    }

    @Redirect(method = "salvageAll", at = @At(value = "INVOKE",
            target = "Ldev/shadowsoffire/apotheosis/adventure/affix/salvaging/SalvagingMenu;salvageItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"))
    private List<ItemStack> esotericalapotheosis$salvageWholeStack(Level level, ItemStack stack) {
        int count = stack.getCount();
        if (count <= 1) return SalvagingMenu.salvageItem(level, stack);

        List<ItemStack> merged = new ArrayList<>();
        for (int n = 0; n < count; n++) {
            for (ItemStack out : SalvagingMenu.salvageItem(level, stack)) {
                if (out.isEmpty()) continue;
                boolean found = false;
                for (ItemStack m : merged) {
                    if (ItemStack.isSameItemSameTags(m, out)) {
                        m.grow(out.getCount());
                        found = true;
                        break;
                    }
                }
                if (!found) merged.add(out.copy());
            }
        }

        List<ItemStack> result = new ArrayList<>();
        for (ItemStack m : merged) {
            int max = Math.max(1, m.getMaxStackSize());
            int remaining = m.getCount();
            while (remaining > 0) {
                ItemStack part = m.copy();
                part.setCount(Math.min(remaining, max));
                result.add(part);
                remaining -= part.getCount();
            }
        }
        return result;
    }
}
