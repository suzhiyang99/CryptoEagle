package org.apache.spark.util;

import java.io.OutputStream;
import org.apache.commons.lang3.SystemUtils;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.StringOps;
import scala.concurrent.duration.FiniteDuration;
import scala.util.Try$;

public final class Benchmark$ {
   public static final Benchmark$ MODULE$;

   static {
      new Benchmark$();
   }

   public String getProcessorName() {
      String cpu = SystemUtils.IS_OS_MAC_OSX ? Utils$.MODULE$.executeAndGetOutput((Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"/usr/sbin/sysctl", "-n", "machdep.cpu.brand_string"}))), Utils$.MODULE$.executeAndGetOutput$default$2(), Utils$.MODULE$.executeAndGetOutput$default$3(), Utils$.MODULE$.executeAndGetOutput$default$4()) : (SystemUtils.IS_OS_LINUX ? (String)Try$.MODULE$.apply(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            String grepPath = (new StringOps(Predef$.MODULE$.augmentString(Utils$.MODULE$.executeAndGetOutput((Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"which", "grep"}))), Utils$.MODULE$.executeAndGetOutput$default$2(), Utils$.MODULE$.executeAndGetOutput$default$3(), Utils$.MODULE$.executeAndGetOutput$default$4())))).stripLineEnd();
            return (new StringOps(Predef$.MODULE$.augmentString(Utils$.MODULE$.executeAndGetOutput((Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{grepPath, "-m", "1", "model name", "/proc/cpuinfo"}))), Utils$.MODULE$.executeAndGetOutput$default$2(), Utils$.MODULE$.executeAndGetOutput$default$3(), Utils$.MODULE$.executeAndGetOutput$default$4())))).stripLineEnd().replaceFirst("model name[\\s*]:[\\s*]", "");
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "Unknown processor";
         }
      }) : System.getenv("PROCESSOR_IDENTIFIER"));
      return cpu;
   }

   public String getJVMOSInfo() {
      String vmName = System.getProperty("java.vm.name");
      String runtimeVersion = System.getProperty("java.runtime.version");
      String osName = System.getProperty("os.name");
      String osVersion = System.getProperty("os.version");
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " ", " on ", " ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{vmName, runtimeVersion, osName, osVersion}));
   }

   public int $lessinit$greater$default$3() {
      return 2;
   }

   public FiniteDuration $lessinit$greater$default$4() {
      return (new scala.concurrent.duration.package.DurationInt(scala.concurrent.duration.package$.MODULE$.DurationInt(2))).seconds();
   }

   public FiniteDuration $lessinit$greater$default$5() {
      return (new scala.concurrent.duration.package.DurationInt(scala.concurrent.duration.package$.MODULE$.DurationInt(2))).seconds();
   }

   public boolean $lessinit$greater$default$6() {
      return false;
   }

   public Option<OutputStream> $lessinit$greater$default$7() {
      return None$.MODULE$;
   }

   private Benchmark$() {
      MODULE$ = this;
   }
}
