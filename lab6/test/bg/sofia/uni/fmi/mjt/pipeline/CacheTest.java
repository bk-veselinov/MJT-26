package bg.sofia.uni.fmi.mjt.pipeline;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CacheTest {

    private Cache cache;

    @BeforeEach
    void setUp() {
        cache = new Cache();
    }

    @Test
    void testCacheValueWithNullKey() {
        assertThrows(IllegalArgumentException.class, () -> cache.cacheValue(null, 1),
                "calling cachedValue with key=null must throw IllegalArgumentException");
    }

    @Test
    void testCacheValueWithNullValue() {
        assertThrows(IllegalArgumentException.class, () -> cache.cacheValue(1, null),
                "calling cachedValue with value=null must throw IllegalArgumentException");
    }

    @Test
    void testCacheValueActualData() {
        cache.cacheValue(1, 1);

        assertTrue(cache.containsKey(1),
                "caching value must save it");
    }

    @Test
    void testCacheValueRecachingValueWithExistingKey() {
        cache.cacheValue(1, 1);
        cache.cacheValue(1, 2);

        assertEquals(2, cache.getCachedValue(1),
                "recaching value with existing key must overwrite the existing value");
    }

    @Test
    void testGetCachedValueWithNullKey() {
        assertThrows(IllegalArgumentException.class, () -> cache.getCachedValue(null),
                "calling getCachedValue with null must throw IllegalArgumentException");
    }

    @Test
    void testGetCachedValueWithNonExistentKey() {
        assertNull(cache.getCachedValue(1),
                "calling getCachedValue with non-existent key must return null");
    }

    @Test
    void testGetCachedValueWithExistingKey() {
        cache.cacheValue(1, 3);

        assertEquals(3, cache.getCachedValue(1),
                "calling getCachedValue with added cached data must return the same value");
    }

    @Test
    void testContainsKeyWithNullKey() {
        assertThrows(IllegalArgumentException.class, () -> cache.containsKey(null),
                "calling containsKey with null must throw IllegalArgumentException");
    }

    @Test
    void testContainsKeyWithNonExistentKey() {
        cache.cacheValue(1, 1);

        assertFalse(cache.containsKey(2),
                "calling containsKey with a non-cached key must return false");
    }

    @Test
    void testContainsKeyOnEmptyCache() {
        assertFalse(cache.containsKey(1),
                "calling containsKey on an empty catch must return false");

    }

    @Test
    void testContainsKeyWithAddedKey() {
        cache.cacheValue(1, 1);

        assertTrue(cache.containsKey(1),
                "calling containsKey with cached key must return true");

    }

    @Test
    void testClearOnEmptyCache() {
        cache.clear();

        assertTrue(cache.isEmpty(),
                "using clear on an empty cache must leave the cache empty");
    }

    @Test
    void testClearOnNonEmptyCache() {
        cache.cacheValue(1, 1);
        cache.clear();

        assertTrue(cache.isEmpty(),
                "using clear on an non-empty cache must remove all cached values");
    }

    @Test
    void testIsEmptyWithNonEmptyCache() {
        cache.cacheValue(1, 1);

        assertFalse(cache.isEmpty(),
                "when isEmpty is called on an non-empty cache, it should return false");
    }

    @Test
    void testIsEmptyWithEmptyCache() {
        assertTrue(cache.isEmpty(),
                "when isEmpty is called on an empty cache objects, it should return true");
    }
}