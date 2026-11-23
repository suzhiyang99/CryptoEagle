package org.apache.tika.mime;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class MediaTypeRegistry implements Serializable {
   private static final long serialVersionUID = 4710974869988895410L;
   private final Map<MediaType, MediaType> registry = new HashMap();
   private final Map<MediaType, MediaType> inheritance = new HashMap();

   public static MediaTypeRegistry getDefaultRegistry() {
      return MimeTypes.getDefaultMimeTypes().getMediaTypeRegistry();
   }

   public SortedSet<MediaType> getTypes() {
      return new TreeSet(this.registry.values());
   }

   public SortedSet<MediaType> getAliases(MediaType type) {
      SortedSet<MediaType> aliases = new TreeSet();

      for(Map.Entry<MediaType, MediaType> entry : this.registry.entrySet()) {
         if (((MediaType)entry.getValue()).equals(type) && !((MediaType)entry.getKey()).equals(type)) {
            aliases.add(entry.getKey());
         }
      }

      return aliases;
   }

   public SortedSet<MediaType> getChildTypes(MediaType type) {
      SortedSet<MediaType> children = new TreeSet();

      for(Map.Entry<MediaType, MediaType> entry : this.inheritance.entrySet()) {
         if (((MediaType)entry.getValue()).equals(type)) {
            children.add(entry.getKey());
         }
      }

      return children;
   }

   public void addType(MediaType type) {
      this.registry.put(type, type);
   }

   public void addAlias(MediaType type, MediaType alias) {
      this.registry.put(alias, type);
   }

   public void addSuperType(MediaType type, MediaType supertype) {
      this.inheritance.put(type, supertype);
   }

   public MediaType normalize(MediaType type) {
      if (type == null) {
         return null;
      } else {
         MediaType canonical = (MediaType)this.registry.get(type.getBaseType());
         if (canonical == null) {
            return type;
         } else {
            return type.hasParameters() ? new MediaType(canonical, type.getParameters()) : canonical;
         }
      }
   }

   public boolean isSpecializationOf(MediaType a, MediaType b) {
      return this.isInstanceOf(this.getSupertype(a), b);
   }

   public boolean isInstanceOf(MediaType a, MediaType b) {
      return a != null && (a.equals(b) || this.isSpecializationOf(a, b));
   }

   public boolean isInstanceOf(String a, MediaType b) {
      return this.isInstanceOf(this.normalize(MediaType.parse(a)), b);
   }

   public MediaType getSupertype(MediaType type) {
      if (type == null) {
         return null;
      } else if (this.inheritance.containsKey(type)) {
         return (MediaType)this.inheritance.get(type);
      } else if (type.hasParameters()) {
         return type.getBaseType();
      } else if (type.getSubtype().endsWith("+xml")) {
         return MediaType.APPLICATION_XML;
      } else if (type.getSubtype().endsWith("+zip")) {
         return MediaType.APPLICATION_ZIP;
      } else if ("text".equals(type.getType()) && !MediaType.TEXT_PLAIN.equals(type)) {
         return MediaType.TEXT_PLAIN;
      } else if (type.getType().contains("empty") && !MediaType.EMPTY.equals(type)) {
         return MediaType.EMPTY;
      } else {
         return !MediaType.OCTET_STREAM.equals(type) ? MediaType.OCTET_STREAM : null;
      }
   }
}
