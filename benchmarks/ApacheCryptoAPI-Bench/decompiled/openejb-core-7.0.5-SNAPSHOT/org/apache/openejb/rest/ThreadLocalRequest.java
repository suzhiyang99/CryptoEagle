package org.apache.openejb.rest;

import java.util.Date;
import java.util.List;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

public class ThreadLocalRequest extends AbstractRestThreadLocalProxy<Request> implements Request {
   protected ThreadLocalRequest() {
      super(Request.class);
   }

   public Response.ResponseBuilder evaluatePreconditions(EntityTag eTag) {
      return ((Request)this.get()).evaluatePreconditions(eTag);
   }

   public Response.ResponseBuilder evaluatePreconditions(Date lastModified) {
      return ((Request)this.get()).evaluatePreconditions(lastModified);
   }

   public Response.ResponseBuilder evaluatePreconditions(Date lastModified, EntityTag eTag) {
      return ((Request)this.get()).evaluatePreconditions(lastModified, eTag);
   }

   public Variant selectVariant(List<Variant> vars) throws IllegalArgumentException {
      return ((Request)this.get()).selectVariant(vars);
   }

   public String getMethod() {
      return ((Request)this.get()).getMethod();
   }

   public Response.ResponseBuilder evaluatePreconditions() {
      return ((Request)this.get()).evaluatePreconditions();
   }
}
