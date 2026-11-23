package org.apache.activemq.artemis.api.core;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ActiveMQExceptionType {
   INTERNAL_ERROR(0) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQInternalErrorException(msg);
      }
   },
   UNSUPPORTED_PACKET(1) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQUnsupportedPacketException(msg);
      }
   },
   NOT_CONNECTED(2) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQNotConnectedException(msg);
      }
   },
   CONNECTION_TIMEDOUT(3) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQConnectionTimedOutException(msg);
      }
   },
   DISCONNECTED(4) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQDisconnectedException(msg);
      }
   },
   UNBLOCKED(5) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQUnBlockedException(msg);
      }
   },
   IO_ERROR(6) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQIOErrorException(msg);
      }
   },
   QUEUE_DOES_NOT_EXIST(100) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQNonExistentQueueException(msg);
      }
   },
   QUEUE_EXISTS(101) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQQueueExistsException(msg);
      }
   },
   OBJECT_CLOSED(102) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQObjectClosedException(msg);
      }
   },
   INVALID_FILTER_EXPRESSION(103) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQInvalidFilterExpressionException(msg);
      }
   },
   ILLEGAL_STATE(104) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQIllegalStateException(msg);
      }
   },
   SECURITY_EXCEPTION(105) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQSecurityException(msg);
      }
   },
   ADDRESS_DOES_NOT_EXIST(106) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQAddressDoesNotExistException(msg);
      }
   },
   ADDRESS_EXISTS(107) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQAddressExistsException(msg);
      }
   },
   INCOMPATIBLE_CLIENT_SERVER_VERSIONS(108) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQIncompatibleClientServerException(msg);
      }
   },
   LARGE_MESSAGE_ERROR_BODY(110) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQLargeMessageException(msg);
      }
   },
   TRANSACTION_ROLLED_BACK(111) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQTransactionRolledBackException(msg);
      }
   },
   SESSION_CREATION_REJECTED(112) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQSessionCreationException(msg);
      }
   },
   DUPLICATE_ID_REJECTED(113) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQDuplicateIdException(msg);
      }
   },
   DUPLICATE_METADATA(114) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQDuplicateMetaDataException(msg);
      }
   },
   TRANSACTION_OUTCOME_UNKNOWN(115) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQTransactionOutcomeUnknownException(msg);
      }
   },
   ALREADY_REPLICATING(116) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQAlreadyReplicatingException(msg);
      }
   },
   INTERCEPTOR_REJECTED_PACKET(117) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQInterceptorRejectedPacketException(msg);
      }
   },
   INVALID_TRANSIENT_QUEUE_USE(118) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQInvalidTransientQueueUseException(msg);
      }
   },
   REMOTE_DISCONNECT(119) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQRemoteDisconnectException(msg);
      }
   },
   TRANSACTION_TIMEOUT(120) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQTransactionTimeoutException(msg);
      }
   },
   GENERIC_EXCEPTION(999),
   NATIVE_ERROR_INTERNAL(200),
   NATIVE_ERROR_INVALID_BUFFER(201),
   NATIVE_ERROR_NOT_ALIGNED(202),
   NATIVE_ERROR_CANT_INITIALIZE_AIO(203),
   NATIVE_ERROR_CANT_RELEASE_AIO(204),
   NATIVE_ERROR_CANT_OPEN_CLOSE_FILE(205),
   NATIVE_ERROR_CANT_ALLOCATE_QUEUE(206),
   NATIVE_ERROR_PREALLOCATE_FILE(208),
   NATIVE_ERROR_ALLOCATE_MEMORY(209),
   ADDRESS_FULL(210) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQAddressFullException(msg);
      }
   },
   LARGE_MESSAGE_INTERRUPTED(211) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQLargeMessageInterruptedException(msg);
      }
   },
   CLUSTER_SECURITY_EXCEPTION(212) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQClusterSecurityException(msg);
      }
   },
   NOT_IMPLEMTNED_EXCEPTION(213),
   MAX_CONSUMER_LIMIT_EXCEEDED(214) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQQueueMaxConsumerLimitReached(msg);
      }
   },
   UNEXPECTED_ROUTING_TYPE_FOR_ADDRESS(215) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQUnexpectedRoutingTypeForAddress(msg);
      }
   },
   INVALID_QUEUE_CONFIGURATION(216) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQInvalidQueueConfiguration(msg);
      }
   },
   DELETE_ADDRESS_ERROR(217) {
      public ActiveMQException createException(String msg) {
         return new ActiveMQDeleteAddressException(msg);
      }
   };

   private static final Map<Integer, ActiveMQExceptionType> TYPE_MAP;
   private final int code;

   private ActiveMQExceptionType(int code) {
      this.code = code;
   }

   public int getCode() {
      return this.code;
   }

   public ActiveMQException createException(String msg) {
      return new ActiveMQException(msg + ", code:" + this);
   }

   public static ActiveMQException createException(int code, String msg) {
      return getType(code).createException(msg);
   }

   public static ActiveMQExceptionType getType(int code) {
      ActiveMQExceptionType type = (ActiveMQExceptionType)TYPE_MAP.get(code);
      return type != null ? type : GENERIC_EXCEPTION;
   }

   static {
      HashMap<Integer, ActiveMQExceptionType> map = new HashMap();

      for(ActiveMQExceptionType type : EnumSet.allOf(ActiveMQExceptionType.class)) {
         map.put(type.getCode(), type);
      }

      TYPE_MAP = Collections.unmodifiableMap(map);
   }
}
