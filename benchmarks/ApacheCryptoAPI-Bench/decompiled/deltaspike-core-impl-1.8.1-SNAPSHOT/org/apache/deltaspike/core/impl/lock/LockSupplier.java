package org.apache.deltaspike.core.impl.lock;

import java.util.concurrent.locks.Lock;

interface LockSupplier {
   Lock get();
}
