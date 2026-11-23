package org.apache.openejb.rest;

import java.net.URI;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class ThreadLocalUriInfo extends AbstractRestThreadLocalProxy<UriInfo> implements UriInfo {
   protected ThreadLocalUriInfo() {
      super(UriInfo.class);
   }

   public URI getAbsolutePath() {
      return ((UriInfo)this.get()).getAbsolutePath();
   }

   public UriBuilder getAbsolutePathBuilder() {
      return ((UriInfo)this.get()).getAbsolutePathBuilder();
   }

   public URI getBaseUri() {
      return ((UriInfo)this.get()).getBaseUri();
   }

   public UriBuilder getBaseUriBuilder() {
      return ((UriInfo)this.get()).getBaseUriBuilder();
   }

   public String getPath() {
      return ((UriInfo)this.get()).getPath();
   }

   public String getPath(boolean decode) {
      return ((UriInfo)this.get()).getPath(decode);
   }

   public List<PathSegment> getPathSegments() {
      return ((UriInfo)this.get()).getPathSegments();
   }

   public List<PathSegment> getPathSegments(boolean decode) {
      return ((UriInfo)this.get()).getPathSegments(decode);
   }

   public MultivaluedMap<String, String> getQueryParameters() {
      return ((UriInfo)this.get()).getQueryParameters();
   }

   public MultivaluedMap<String, String> getQueryParameters(boolean decode) {
      return ((UriInfo)this.get()).getQueryParameters(decode);
   }

   public URI getRequestUri() {
      return ((UriInfo)this.get()).getRequestUri();
   }

   public UriBuilder getRequestUriBuilder() {
      return ((UriInfo)this.get()).getRequestUriBuilder();
   }

   public MultivaluedMap<String, String> getPathParameters() {
      return ((UriInfo)this.get()).getPathParameters();
   }

   public MultivaluedMap<String, String> getPathParameters(boolean decode) {
      return ((UriInfo)this.get()).getPathParameters(decode);
   }

   public List<Object> getMatchedResources() {
      return ((UriInfo)this.get()).getMatchedResources();
   }

   public URI resolve(URI uri) {
      return ((UriInfo)this.get()).resolve(uri);
   }

   public URI relativize(URI uri) {
      return ((UriInfo)this.get()).relativize(uri);
   }

   public List<String> getMatchedURIs() {
      return ((UriInfo)this.get()).getMatchedURIs();
   }

   public List<String> getMatchedURIs(boolean decode) {
      return ((UriInfo)this.get()).getMatchedURIs(decode);
   }
}
