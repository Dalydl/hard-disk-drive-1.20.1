package net.Wekston.hard_disk_drive.event;

import net.Wekston.hard_disk_drive.blocks.Encrypt.EncryptorBlockEntity;
import net.Wekston.hard_disk_drive.items.EncryptedItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static net.Wekston.hard_disk_drive.Config.encryptorNumber;

public class EncryptTextPacket {
    private final BlockPos pos;
    private final String text;

    public EncryptTextPacket(BlockPos pos, String text) {
        this.pos = pos;
        this.text = text;
    }

    public static void encode(EncryptTextPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeUtf(packet.text != null ? packet.text : "", encryptorNumber);
    }

    public static EncryptTextPacket decode(FriendlyByteBuf buffer) {
        return new EncryptTextPacket(buffer.readBlockPos(), buffer.readUtf(encryptorNumber));
    }

    public static void handle(EncryptTextPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                Level level = player.level();
                if (level.getBlockEntity(packet.pos) instanceof EncryptorBlockEntity blockEntity) {
                    ItemStack stack = blockEntity.getItem(0);

                    if (stack.getItem() instanceof EncryptedItem && packet.text != null && !packet.text.trim().isEmpty()) {
                        EncryptedItem.setEncryptedText(stack, packet.text.trim());
                        blockEntity.setItem(0, stack);
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}