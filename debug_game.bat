@echo off
echo ========================================
echo DEBUG MEMORY MATH GAME
echo ========================================

echo.
echo Limpando projeto...
call mvn clean

echo.
echo Compilando o projeto...
call mvn compile

echo.
echo Verificando se as imagens existem...
if exist "src\main\resources\images\verso-carta.png" (
    echo ✓ verso-carta.png encontrado
) else (
    echo ✗ ERRO: verso-carta.png não encontrado
)

if exist "src\main\resources\images\amareloEstimulo.jpg" (
    echo ✓ amareloEstimulo.jpg encontrado
) else (
    echo ✗ ERRO: amareloEstimulo.jpg não encontrado
)

if exist "src\main\resources\images\azulCerebro.png" (
    echo ✓ azulCerebro.png encontrado
) else (
    echo ✗ ERRO: azulCerebro.png não encontrado
)

if exist "src\main\resources\images\roxoDesafio.png" (
    echo ✓ roxoDesafio.png encontrado
) else (
    echo ✗ ERRO: roxoDesafio.png não encontrado
)

if exist "src\main\resources\images\verdeLogico.jpg" (
    echo ✓ verdeLogico.jpg encontrado
) else (
    echo ✗ ERRO: verdeLogico.jpg não encontrado
)

echo.
echo Executando o jogo com debug...
call mvn clean javafx:run

echo.
echo Debug concluído!
pause 