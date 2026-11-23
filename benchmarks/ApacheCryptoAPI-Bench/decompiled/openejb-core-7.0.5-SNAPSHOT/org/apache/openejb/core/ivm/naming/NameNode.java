package org.apache.openejb.core.ivm.naming;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import org.apache.openejb.OpenEJBRuntimeException;

public class NameNode implements Serializable {
   private final String atomicName;
   private final int atomicHash;
   private NameNode lessTree;
   private NameNode grtrTree;
   private NameNode subTree;
   private NameNode parentTree;
   private final NameNode parent;
   private Object myObject;
   private transient IvmContext myContext;
   private boolean unbound;
   private boolean subTreeUnbound;

   public NameNode(NameNode parent, ParsedName name, Object obj, NameNode parentTree) {
      this.atomicName = name.getComponent();
      this.atomicHash = name.getComponentHashCode();
      this.parent = parent;
      this.parentTree = parentTree;
      if (name.next()) {
         this.subTree = new NameNode(this, name, obj, this);
      } else if (obj instanceof Context) {
         this.myObject = new Federation();
         ((Federation)this.myObject).add((Context)obj);
      } else {
         this.myObject = obj;
      }

   }

   void setMyContext(IvmContext myContext) {
      this.myContext = myContext;
   }

   public Object getBinding() {
      return this.getBinding(false);
   }

   public Object getBinding(boolean createReadOnlyContext) {
      if (this.myObject != null && !(this.myObject instanceof Federation)) {
         return this.myObject;
      } else {
         if (this.myContext == null) {
            this.myContext = new IvmContext(this, createReadOnlyContext);
         }

         return this.myContext;
      }
   }

   public Object getObject() {
      return this.myObject;
   }

   public Object resolve(ParsedName name) throws NameNotFoundException {
      return this.resolve(name, false);
   }

   public Object resolve(ParsedName name, boolean createReadOnlyContext) throws NameNotFoundException {
      int compareResult = name.compareTo(this.atomicHash);
      NameNotFoundException n = null;
      int pos = name.getPos();
      if (compareResult == 0 && name.getComponent().equals(this.atomicName)) {
         if (name.next()) {
            if (this.subTree != null) {
               try {
                  return this.subTree.resolve(name, createReadOnlyContext);
               } catch (NameNotFoundException e) {
                  n = e;
               }
            } else if (!this.subTreeUnbound && !this.unbound && this.myContext != null && !Federation.class.isInstance(this.myObject)) {
               try {
                  return this.myContext.mynode.resolve(name, createReadOnlyContext);
               } catch (NameNotFoundException e) {
                  n = e;
               }
            }
         } else if (!this.unbound) {
            return this.getBinding(createReadOnlyContext);
         }
      } else if (compareResult == -1) {
         if (this.lessTree != null) {
            return this.lessTree.resolve(name, createReadOnlyContext);
         }
      } else if (this.grtrTree != null) {
         return this.grtrTree.resolve(name, createReadOnlyContext);
      }

      if (this.myObject instanceof Federation) {
         name.reset(pos);
         String nameInContext = compareResult != 0 ? name.path() : name.remaining().path();
         Federation f = null;

         for(Context c : (Federation)this.myObject) {
            try {
               Object o = c.lookup(nameInContext);
               if (!(o instanceof Context)) {
                  return o;
               }

               if (f == null) {
                  f = new Federation();
               }

               f.add((Context)o);
            } catch (javax.naming.NamingException var11) {
            }
         }

         if (f != null) {
            NameNode node = new NameNode((NameNode)null, new ParsedName(""), f, (NameNode)null);
            return new IvmContext(node, createReadOnlyContext);
         }
      }

      if (n != null) {
         throw n;
      } else {
         throw new NameNotFoundException("Cannot resolve " + name);
      }
   }

   public void bind(ParsedName name, Object obj) throws NameAlreadyBoundException {
      int compareResult = name.compareTo(this.atomicHash);
      if (compareResult == 0 && name.getComponent().equals(this.atomicName)) {
         if (name.next()) {
            if (this.myObject != null && !(this.myObject instanceof Federation)) {
               throw new NameAlreadyBoundException();
            }

            if (this.subTree == null) {
               this.subTree = new NameNode(this, name, obj, this);
               this.subTreeUnbound = false;
            } else {
               this.subTree.bind(name, obj);
            }
         } else if (obj instanceof Context) {
            if (this.myObject != null) {
               if (!(this.myObject instanceof Federation)) {
                  throw new NameAlreadyBoundException(name.toString());
               }
            } else {
               this.myObject = new Federation();
            }

            ((Federation)this.myObject).add((Context)obj);
         } else {
            if (this.subTree != null) {
               throw new NameAlreadyBoundException(name.toString());
            }

            if (this.myObject != null) {
               throw new NameAlreadyBoundException(name.toString());
            }

            this.unbound = false;
            this.myObject = obj;
         }
      } else if (compareResult == -1) {
         if (this.lessTree == null) {
            this.lessTree = new NameNode(this.parent, name, obj, this);
         } else {
            this.lessTree.bind(name, obj);
         }
      } else if (this.grtrTree == null) {
         this.grtrTree = new NameNode(this.parent, name, obj, this);
      } else {
         this.grtrTree.bind(name, obj);
      }

   }

   public void tree(String indent, PrintStream out) {
      out.println(this.atomicName + " @ " + this.atomicHash + (this.myObject != null ? " [" + this.myObject + "]" : ""));
      if (this.grtrTree != null) {
         out.print(indent + " + ");
         this.grtrTree.tree(indent + "    ", out);
      }

      if (this.lessTree != null) {
         out.print(indent + " - ");
         this.lessTree.tree(indent + "    ", out);
      }

      if (this.subTree != null) {
         out.print(indent + " - ");
         this.subTree.tree(indent + "    ", out);
      }

   }

   public int compareTo(int otherHash) {
      if (this.atomicHash == otherHash) {
         return 0;
      } else {
         return this.atomicHash > otherHash ? 1 : -1;
      }
   }

   private void bind(NameNode node) {
      int compareResult = node.compareTo(this.atomicHash);
      if (node.parent == this) {
         compareResult = 0;
      }

      if (compareResult == 0) {
         if (this.subTree == null) {
            this.subTree = node;
            this.subTreeUnbound = false;
            this.subTree.parentTree = this;
         } else {
            this.subTree.bind(node);
         }
      } else if (compareResult == -1) {
         if (this.lessTree == null) {
            this.lessTree = node;
            this.lessTree.parentTree = this;
         } else {
            this.lessTree.bind(node);
         }
      } else if (this.grtrTree == null) {
         this.grtrTree = node;
         this.grtrTree.parentTree = this;
      } else {
         this.grtrTree.bind(node);
      }

   }

   public void unbind(ParsedName name) throws NameAlreadyBoundException {
      int compareResult = name.compareTo(this.atomicHash);
      if (compareResult == 0 && name.getComponent().equals(this.atomicName)) {
         if (name.next()) {
            if (this.subTree != null) {
               this.subTree.unbind(name);
            }
         } else {
            this.unbound = true;
            this.myObject = null;
            this.parentTree.unbind(this);
         }
      } else if (compareResult == -1) {
         if (this.lessTree != null) {
            this.lessTree.unbind(name);
         }
      } else if (this.grtrTree != null) {
         this.grtrTree.unbind(name);
      }

   }

   private void unbind(NameNode node) {
      if (this.subTree == node) {
         this.subTree = null;
         this.subTreeUnbound = true;
      } else if (this.grtrTree == node) {
         this.grtrTree = null;
      } else if (this.lessTree == node) {
         this.lessTree = null;
      }

      this.rebalance(node);
   }

   void setReadOnly(boolean isReadOnly) {
      if (this.myContext != null) {
         this.myContext.readOnly = isReadOnly;
      }

      if (this.myObject instanceof Federation) {
         for(Context current : (Federation)this.myObject) {
            if (IvmContext.class.isInstance(current)) {
               ((IvmContext)IvmContext.class.cast(current)).setReadOnly(isReadOnly);
            }
         }
      }

      if (this.subTree != null) {
         this.subTree.setReadOnly(isReadOnly);
      }

      if (this.lessTree != null) {
         this.lessTree.setReadOnly(isReadOnly);
      }

      if (this.grtrTree != null) {
         this.grtrTree.setReadOnly(isReadOnly);
      }

   }

   private void rebalance(NameNode node) {
      if (node.subTree != null) {
         this.bind(node.subTree);
      }

      if (node.lessTree != null) {
         this.bind(node.lessTree);
      }

      if (node.grtrTree != null) {
         this.bind(node.grtrTree);
      }

   }

   protected void prune() {
      this.prune(this);
   }

   private void prune(NameNode until) {
      if (this.subTree != null) {
         this.subTree.prune(until);
      }

      if (this.lessTree != null) {
         this.lessTree.prune(until);
      }

      if (this.grtrTree != null) {
         this.grtrTree.prune(until);
      }

      if (this != until) {
         if (!this.hasChildren() && this.myObject == null) {
            this.parentTree.unbind(this);
         }

      }
   }

   private boolean hasChildren() {
      return this.hasChildren(this);
   }

   private boolean hasChildren(NameNode node) {
      if (this.subTree != null && this.subTree.hasChildren(node)) {
         return true;
      } else if (this.grtrTree != null && this.grtrTree.hasChildren(node)) {
         return true;
      } else if (this.lessTree != null && this.lessTree.hasChildren(node)) {
         return true;
      } else {
         return this.parent == node;
      }
   }

   protected void clearCache() {
      if (this.myContext != null) {
         this.myContext.fastCache.clear();
      }

      if (this.grtrTree != null) {
         this.grtrTree.clearCache();
      }

      if (this.lessTree != null) {
         this.lessTree.clearCache();
      }

      if (this.subTree != null) {
         this.subTree.clearCache();
      }

   }

   public IvmContext createSubcontext(ParsedName name) throws NameAlreadyBoundException {
      return this.createSubcontext(name, false);
   }

   public IvmContext createSubcontext(ParsedName name, boolean createReadOnlyContext) throws NameAlreadyBoundException {
      try {
         this.bind(name, (Object)null);
         name.reset();
         return (IvmContext)this.resolve(name, createReadOnlyContext);
      } catch (NameNotFoundException exception) {
         exception.printStackTrace();
         throw new OpenEJBRuntimeException(exception);
      }
   }

   public String getAtomicName() {
      return this.atomicName;
   }

   public NameNode getLessTree() {
      return this.lessTree;
   }

   public NameNode getGrtrTree() {
      return this.grtrTree;
   }

   public NameNode getSubTree() {
      return this.subTree;
   }

   public NameNode getParent() {
      return this.parent;
   }

   public NameNode getParentTree() {
      return this.parentTree;
   }

   public String toString() {
      return "NameNode{atomicName='" + this.atomicName + '\'' + ", atomicHash=" + this.atomicHash + ", lessTree=" + (this.lessTree != null ? this.lessTree.atomicName : "null") + ", grtrTree=" + (this.grtrTree != null ? this.grtrTree.atomicName : "null") + ", subTree=" + (this.subTree != null ? this.subTree.atomicName : "null") + ", parentTree=" + (this.parentTree != null ? this.parentTree.atomicName : "null") + ", parent=" + (this.parent != null ? this.parent.atomicName : "null") + ", myObject=" + this.myObject + ", myContext=" + this.myContext + ", unbound=" + this.unbound + '}';
   }

   public static class Federation extends ArrayList<Context> {
   }
}
