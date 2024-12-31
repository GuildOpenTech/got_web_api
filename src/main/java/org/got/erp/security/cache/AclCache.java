package org.got.erp.security.cache;

import java.util.function.Function;

public interface AclCache {
    boolean get(String key, Function<String, Boolean> loader);
    void invalidateForUser(Long userId);
    void invalidateAll();
}