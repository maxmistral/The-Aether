package com.aetherteam.aether.network.packet;

import com.aetherteam.aether.capability.player.AetherPlayer;
import com.aetherteam.aether.network.AetherPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public record AetherPlayerSyncPacket(int playerID, CompoundTag tag) implements AetherPacket {
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.playerID);
        buf.writeNbt(this.tag);
    }

    public static AetherPlayerSyncPacket decode(FriendlyByteBuf buf) {
        int playerID = buf.readInt();
        CompoundTag tag = buf.readNbt();
        return new AetherPlayerSyncPacket(playerID, tag);
    }

    @Override
    public void execute(Player playerEntity) {
        if (playerEntity != null && playerEntity.getServer() != null && playerEntity.level.getEntity(this.playerID) instanceof ServerPlayer serverPlayer && this.tag != null) {
            AetherPlayer.get(serverPlayer).ifPresent(aetherPlayer -> aetherPlayer.deserializeSynchableNBT(this.tag));
        } else {
            if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null && Minecraft.getInstance().level.getEntity(this.playerID) instanceof Player player && this.tag != null) {
                AetherPlayer.get(player).ifPresent(aetherPlayer -> aetherPlayer.deserializeSynchableNBT(this.tag));
            }
        }
    }
}
