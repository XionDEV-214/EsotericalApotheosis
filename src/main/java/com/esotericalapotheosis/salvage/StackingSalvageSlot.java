package com.esotericalapotheosis.salvage;

import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import dev.shadowsoffire.placebo.menu.FilteredSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class StackingSalvageSlot extends FilteredSlot {

    private final AbstractContainerMenu menu;

    public StackingSalvageSlot(AbstractContainerMenu menu, InternalItemHandler handler, int index, int x, int y, Predicate<ItemStack> filter) {
        super(handler, index, x, y, filter);
        this.menu = menu;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.menu.slotsChanged(null);
    }
}
