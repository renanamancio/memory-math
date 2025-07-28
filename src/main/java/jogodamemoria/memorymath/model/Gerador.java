package jogodamemoria.memorymath.model;

import java.util.Random;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class Gerador {
    private Integer[][] matrizGeradaOps1;
    private Integer[][] matrizGeradaOps2;
    private Integer[][] matrizGeradaRes;
    private Random random = new Random();
    private int[] operacoesEscolhidas;

    public static final int SOMA = 0;
    public static final int SUBTRACAO = 1;
    public static final int MULTIPLICACAO = 2;
    public static final int DIVISAO = 3;

    public Gerador(int tamanhoMatrizL, int tamanhoMatrizC, int[] operacoesEscolhidas) {
        this.operacoesEscolhidas = operacoesEscolhidas;
        this.matrizGeradaOps1 = new Integer[tamanhoMatrizL][tamanhoMatrizC];
        this.matrizGeradaOps2 = new Integer[tamanhoMatrizL][tamanhoMatrizC];
        this.matrizGeradaRes = new Integer[tamanhoMatrizL][tamanhoMatrizC];
        gerarMatrizOperacoes();
    }

    private void gerarMatrizOperacoes() {
        int linhas = matrizGeradaRes.length;
        int colunas = matrizGeradaRes[0].length;
        List<Integer> todosResultados = new ArrayList<>();

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                int tipoOperacao = escolherOperacaoAleatoria();
                gerarOperacao(i, j, tipoOperacao);
                todosResultados.add(matrizGeradaRes[i][j]);
            }
        }

        Collections.shuffle(todosResultados);

        redistribuirResultados(todosResultados);
    }

    private void gerarOperacao(int i, int j, int tipoOperacao) {
        switch (tipoOperacao) {
            case SOMA:
                gerarSoma(i, j);
                break;
            case SUBTRACAO:
                gerarSubtracao(i, j);
                break;
            case MULTIPLICACAO:
                gerarMultiplicacao(i, j);
                break;
            case DIVISAO:
                gerarDivisao(i, j);
                break;
        }
    }

    private void redistribuirResultados(List<Integer> resultados) {
        int linhas = matrizGeradaRes.length;
        int colunas = matrizGeradaRes[0].length;
        int index = 0;

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                matrizGeradaRes[i][j] = resultados.get(index++);
                ajustarOperandosParaResultado(i, j);
            }
        }
    }

    private int escolherOperacaoAleatoria() {
        List<Integer> opDisponiveis = new ArrayList<>();

        for (int i = 0; i < operacoesEscolhidas.length; i++) {
            if (operacoesEscolhidas[i] == 1) {
                opDisponiveis.add(i);
            }
        }

        if (opDisponiveis.isEmpty()) {
            return SOMA;
        }

        return opDisponiveis.get(random.nextInt(opDisponiveis.size()));
    }

    private void gerarSoma(int i, int j) {
        int a = random.nextInt(101);
        int b = random.nextInt(101);
        matrizGeradaOps1[i][j] = a;
        matrizGeradaOps2[i][j] = b;
        matrizGeradaRes[i][j] = a + b;
    }

    private void gerarSubtracao(int i, int j) {
        int a, b;
        do {
            a = random.nextInt(201);
            b = random.nextInt(201);
        } while (a <= b);

        matrizGeradaOps1[i][j] = a;
        matrizGeradaOps2[i][j] = b;
        matrizGeradaRes[i][j] = a - b;
    }

    private void gerarMultiplicacao(int i, int j) {
        int a = random.nextInt(10) + 1;
        int b = random.nextInt(10) + 1;
        matrizGeradaOps1[i][j] = a;
        matrizGeradaOps2[i][j] = b;
        matrizGeradaRes[i][j] = a * b;
    }

    private void gerarDivisao(int i, int j) {
        int divisor, dividendo;
        do {
            divisor = random.nextInt(30) + 1;
            dividendo = divisor * (random.nextInt(10) + 1);
        } while (dividendo < divisor);

        matrizGeradaOps1[i][j] = dividendo;
        matrizGeradaOps2[i][j] = divisor;
        matrizGeradaRes[i][j] = dividendo / divisor;
    }

    private void ajustarOperandosParaResultado(int i, int j) {
        int resultado = matrizGeradaRes[i][j];
        int operacao = escolherOperacaoAleatoria();

        switch (operacao) {
            case SOMA:
                matrizGeradaOps1[i][j] = random.nextInt(resultado + 1);
                matrizGeradaOps2[i][j] = resultado - matrizGeradaOps1[i][j];
                break;
            case SUBTRACAO:
                matrizGeradaOps1[i][j] = resultado + random.nextInt(100) + 1;
                matrizGeradaOps2[i][j] = matrizGeradaOps1[i][j] - resultado;
                break;
            case MULTIPLICACAO:
                List<Integer> fatores = encontrarFatores(resultado);
                if (!fatores.isEmpty() && fatores.size() >= 2) {
                    int idx = random.nextInt(fatores.size() / 2) * 2;
                    if (idx + 1 < fatores.size()) {
                        matrizGeradaOps1[i][j] = fatores.get(idx);
                        matrizGeradaOps2[i][j] = fatores.get(idx + 1);
                    } else {
                        matrizGeradaOps1[i][j] = random.nextInt(resultado + 1);
                        matrizGeradaOps2[i][j] = resultado - matrizGeradaOps1[i][j];
                    }
                } else {
                    matrizGeradaOps1[i][j] = random.nextInt(resultado + 1);
                    matrizGeradaOps2[i][j] = resultado - matrizGeradaOps1[i][j];
                }
                break;
            case DIVISAO:
                List<Integer> divisores = encontrarDivisores(resultado);
                if (!divisores.isEmpty()) {
                    int idx = random.nextInt(divisores.size());
                    matrizGeradaOps2[i][j] = divisores.get(idx);
                    matrizGeradaOps1[i][j] = resultado * matrizGeradaOps2[i][j];
                } else {
                    matrizGeradaOps1[i][j] = random.nextInt(resultado + 1);
                    matrizGeradaOps2[i][j] = resultado - matrizGeradaOps1[i][j];
                }
                break;
        }

        if (matrizGeradaOps1[i][j] == null) {
            matrizGeradaOps1[i][j] = 0;
        }
        if (matrizGeradaOps2[i][j] == null) {
            matrizGeradaOps2[i][j] = 0;
        }
    }

    private List<Integer> encontrarFatores(int numero) {
        List<Integer> fatores = new ArrayList<>();

        if (numero == 0) {
            fatores.add(0);
            fatores.add(1);
            return fatores;
        }

        if (numero == 1) {
            fatores.add(1);
            fatores.add(1);
            return fatores;
        }

        for (int i = 1; i <= Math.sqrt(numero); i++) {
            if (numero % i == 0) {
                fatores.add(i);
                fatores.add(numero / i);
            }
        }

        if (fatores.isEmpty()) {
            fatores.add(1);
            fatores.add(numero);
        }
        
        return fatores;
    }

    private List<Integer> encontrarDivisores(int numero) {
        List<Integer> divisores = new ArrayList<>();

        if (numero == 0) {
            divisores.add(1);
            return divisores;
        }

        for (int i = 1; i <= 10 && i <= numero; i++) {
            if (numero % i == 0) {
                divisores.add(i);
            }
        }

        if (divisores.isEmpty()) {
            divisores.add(1);
        }
        
        return divisores;
    }

    public Integer[][] getMatrizOperandos1() {
        return matrizGeradaOps1;
    }

    public Integer[][] getMatrizOperandos2() {
        return matrizGeradaOps2;
    }

    public Integer[][] getMatrizResultados() {
        return matrizGeradaRes;
    }

    public void imprimirMatrizOperacoes() {
        int linhas = matrizGeradaOps1.length;
        int colunas = matrizGeradaOps1[0].length;
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                char operacao = determinarOperacao(i, j);
                if (matrizGeradaOps1[i][j] != null && matrizGeradaOps2[i][j] != null && matrizGeradaRes[i][j] != null) {
                    System.out.printf("%3d %c %3d = %3d\t",
                            matrizGeradaOps1[i][j],
                            operacao,
                            matrizGeradaOps2[i][j],
                            matrizGeradaRes[i][j]);
                } else {
                    System.out.printf("null %c null = null\t", operacao);
                }
            }
            System.out.println();
        }
    }

    private char determinarOperacao(int i, int j) {
        Integer op1 = matrizGeradaOps1[i][j];
        Integer op2 = matrizGeradaOps2[i][j];
        Integer res = matrizGeradaRes[i][j];

        if (op1 == null || op2 == null || res == null) {
            return '?';
        }

        if (op1 + op2 == res) return '+';
        if (op1 - op2 == res) return '-';
        if (op1 * op2 == res) return '×';
        if (op2 != 0 && op1 / op2 == res) return '÷';
        return '?';
    }
}