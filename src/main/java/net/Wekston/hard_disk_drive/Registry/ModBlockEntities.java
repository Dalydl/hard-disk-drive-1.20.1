package net.Wekston.hard_disk_drive.Registry;

import net.Wekston.hard_disk_drive.HardDiskDrive;
import net.Wekston.hard_disk_drive.blocks.Encrypt.EncryptorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, HardDiskDrive.MODID);

    public static final RegistryObject<BlockEntityType<EncryptorBlockEntity>> ENCRYPTOR_BLOCK_BE =
            BLOCK_ENTITIES.register("encryptor_block_be", () ->
                    BlockEntityType.Builder.of(EncryptorBlockEntity::new,
                            ModBlocks.ENCRYPTOR_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}