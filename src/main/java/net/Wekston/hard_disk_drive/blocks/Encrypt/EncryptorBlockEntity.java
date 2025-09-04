package net.Wekston.hard_disk_drive.blocks.Encrypt;

import net.Wekston.hard_disk_drive.Registry.ModBlockEntities;
import net.Wekston.hard_disk_drive.Registry.ModBlocks;
import net.Wekston.hard_disk_drive.blocks.EncryptorMenu;
import net.Wekston.hard_disk_drive.event.EncryptorContainer;
import net.Wekston.hard_disk_drive.items.TextStorageUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;


public class EncryptorBlockEntity extends BlockEntity implements MenuProvider, Container {
    private ItemStack itemStack = ItemStack.EMPTY;
    private String text = "";

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 1);
            }
        }
    };
    public void drops() {
        if (level == null || level.isClientSide()) return;
        if (!itemStack.isEmpty()) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), itemStack);
        }
        ItemStack blockStack = new ItemStack(ModBlocks.ENCRYPTOR_BLOCK.get());
        Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), blockStack);
    }
    public Container getContainer() {
        return new EncryptorContainer(this);
    }
    public EncryptorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENCRYPTOR_BLOCK_BE.get(), pos, state);
    }
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return index == 0 ? text.length() : 0;
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 1;
        }
    };
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }


    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            text = TextStorageUtil.getTextFromNBT(tag, "text");
        }
    }
    public ContainerData getDataAccess() {
        return dataAccess;
    }
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new EncryptorMenu(id, playerInventory, this);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return itemStack.isEmpty();
    }

    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? getItem() : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        if (index == 0 && !itemStack.isEmpty()) {
            ItemStack result = itemStack.split(count);
            if (itemStack.isEmpty()) {
                itemStack = ItemStack.EMPTY;
            }
            setChanged();
            return result;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        if (index == 0) {
            ItemStack stack = itemStack;
            itemStack = ItemStack.EMPTY;
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index == 0) {
            setItem(stack);
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D,
                    (double)this.worldPosition.getY() + 0.5D,
                    (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clearContent() {
        itemStack = ItemStack.EMPTY;
        setChanged();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text != null ? text : "";
        this.setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.hard_disk_drive.encryptor_block");
    }
    public ItemStack getItem() {
        return itemStack.copy();
    }
    public void setItem(ItemStack stack) {
        this.itemStack = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        this.setChanged();

        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition,
                    this.getBlockState(),
                    this.getBlockState(),
                    Block.UPDATE_ALL);
        }
    }
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        if (!itemStack.isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            itemStack.save(itemTag);
            tag.put("item", itemTag);
        }

        TextStorageUtil.setTextToNBT(tag, "text", text);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);

        if (tag.contains("item")) {
            itemStack = ItemStack.of(tag.getCompound("item"));
        } else {
            itemStack = ItemStack.EMPTY;
        }
    }
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        if (!itemStack.isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            itemStack.save(itemTag);
            tag.put("item", itemTag);
        }

        TextStorageUtil.setTextToNBT(tag, "text", text);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("item")) {
            itemStack = ItemStack.of(tag.getCompound("item"));
        } else {
            itemStack = ItemStack.EMPTY;
        }

        text = TextStorageUtil.getTextFromNBT(tag, "text");
    }
}