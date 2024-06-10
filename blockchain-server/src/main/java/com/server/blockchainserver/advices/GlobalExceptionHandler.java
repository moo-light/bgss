package com.server.blockchainserver.advices;

import com.server.blockchainserver.exeptions.DashboardStatisticException;
import com.server.blockchainserver.exeptions.NotFoundException;
import com.server.blockchainserver.exeptions.WithdrawalNotAllowedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, HttpServletRequest request, HttpServletResponse response){
        // Tạo response body cho các lỗi tổng quát
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", getStatus(ex).value());
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage());
        body.put("path", request.getServletPath());

        // Đây là mã trạng thái mặc định cho các lỗi khác, có thể thay đổi dựa trên loại lỗi
        return new ResponseEntity<>(body, getStatus(ex));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        // Logic xử lý AuthenticationException
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Path Not found");
        body.put("message", ex.getLocalizedMessage());
        body.put("path", request.getServletPath());

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest request) {

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", "The requested path " + request.getRequestURI() + " was not found.");
        body.put("path", request.getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        // Logic xử lý AccessDeniedException
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Forbidden");
        body.put("message", ex.getLocalizedMessage());
        body.put("path", request.getServletPath());

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(WithdrawalNotAllowedException.class)
    public ResponseEntity<Object> handleWithdrawalNotAllowedException(WithdrawalNotAllowedException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Withdrawal Not Allowed");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DashboardStatisticException.class)
    public ResponseEntity<Object> handleDashboardStatisticException(DashboardStatisticException ex, WebRequest request){
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Error at DashboardStatisticController");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(CheckEnumException.class)
//    public ResponseEntity<Object> handleIllegalArgumentException(CheckEnumException ex) {
//        // Định nghĩa response details hoặc status code mà bạn muốn gửi trả
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("timestamp", LocalDateTime.now());
//        body.put("status", HttpStatus.BAD_REQUEST.value());
//        body.put("error", "Invalid argument");
//        body.put("message", ex.getMessage());
//
//        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
//    }

    private HttpStatus getStatus(Exception ex) {
        // Tạo logic để định nghĩa mã trạng thái HTTP dựa trên exception
        // Ví dụ: chúng ta có thể trả về mã khác nhau nếu có SubException
        if (ex instanceof NotFoundException) {
            return HttpStatus.NOT_FOUND; // hoặc mã trạng thái khác tương ứng
        }
        if (ex instanceof WithdrawalNotAllowedException) {
            return HttpStatus.BAD_REQUEST;
        }
        if (ex instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        }
//        if (ex instanceof CheckEnumException) {
//            return HttpStatus.BAD_REQUEST;
//        }
        return HttpStatus.INTERNAL_SERVER_ERROR; // mặc định là lỗi server
    }
}
