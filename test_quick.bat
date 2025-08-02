@echo off
echo ========================================
echo TESTE RÁPIDO - MEMORY MATH GAME
echo ========================================

echo.
echo Compilando e executando...
call mvn clean compile

echo.
echo Executando o jogo...
echo ATENÇÃO: Observe o console para mensagens de debug
call mvn clean javafx:run

echo.
echo Teste concluído!
pause 