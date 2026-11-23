package org.spark_project.jetty.http.pathmap;

public enum PathSpecGroup {
   EXACT,
   MIDDLE_GLOB,
   PREFIX_GLOB,
   SUFFIX_GLOB,
   ROOT,
   DEFAULT;
}
