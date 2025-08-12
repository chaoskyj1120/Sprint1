package com.codeit.discodeit.exception.global;

import com.codeit.discodeit.exception.binarycontent.BinaryContentException;
import com.codeit.discodeit.exception.binarycontentstorage.BinaryContentStorageException;
import com.codeit.discodeit.exception.channel.ChannelException;
import com.codeit.discodeit.exception.message.MessageException;
import com.codeit.discodeit.exception.readstatus.ReadStatusException;
import com.codeit.discodeit.exception.user.UserException;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(UserException userException) {

    ErrorResponse errorResponse = new ErrorResponse(
        userException.getTimestamp(),
        userException.getErrorCode().getName(),
        userException.getErrorCode().getMessage(),
        userException.getDetails(),
        "UserException",
        userException.getErrorCode().getStatus()
    );

    return ResponseEntity
        .status(errorResponse.status())
        .body(errorResponse);
  }

  @ExceptionHandler(BinaryContentException.class)
  public ResponseEntity<ErrorResponse> handleBinaryContentException(BinaryContentException binaryContentException) {
    ErrorResponse errorResponse = new ErrorResponse(
        binaryContentException.getTimestamp(),
        binaryContentException.getErrorCode().getName(),
        binaryContentException.getErrorCode().getMessage(),
        binaryContentException.getDetails(),
        "BinaryContentException",
        binaryContentException.getErrorCode().getStatus()
    );
    return ResponseEntity.status(errorResponse.status())
        .body(errorResponse);
  }

  @ExceptionHandler(ChannelException.class)
  public ResponseEntity<ErrorResponse> handleChannelException(ChannelException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        ex.getTimestamp(),
        ex.getErrorCode().getName(),
        ex.getErrorCode().getMessage(),
        ex.getDetails(),
        "ChannelException",
        ex.getErrorCode().getStatus()
    );
    return ResponseEntity.status(errorResponse.status()).body(errorResponse);
  }

  @ExceptionHandler(MessageException.class)
  public ResponseEntity<ErrorResponse> handleMessageException(MessageException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        ex.getTimestamp(),
        ex.getErrorCode().getName(),
        ex.getErrorCode().getMessage(),
        ex.getDetails(),
        "MessageException",
        ex.getErrorCode().getStatus()
    );
    return ResponseEntity.status(errorResponse.status()).body(errorResponse);
  }

  @ExceptionHandler(BinaryContentStorageException.class)
  public ResponseEntity<ErrorResponse> handleBinaryContentStorageException(BinaryContentStorageException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        ex.getTimestamp(),
        ex.getErrorCode().getName(),
        ex.getErrorCode().getMessage(),
        ex.getDetails(),
        "BinaryContentStorageException",
        ex.getErrorCode().getStatus()
    );
    return ResponseEntity.status(errorResponse.status()).body(errorResponse);
  }

  @ExceptionHandler(ReadStatusException.class)
  public ResponseEntity<ErrorResponse> handleReadStatusException(ReadStatusException ex) {
    ErrorResponse errorResponse = new ErrorResponse(
        ex.getTimestamp(),
        ex.getErrorCode().getName(),
        ex.getErrorCode().getMessage(),
        ex.getDetails(),
        "ReadStatusException",
        ex.getErrorCode().getStatus()
    );
    return ResponseEntity.status(errorResponse.status()).body(errorResponse);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<?> handleUnexpectedError(RuntimeException ex) {
    return ResponseEntity.internalServerError().body(Map.of("error", "예상치 못한 오류가 발생했습니다."));
  }
}