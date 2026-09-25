package com.example.exploredchunks;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.world.level.ChunkPos;

public class ExploredChunks implements ClientModInitializer {
    /** How many chunks around you get marked. 1 = a 3x3 square (9 chunks). */
    private static final int RADIUS = 1;

    @Override
    public void onInitializeClient() {
        // Start fresh every time you join a world or server.
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ExploredStore.clear());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ExploredStore.clear());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) return;
            ChunkPos center = client.player.chunkPosition();
            String dimension = client.level.dimension().toString();
            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    ExploredStore.markExplored(dimension, center.x + dx, center.z + dz);
                }
            }
        });
    }
}
