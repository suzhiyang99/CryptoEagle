package org.spark_project.jetty.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.spark_project.jetty.client.api.Authentication;
import org.spark_project.jetty.client.api.Connection;
import org.spark_project.jetty.client.api.ContentResponse;
import org.spark_project.jetty.client.api.Request;
import org.spark_project.jetty.client.api.Response;
import org.spark_project.jetty.client.api.Result;
import org.spark_project.jetty.client.util.BufferingResponseListener;
import org.spark_project.jetty.http.HttpField;
import org.spark_project.jetty.http.HttpHeader;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public abstract class AuthenticationProtocolHandler implements ProtocolHandler {
   public static final int DEFAULT_MAX_CONTENT_LENGTH = 16384;
   public static final Logger LOG = Log.getLogger(AuthenticationProtocolHandler.class);
   private static final Pattern AUTHENTICATE_PATTERN = Pattern.compile("([^\\s]+)\\s+realm=\"([^\"]*)\"(.*)", 2);
   private final HttpClient client;
   private final int maxContentLength;
   private final ResponseNotifier notifier;

   protected AuthenticationProtocolHandler(HttpClient client, int maxContentLength) {
      this.client = client;
      this.maxContentLength = maxContentLength;
      this.notifier = new ResponseNotifier();
   }

   protected HttpClient getHttpClient() {
      return this.client;
   }

   protected abstract HttpHeader getAuthenticateHeader();

   protected abstract HttpHeader getAuthorizationHeader();

   protected abstract URI getAuthenticationURI(Request var1);

   protected abstract String getAuthenticationAttribute();

   public Response.Listener getResponseListener() {
      return new AuthenticationListener();
   }

   private class AuthenticationListener extends BufferingResponseListener {
      private AuthenticationListener() {
         super(AuthenticationProtocolHandler.this.maxContentLength);
      }

      public void onComplete(Result result) {
         HttpRequest request = (HttpRequest)result.getRequest();
         ContentResponse response = new HttpContentResponse(result.getResponse(), this.getContent(), this.getMediaType(), this.getEncoding());
         if (result.isFailed()) {
            if (AuthenticationProtocolHandler.LOG.isDebugEnabled()) {
               AuthenticationProtocolHandler.LOG.debug("Authentication challenge failed {}", result.getFailure());
            }

            this.forwardFailureComplete(request, result.getRequestFailure(), response, result.getResponseFailure());
         } else {
            String authenticationAttribute = AuthenticationProtocolHandler.this.getAuthenticationAttribute();
            HttpConversation conversation = request.getConversation();
            if (conversation.getAttribute(authenticationAttribute) != null) {
               if (AuthenticationProtocolHandler.LOG.isDebugEnabled()) {
                  AuthenticationProtocolHandler.LOG.debug("Bad credentials for {}", request);
               }

               this.forwardSuccessComplete(request, response);
            } else {
               HttpHeader header = AuthenticationProtocolHandler.this.getAuthenticateHeader();
               List<Authentication.HeaderInfo> headerInfos = this.parseAuthenticateHeader(response, header);
               if (headerInfos.isEmpty()) {
                  if (AuthenticationProtocolHandler.LOG.isDebugEnabled()) {
                     AuthenticationProtocolHandler.LOG.debug("Authentication challenge without {} header", header);
                  }

                  this.forwardFailureComplete(request, (Throwable)null, response, new HttpResponseException("HTTP protocol violation: Authentication challenge without " + header + " header", response));
               } else {
                  Authentication authentication = null;
                  Authentication.HeaderInfo headerInfo = null;
                  URI authURI = this.resolveURI(request, AuthenticationProtocolHandler.this.getAuthenticationURI(request));
                  if (authURI != null) {
                     for(Authentication.HeaderInfo element : headerInfos) {
                        authentication = AuthenticationProtocolHandler.this.client.getAuthenticationStore().findAuthentication(element.getType(), authURI, element.getRealm());
                        if (authentication != null) {
                           headerInfo = element;
                           break;
                        }
                     }
                  }

                  if (authentication == null) {
                     if (AuthenticationProtocolHandler.LOG.isDebugEnabled()) {
                        AuthenticationProtocolHandler.LOG.debug("No authentication available for {}", request);
                     }

                     this.forwardSuccessComplete(request, response);
                  } else {
                     try {
                        Authentication.Result authnResult = authentication.authenticate(request, response, headerInfo, conversation);
                        if (AuthenticationProtocolHandler.LOG.isDebugEnabled()) {
                           AuthenticationProtocolHandler.LOG.debug("Authentication result {}", authnResult);
                        }

                        if (authnResult == null) {
                           this.forwardSuccessComplete(request, response);
                           return;
                        }

                        conversation.setAttribute(authenticationAttribute, true);
                        URI requestURI = request.getURI();
                        String path = null;
                        if (requestURI == null) {
                           requestURI = this.resolveURI(request, (URI)null);
                           path = request.getPath();
                        }

                        Request newRequest = AuthenticationProtocolHandler.this.client.copyRequest(request, requestURI);
                        if (path != null) {
                           newRequest.path(path);
                        }

                        authnResult.apply(newRequest);
                        this.copyIfAbsent(request, newRequest, HttpHeader.AUTHORIZATION);
                        this.copyIfAbsent(request, newRequest, HttpHeader.PROXY_AUTHORIZATION);
                        newRequest.onResponseSuccess((r) -> AuthenticationProtocolHandler.this.client.getAuthenticationStore().addAuthenticationResult(authnResult));
                        Connection connection = (Connection)request.getAttributes().get(Connection.class.getName());
                        if (connection != null) {
                           connection.send(newRequest, (Response.CompleteListener)null);
                        } else {
                           newRequest.send((Response.CompleteListener)null);
                        }
                     } catch (Throwable var16) {
                        if (AuthenticationProtocolHandler.LOG.isDebugEnabled()) {
                           AuthenticationProtocolHandler.LOG.debug("Authentication failed", var16);
                        }

                        this.forwardFailureComplete(request, (Throwable)null, response, var16);
                     }

                  }
               }
            }
         }
      }

      private URI resolveURI(HttpRequest request, URI uri) {
         if (uri != null) {
            return uri;
         } else {
            String target = request.getScheme() + "://" + request.getHost();
            int port = request.getPort();
            if (port > 0) {
               target = target + ":" + port;
            }

            return URI.create(target);
         }
      }

      private void copyIfAbsent(HttpRequest oldRequest, Request newRequest, HttpHeader header) {
         HttpField field = oldRequest.getHeaders().getField(header);
         if (field != null && !newRequest.getHeaders().contains(header)) {
            newRequest.getHeaders().put(field);
         }

      }

      private void forwardSuccessComplete(HttpRequest request, Response response) {
         HttpConversation conversation = request.getConversation();
         conversation.updateResponseListeners((Response.ResponseListener)null);
         AuthenticationProtocolHandler.this.notifier.forwardSuccessComplete(conversation.getResponseListeners(), request, response);
      }

      private void forwardFailureComplete(HttpRequest request, Throwable requestFailure, Response response, Throwable responseFailure) {
         HttpConversation conversation = request.getConversation();
         conversation.updateResponseListeners((Response.ResponseListener)null);
         List<Response.ResponseListener> responseListeners = conversation.getResponseListeners();
         if (responseFailure == null) {
            AuthenticationProtocolHandler.this.notifier.forwardSuccess(responseListeners, response);
         } else {
            AuthenticationProtocolHandler.this.notifier.forwardFailure(responseListeners, response, responseFailure);
         }

         AuthenticationProtocolHandler.this.notifier.notifyComplete(responseListeners, new Result(request, requestFailure, response, responseFailure));
      }

      private List<Authentication.HeaderInfo> parseAuthenticateHeader(Response response, HttpHeader header) {
         List<Authentication.HeaderInfo> result = new ArrayList();

         for(String value : Collections.list(response.getHeaders().getValues(header.asString()))) {
            Matcher matcher = AuthenticationProtocolHandler.AUTHENTICATE_PATTERN.matcher(value);
            if (matcher.matches()) {
               String type = matcher.group(1);
               String realm = matcher.group(2);
               String params = matcher.group(3);
               Authentication.HeaderInfo headerInfo = new Authentication.HeaderInfo(type, realm, params, AuthenticationProtocolHandler.this.getAuthorizationHeader());
               result.add(headerInfo);
            }
         }

         return result;
      }
   }
}
