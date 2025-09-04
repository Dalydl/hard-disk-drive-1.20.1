package net.Wekston.hard_disk_drive.Registry;


import net.Wekston.hard_disk_drive.HardDiskDrive;
import net.Wekston.hard_disk_drive.items.EncryptedItem;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HardDiskDrive.MODID);
    public static final RegistryObject<Item> ENCRYPTED_ITEM = ITEMS.register("encrypted_item",
            () -> new EncryptedItem());
}



