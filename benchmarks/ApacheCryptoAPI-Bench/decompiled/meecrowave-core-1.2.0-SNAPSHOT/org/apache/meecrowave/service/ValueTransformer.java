package org.apache.meecrowave.service;

import java.util.function.Function;

public interface ValueTransformer extends Function<String, String> {
   String name();
}
