package net.Wekston.hard_disk_drive.event;


import net.Wekston.hard_disk_drive.blocks.Encrypt.EncryptorBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class EncryptorContainer implements Container {
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private final EncryptorBlockEntity blockEntity;

    public EncryptorContainer(EncryptorBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        this.items.set(0, blockEntity.getItem(1));
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return items.get(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? blockEntity.getItem(1) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        if (index == 0) {
            ItemStack stack = blockEntity.getItem(1);
            if (!stack.isEmpty()) {
                ItemStack result = stack.split(count);
                if (stack.isEmpty()) {
                    blockEntity.setItem(ItemStack.EMPTY);
                } else {
                    blockEntity.setItem(stack);
                }
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        if (index == 0) {
            ItemStack stack = blockEntity.getItem(1);
            blockEntity.setItem(ItemStack.EMPTY);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index == 0) {
            blockEntity.setItem(stack);
        }
    }

    @Override
    public void setChanged() {
        blockEntity.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        blockEntity.setItem(ItemStack.EMPTY);
    }
}