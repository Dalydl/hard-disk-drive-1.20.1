package net.Wekston.hard_disk_drive.Registry;


import net.Wekston.hard_disk_drive.HardDiskDrive;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTab {
    public class ModCreativeModTabs {
        public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
                DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HardDiskDrive.MODID);

        public static final RegistryObject<CreativeModeTab> HardDiskDriveTab = CREATIVE_MODE_TABS.register("realfoodtab",
                () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.ENCRYPTOR_BLOCK.get()))
                        .title(Component.translatable("creativetab.disktab"))
                        .displayItems((pParameters, add) -> {
                            add.accept(ModBlocks.ENCRYPTOR_BLOCK.get());
                            add.accept(ModItems.ENCRYPTED_ITEM.get());
                        })
                        .build());


        public static void register(IEventBus eventBus) {
            CREATIVE_MODE_TABS.register(eventBus);
        }
    }
}
