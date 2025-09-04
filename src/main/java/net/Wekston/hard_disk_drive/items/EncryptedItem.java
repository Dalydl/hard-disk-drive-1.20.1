package net.Wekston.hard_disk_drive.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import static net.Wekston.hard_disk_drive.Config.encryptorNumber;


public class EncryptedItem extends Item {
    public EncryptedItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (hasEncryptedText(stack)) {
            tooltip.add(Component.translatable("toolip.hard_disk_drive.encrypt"));

        }
    }

    public static boolean hasEncryptedText(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("EncryptedText") &&
                !stack.getTag().getString("EncryptedText").isEmpty();
    }

    public static String getEncryptedText(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("EncryptedText")) {
            return stack.getTag().getString("EncryptedText");
        }
        return "";
    }

    public static void setEncryptedText(ItemStack stack, String text) {
        CompoundTag tag = stack.getOrCreateTag();
        String finalText = text != null && text.length() > encryptorNumber ? text.substring(0, encryptorNumber) : text;
        tag.putString("EncryptedText", finalText != null ? finalText : "");
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasEncryptedText(stack);
    }
}