package de.eydamos.backpack.misc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.eydamos.backpack.Backpack;
import de.eydamos.backpack.network.message.MessageBackpackInfoRequest;

@SideOnly(Side.CLIENT)
public class BackpackUsageCache {

    private static final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private static final Map<String, Long> requestTimestamps = new ConcurrentHashMap<>();

    // Configuration constants
    private static final long CACHE_DURATION_MS = 60000; // 1 minute
    private static final long REQUEST_THROTTLE_MS = 1000; // 1 second throttle between requests
    private static final int MAX_CACHE_SIZE = 100; // Prevent memory leaks

    private static final ReadWriteLock cacheLock = new ReentrantReadWriteLock();

    private static class CacheEntry {

        public final BackpackSlotUsageInfo info;
        public final long timestamp;
        public final boolean isValid;

        public CacheEntry(BackpackSlotUsageInfo info, long timestamp, boolean isValid) {
            this.info = info;
            this.timestamp = timestamp;
            this.isValid = isValid;
        }

        public boolean isExpired() {
            return (System.currentTimeMillis() - timestamp) > CACHE_DURATION_MS;
        }
    }

    public static class BackpackSlotUsageInfo {

        public final int usedSlots;
        public final int totalSlots;

        public BackpackSlotUsageInfo(int used, int total) {
            if (used < 0 || total < 0 || used > total) {
                throw new IllegalArgumentException("Invalid backpack slot values: used=" + used + ", total=" + total);
            }
            this.usedSlots = used;
            this.totalSlots = total;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            BackpackSlotUsageInfo that = (BackpackSlotUsageInfo) obj;
            return usedSlots == that.usedSlots && totalSlots == that.totalSlots;
        }

        @Override
        public int hashCode() {
            return 31 * usedSlots + totalSlots;
        }

        @Override
        public String toString() {
            return "BackpackInfo{used=" + usedSlots + ", total=" + totalSlots + "}";
        }
    }

    /**
     * Updates backpack information in the cache
     * 
     * @param uuid  The backpack UUID
     * @param used  Number of used slots
     * @param total Total number of slots
     */
    public static void updateBackpackInfo(String uuid, int used, int total) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new IllegalArgumentException("UUID cannot be null or empty");
        }

        BackpackSlotUsageInfo info = new BackpackSlotUsageInfo(used, total);
        long currentTime = System.currentTimeMillis();
        CacheEntry entry = new CacheEntry(info, currentTime, true);

        cacheLock.readLock().lock();
        try {
            cache.put(uuid, entry);

            if (cache.size() > MAX_CACHE_SIZE) {
                evictOldestEntries();
            }
        } finally {
            cacheLock.readLock().unlock();
        }
    }

    /**
     * Retrieves backpack information from cache
     * 
     * @param uuid The backpack UUID
     * @return BackpackInfo if valid and not expired, null otherwise
     */
    public static BackpackSlotUsageInfo getBackpackInfo(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            return null;
        }

        CacheEntry entry = cache.get(uuid);
        if (entry == null || !entry.isValid || entry.isExpired()) {
            if (entry != null && entry.isExpired()) {
                invalidate(uuid);
            }
            return null;
        }

        return entry.info;
    }

    /**
     * Requests backpack information with throttling
     *
     * @param uuid The player UUID
     */
    public static void requestBackpackInfo(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            throw new IllegalArgumentException("UUID cannot be null or empty");
        }

        long currentTime = System.currentTimeMillis();
        Long lastRequestTime = requestTimestamps.get(uuid);

        // Check throttling
        if (lastRequestTime != null && (currentTime - lastRequestTime) < REQUEST_THROTTLE_MS) {
            return;
        }

        BackpackSlotUsageInfo cachedInfo = getBackpackInfo(uuid);
        if (cachedInfo != null) {
            return;
        }

        Backpack.packetHandler.networkWrapper.sendToServer(new MessageBackpackInfoRequest(uuid));
        requestTimestamps.put(uuid, currentTime);

    }

    /**
     * Invalidates a specific UUID from the cache
     * 
     * @param uuid The player UUID to invalidate
     */
    public static void invalidate(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            return;
        }

        cache.remove(uuid);
        requestTimestamps.remove(uuid);
    }

    /**
     * Evicts oldest entries when cache size exceeds limit
     */
    private static void evictOldestEntries() {
        cacheLock.writeLock().lock();
        try {
            if (cache.size() <= MAX_CACHE_SIZE) {
                return;
            }

            // Find oldest entries to evict (evict 10% of cache)
            int entriesToEvict = Math.max(1, cache.size() / 10);

            cache.entrySet().stream().sorted((e1, e2) -> Long.compare(e1.getValue().timestamp, e2.getValue().timestamp))
                    .limit(entriesToEvict).map(Map.Entry::getKey).forEach(key -> {
                        cache.remove(key);
                        requestTimestamps.remove(key);
                    });

        } finally {
            cacheLock.writeLock().unlock();
        }
    }
}
