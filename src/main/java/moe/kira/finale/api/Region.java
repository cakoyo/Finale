package moe.kira.finale.api;

import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Chunk;

import com.google.common.collect.Sets;

public class Region {
    private final Set<Long> chunks = Sets.newHashSet();
    
    public void forEachChunkKey(Consumer<Long> chunKeyConsumer) {
        chunks.forEach(chunKeyConsumer);
    }
    
    public boolean contains(Chunk chunk) {
        return contains(chunk.getChunkKey());
    }
    
    public boolean contains(Long chunkKey) {
        return chunks.contains(chunkKey);
    }
}
