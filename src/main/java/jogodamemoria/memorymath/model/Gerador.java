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
        int totalCartas = linhas * colunas;
        
        // Conta quantas operações foram selecionadas
        List<Integer> operacoesAtivas = new ArrayList<>();
        for (int i = 0; i < operacoesEscolhidas.length; i++) {
            if (operacoesEscolhidas[i] == 1) {
                operacoesAtivas.add(i);
            }
        }
        
        int numOperacoes = operacoesAtivas.size();
        int cartasPorOperacao = totalCartas / numOperacoes;
        int cartasRestantes = totalCartas % numOperacoes;
        
        System.out.println("=== DISTRIBUIÇÃO DE OPERAÇÕES ===");
        System.out.println("Total de cartas: " + totalCartas);
        System.out.println("Número de operações: " + numOperacoes);
        System.out.println("Cartas por operação: " + cartasPorOperacao);
        System.out.println("Cartas restantes: " + cartasRestantes);
        
        // Cria lista de operações distribuídas
        List<Integer> operacoesDistribuidas = new ArrayList<>();
        for (int i = 0; i < numOperacoes; i++) {
            int operacao = operacoesAtivas.get(i);
            int quantidade = cartasPorOperacao + (i < cartasRestantes ? 1 : 0);
            
            for (int j = 0; j < quantidade; j++) {
                operacoesDistribuidas.add(operacao);
            }
            
            System.out.println("Operação " + getNomeOperacao(operacao) + ": " + quantidade + " cartas");
        }
        
        // Embaralha a lista de operações
        Collections.shuffle(operacoesDistribuidas);
        
        // Gera as operações na ordem embaralhada
        int index = 0;
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                if (index < operacoesDistribuidas.size()) {
                    int tipoOperacao = operacoesDistribuidas.get(index++);
                    gerarOperacao(i, j, tipoOperacao);
                }
            }
        }
        
        System.out.println("=== FIM DA DISTRIBUIÇÃO ===\n");
    }

    private String getNomeOperacao(int operacao) {
        switch (operacao) {
            case SOMA: return "Soma";
            case SUBTRACAO: return "Subtração";
            case MULTIPLICACAO: return "Multiplicação";
            case DIVISAO: return "Divisão";
            default: return "Desconhecida";
        }
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