#!/bin/bash

echo "=== INICIALIZANDO USUÁRIOS ==="

# Obtém token JWT antes, se disponível (tenta com usuários que podem já existir)
function obter_token() {
    local usuario=$1
    local senha=$2
    local token=$(curl -s -X POST "http://localhost:8080/api/public/auth/login" \
        -H "Content-Type: application/json" \
        -d "{\"username\": \"$usuario\", \"password\": \"$senha\"}" | jq -r '.token')
    
    if [ "$token" = "null" ]; then
        echo "Não foi possível obter token para $usuario"
        return 1
    fi
    
    echo "$token"
    return 0
}

# Registra um novo usuário
function registrar_usuario() {
    local username=$1
    local password=$2
    local role=$3
    
    echo "Registrando usuário: $username com role $role"
    
    curl -s -X POST "http://localhost:8080/api/public/registro" \
        -H "Content-Type: application/json" \
        -d "{\"username\":\"$username\",\"password\":\"$password\",\"role\":\"$role\"}" | jq
}

# Primeiro, tenta obter token com usuário admin 
TOKEN=$(obter_token "admin" "admin123")

if [ $? -ne 0 ]; then
    echo "Admin não encontrado. Vamos criar usuários..."
    
    # Registra usuários com senhas de pelo menos 6 caracteres
    registrar_usuario "admin" "admin123" "ADMIN"
    registrar_usuario "gestor" "gestor123" "GESTOR"
    registrar_usuario "auditor" "auditor123" "AUDITOR"
    
    # Tenta obter token novamente
    TOKEN=$(obter_token "admin" "admin123")
fi

# Se conseguimos token, vamos testar o endpoint de normas ambientais
if [ $? -eq 0 ]; then
    echo "Token obtido com sucesso. Testando endpoint de normas ambientais..."
    
    NORMAS=$(curl -s -X GET "http://localhost:8080/api/normas" \
        -H "Authorization: Bearer $TOKEN")
    
    echo "Resposta do endpoint de normas ambientais:"
    echo "$NORMAS" | jq
fi

echo "Inicialização de usuários concluída."
