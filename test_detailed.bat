@echo off
echo ========================================
echo TESTE DETALHADO - MEMORY MATH GAME
echo ========================================

echo.
echo 1. Limpando projeto...
call mvn clean

echo.
echo 2. Compilando projeto...
call mvn compile

echo.
echo 3. Verificando se as imagens existem...
if exist "src\main\resources\images\verso-carta.png" (
    echo ✓ Imagem verso-carta.png encontrada
) else (
    echo ✗ ERRO: Imagem verso-carta.png não encontrada
)

if exist "src\main\resources\images\amareloEstimulo.jpg" (
    echo ✓ Imagem amareloEstimulo.jpg encontrada
) else (
    echo ✗ ERRO: Imagem amareloEstimulo.jpg não encontrada
)

if exist "src\main\resources\images\azulCerebro.png" (
    echo ✓ Imagem azulCerebro.png encontrada
) else (
    echo ✗ ERRO: Imagem azulCerebro.png não encontrada
)

echo.
echo 4. Verificando se os arquivos FXML existem...
if exist "src\main\resources\fxml\game-view.fxml" (
    echo ✓ game-view.fxml encontrado
) else (
    echo ✗ ERRO: game-view.fxml não encontrado
)

echo.
echo 5. Executando o jogo...
echo ATENÇÃO: Observe o console para mensagens de debug
call mvn clean javafx:run

echo.
echo Teste concluído!
pause 