package baubles.common.network;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.Baubles;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;
import java.util.Objects;

public class PacketSync implements IMessage, IMessageHandler<PacketSync, IMessage> {

    int playerId;
    byte slot = 0;
    ItemStack bauble;

    public PacketSync() {
    }

    public PacketSync(EntityPlayer player, int slot, ItemStack bauble) {
        this((EntityLivingBase) player, slot, bauble);
    }

    public PacketSync(EntityLivingBase p, int slot, ItemStack bauble) {
        this.slot = (byte) slot;
        this.bauble = bauble;
        this.playerId = p.getEntityId();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(playerId);
        buffer.writeByte(slot);
        new PacketBuffer(buffer).writeCompoundTag(bauble.serializeNBT());
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        playerId = buffer.readInt();
        slot = buffer.readByte();
        try {
            bauble = new ItemStack(Objects.requireNonNull(new PacketBuffer(buffer).readCompoundTag()));
        } catch (IOException e) {
            throw new RuntimeException("How did you achieve this?", e);
        }
    }

    @Override
    public IMessage onMessage(PacketSync message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            World world = Baubles.proxy.getClientWorld();
            if (world == null) return;
            Entity p = world.getEntityByID(message.playerId);
            if (p instanceof EntityLivingBase) {
                IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityLivingBase) p);
                if (baubles == null) return;
                baubles.setStackInSlot(message.slot, message.bauble);
            }
        });
        return null;
    }
}
