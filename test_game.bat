@echo off
echo ========================================
echo TESTANDO MEMORY MATH GAME
echo ========================================

echo.
echo Compilando o projeto...
call mvn clean compile

echo.
echo Executando o jogo...
call mvn clean javafx:run

echo.
echo Teste concluído!
pause 