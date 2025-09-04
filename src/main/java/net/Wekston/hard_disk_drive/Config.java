package net.Wekston.hard_disk_drive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = HardDiskDrive.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue ENCRYPTOR_NUMBERS = BUILDER
            .comment("The maximum number of characters in a hard drive and a computer.\nМаксимальное количество символов в жестком диске и компьютере.\n\ndo not put less than if there are more characters in the computer (it will kick)\nне ставить меньше, если в компьютере больше символов (будет кикать)")
            .defineInRange("encryptorNumber", 5000, 1, Integer.MAX_VALUE);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int encryptorNumber;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        encryptorNumber = ENCRYPTOR_NUMBERS.get();
    }
}
