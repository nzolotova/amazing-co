package com.amazingco.node;

import com.amazingco.node.NodeService.NodeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class NodeExceptionHandler {

    @ExceptionHandler
    protected ResponseEntity<String> handle(NodeException nodeException) {
        return new ResponseEntity<>(nodeException.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
