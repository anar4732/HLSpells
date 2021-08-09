package com.divinity.hlspells.network.packets;

import com.divinity.hlspells.items.WandItem;
import com.divinity.hlspells.items.capabilities.WandItemProvider;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class WandInputPacket
{
    private int key;

    public WandInputPacket() {}

    public WandInputPacket(int key)
    {
        this.key = key;
    }

    public static void encode(WandInputPacket message, PacketBuffer buffer)
    {
        buffer.writeInt(message.key);
    }

    public static WandInputPacket decode(PacketBuffer buffer)
    {
        return new WandInputPacket(buffer.readInt());
    }

    public static void handle(WandInputPacket message, Supplier<NetworkEvent.Context> contextSupplier)
    {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            if (player != null)
            {
                if (player.getMainHandItem().getItem() instanceof WandItem && player.getOffhandItem().getItem() instanceof WandItem)
                {
                    ItemStack wandItem = player.getMainHandItem();
                    wandItem.getCapability(WandItemProvider.WAND_CAP, null).ifPresent(p -> p.setCurrentSpellCycle(p.getCurrentSpellCycle() + 1));
                }

                else if (player.getMainHandItem().getItem() instanceof WandItem)
                {
                    ItemStack wandItem = player.getMainHandItem();
                    wandItem.getCapability(WandItemProvider.WAND_CAP, null).ifPresent(p -> p.setCurrentSpellCycle(p.getCurrentSpellCycle() + 1));
                }

                else if (player.getOffhandItem().getItem() instanceof WandItem)
                {
                    ItemStack wandItem = player.getOffhandItem();
                    wandItem.getCapability(WandItemProvider.WAND_CAP, null).ifPresent(p -> p.setCurrentSpellCycle(p.getCurrentSpellCycle() + 1));
                }
            }
        });
        context.setPacketHandled(true);
    }
}