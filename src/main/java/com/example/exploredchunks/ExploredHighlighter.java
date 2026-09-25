package com.example.exploredchunks;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xaero.common.minimap.highlight.ChunkHighlighter;
import xaero.hud.minimap.info.render.compile.InfoDisplayCompiler;

/**
 * Tells Xaero's Minimap to tint nearby chunks red, with a brighter edge
 * where a marked chunk borders an unmarked one.
 */
public class ExploredHighlighter extends ChunkHighlighter {
    // Colors are 0xRRGGBBAA; the last byte is how opaque the tint is.
    private static final int FILL = 0xFF3030_50;
    private static final int EDGE = 0xFF3030_C0;

    public ExploredHighlighter() {
        super(false);
    }

    @Override
    public boolean regionHasHighlights(ResourceKey<Level> dimension, int regionX, int regionZ) {
        return ExploredStore.regionHasExplored(dimension.toString(), regionX, regionZ);
    }

    @Override
    public boolean chunkIsHighlit(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        return ExploredStore.isExplored(dimension.toString(), chunkX, chunkZ);
    }

    @Override
    protected int[] getColors(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        String dim = dimension.toString();
        if (!ExploredStore.isExplored(dim, chunkX, chunkZ)) return null;
        resultStore[0] = FILL;
        resultStore[1] = edge(dim, chunkX, chunkZ - 1); // top
        resultStore[2] = edge(dim, chunkX + 1, chunkZ); // right
        resultStore[3] = edge(dim, chunkX, chunkZ + 1); // bottom
        resultStore[4] = edge(dim, chunkX - 1, chunkZ); // left
        return resultStore;
    }

    private static int edge(String dim, int neighbourX, int neighbourZ) {
        return ExploredStore.isExplored(dim, neighbourX, neighbourZ) ? FILL : EDGE;
    }

    @Override
    public void addChunkHighlightTooltips(InfoDisplayCompiler compiler, ResourceKey<Level> dimension, int chunkX, int chunkZ, int width) {
    }
}
