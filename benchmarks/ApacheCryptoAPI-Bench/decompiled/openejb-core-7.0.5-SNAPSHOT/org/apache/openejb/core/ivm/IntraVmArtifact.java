package org.apache.openejb.core.ivm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.List;

public class IntraVmArtifact implements Externalizable {
   private static final Handles staticHandles = new Handles() {
      public synchronized int add(Object obj) {
         return super.add(obj);
      }

      public synchronized Object get(int id) {
         return super.get(id);
      }
   };
   private static final ThreadLocal<Handles> threadHandles = new ThreadLocal<Handles>() {
      protected Handles initialValue() {
         return new Handles();
      }
   };
   private static final String NO_ARTIFACT_ERROR = "The artifact this object represents could not be found.";
   private int instanceHandle;
   private boolean staticArtifact;

   public IntraVmArtifact(Object obj) {
      this(obj, false);
   }

   public IntraVmArtifact(Object obj, boolean storeStatically) {
      this.staticArtifact = storeStatically;
      Handles handles = getHandles(storeStatically);
      this.instanceHandle = handles.add(obj);
   }

   private static Handles getHandles(boolean staticArtifact) {
      return staticArtifact ? staticHandles : (Handles)threadHandles.get();
   }

   public IntraVmArtifact() {
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeBoolean(this.staticArtifact);
      out.write(this.instanceHandle);
   }

   public void readExternal(ObjectInput in) throws IOException {
      this.staticArtifact = in.readBoolean();
      this.instanceHandle = in.read();
   }

   protected Object readResolve() throws ObjectStreamException {
      Handles handles = getHandles(this.staticArtifact);
      Object artifact = handles.get(this.instanceHandle);
      if (artifact == null) {
         throw new InvalidObjectException("The artifact this object represents could not be found." + this.instanceHandle);
      } else {
         return artifact;
      }
   }

   private static class Handles {
      private final List<Object> list;

      private Handles() {
         this.list = new ArrayList();
      }

      public int add(Object obj) {
         int id = this.list.size();
         this.list.add(obj);
         return id;
      }

      public Object get(int id) {
         Object obj = this.list.get(id);
         if (this.list.size() == id + 1) {
            this.list.clear();
         }

         return obj;
      }
   }
}
