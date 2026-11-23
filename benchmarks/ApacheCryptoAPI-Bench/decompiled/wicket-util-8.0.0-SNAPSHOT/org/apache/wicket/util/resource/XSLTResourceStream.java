package org.apache.wicket.util.resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.time.Time;

public class XSLTResourceStream extends AbstractResourceStream {
   private static final long serialVersionUID = 1L;
   private final transient ByteArrayOutputStream out;

   protected Map<Object, Object> getParameters() {
      return null;
   }

   public XSLTResourceStream(IResourceStream xsltResource, IResourceStream xmlResource) {
      try {
         Source xmlSource = new StreamSource(xmlResource.getInputStream());
         Source xsltSource = new StreamSource(xsltResource.getInputStream());
         this.out = new ByteArrayOutputStream();
         Result result = new StreamResult(this.out);
         TransformerFactory transFact = TransformerFactory.newInstance();
         Transformer trans = transFact.newTransformer(xsltSource);
         Map<Object, Object> parameters = this.getParameters();
         if (parameters != null) {
            for(Map.Entry<Object, Object> e : parameters.entrySet()) {
               trans.setParameter(e.getKey().toString(), e.getValue().toString());
            }
         }

         trans.transform(xmlSource, result);
      } catch (Exception e) {
         throw new RuntimeException(e);
      } finally {
         IOUtils.closeQuietly(xmlResource);
         IOUtils.closeQuietly(xsltResource);
      }

   }

   public void close() throws IOException {
   }

   public String getContentType() {
      return null;
   }

   public InputStream getInputStream() throws ResourceStreamNotFoundException {
      return new ByteArrayInputStream(this.out.toByteArray());
   }

   public Bytes length() {
      return Bytes.bytes((long)this.out.size());
   }

   public Time lastModifiedTime() {
      return null;
   }
}
