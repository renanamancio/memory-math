#!/bin/bash

echo "Memory Math - Jogo de Memória Matemática"
echo "==========================================="
echo

echo "Verificando se o Java está instalado..."
if ! command -v java &> /dev/null; then
    echo "ERRO: Java não encontrado! Por favor, instale o Java 21 ou superior."
    exit 1
fi

echo "Verificando se o Maven está instalado..."
if ! command -v mvn &> /dev/null; then
    echo "ERRO: Maven não encontrado! Por favor, instale o Maven 3.6 ou superior."
    exit 1
fi

echo
echo "Compilando e executando o jogo..."
echo

mvn clean javafx:run

if [ $? -ne 0 ]; then
    echo
    echo "ERRO: Falha ao executar o jogo!"
    echo "Verifique se todas as dependências estão instaladas."
    exit 1
fi

echo
echo "Jogo finalizado." 