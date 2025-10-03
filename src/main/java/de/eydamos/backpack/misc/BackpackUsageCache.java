package de.eydamos.backpack.misc;

import java.util.Comparator;
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

    // Lock is only needed for the compound "put-and-evict" operation
    private static final ReadWriteLock cacheLock = new ReentrantReadWriteLock();

    private static class CacheEntry {

        public final BackpackSlotUsageInfo info;
        public final long timestamp;

        public CacheEntry(BackpackSlotUsageInfo info, long timestamp) {
            this.info = info;
            this.timestamp = timestamp;
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
        CacheEntry entry = new CacheEntry(info, currentTime);

        // A write lock is required here to make the "put-and-evict" operation atomic.
        cacheLock.writeLock().lock();
        try {
            cache.put(uuid, entry);

            if (cache.size() > MAX_CACHE_SIZE) {
                evictOldestEntries();
            }
        } finally {
            cacheLock.writeLock().unlock();
        }
    }

    /**
     * Retrieves backpack information from cache. If an entry is found to be expired, it is removed
     *
     * @param uuid The backpack UUID
     * @return BackpackSlotUsageInfo if valid and not expired, null otherwise
     */
    public static BackpackSlotUsageInfo getBackpackInfo(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            return null;
        }

        CacheEntry entry = cache.get(uuid);

        if (entry == null) {
            return null;
        }

        if (entry.isExpired()) {
            invalidate(uuid);
            return null;
        }

        return entry.info;
    }

    /**
     * Requests backpack information from the server if not present in the cache, respecting a throttle to prevent
     * spamming requests.
     *
     * @param uuid The player UUID
     */
    public static void requestBackpackInfo(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            return;
        }

        if (getBackpackInfo(uuid) != null) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        requestTimestamps.compute(uuid, (key, lastRequestTime) -> {
            // Throttle check
            if (lastRequestTime != null && (currentTime - lastRequestTime) < REQUEST_THROTTLE_MS) {
                return lastRequestTime;
            }

            Backpack.packetHandler.networkWrapper.sendToServer(new MessageBackpackInfoRequest(key));
            return currentTime;
        });
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
     * Evicts the oldest entries when cache size exceeds the limit. WARNING: This method must be called from within a
     * write-locked context.
     */
    private static void evictOldestEntries() {
        // This check is a safeguard, but the caller should ensure the lock is held.
        if (cache.size() <= MAX_CACHE_SIZE) {
            return;
        }

        // Find the oldest entries to evict (evict 10% of cache)
        int entriesToEvict = Math.max(1, cache.size() / 10);

        cache.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.comparingLong(e -> e.timestamp)))
                .limit(entriesToEvict).map(Map.Entry::getKey).forEach(key -> {
                    cache.remove(key);
                    requestTimestamps.remove(key);
                });
    }
}
