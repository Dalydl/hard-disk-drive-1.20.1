package net.Wekston.hard_disk_drive.Registry;


import net.Wekston.hard_disk_drive.HardDiskDrive;
import net.Wekston.hard_disk_drive.blocks.EncryptorMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, HardDiskDrive.MODID);

    public static final RegistryObject<MenuType<EncryptorMenu>> ENCRYPTOR_MENU = MENUS.register("encryptor_menu",
            () -> IForgeMenuType.create(EncryptorMenu::new));
}