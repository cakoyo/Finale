package moe.kira.finale.api;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.annotation.concurrent.NotThreadSafe;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import com.google.common.collect.Maps;

@NotThreadSafe
public abstract class RegionAPI {
    private static final Map<Long, Region> regions = Maps.newHashMap();
    
    public static Region put(Region region) {
        region.forEachChunkKey((Long chunkKey) -> regions.put(chunkKey, region));
        return region;
    }
    
    public static Optional<Region> get(Entity entity) {
        return get(entity.getChunk());
    }
    
    public static Optional<Region> get(Block block) {
        return get(block.getChunk());
    }
    
    public static Optional<Region> get(Location location) {
        return get(location.getChunk());
    }
    
    public static Optional<Region> get(Chunk chunk) {
        return get(chunk.getChunkKey());
    }
    
    public static Optional<Region> get(Long chunkKey) {
        for (Entry<Long, Region> regionEntry : regions.entrySet())
            if (regionEntry.getKey().equals(chunkKey))
                return Optional.of(regionEntry.getValue());
        
        return Optional.empty();
    }
}
