package org.apache.openejb.assembler.classic;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class BeansInfo extends InfoObject {
   public final List<String> duplicatedInterceptors = new ArrayList();
   public final List<String> duplicatedDecorators = new ArrayList();
   public final List<String> duplicatedAlternativeClasses = new ArrayList();
   public final List<String> duplicatedAlternativeStereotypes = new ArrayList();
   public final Set<String> startupClasses = new HashSet();
   public String version = "1.1";
   public String discoveryMode;
   public final List<ExclusionEntryInfo> excludes = new LinkedList();
   public final List<BDAInfo> bdas = new LinkedList();
   public final List<BDAInfo> noDescriptorBdas = new LinkedList();

   public static class ExclusionEntryInfo extends InfoObject {
      public String name;
      public ExclusionInfo exclusion;
   }

   public static class BDAInfo extends InfoObject {
      public final Set<String> managedClasses = new HashSet();
      public final List<String> interceptors = new LinkedList();
      public final List<String> decorators = new LinkedList();
      public final List<String> alternatives = new LinkedList();
      public final List<String> stereotypeAlternatives = new LinkedList();
      public String discoveryMode;
      public URI uri;
   }
}
