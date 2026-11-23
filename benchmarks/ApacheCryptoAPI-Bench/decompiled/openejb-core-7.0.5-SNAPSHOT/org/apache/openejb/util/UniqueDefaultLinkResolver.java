package org.apache.openejb.util;

import java.net.URI;

public class UniqueDefaultLinkResolver<E> extends LinkResolver<E> {
   public E resolveLink(String link, URI moduleUri) {
      return (E)(link != null && link.length() != 0 ? super.resolveLink(link, moduleUri) : this.getUniqueMember());
   }
}
