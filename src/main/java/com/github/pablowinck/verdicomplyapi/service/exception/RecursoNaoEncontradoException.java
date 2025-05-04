package com.github.pablowinck.verdicomplyapi.service.exception;

public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
    
    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(String.format("%s não encontrado(a) com id: %d", recurso, id));
    }
}
