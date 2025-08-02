@echo off
echo ========================================
echo TESTE ESPECÍFICO - IA MEMORY MATH
echo ========================================

echo.
echo Este teste irá verificar se a IA está funcionando corretamente
echo Observe o console para mensagens de debug da IA
echo.

echo Compilando projeto...
call mvn clean compile

echo.
echo Executando jogo com debug da IA...
echo ATENÇÃO: Configure o jogo para modo PvE (Jogador vs IA)
echo e observe se a IA revela as cartas corretamente
call mvn clean javafx:run

echo.
echo Teste da IA concluído!
pause 