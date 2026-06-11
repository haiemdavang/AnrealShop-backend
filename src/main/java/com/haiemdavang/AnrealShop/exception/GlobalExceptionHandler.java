package com.haiemdavang.AnrealShop.exception;

import com.haiemdavang.AnrealShop.dto.common.ErrorResponseDto;
import com.haiemdavang.AnrealShop.dto.common.ItemError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    private ErrorResponseDto buildErrorResponse(HttpStatus status, String message, List<ItemError> details) {
        return ErrorResponseDto.builder()
                .code(String.valueOf(status.value()))
                .message(message)
                .details(details)
                .build();
    }

    @ExceptionHandler(AnrealShopException.class)
    public ResponseEntity<ErrorResponseDto> handleAnrealShopException(AnrealShopException ex, Locale locale) {
        log.error("AnrealShopException: {}", ex.getMessage());
        String errorMessage = messageSource.getMessage(ex.getMessage(), null, "Lỗi không xác định từ AnrealShop.", locale);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, null));
    }

    @ExceptionHandler(UnAuthException.class)
    public ResponseEntity<ErrorResponseDto> authException(UnAuthException ex, Locale locale) {
        log.error("UnAuthException: {}", ex.getMessage());
        String errorMessage = messageSource.getMessage(ex.getMessage(), null, "Lỗi xác thực: Bạn không có quyền truy cập.", locale);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildErrorResponse(HttpStatus.UNAUTHORIZED, errorMessage, null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        List<ItemError> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ItemError(error.getField(),
                        messageSource.getMessage(Objects.requireNonNull(error.getDefaultMessage()), null, "Dữ liệu không hợp lệ", Locale.getDefault())))
                .collect(Collectors.toList());

        String errorMessage = "Dữ liệu đầu vào không hợp lệ.";
        return ResponseEntity
                .badRequest()
                .body(buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, details));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.error("Type mismatch error: {}", ex.getMessage());

        String message = "Giá trị không hợp lệ cho tham số '" + ex.getName() + "'";
        if (ex.getRequiredType() == LocalDate.class) {
            message = "Sai định dạng ngày cho tham số '" + ex.getName() + "'. Định dạng đúng là yyyy-MM-dd.";
        }

        List<ItemError> details = List.of(new ItemError(ex.getName(), message));
        return ResponseEntity
                .badRequest()
                .body(buildErrorResponse(HttpStatus.BAD_REQUEST, "Tham số không hợp lệ", details));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("Bad credentials: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildErrorResponse(HttpStatus.UNAUTHORIZED, "Tên đăng nhập hoặc mật khẩu không đúng.", null));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDto> handleForbiddentException(ForbiddenException ex, Locale locale) {
        log.error("forbidden error: {}", ex.getMessage());
        String errorMessage = messageSource.getMessage(ex.getMessage(), null, "Quyền truy cập hạn chế.", locale);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(buildErrorResponse(HttpStatus.FORBIDDEN, errorMessage, null));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequesttException(BadRequestException ex, Locale locale) {
        log.error("badRequest error: {}", ex.getMessage());
        String errorMessage = messageSource.getMessage(ex.getMessage(), null, "Dữ liệu không đúng", locale);
        log.error(errorMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage , null));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDto> handleConflictException(ConflictException ex, Locale locale) {
        log.error("Conflict error: {}", ex.getMessage());
        String errorMessage = messageSource.getMessage(ex.getMessage(), null, "Xung đột dữ liệu.", locale);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(buildErrorResponse(HttpStatus.CONFLICT, errorMessage, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception ex) {
        log.error("Đã xảy ra lỗi không mong muốn: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.", null));
    }
}