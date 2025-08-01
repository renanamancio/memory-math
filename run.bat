@echo off
echo Memory Math - Jogo de Memoria Matematica
echo ===========================================
echo.

echo Verificando se o Java esta instalado...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: Java nao encontrado! Por favor, instale o Java 21 ou superior.
    pause
    exit /b 1
)

echo Verificando se o Maven esta instalado...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: Maven nao encontrado! Por favor, instale o Maven 3.6 ou superior.
    pause
    exit /b 1
)

echo.
echo Compilando e executando o jogo...
echo.

mvn clean javafx:run

if %errorlevel% neq 0 (
    echo.
    echo ERRO: Falha ao executar o jogo!
    echo Verifique se todas as dependencias estao instaladas.
    pause
    exit /b 1
)

echo.
echo Jogo finalizado.
pause 