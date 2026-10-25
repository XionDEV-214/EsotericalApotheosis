package com.esotericalapotheosis.salvage;

import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import dev.shadowsoffire.placebo.menu.FilteredSlot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

/**
 * Salvaging Table 입력 슬롯 대체용.
 * <p>
 * 원본(SalvagingMenu 의 익명 UpdatingSlot)은 getMaxStackSize 를 1 로 고정한다.
 * 이 슬롯은 그 오버라이드를 하지 않으므로 SlotItemHandler 기본 동작
 * (핸들러 슬롯 한도 64 와 아이템 자체 max stack 중 작은 값)을 그대로 따른다.
 * setChanged 시 메뉴 갱신은 Placebo UpdatingSlot 과 동일하게 수행한다.
 */
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
