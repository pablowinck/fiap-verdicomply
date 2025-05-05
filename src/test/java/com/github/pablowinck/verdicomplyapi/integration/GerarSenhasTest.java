package com.github.pablowinck.verdicomplyapi.integration;

import org.junit.jupiter.api.Test;

import com.github.pablowinck.verdicomplyapi.util.GeradorSenhasUtil;

/**
 * Classe utilitária para gerar hashes de senhas para testes
 */
public class GerarSenhasTest {
    
    @Test
    void gerarHashesSenhas() {
        // Senhas dos usuários para testes
        String[] senhas = {"admin", "gestor", "auditor"};
        
        // Usando a classe utilitária para exibir hashes
        GeradorSenhasUtil.exibirHashesSenhas(senhas);
        
        // Gerar script SQL para inserção de usuários
        StringBuilder sql = new StringBuilder(GeradorSenhasUtil.gerarScriptUsuarios(senhas));
        
        sql.append("\n-- Insercao de departamentos para testes (com nomes unicos para evitar duplicacao)\n");
        sql.append("INSERT INTO departamento (nome_departamento)\n");
        sql.append("VALUES\n");
        sql.append("    ('Manufatura_Integracao'),\n");
        sql.append("    ('RH_Integracao'),\n");
        sql.append("    ('TI_Integracao');\n");
        
        System.out.println(sql.toString());
        
        // Insere script para criação de normas ambientais
        sql.append("\n-- Insercao de normas ambientais\n");
        sql.append("INSERT INTO norma_ambiental (codigo_norma, descricao, orgao_fiscalizador)\n");
        sql.append("VALUES\n");
        sql.append("    ('CONAMA-01', 'Controle de emissão de poluentes', 'CONAMA'),\n");
        sql.append("    ('ABNT-02', 'Gerenciamento de resíduos', 'ABNT'),\n");
        sql.append("    ('ISO-14001', 'Sistema de gestão ambiental', 'ISO');\n");
        
        System.out.println("\n");
        System.out.println("SCRIPT SQL GERADO COM SUCESSO");
        System.out.println("===========================");
    }
}
