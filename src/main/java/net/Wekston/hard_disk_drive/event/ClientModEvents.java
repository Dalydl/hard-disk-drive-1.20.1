package net.Wekston.hard_disk_drive.event;

import net.Wekston.hard_disk_drive.HardDiskDrive;
import net.Wekston.hard_disk_drive.Registry.ModBlockEntities;
import net.Wekston.hard_disk_drive.blocks.Encrypt.EncryptorBlockEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HardDiskDrive.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)

public class ClientModEvents {
    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.ENCRYPTOR_BLOCK_BE.get(), EncryptorBlockEntityRenderer::new);

    }
}
