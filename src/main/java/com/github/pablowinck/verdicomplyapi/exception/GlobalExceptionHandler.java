package com.github.pablowinck.verdicomplyapi.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manipulador global de exceções para a aplicação
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata exceções de credenciais inválidas
     * 
     * @param ex A exceção de credenciais inválidas
     * @return Resposta com status 401 (Unauthorized)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Credenciais inválidas");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    /**
     * Trata exceções de acesso negado
     *
     * @param ex A exceção de acesso negado
     * @return Resposta com status 403 (Forbidden)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Acesso negado");
        response.put("message", "Você não tem permissão para acessar este recurso");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Trata exceções de validação de argumentos
     *
     * @param ex A exceção de validação
     * @return Resposta com status 400 (Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        response.put("error", "Erro de validação");
        response.put("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Trata exceções de integridade de dados
     *
     * @param ex A exceção de integridade
     * @return Resposta com status 400 (Bad Request)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> response = new HashMap<>();

        String message = ex.getMessage();
        if (message.contains("foreign key constraint")) {
            response.put("error", "Violação de integridade");
            response.put("message", "Não é possível excluir este registro pois existem outros registros vinculados a ele");
        } else if (message.contains("unique constraint")) {
            response.put("error", "Violação de unicidade");
            response.put("message", "Já existe um registro com estes dados");
        } else {
            response.put("error", "Erro de integridade de dados");
            response.put("message", "Operação violou uma restrição do banco de dados");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Trata exceções de parsing de mensagens HTTP (JSON mal formatado)
     *
     * @param ex A exceção de parsing
     * @return Resposta com status 400 (Bad Request)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Erro de formato de dados");

        String message = ex.getMessage();
        if (message != null) {
            if (message.contains("LocalDate")) {
                response.put("message", "Formato de data inválido. Use o formato: YYYY-MM-DD");
            } else if (message.contains("LocalDateTime")) {
                response.put("message", "Formato de data/hora inválido. Use o formato: YYYY-MM-DDTHH:mm:ss");
            } else if (message.contains("Cannot deserialize")) {
                response.put("message", "Dados inválidos no corpo da requisição");
            } else {
                response.put("message", "JSON mal formatado ou dados inválidos");
            }
        } else {
            response.put("message", "Corpo da requisição inválido");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Trata todas as exceções não capturadas pelos handlers específicos
     *
     * @param ex A exceção genérica
     * @return Resposta com status 500 (Internal Server Error) ou 400 se for erro do cliente
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> response = new HashMap<>();

        // Log para debug
        ex.printStackTrace();

        // Verifica se é um erro de validação ou parsing que não foi capturado
        String message = ex.getMessage();
        if (message != null && (message.contains("parse") || message.contains("format") ||
            message.contains("Invalid") || message.contains("Cannot deserialize"))) {
            response.put("error", "Erro de validação");
            response.put("message", "Dados inválidos na requisição");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("error", "Erro interno do servidor");
        response.put("message", ex.getMessage() != null ? ex.getMessage() : "Erro desconhecido");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
