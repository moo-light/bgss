package com.server.blockchainserver.advices;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.blockchainserver.exeptions.BadRequestException;
import com.server.blockchainserver.exeptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Response {
    private HttpStatus status;
    private String message;
    private Object data;

    public Response() {
    }

    public Response(HttpStatus status, String message, Object data) {
        this.status = status;
        this.message = message;
        setData(data);
    }

    public Response(HttpStatus status, String message) {
        this(status, message, null);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static ResponseEntity<Response> getResponseFromException(Exception e) {
        return getResponseFromException_2(e).getResponseEntity();
    }
    public static Response getResponseFromException_2(Exception e) {
        if (e instanceof NotFoundException) {
            return new Response(HttpStatus.NOT_FOUND, e.getMessage());
        }
        if (e instanceof BadRequestException) {
            return new Response(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        e.printStackTrace();
        return new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
    @JsonIgnore
    public ResponseEntity<Response> getResponseEntity() {
        return new ResponseEntity<>(this, this.status);
    }
}
