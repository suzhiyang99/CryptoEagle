package org.apache.openejb.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class ThreadLocalHttpServletResponse extends AbstractRestThreadLocalProxy<HttpServletResponse> implements HttpServletResponse {
   protected ThreadLocalHttpServletResponse() {
      super(HttpServletResponse.class);
   }

   public void addCookie(Cookie cookie) {
      ((HttpServletResponse)this.get()).addCookie(cookie);
   }

   public void addDateHeader(String name, long date) {
      ((HttpServletResponse)this.get()).addDateHeader(name, date);
   }

   public void addHeader(String name, String value) {
      ((HttpServletResponse)this.get()).addHeader(name, value);
   }

   public void addIntHeader(String name, int value) {
      ((HttpServletResponse)this.get()).addIntHeader(name, value);
   }

   public boolean containsHeader(String name) {
      return ((HttpServletResponse)this.get()).containsHeader(name);
   }

   public String encodeURL(String url) {
      return ((HttpServletResponse)this.get()).encodeURL(url);
   }

   public String encodeRedirectURL(String url) {
      return ((HttpServletResponse)this.get()).encodeRedirectURL(url);
   }

   public String encodeUrl(String url) {
      return ((HttpServletResponse)this.get()).encodeUrl(url);
   }

   public String encodeRedirectUrl(String url) {
      return ((HttpServletResponse)this.get()).encodeRedirectUrl(url);
   }

   public String getHeader(String name) {
      return ((HttpServletResponse)this.get()).getHeader(name);
   }

   public Collection<String> getHeaderNames() {
      return ((HttpServletResponse)this.get()).getHeaderNames();
   }

   public Collection<String> getHeaders(String headerName) {
      return ((HttpServletResponse)this.get()).getHeaders(headerName);
   }

   public int getStatus() {
      return ((HttpServletResponse)this.get()).getStatus();
   }

   public void sendError(int sc) throws IOException {
      ((HttpServletResponse)this.get()).sendError(sc);
   }

   public void sendError(int sc, String msg) throws IOException {
      ((HttpServletResponse)this.get()).sendError(sc, msg);
   }

   public void sendRedirect(String location) throws IOException {
      ((HttpServletResponse)this.get()).sendRedirect(location);
   }

   public void setDateHeader(String name, long date) {
      ((HttpServletResponse)this.get()).setDateHeader(name, date);
   }

   public void setHeader(String name, String value) {
      ((HttpServletResponse)this.get()).setHeader(name, value);
   }

   public void setIntHeader(String name, int value) {
      ((HttpServletResponse)this.get()).setIntHeader(name, value);
   }

   public void setStatus(int sc) {
      ((HttpServletResponse)this.get()).setStatus(sc);
   }

   public void setStatus(int sc, String sm) {
      ((HttpServletResponse)this.get()).setStatus(sc, sm);
   }

   public void flushBuffer() throws IOException {
      ((HttpServletResponse)this.get()).flushBuffer();
   }

   public int getBufferSize() {
      return ((HttpServletResponse)this.get()).getBufferSize();
   }

   public String getCharacterEncoding() {
      return ((HttpServletResponse)this.get()).getCharacterEncoding();
   }

   public String getContentType() {
      return ((HttpServletResponse)this.get()).getContentType();
   }

   public Locale getLocale() {
      return ((HttpServletResponse)this.get()).getLocale();
   }

   public ServletOutputStream getOutputStream() throws IOException {
      return ((HttpServletResponse)this.get()).getOutputStream();
   }

   public PrintWriter getWriter() throws IOException {
      return ((HttpServletResponse)this.get()).getWriter();
   }

   public boolean isCommitted() {
      return ((HttpServletResponse)this.get()).isCommitted();
   }

   public void reset() {
      ((HttpServletResponse)this.get()).reset();
   }

   public void resetBuffer() {
      ((HttpServletResponse)this.get()).resetBuffer();
   }

   public void setBufferSize(int size) {
      ((HttpServletResponse)this.get()).setBufferSize(size);
   }

   public void setCharacterEncoding(String charset) {
      ((HttpServletResponse)this.get()).setCharacterEncoding(charset);
   }

   public void setContentLength(int len) {
      ((HttpServletResponse)this.get()).setContentLength(len);
   }

   public void setContentLengthLong(long length) {
      ((HttpServletResponse)this.get()).setContentLengthLong(length);
   }

   public void setContentType(String type) {
      ((HttpServletResponse)this.get()).setContentType(type);
   }

   public void setLocale(Locale loc) {
      ((HttpServletResponse)this.get()).setLocale(loc);
   }
}
