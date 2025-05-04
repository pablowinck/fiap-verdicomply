package com.github.pablowinck.verdicomplyapi.controller.exception;

import com.github.pablowinck.verdicomplyapi.service.exception.RecursoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ManipuladorGlobalDeExcecoes {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> handleRecursoNaoEncontradoException(
            RecursoNaoEncontradoException e, HttpServletRequest request) {
        
        HttpStatus status = HttpStatus.NOT_FOUND;
        
        ErroResposta erro = ErroResposta.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .erro("Recurso não encontrado")
                .mensagem(e.getMessage())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(status).body(erro);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        
        HttpStatus status = HttpStatus.BAD_REQUEST;
        
        List<ErroResposta.CampoErro> camposErro = new ArrayList<>();
        
        for (FieldError field : e.getBindingResult().getFieldErrors()) {
            camposErro.add(ErroResposta.CampoErro.builder()
                    .campo(field.getField())
                    .mensagem(field.getDefaultMessage())
                    .build());
        }
        
        ErroResposta erro = ErroResposta.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .erro("Erro de validação")
                .mensagem("Um ou mais campos estão inválidos")
                .path(request.getRequestURI())
                .campos(camposErro)
                .build();
        
        return ResponseEntity.status(status).body(erro);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> handleGenericException(
            Exception e, HttpServletRequest request) {
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        ErroResposta erro = ErroResposta.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .erro("Erro interno do servidor")
                .mensagem("Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.")
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(status).body(erro);
    }
}
