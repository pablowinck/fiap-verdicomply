package com.github.pablowinck.verdicomplyapi.controller.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErroResposta {
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime timestamp;
    private Integer status;
    private String erro;
    private String mensagem;
    private String path;
    
    @Builder.Default
    private List<CampoErro> campos = new ArrayList<>();
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CampoErro {
        private String campo;
        private String mensagem;
    }
}
