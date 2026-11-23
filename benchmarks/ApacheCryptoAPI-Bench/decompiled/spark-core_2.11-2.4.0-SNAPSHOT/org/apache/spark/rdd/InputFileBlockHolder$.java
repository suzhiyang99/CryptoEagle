package org.apache.spark.rdd;

import org.apache.spark.unsafe.types.UTF8String;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.runtime.BoxesRunTime;

public final class InputFileBlockHolder$ {
   public static final InputFileBlockHolder$ MODULE$;
   private final InheritableThreadLocal<InputFileBlockHolder.FileBlock> inputBlock;

   static {
      new InputFileBlockHolder$();
   }

   public UTF8String getInputFilePath() {
      return ((InputFileBlockHolder.FileBlock)this.inputBlock.get()).filePath();
   }

   public long getStartOffset() {
      return ((InputFileBlockHolder.FileBlock)this.inputBlock.get()).startOffset();
   }

   public long getLength() {
      return ((InputFileBlockHolder.FileBlock)this.inputBlock.get()).length();
   }

   public void set(String filePath, long startOffset, long length) {
      Predef$.MODULE$.require(filePath != null, new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "filePath cannot be null";
         }
      });
      Predef$.MODULE$.require(startOffset >= 0L, new Serializable(startOffset) {
         public static final long serialVersionUID = 0L;
         private final long startOffset$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"startOffset (", ") cannot be negative"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.startOffset$1)}));
         }

         public {
            this.startOffset$1 = startOffset$1;
         }
      });
      Predef$.MODULE$.require(length >= 0L, new Serializable(length) {
         public static final long serialVersionUID = 0L;
         private final long length$1;

         public final String apply() {
            return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"length (", ") cannot be negative"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.length$1)}));
         }

         public {
            this.length$1 = length$1;
         }
      });
      this.inputBlock.set(new InputFileBlockHolder.FileBlock(UTF8String.fromString(filePath), startOffset, length));
   }

   public void unset() {
      this.inputBlock.remove();
   }

   private InputFileBlockHolder$() {
      MODULE$ = this;
      this.inputBlock = new InheritableThreadLocal<InputFileBlockHolder.FileBlock>() {
         public InputFileBlockHolder.FileBlock initialValue() {
            return new InputFileBlockHolder.FileBlock();
         }
      };
   }
}
