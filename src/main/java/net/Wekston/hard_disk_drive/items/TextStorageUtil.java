package net.Wekston.hard_disk_drive.items;
import net.minecraft.nbt.CompoundTag;
import java.util.Base64;


public class TextStorageUtil {
    public static void setTextToNBT(CompoundTag tag, String key, String text) {
        if (text != null && !text.isEmpty()) {
            String encoded = Base64.getEncoder().encodeToString(text.getBytes());
            tag.putString(key, encoded);
        } else {
            tag.remove(key);
        }
    }

    public static String getTextFromNBT(CompoundTag tag, String key) {
        if (tag.contains(key)) {
            try {
                String encoded = tag.getString(key);
                byte[] decoded = Base64.getDecoder().decode(encoded);
                return new String(decoded);
            } catch (IllegalArgumentException e) {
                return "";
            }
        }
        return "";
    }
}