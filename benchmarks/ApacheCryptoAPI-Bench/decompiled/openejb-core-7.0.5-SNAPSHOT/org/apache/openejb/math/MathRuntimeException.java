package org.apache.openejb.math;

import [Ljava.lang.Object;;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ConcurrentModificationException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import org.apache.openejb.OpenEJBRuntimeException;

public class MathRuntimeException extends RuntimeException {
   private static final long serialVersionUID = -1238983364075381060L;
   private final String pattern;
   private final Object[] arguments;

   public MathRuntimeException(String pattern, Object... arguments) {
      this.pattern = pattern;
      this.arguments = arguments == null ? new Object[0] : (Object[])((Object;)arguments).clone();
   }

   public MathRuntimeException(Throwable rootCause) {
      super(rootCause);
      this.pattern = this.getMessage();
      this.arguments = new Object[0];
   }

   public MathRuntimeException(Throwable rootCause, String pattern, Object... arguments) {
      super(rootCause);
      this.pattern = pattern;
      this.arguments = arguments == null ? new Object[0] : (Object[])((Object;)arguments).clone();
   }

   private static String translate(String s, Locale locale) {
      try {
         ResourceBundle bundle = ResourceBundle.getBundle("org.apache.commons.math.MessagesResources", locale);
         if (bundle.getLocale().getLanguage().equals(locale.getLanguage())) {
            return bundle.getString(s);
         }
      } catch (MissingResourceException var3) {
      }

      return s;
   }

   private static String buildMessage(Locale locale, String pattern, Object... arguments) {
      return pattern == null ? "" : (new MessageFormat(translate(pattern, locale), locale)).format(arguments);
   }

   public String getPattern() {
      return this.pattern;
   }

   public Object[] getArguments() {
      return this.arguments.clone();
   }

   public String getMessage(Locale locale) {
      return buildMessage(locale, this.pattern, this.arguments);
   }

   public String getMessage() {
      return this.getMessage(Locale.US);
   }

   public String getLocalizedMessage() {
      return this.getMessage(Locale.getDefault());
   }

   public void printStackTrace() {
      this.printStackTrace(System.err);
   }

   public void printStackTrace(PrintStream out) {
      synchronized(out) {
         PrintWriter pw = new PrintWriter(out, false);
         this.printStackTrace(pw);
         pw.flush();
      }
   }

   public static ArithmeticException createArithmeticException(final String pattern, final Object... arguments) {
      return new ArithmeticException() {
         private static final long serialVersionUID = 7705628723242533939L;

         public String getMessage() {
            return MathRuntimeException.buildMessage(Locale.US, pattern, arguments);
         }

         public String getLocalizedMessage() {
            return MathRuntimeException.buildMessage(Locale.getDefault(), pattern, arguments);
         }
      };
   }

   public static ArrayIndexOutOfBoundsException createArrayIndexOutOfBoundsException(final String pattern, final Object... arguments) {
      return new ArrayIndexOutOfBoundsException() {
         private static final long serialVersionUID = -3394748305449283486L;

         public String getMessage() {
            return MathRuntimeException.buildMessage(Locale.US, pattern, arguments);
         }

         public String getLocalizedMessage() {
            return MathRuntimeException.buildMessage(Locale.getDefault(), pattern, arguments);
         }
      };
   }

   public static EOFException createEOFException(final String pattern, final Object... arguments) {
      return new EOFException() {
         private static final long serialVersionUID = 279461544586092584L;

         public String getMessage() {
            return MathRuntimeException.buildMessage(Locale.US, pattern, arguments);
         }

         public String getLocalizedMessage() {
            return MathRuntimeException.buildMessage(Locale.getDefault(), pattern, arguments);
         }
      };
   }

   public static IOException createIOException(Throwable rootCause) {
      IOException ioe = new IOException(rootCause.getLocalizedMessage());
      ioe.initCause(rootCause);
      return ioe;
   }

   public static IllegalArgumentException createIllegalArgumentException(final String pattern, final Object... arguments) {
      return new IllegalArgumentException() {
         private static final long serialVersionUID = -6555453980658317913L;

         public String getMessage() {
            return MathRuntimeException.buildMessage(Locale.US, pattern, arguments);
         }

         public String getLocalizedMessage() {
            return MathRuntimeException.buildMessage(Locale.getDefault(), pattern, arguments);
         }
      };
   }

   public static IllegalArgumentException createIllegalArgumentException(Throwable rootCause) {
      IllegalArgumentException iae = new IllegalArgumentException(rootCause.getLocalizedMessage());
      iae.initCause(rootCause);
      return iae;
   }

   public static IllegalStateException createIllegalStateException(final String pattern, final Object... arguments) {
      return new IllegalStateException() {
         private static final long serialVersionUID = -95247648156277208L;

         public String getMessage() {
            return MathRuntimeException.buildMessage(Locale.US, pattern, arguments);
         }

         public String getLocalizedMessage() {
            return MathRuntimeException.buildMessage(Locale.getDefault(), pattern, arguments);
         }
      };
   }

   public static ConcurrentModificationException createConcurrentModificationException(final String pattern, final Object... arguments) {
      return new ConcurrentModificationException() {
         private static final long serialVersionUID = 6134247282754009421L;

         public String getMessage() {
            return MathRuntimeException.buildMessage(Locale.US, pattern, arguments);
         }

         public String getLocalizedMessage() {
            return MathRuntimeException.buildMessage(Locale.getDefault(), pattern, arguments);
         }
      };
   }

   public static NoSuchElementException createNoSuchElementException(final String pattern, final Object... arguments) {
      return new NoSuchElementException() {
         private static final long serialVersionUID = 7304273322489425799L;

         public String getMessage() {
            return MathRuntimeException.buildMessage(Locale.US, pattern, arguments);
         }

         public String getLocalizedMessage() {
            return MathRuntimeException.buildMessage(Locale.getDefault(), pattern, arguments);
         }
      };
   }

   public static NullPointerException createNullPointerException(final String pattern, final Object... arguments) {
      return new NullPointerException() {
         private static final long serialVersionUID = -3075660477939965216L;

         public String getMessage() {
            return MathRuntimeException.buildMessage(Locale.US, pattern, arguments);
         }

         public String getLocalizedMessage() {
            return MathRuntimeException.buildMessage(Locale.getDefault(), pattern, arguments);
         }
      };
   }

   public static ParseException createParseException(int offset, final String pattern, final Object... arguments) {
      return new ParseException((String)null, offset) {
         private static final long serialVersionUID = -1103502177342465975L;

         public String getMessage() {
            return MathRuntimeException.buildMessage(Locale.US, pattern, arguments);
         }

         public String getLocalizedMessage() {
            return MathRuntimeException.buildMessage(Locale.getDefault(), pattern, arguments);
         }
      };
   }

   public static RuntimeException createInternalError(Throwable cause) {
      return new OpenEJBRuntimeException("internal error, please fill a bug report at https://issues.apache.org/jira/browse/MATH", cause);
   }
}
