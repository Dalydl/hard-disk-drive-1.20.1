package net.Wekston.hard_disk_drive.blocks;
import net.Wekston.hard_disk_drive.Registry.ModMenus;
import net.Wekston.hard_disk_drive.blocks.Encrypt.EncryptorBlockEntity;
import net.Wekston.hard_disk_drive.items.EncryptedItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
public class EncryptorMenu extends AbstractContainerMenu {
    private final EncryptorBlockEntity blockEntity;
    private final ContainerData data;

    public EncryptorMenu(int id, Inventory playerInventory, EncryptorBlockEntity blockEntity) {
        super(ModMenus.ENCRYPTOR_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.data = new SimpleContainerData(1);
        this.addSlot(new Slot(blockEntity, 0, 8, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof EncryptedItem;
            }

            @Override
            public void setChanged() {
                super.setChanged();
                blockEntity.setChanged();
            }
        });

        layoutPlayerInventorySlots(playerInventory, 8, 84);

        this.addDataSlots(data);
    }

    public EncryptorMenu(int id, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(id, playerInventory, getBlockEntity(playerInventory, extraData));
    }

    private static EncryptorBlockEntity getBlockEntity(Inventory playerInventory, FriendlyByteBuf extraData) {
        BlockPos pos = extraData.readBlockPos();
        Level level = playerInventory.player.level();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof EncryptorBlockEntity) {
            return (EncryptorBlockEntity) blockEntity;
        }
        throw new IllegalStateException("Wrong block entity type at " + pos);
    }

    private void layoutPlayerInventorySlots(Inventory playerInventory, int leftCol, int topRow) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, leftCol + j * 18, topRow + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, leftCol + k * 18, topRow + 58));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index == 0) {
                if (!this.moveItemStackTo(itemstack1, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemstack1.getItem() instanceof EncryptedItem) {
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 28) {
                if (!this.moveItemStackTo(itemstack1, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 37) {
                if (!this.moveItemStackTo(itemstack1, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    public void encryptText(String text) {
        ItemStack stack = blockEntity.getItem(0);

        if (stack.getItem() instanceof EncryptedItem && text != null && !text.trim().isEmpty()) {
            EncryptedItem.setEncryptedText(stack, text.trim());
            blockEntity.setItem(0, stack);
            blockEntity.setChanged();
        }
    }
    public void decryptText() {
        ItemStack stack = blockEntity.getItem(0);
        if (stack.getItem() instanceof EncryptedItem) {
            String decryptedText = EncryptedItem.getEncryptedText(stack);
            blockEntity.setText(decryptedText);
            blockEntity.setChanged();
        }
    }

    public EncryptorBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity.stillValid(player);
    }

    public ItemStack getEncryptedItem() {
        return blockEntity.getItem(0);
    }
}