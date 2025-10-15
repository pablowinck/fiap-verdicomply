package com.github.pablowinck.verdicomplyapi.controller.exception;

import com.github.pablowinck.verdicomplyapi.service.exception.RecursoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResposta> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        String mensagem = "Erro de formato de dados no corpo da requisição";
        String message = e.getMessage();
        if (message != null) {
            if (message.contains("LocalDate")) {
                mensagem = "Formato de data inválido. Use o formato: YYYY-MM-DD";
            } else if (message.contains("LocalDateTime")) {
                mensagem = "Formato de data/hora inválido. Use o formato: YYYY-MM-DDTHH:mm:ss";
            } else if (message.contains("Cannot deserialize")) {
                mensagem = "Dados inválidos no corpo da requisição";
            }
        }

        ErroResposta erro = ErroResposta.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .erro("Erro de formato de dados")
                .mensagem(mensagem)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> handleDataIntegrityViolationException(
            DataIntegrityViolationException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        String mensagem = "Operação violou uma restrição do banco de dados";
        String message = e.getMessage();
        if (message != null) {
            if (message.contains("foreign key constraint")) {
                mensagem = "Não é possível excluir este registro pois existem outros registros vinculados a ele";
            } else if (message.contains("unique constraint")) {
                mensagem = "Já existe um registro com estes dados";
            }
        }

        ErroResposta erro = ErroResposta.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .erro("Violação de integridade")
                .mensagem(mensagem)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroResposta> handleBadCredentialsException(
            BadCredentialsException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ErroResposta erro = ErroResposta.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .erro("Credenciais inválidas")
                .mensagem("Usuário ou senha incorretos")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResposta> handleAccessDeniedException(
            AccessDeniedException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.FORBIDDEN;

        ErroResposta erro = ErroResposta.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .erro("Acesso negado")
                .mensagem("Você não tem permissão para acessar este recurso")
                .path(request.getRequestURI())
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
