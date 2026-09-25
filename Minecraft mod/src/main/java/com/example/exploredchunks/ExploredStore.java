package com.example.exploredchunks;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Remembers which chunks you have been near, per dimension.
 * Only kept in memory, so it resets when you leave the world or server.
 * Read from Xaero's minimap thread, written from the client thread, hence the concurrent sets.
 */
public final class ExploredStore {
    private static final Map<String, Dimension> dimensions = new ConcurrentHashMap<>();

    private static final class Dimension {
        final Set<Long> chunks = ConcurrentHashMap.newKeySet();
        final Set<Long> regions = ConcurrentHashMap.newKeySet();
    }

    private ExploredStore() {}

    public static void clear() {
        dimensions.clear();
    }

    public static void markExplored(String dimension, int chunkX, int chunkZ) {
        Dimension dim = dimensions.computeIfAbsent(dimension, name -> new Dimension());
        if (dim.chunks.add(key(chunkX, chunkZ))) {
            dim.regions.add(key(chunkX >> 5, chunkZ >> 5));
        }
    }

    public static boolean isExplored(String dimension, int chunkX, int chunkZ) {
        Dimension dim = dimensions.get(dimension);
        return dim != null && dim.chunks.contains(key(chunkX, chunkZ));
    }

    /** Xaero's minimap regions are 32x32 chunks. */
    public static boolean regionHasExplored(String dimension, int regionX, int regionZ) {
        Dimension dim = dimensions.get(dimension);
        return dim != null && dim.regions.contains(key(regionX, regionZ));
    }

    private static long key(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }
}
