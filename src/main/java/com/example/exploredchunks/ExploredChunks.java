package com.example.exploredchunks;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.world.level.ChunkPos;
import xaero.common.XaeroMinimapSession;
import xaero.common.minimap.highlight.DimensionHighlighterHandler;
import xaero.common.minimap.write.MinimapWriter;

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
                    int cx = center.x + dx;
                    int cz = center.z + dz;
                    // Xaero caches minimap tiles and only redraws a chunk's highlight
                    // when told to, so only poke it when a chunk is newly marked -
                    // poking it every tick made the whole minimap redraw constantly and lag.
                    if (ExploredStore.markExplored(dimension, cx, cz)) {
                        refreshHighlight(cx, cz);
                        refreshHighlight(cx - 1, cz);
                        refreshHighlight(cx + 1, cz);
                        refreshHighlight(cx, cz - 1);
                        refreshHighlight(cx, cz + 1);
                    }
                }
            }
        });
    }

    private static void refreshHighlight(int chunkX, int chunkZ) {
        XaeroMinimapSession session = XaeroMinimapSession.getCurrentSession();
        if (session == null) return;
        MinimapWriter writer = session.getMinimapProcessor().getMinimapWriter();
        if (writer == null) return;
        DimensionHighlighterHandler handler = writer.getDimensionHighlightHandler();
        if (handler == null) return;
        handler.requestRefresh(chunkX >> 5, chunkZ >> 5);
    }
}
