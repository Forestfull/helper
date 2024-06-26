package com.forestfull.helper.entity;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.LinkedHashMap;

public class NetworkVO {

    public static class Request extends LinkedHashMap<String, Object> {
    }

    public static class Response<T> extends ResponseEntity<T> {

        public Response(DATA_TYPE dataType, T contents, HttpStatus httpStatus) {
            super(contents, CollectionUtils.toMultiValueMap(Collections.singletonMap("data-type", Collections.singletonList(dataType.name()))), httpStatus);
        }

        public static <T> Response<T> ok(DATA_TYPE dataType, T body) {
            return new Response<>(dataType, body, HttpStatus.OK);
        }

        public static <T> Response<T> fail(HttpStatus httpStatus) {
            return fail(httpStatus, null);
        }

        public static <T> Response<T> fail(HttpStatus httpStatus, T body) {
            return new Response<>(DATA_TYPE.ERROR, body, httpStatus);
        }
    }

    public enum DATA_TYPE {
        STRING, JSON, XML, JS_SCRIPT, ERROR
    }
}
