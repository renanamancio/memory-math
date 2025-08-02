package jogodamemoria.memorymath.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import jogodamemoria.memorymath.GameManager;
import jogodamemoria.memorymath.Player;
import jogodamemoria.memorymath.AIPlayer;
import jogodamemoria.memorymath.model.Card;
import jogodamemoria.memorymath.model.Gerador;
import jogodamemoria.memorymath.transitions.SceneManager;
import jogodamemoria.memorymath.util.ImageUtils;
import jogodamemoria.memorymath.util.AlertUtils;
import jogodamemoria.memorymath.util.ResourceLoadException;
import jogodamemoria.memorymath.util.AudioManager;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Controlador responsável pela tela principal do jogo.
 * Gerencia a interface do tabuleiro de cartas e a lógica de jogo.
 * 
 * @author Renan Amancio
 * @version 1.0
 */
public class GameController {
    private static final int NUMERO_COLUNAS = 3;
    private static final int NUMERO_LINHAS = 4;
    private static final int TOTAL_PARES = 12;

    private static final String CAMINHO_IMAGEM_VERSO_CARTA = "/images/verso-carta.png";
    private static final String CAMINHO_IMAGEM_REVELACAO_SOMA = "/images/amareloEstimulo.jpg";
    private static final String CAMINHO_IMAGEM_REVELACAO_SUBTRACAO = "/images/azulCerebro.png";
    private static final String CAMINHO_IMAGEM_REVELACAO_MULTIPLICACAO = "/images/roxoDesafio.png";
    private static final String CAMINHO_IMAGEM_REVELACAO_DIVISAO = "/images/verdeLogico.jpg";

    @FXML private GridPane gradeOperacoes;
    @FXML private GridPane gradeResultados;
    @FXML private Label rotuloIndicadorTurno;
    @FXML private Label nomeJogador1;
    @FXML private Label pontuacaoJogador1;
    @FXML private Label nomeJogador2;
    @FXML private Label pontuacaoJogador2;

    private Image imagemVersoCarta;
    private Image imagemRevelacaoSoma;
    private Image imagemRevelacaoSubtracao;
    private Image imagemRevelacaoMultiplicacao;
    private Image imagemRevelacaoDivisao;

    private Gerador gerador;
    private StackPane primeiraCartaSelecionada;
    private StackPane segundaCartaSelecionada;
    private boolean aguardandoSegundaCarta = false;
    private int paresEncontrados = 0;
    private final int TOTAL_CARTAS = NUMERO_LINHAS * NUMERO_COLUNAS * 2;

    private Integer[][] resultadosEmbaralhados;

    private boolean turnoIA = false;
    private PauseTransition atrasoIA;

    private static final int PONTOS_ACERTO = 5;
    private static final int PONTOS_ERRO = -1;

    /**
     * Método chamado automaticamente pelo JavaFX quando o FXML é carregado.
     */
    @FXML
    public void initialize() {
        System.out.println("=== INICIALIZAÇÃO AUTOMÁTICA DO GAME CONTROLLER ===");
        System.out.println("Método initialize() chamado automaticamente pelo JavaFX");

        if (gradeOperacoes == null) {
            System.err.println("ERRO: gradeOperacoes não foi injetado!");
        } else {
            System.out.println("✓ gradeOperacoes injetado corretamente");
        }
        
        if (gradeResultados == null) {
            System.err.println("ERRO: gradeResultados não foi injetado!");
        } else {
            System.out.println("✓ gradeResultados injetado corretamente");
        }
        
        if (rotuloIndicadorTurno == null) {
            System.err.println("ERRO: rotuloIndicadorTurno não foi injetado!");
        } else {
            System.out.println("✓ rotuloIndicadorTurno injetado corretamente");
        }

        javafx.application.Platform.runLater(() -> {
            try {
                System.out.println("Chamando inicializar() via Platform.runLater...");
                inicializar();
            } catch (Exception e) {
                System.err.println("ERRO na inicialização automática: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Verifica se o GameManager está configurado corretamente.
     * @return true se estiver configurado, false caso contrário
     */
    private boolean verificarConfiguracaoGameManager() {
        GameManager gerenciadorJogo = GameManager.getInstance();
        if (gerenciadorJogo == null) {
            System.err.println("ERRO: GameManager é null!");
            return false;
        }
        
        if (gerenciadorJogo.getPlayer1() == null) {
            System.err.println("ERRO: Player1 é null!");
            return false;
        }
        
        if (gerenciadorJogo.getPlayer2() == null) {
            System.err.println("ERRO: Player2 é null!");
            return false;
        }
        
        if (gerenciadorJogo.getSelectedOperations() == null || gerenciadorJogo.getSelectedOperations().isEmpty()) {
            System.err.println("ERRO: Operações não selecionadas!");
            return false;
        }
        
        System.out.println("GameManager configurado corretamente:");
        System.out.println("- Jogador 1: " + gerenciadorJogo.getPlayer1().getName());
        System.out.println("- Jogador 2: " + gerenciadorJogo.getPlayer2().getName());
        System.out.println("- Operações: " + gerenciadorJogo.getSelectedOperations());
        
        return true;
    }

    @FXML
    public void inicializar() {
        System.out.println("=== INICIANDO GAME CONTROLLER ===");
        paresEncontrados = 0;

        if (gradeOperacoes == null) {
            System.err.println("ERRO: gradeOperacoes é null!");
            return;
        }
        if (gradeResultados == null) {
            System.err.println("ERRO: gradeResultados é null!");
            return;
        }
        
        System.out.println("Grids injetados corretamente");

        if (!verificarConfiguracaoGameManager()) {
            System.err.println("ERRO: GameManager não está configurado corretamente!");
            return;
        }
        
        carregarTodasImagensCartas();
        configurarJogo();
        configurarLayoutResponsivo();
        popularGrades();
        configurarInformacoesJogadores();
        atualizarIndicadorTurno();
        
        System.out.println("=== GAME CONTROLLER INICIALIZADO ===");
    }

    /**
     * Encerra a partida e retorna ao menu principal.
     */
    @FXML
    private void encerraPartida() {
        SceneManager.getInstance().carregarCena("/fxml/menu-view.fxml");
    }

    /**
     * Configura o layout responsivo para diferentes tamanhos de tela.
     */
    private void configurarLayoutResponsivo() {
        if (gradeOperacoes == null || gradeResultados == null) {
            System.err.println("ERRO: Grids são null em configurarLayoutResponsivo");
            return;
        }
        
        System.out.println("Configurando layout responsivo...");
        
        gradeOperacoes.setHgap(8);
        gradeOperacoes.setVgap(8);
        gradeResultados.setHgap(8);
        gradeResultados.setVgap(8);

        gradeOperacoes.getColumnConstraints().clear();
        gradeOperacoes.getRowConstraints().clear();
        gradeResultados.getColumnConstraints().clear();
        gradeResultados.getRowConstraints().clear();

        for (int i = 0; i < NUMERO_COLUNAS; i++) {
            ColumnConstraints restricoesColuna = new ColumnConstraints();
            restricoesColuna.setPercentWidth(100.0 / NUMERO_COLUNAS);
            restricoesColuna.setHgrow(Priority.ALWAYS);
            gradeOperacoes.getColumnConstraints().add(restricoesColuna);
            gradeResultados.getColumnConstraints().add(restricoesColuna);
        }
        
        for (int i = 0; i < NUMERO_LINHAS; i++) {
            RowConstraints restricoesLinha = new RowConstraints();
            restricoesLinha.setPercentHeight(100.0 / NUMERO_LINHAS);
            restricoesLinha.setVgrow(Priority.ALWAYS);
            gradeOperacoes.getRowConstraints().add(restricoesLinha);
            gradeResultados.getRowConstraints().add(restricoesLinha);
        }
        
        System.out.println("Layout responsivo configurado");
    }

    /**
     * Verifica se o Gerador está funcionando corretamente.
     * @return true se estiver funcionando, false caso contrário
     */
    private boolean verificarGerador() {
        if (gerador == null) {
            System.err.println("ERRO: Gerador é null!");
            return false;
        }
        
        try {
            Integer[][] operandos1 = gerador.getMatrizOperandos1();
            Integer[][] operandos2 = gerador.getMatrizOperandos2();
            Integer[][] resultados = gerador.getMatrizResultados();
            
            if (operandos1 == null || operandos2 == null || resultados == null) {
                System.err.println("ERRO: Matrizes do gerador são null!");
                return false;
            }
            
            System.out.println("Gerador funcionando corretamente:");
            System.out.println("- Dimensões: " + operandos1.length + "x" + operandos1[0].length);
            System.out.println("- Operandos1[0][0]: " + operandos1[0][0]);
            System.out.println("- Operandos2[0][0]: " + operandos2[0][0]);
            System.out.println("- Resultados[0][0]: " + resultados[0][0]);
            
            return true;
        } catch (Exception e) {
            System.err.println("ERRO ao verificar gerador: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Configura o jogo inicializando o gerador e as operações.
     */
    private void configurarJogo() {
        System.out.println("Configurando jogo...");
        
        GameManager gerenciadorJogo = GameManager.getInstance();
        if (gerenciadorJogo == null) {
            System.err.println("ERRO: GameManager é null");
            return;
        }
        
        List<Card.OperationType> operacoesSelecionadas = gerenciadorJogo.getSelectedOperations();
        if (operacoesSelecionadas == null || operacoesSelecionadas.isEmpty()) {
            System.err.println("ERRO: Nenhuma operação selecionada");
            operacoesSelecionadas = List.of(Card.OperationType.SOMA, Card.OperationType.SUBTRACAO);
            System.out.println("Usando operações padrão: " + operacoesSelecionadas);
        }
        
        int[] arrayOperacoes = new int[4];
        
        for (Card.OperationType op : operacoesSelecionadas) {
            switch (op) {
                case SOMA:
                    arrayOperacoes[Gerador.SOMA] = 1;
                    break;
                case SUBTRACAO:
                    arrayOperacoes[Gerador.SUBTRACAO] = 1;
                    break;
                case MULTIPLICACAO:
                    arrayOperacoes[Gerador.MULTIPLICACAO] = 1;
                    break;
                case DIVISAO:
                    arrayOperacoes[Gerador.DIVISAO] = 1;
                    break;
            }
        }

        try {
            System.out.println("Criando gerador com dimensões: " + NUMERO_LINHAS + "x" + NUMERO_COLUNAS);
            gerador = new Gerador(NUMERO_LINHAS, NUMERO_COLUNAS, arrayOperacoes);
            
            if (!verificarGerador()) {
                System.err.println("ERRO: Gerador não está funcionando corretamente!");
                return;
            }
            
            embaralharResultados();
            System.out.println("Jogo configurado com sucesso");
        } catch (Exception e) {
            System.err.println("ERRO ao configurar jogo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Embaralha os resultados para criar posições aleatórias no grid de resultados.
     */
    private void embaralharResultados() {
        if (gerador == null) {
            System.err.println("ERRO: Gerador é null em embaralharResultados");
            return;
        }
        
        System.out.println("Embaralhando resultados...");
        
        resultadosEmbaralhados = new Integer[NUMERO_LINHAS][NUMERO_COLUNAS];

        List<Integer> todosResultados = new ArrayList<>();
        for (int i = 0; i < NUMERO_LINHAS; i++) {
            for (int j = 0; j < NUMERO_COLUNAS; j++) {
                todosResultados.add(gerador.getMatrizResultados()[i][j]);
            }
        }

        Collections.shuffle(todosResultados);

        int indice = 0;
        for (int i = 0; i < NUMERO_LINHAS; i++) {
            for (int j = 0; j < NUMERO_COLUNAS; j++) {
                resultadosEmbaralhados[i][j] = todosResultados.get(indice++);
            }
        }
        
        System.out.println("Resultados embaralhados com sucesso");
    }

    /**
     * Carrega todas as imagens das cartas (verso e faces coloridas).
     */
    private void carregarTodasImagensCartas() {
        System.out.println("=== CARREGANDO IMAGENS ===");
        carregarImagemVersoCarta();
        carregarImagensRevelacao();
        System.out.println("=== IMAGENS CARREGADAS ===");
    }

    /**
     * Carrega a imagem do verso da carta com tratamento de erro.
     */
    private void carregarImagemVersoCarta() {
        try {
            System.out.println("Carregando imagem do verso da carta: " + CAMINHO_IMAGEM_VERSO_CARTA);
            imagemVersoCarta = ImageUtils.carregarImagem(CAMINHO_IMAGEM_VERSO_CARTA);

            if (imagemVersoCarta == null) {
                throw new RuntimeException("Imagem do verso da carta é null");
            }

            if (imagemVersoCarta.isError()) {
                throw new RuntimeException("Erro ao carregar a imagem do verso da carta");
            }
            
            System.out.println("Imagem do verso da carta carregada com sucesso");
        } catch (ResourceLoadException e) {
            System.err.println("ERRO ao carregar imagem do verso: " + e.getMessage());
            AlertUtils.mostrarErro("Erro ao carregar recursos do jogo", e.getMessage());
            imagemVersoCarta = null;
        } catch (Exception e) {
            System.err.println("ERRO inesperado ao carregar imagem do verso: " + e.getMessage());
            AlertUtils.mostrarErro("Erro ao carregar recursos do jogo", "Erro inesperado: " + e.getMessage());
            imagemVersoCarta = null;
        }
    }

    /**
     * Carrega as imagens de revelação das cartas (faces coloridas).
     */
    private void carregarImagensRevelacao() {
        try {
            imagemRevelacaoSoma = ImageUtils.carregarImagem(CAMINHO_IMAGEM_REVELACAO_SOMA);
            if (imagemRevelacaoSoma.isError()) {
                throw new RuntimeException("Erro ao carregar imagem de soma");
            }

            imagemRevelacaoSubtracao = ImageUtils.carregarImagem(CAMINHO_IMAGEM_REVELACAO_SUBTRACAO);
            if (imagemRevelacaoSubtracao.isError()) {
                throw new RuntimeException("Erro ao carregar imagem de subtração");
            }

            imagemRevelacaoMultiplicacao = ImageUtils.carregarImagem(CAMINHO_IMAGEM_REVELACAO_MULTIPLICACAO);
            if (imagemRevelacaoMultiplicacao.isError()) {
                throw new RuntimeException("Erro ao carregar imagem de multiplicação");
            }

            imagemRevelacaoDivisao = ImageUtils.carregarImagem(CAMINHO_IMAGEM_REVELACAO_DIVISAO);
            if (imagemRevelacaoDivisao.isError()) {
                throw new RuntimeException("Erro ao carregar imagem de divisão");
            }
        } catch (ResourceLoadException e) {
            AlertUtils.mostrarErro("Erro ao carregar recursos do jogo", e.getMessage());
            imagemRevelacaoSoma = imagemRevelacaoSubtracao = imagemRevelacaoMultiplicacao = imagemRevelacaoDivisao = null;
        }
    }

    /**
     * Obtém a imagem de revelação baseada no tipo de operação.
     * @param tipoOperacao tipo de operação matemática
     * @return Image correspondente à operação
     */
    private Image obterImagemRevelacaoParaOperacao(Card.OperationType tipoOperacao) {
        switch (tipoOperacao) {
            case SOMA:
                return imagemRevelacaoSoma;
            case SUBTRACAO:
                return imagemRevelacaoSubtracao;
            case MULTIPLICACAO:
                return imagemRevelacaoMultiplicacao;
            case DIVISAO:
                return imagemRevelacaoDivisao;
            default:
                return imagemVersoCarta;
        }
    }

    /**
     * Popula os grids de operações e resultados.
     */
    private void popularGrades() {
        System.out.println("=== POPULANDO GRADES ===");
        
        if (gradeOperacoes == null) {
            System.err.println("ERRO: gradeOperacoes é null em popularGrades");
            return;
        }
        
        if (gradeResultados == null) {
            System.err.println("ERRO: gradeResultados é null em popularGrades");
            return;
        }
        
        popularGrade(gradeOperacoes, "op");
        popularGrade(gradeResultados, "re");
        
        System.out.println("=== GRADES POPULADAS ===");
    }

    /**
     * Configura as informações dos jogadores na interface.
     */
    private void configurarInformacoesJogadores() {
        System.out.println("Configurando informações dos jogadores...");
        
        GameManager gerenciadorJogo = GameManager.getInstance();
        if (gerenciadorJogo == null) {
            System.err.println("ERRO: GameManager é null em configurarInformacoesJogadores");
            return;
        }
        
        if (nomeJogador1 != null && gerenciadorJogo.getPlayer1() != null) {
            nomeJogador1.setText(gerenciadorJogo.getPlayer1().getName());
        }
        if (pontuacaoJogador1 != null && gerenciadorJogo.getPlayer1() != null) {
            pontuacaoJogador1.setText(String.valueOf(gerenciadorJogo.getPlayer1().getScore()));
        }
        if (nomeJogador2 != null && gerenciadorJogo.getPlayer2() != null) {
            nomeJogador2.setText(gerenciadorJogo.getPlayer2().getName());
        }
        if (pontuacaoJogador2 != null && gerenciadorJogo.getPlayer2() != null) {
            pontuacaoJogador2.setText(String.valueOf(gerenciadorJogo.getPlayer2().getScore()));
        }
        
        System.out.println("Informações dos jogadores configuradas");
    }

    /**
     * Atualiza o indicador de turno.
     */
    private void atualizarIndicadorTurno() {
        System.out.println("Atualizando indicador de turno...");
        
        GameManager gerenciadorJogo = GameManager.getInstance();
        if (gerenciadorJogo == null) {
            System.err.println("ERRO: GameManager é null em atualizarIndicadorTurno");
            return;
        }
        
        if (rotuloIndicadorTurno != null && gerenciadorJogo.getCurrentPlayer() != null) {
            rotuloIndicadorTurno.setText("Vez de: " + gerenciadorJogo.getCurrentPlayer().getName());
        }

        if (gerenciadorJogo.getCurrentPlayer() instanceof AIPlayer) {
            turnoIA = true;
            if (!aguardandoSegundaCarta) {
                iniciarTurnoIA();
            }
        } else {
            turnoIA = false;
        }
        
        System.out.println("Indicador de turno atualizado");
    }

    /**
     * Inicia o turno da IA.
     */
    private void iniciarTurnoIA() {
        System.out.println("=== INICIANDO TURNO DA IA ===");
        if (!turnoIA) {
            System.err.println("ERRO: turnoIA é false!");
            return;
        }

        System.out.println("Configurando atraso para IA...");
        atrasoIA = new PauseTransition(Duration.seconds(1.0));
        atrasoIA.setOnFinished(event -> {
            System.out.println("Atraso da IA finalizado, executando jogada...");
            executarJogadaIA();
        });
        atrasoIA.play();
        System.out.println("Atraso da IA iniciado");
    }

    /**
     * Executa a jogada da IA.
     */
    private void executarJogadaIA() {
        System.out.println("=== EXECUTANDO JOGADA DA IA ===");
        if (!turnoIA) {
            System.err.println("ERRO: turnoIA é false em executarJogadaIA!");
            return;
        }
        
        GameManager gerenciadorJogo = GameManager.getInstance();
        if (gerenciadorJogo == null) {
            System.err.println("ERRO: GameManager é null!");
            return;
        }
        
        AIPlayer jogadorIA = (AIPlayer) gerenciadorJogo.getCurrentPlayer();
        if (jogadorIA == null) {
            System.err.println("ERRO: Jogador atual não é IA!");
            return;
        }

        System.out.println("IA: " + jogadorIA.getName() + " - Dificuldade: " + jogadorIA.getDifficulty());

        List<String> cartasDisponiveis = new ArrayList<>();
        for (int i = 0; i < gradeOperacoes.getChildren().size(); i++) {
            StackPane carta = (StackPane) gradeOperacoes.getChildren().get(i);
            if (!isCartaRevelada(carta) && !isCartaEncontrada(carta)) {
                cartasDisponiveis.add(carta.getId());
            }
        }
        
        System.out.println("Cartas disponíveis (operações): " + cartasDisponiveis.size());
        for (String id : cartasDisponiveis) {
            System.out.println("  - " + id);
        }
        
        if (cartasDisponiveis.isEmpty()) {
            System.err.println("ERRO: Nenhuma carta disponível para IA!");
            return;
        }

        String melhorJogada = jogadorIA.calcularMelhorJogada(cartasDisponiveis);
        System.out.println("Melhor jogada calculada: " + melhorJogada);
        
        if (melhorJogada != null) {
            StackPane cartaParaClicar = encontrarCartaPorId(melhorJogada);
            if (cartaParaClicar != null) {
                System.out.println("Carta encontrada para clicar: " + cartaParaClicar.getId());
                if (!aguardandoSegundaCarta) {
                    System.out.println("Primeira jogada da IA - revelando carta...");
                    primeiraCartaSelecionada = cartaParaClicar;
                    revelarCarta(cartaParaClicar);
                    registrarCartaParaIA(cartaParaClicar);
                    aguardandoSegundaCarta = true;

                    PauseTransition atrasoSegundaCarta = new PauseTransition(Duration.seconds(1.0));
                    atrasoSegundaCarta.setOnFinished(event -> {
                        System.out.println("Atraso da segunda jogada finalizado...");
                        executarSegundaJogadaIA();
                    });
                    atrasoSegundaCarta.play();
                    System.out.println("Atraso da segunda jogada iniciado");
                } else {
                    System.err.println("ERRO: IA tentando fazer primeira jogada mas aguardandoSegundaCarta é true!");
                }
            } else {
                System.err.println("ERRO: Carta para clicar é null!");
            }
        } else {
            System.err.println("ERRO: Melhor jogada é null!");
        }
    }

    /**
     * Executa a segunda jogada da IA.
     */
    private void executarSegundaJogadaIA() {
        System.out.println("=== EXECUTANDO SEGUNDA JOGADA DA IA ===");
        if (!turnoIA || !aguardandoSegundaCarta) {
            System.err.println("ERRO: turnoIA=" + turnoIA + ", aguardandoSegundaCarta=" + aguardandoSegundaCarta);
            return;
        }
        
        GameManager gerenciadorJogo = GameManager.getInstance();
        if (gerenciadorJogo == null) {
            System.err.println("ERRO: GameManager é null!");
            return;
        }
        
        AIPlayer jogadorIA = (AIPlayer) gerenciadorJogo.getCurrentPlayer();
        if (jogadorIA == null) {
            System.err.println("ERRO: Jogador atual não é IA!");
            return;
        }

        System.out.println("IA: " + jogadorIA.getName() + " - Segunda jogada");

        List<String> cartasDisponiveis = new ArrayList<>();
        for (int i = 0; i < gradeResultados.getChildren().size(); i++) {
            StackPane carta = (StackPane) gradeResultados.getChildren().get(i);
            if (!isCartaRevelada(carta) && !isCartaEncontrada(carta) && (primeiraCartaSelecionada == null || !carta.getId().equals(primeiraCartaSelecionada.getId()))) {
                cartasDisponiveis.add(carta.getId());
            }
        }
        
        System.out.println("Cartas disponíveis (resultados): " + cartasDisponiveis.size());
        for (String id : cartasDisponiveis) {
            System.out.println("  - " + id);
        }
        
        if (cartasDisponiveis.isEmpty()) {
            System.err.println("ERRO: Nenhuma carta disponível para segunda jogada da IA!");
            return;
        }

        String melhorJogada = jogadorIA.calcularMelhorJogada(cartasDisponiveis);
        System.out.println("Melhor jogada calculada (segunda): " + melhorJogada);
        
        if (melhorJogada != null) {
            StackPane cartaParaClicar = encontrarCartaPorId(melhorJogada);
            if (cartaParaClicar != null) {
                System.out.println("Segunda carta encontrada para clicar: " + cartaParaClicar.getId());
                segundaCartaSelecionada = cartaParaClicar;
                revelarCarta(cartaParaClicar);
                registrarCartaParaIA(cartaParaClicar);

                PauseTransition atrasoVerificacao = new PauseTransition(Duration.seconds(0.5));
                atrasoVerificacao.setOnFinished(event -> {
                    System.out.println("Atraso de verificação finalizado - verificando par...");
                    verificarPar();
                });
                atrasoVerificacao.play();
                System.out.println("Atraso de verificação iniciado");
            } else {
                System.err.println("ERRO: Segunda carta para clicar é null!");
            }
        } else {
            System.err.println("ERRO: Melhor jogada (segunda) é null!");
        }
    }

    /**
     * Encontra uma carta pelo ID.
     * @param idCarta ID da carta
     * @return StackPane da carta, ou null se não encontrada
     */
    private StackPane encontrarCartaPorId(String idCarta) {
        System.out.println("Procurando carta com ID: " + idCarta);

        for (int i = 0; i < gradeOperacoes.getChildren().size(); i++) {
            StackPane carta = (StackPane) gradeOperacoes.getChildren().get(i);
            if (carta.getId() != null && carta.getId().equals(idCarta)) {
                System.out.println("Carta encontrada no grid de operações: " + carta.getId());
                return carta;
            }
        }

        for (int i = 0; i < gradeResultados.getChildren().size(); i++) {
            StackPane carta = (StackPane) gradeResultados.getChildren().get(i);
            if (carta.getId() != null && carta.getId().equals(idCarta)) {
                System.out.println("Carta encontrada no grid de resultados: " + carta.getId());
                return carta;
            }
        }
        
        System.err.println("ERRO: Carta com ID '" + idCarta + "' não encontrada!");
        return null;
    }

    /**
     * Registra carta revelada na memória da IA.
     * @param carta Carta revelada
     */
    private void registrarCartaParaIA(StackPane carta) {
        GameManager gerenciadorJogo = GameManager.getInstance();
        if (gerenciadorJogo.getPlayer1() instanceof AIPlayer) {
            AIPlayer jogadorIA = (AIPlayer) gerenciadorJogo.getPlayer1();
            InformacoesCarta informacoesCarta = obterInformacoesCarta(carta);

            AIPlayer.CardInfo informacoesCartaIA = new AIPlayer.CardInfo(
                informacoesCarta.textoExibicao, 
                informacoesCarta.resultado, 
                informacoesCarta.isOperacao
            );
            
            jogadorIA.registrarCartaRevelada(carta.getId(), informacoesCartaIA);
        }
        
        if (gerenciadorJogo.getPlayer2() instanceof AIPlayer) {
            AIPlayer jogadorIA = (AIPlayer) gerenciadorJogo.getPlayer2();
            InformacoesCarta informacoesCarta = obterInformacoesCarta(carta);

            AIPlayer.CardInfo informacoesCartaIA = new AIPlayer.CardInfo(
                informacoesCarta.textoExibicao, 
                informacoesCarta.resultado, 
                informacoesCarta.isOperacao
            );
            
            jogadorIA.registrarCartaRevelada(carta.getId(), informacoesCartaIA);
        }
    }

    /**
     * Remove carta da memória da IA quando encontrada.
     * @param carta Carta encontrada
     */
    private void removerCartaDaIA(StackPane carta) {
        GameManager gerenciadorJogo = GameManager.getInstance();
        if (gerenciadorJogo.getPlayer1() instanceof AIPlayer) {
            AIPlayer jogadorIA = (AIPlayer) gerenciadorJogo.getPlayer1();
            jogadorIA.removerCartaDaMemoria(carta.getId());
        }
        
        if (gerenciadorJogo.getPlayer2() instanceof AIPlayer) {
            AIPlayer jogadorIA = (AIPlayer) gerenciadorJogo.getPlayer2();
            jogadorIA.removerCartaDaMemoria(carta.getId());
        }
    }
    
    /**
     * Registra um par de cartas abertas na memória das IAs.
     * @param carta1 Primeira carta do par
     * @param carta2 Segunda carta do par
     */
    private void registrarParParaIA(StackPane carta1, StackPane carta2) {
        GameManager gerenciadorJogo = GameManager.getInstance();
        if (gerenciadorJogo.getPlayer1() instanceof AIPlayer) {
            AIPlayer jogadorIA = (AIPlayer) gerenciadorJogo.getPlayer1();
            jogadorIA.registrarParAberto(carta1.getId(), carta2.getId());
        }
        
        if (gerenciadorJogo.getPlayer2() instanceof AIPlayer) {
            AIPlayer jogadorIA = (AIPlayer) gerenciadorJogo.getPlayer2();
            jogadorIA.registrarParAberto(carta1.getId(), carta2.getId());
        }
    }

    /**
     * Popula um grid com cartas.
     * @param grade GridPane a ser populado
     * @param prefixo Prefixo para identificação das cartas
     */
    private void popularGrade(GridPane grade, String prefixo) {
        if (grade == null) {
            System.err.println("ERRO: Grid é null para prefixo: " + prefixo);
            return;
        }
        
        System.out.println("Populando grade: " + prefixo + " com " + NUMERO_LINHAS + "x" + NUMERO_COLUNAS + " cartas");
        
        for (int linha = 0; linha < NUMERO_LINHAS; linha++) {
            for (int coluna = 0; coluna < NUMERO_COLUNAS; coluna++) {
                StackPane painelCarta = criarPainelCarta(prefixo, linha, coluna);
                if (painelCarta != null) {
                    grade.add(painelCarta, coluna, linha);
                    System.out.println("Adicionada carta " + painelCarta.getId() + " na posição (" + coluna + "," + linha + ")");
                } else {
                    System.err.println("ERRO: Falha ao criar carta para posição (" + coluna + "," + linha + ")");
                }
            }
        }
        
        System.out.println("Grade " + prefixo + " populada com " + grade.getChildren().size() + " cartas");
    }

    /**
     * Cria um painel de carta individual.
     * @param prefixo Prefixo da carta
     * @param linha Linha da carta
     * @param coluna Coluna da carta
     * @return StackPane representando a carta
     */
    private StackPane criarPainelCarta(String prefixo, int linha, int coluna) {
        if (imagemVersoCarta == null) {
            System.err.println("ERRO: imagemVersoCarta é null! Criando carta com fallback.");
            StackPane painelCarta = new StackPane();
            painelCarta.setId("carta-" + prefixo + "-" + linha + "-" + coluna);
            painelCarta.getStyleClass().add("carta");
            painelCarta.setStyle("-fx-background-color: #4A90E2; -fx-border-color: black; -fx-border-width: 2; " +
                              "-fx-background-radius: 5; -fx-border-radius: 5; -fx-min-width: 90px; -fx-min-height: 90px; " +
                              "-fx-pref-width: 90px; -fx-pref-height: 90px; -fx-max-width: 90px; -fx-max-height: 90px;");
            
            Label rotuloCarta = new Label("?");
            rotuloCarta.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; " +
                              "-fx-padding: 10px; -fx-alignment: center;");
            painelCarta.getChildren().add(rotuloCarta);
            
            painelCarta.setOnMouseClicked(event -> manipularCliqueCarta(painelCarta));
            return painelCarta;
        }

        ImageView versoCarta = new ImageView(imagemVersoCarta);
        versoCarta.setFitWidth(90);
        versoCarta.setFitHeight(90);
        versoCarta.setPreserveRatio(false);
        versoCarta.setSmooth(true);
        versoCarta.setCache(true);

        ImageView frenteCarta = new ImageView();
        frenteCarta.setFitWidth(90);
        frenteCarta.setFitHeight(90);
        frenteCarta.setPreserveRatio(false);
        frenteCarta.setVisible(false);

        Label rotuloCarta = new Label("?");
        rotuloCarta.setVisible(false);
        rotuloCarta.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; " +
                          "-fx-padding: 10px; -fx-alignment: center;");

        StackPane painelCarta = new StackPane(versoCarta, frenteCarta, rotuloCarta);
        painelCarta.setId("carta-" + prefixo + "-" + linha + "-" + coluna);
        painelCarta.getStyleClass().add("carta");
        painelCarta.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5; " +
                          "-fx-min-width: 90px; -fx-min-height: 90px; -fx-pref-width: 90px; -fx-pref-height: 90px; " +
                          "-fx-max-width: 90px; -fx-max-height: 90px;");

        painelCarta.setOnMouseClicked(event -> manipularCliqueCarta(painelCarta));
        
        return painelCarta;
    }

    /**
     * Manipula o clique em uma carta.
     * @param cartaClicada Carta que foi clicada
     */
    private void manipularCliqueCarta(StackPane cartaClicada) {
        if (turnoIA) {
            return;
        }
        
        if (cartaClicada == null || isCartaRevelada(cartaClicada) || isCartaEncontrada(cartaClicada)) {
            return;
        }

        registrarCartaParaIA(cartaClicada);

        if (!aguardandoSegundaCarta) {
            if (!isCartaOperacao(cartaClicada)) {
                AlertUtils.mostrarErro("Jogada inválida", "A primeira carta deve ser do grid de operações.");
                return;
            }
            primeiraCartaSelecionada = cartaClicada;
            revelarCarta(cartaClicada);
            aguardandoSegundaCarta = true;
        } else {
            if (!isCartaResultado(cartaClicada)) {
                AlertUtils.mostrarErro("Jogada inválida", "A segunda carta deve ser do grid de resultados.");
                return;
            }
            if (cartaClicada != primeiraCartaSelecionada) {
                segundaCartaSelecionada = cartaClicada;
                revelarCarta(cartaClicada);
                registrarCartaParaIA(cartaClicada);
                verificarPar();
            }
        }
    }

    /**
     * Verifica se uma carta está revelada.
     * @param carta Carta a ser verificada
     * @return true se a carta está revelada
     */
    private boolean isCartaRevelada(StackPane carta) {
        if (carta.getChildren().size() >= 2) {
            return !carta.getChildren().get(0).isVisible();
        }
        return false;
    }

    /**
     * Verifica se uma carta já foi encontrada (matched).
     * @param carta Carta a ser verificada
     * @return true se a carta foi encontrada
     */
    private boolean isCartaEncontrada(StackPane carta) {

        return carta.getStyleClass().contains("matched");
    }

    /**
     * Revela o conteúdo de uma carta.
     * @param carta Carta a ser revelada
     */
    private void revelarCarta(StackPane carta) {
        if (carta == null) {
            System.err.println("ERRO: Carta é null em revelarCarta");
            return;
        }
        
        if (carta.getChildren().size() < 3) {
            System.err.println("ERRO: Carta não tem elementos suficientes: " + carta.getChildren().size());
            return;
        }
        
        carta.getChildren().get(0).setVisible(false);

        ImageView frenteCarta = (ImageView) carta.getChildren().get(1);
        frenteCarta.setVisible(true);

        InformacoesCarta informacoesCarta = obterInformacoesCarta(carta);

        if (informacoesCarta.tipoOperacao != null) {
            Image imagemRevelacao = obterImagemRevelacaoParaOperacao(informacoesCarta.tipoOperacao);
            if (imagemRevelacao != null) {
                frenteCarta.setImage(imagemRevelacao);
            } else {
                System.err.println("ERRO: Imagem de revelação é null para tipo: " + informacoesCarta.tipoOperacao);
                frenteCarta.setImage(imagemRevelacaoSoma != null ? imagemRevelacaoSoma : imagemVersoCarta);
            }
        } else {
            System.err.println("ERRO: Tipo de operação é null");
            frenteCarta.setImage(imagemRevelacaoSoma != null ? imagemRevelacaoSoma : imagemVersoCarta);
        }

        Label rotuloCarta = (Label) carta.getChildren().get(2);
        rotuloCarta.setText(informacoesCarta.textoExibicao);
        rotuloCarta.setVisible(true);

        AudioManager.getInstance().tocarSomRevelarCarta();
    }

    /**
     * Verifica se as duas cartas selecionadas formam um par.
     */
    private void verificarPar() {
        InformacoesCarta informacoes1 = obterInformacoesCarta(primeiraCartaSelecionada);
        InformacoesCarta informacoes2 = obterInformacoesCarta(segundaCartaSelecionada);
        
        boolean isPar = false;

        if (isCartaOperacao(primeiraCartaSelecionada) && isCartaResultado(segundaCartaSelecionada)) {
            isPar = informacoes1.resultado == informacoes2.resultado;
            System.out.println("Verificando par: " + informacoes1.textoExibicao + " (" + informacoes1.resultado + ") == " + informacoes2.textoExibicao + " (" + informacoes2.resultado + ") = " + isPar);
        } else if (isCartaResultado(primeiraCartaSelecionada) && isCartaOperacao(segundaCartaSelecionada)) {
            isPar = informacoes2.resultado == informacoes1.resultado;
            System.out.println("Verificando par: " + informacoes2.textoExibicao + " (" + informacoes2.resultado + ") == " + informacoes1.textoExibicao + " (" + informacoes1.resultado + ") = " + isPar);
        }
        
        if (isPar) {
            System.out.println("PAR ENCONTRADO!");
            manipularParEncontrado();
        } else {
            System.out.println("NÃO É PAR!");
            manipularParNaoEncontrado();
        }
    }

    /**
     * Manipula quando um par é encontrado.
     */
    private void manipularParEncontrado() {
        primeiraCartaSelecionada.getStyleClass().removeAll("erro-match");
        segundaCartaSelecionada.getStyleClass().removeAll("erro-match");
        primeiraCartaSelecionada.getStyleClass().add("matched");
        segundaCartaSelecionada.getStyleClass().add("matched");

        removerCartaDaIA(primeiraCartaSelecionada);
        removerCartaDaIA(segundaCartaSelecionada);

        paresEncontrados++;

        GameManager gerenciadorJogo = GameManager.getInstance();
        Player jogador = gerenciadorJogo.getCurrentPlayer();
        jogador.adicionarPontos(PONTOS_ACERTO);

        atualizarPontuacoesJogadores();

        AudioManager.getInstance().tocarSomMatch();

        if (paresEncontrados >= TOTAL_PARES) {
            manipularFimJogo();
        } else {
            resetarSelecaoCartas();

            if (gerenciadorJogo.getCurrentPlayer() instanceof AIPlayer) {
                PauseTransition atrasoIA = new PauseTransition(Duration.seconds(0.5));
                atrasoIA.setOnFinished(event -> iniciarTurnoIA());
                atrasoIA.play();
            }
        }
    }

    /**
     * Manipula quando um par não é encontrado.
     */
    private void manipularParNaoEncontrado() {
        AudioManager.getInstance().tocarSomNaoMatch();
        
        registrarParParaIA(primeiraCartaSelecionada, segundaCartaSelecionada);
        
        GameManager gerenciadorJogo = GameManager.getInstance();
        Player jogador = gerenciadorJogo.getCurrentPlayer();
        jogador.adicionarPontos(PONTOS_ERRO);
        atualizarPontuacoesJogadores();
        
        primeiraCartaSelecionada.getStyleClass().removeAll("matched");
        segundaCartaSelecionada.getStyleClass().removeAll("matched");
        primeiraCartaSelecionada.getStyleClass().add("erro-match");
        segundaCartaSelecionada.getStyleClass().add("erro-match");
        PauseTransition atraso = new PauseTransition(Duration.seconds(1.5));
        atraso.setOnFinished(event -> {
            esconderCarta(primeiraCartaSelecionada);
            esconderCarta(segundaCartaSelecionada);
            resetarSelecaoCartas();
            gerenciadorJogo.trocarTurno();
            atualizarIndicadorTurno();
        });
        atraso.play();
    }

    /**
     * Reseta a seleção de cartas.
     */
    private void resetarSelecaoCartas() {
        primeiraCartaSelecionada = null;
        segundaCartaSelecionada = null;
        aguardandoSegundaCarta = false;
    }

    /**
     * Manipula o fim do jogo.
     */
    private void manipularFimJogo() {
        GameManager gerenciadorJogo = GameManager.getInstance();
        Player vencedor = null;
        boolean empate = false;
        
        if (gerenciadorJogo.getPlayer1().getScore() == gerenciadorJogo.getPlayer2().getScore()) {
            empate = true;
        } else {
            vencedor = gerenciadorJogo.getPlayer1().getScore() > gerenciadorJogo.getPlayer2().getScore() ? gerenciadorJogo.getPlayer1() : gerenciadorJogo.getPlayer2();
        }
        gerenciadorJogo.setVencedor(empate ? null : vencedor);
        
        if (empate) {
            AudioManager.getInstance().tocarSomVitoria();
        } else if (gerenciadorJogo.getGameMode() == GameManager.GameMode.PVE) {
            if (vencedor instanceof AIPlayer) {
                AudioManager.getInstance().tocarSomDerrota();
            } else {
                AudioManager.getInstance().tocarSomVitoria();
            }
        } else {
            AudioManager.getInstance().tocarSomVitoria();
        }
        
        jogodamemoria.memorymath.transitions.SceneManager.getInstance().carregarCena("/fxml/vitoria-view.fxml");
    }

    /**
     * Atualiza os scores dos jogadores na interface.
     */
    private void atualizarPontuacoesJogadores() {
        GameManager gerenciadorJogo = GameManager.getInstance();
        pontuacaoJogador1.setText(String.valueOf(gerenciadorJogo.getPlayer1().getScore()));
        pontuacaoJogador2.setText(String.valueOf(gerenciadorJogo.getPlayer2().getScore()));
    }

    /**
     * Verifica se uma carta é de operação.
     * @param carta Carta a ser verificada
     * @return true se é carta de operação
     */
    private boolean isCartaOperacao(StackPane carta) {
        return carta.getId().contains("op");
    }

    /**
     * Verifica se uma carta é de resultado.
     * @param carta Carta a ser verificada
     * @return true se é carta de resultado
     */
    private boolean isCartaResultado(StackPane carta) {
        return carta.getId().contains("re");
    }

    /**
     * Obtém as informações de uma carta baseada em sua posição.
     * @param carta Carta para obter informações
     * @return InformacoesCarta com as informações da carta
     */
    private InformacoesCarta obterInformacoesCarta(StackPane carta) {
        if (carta == null) {
            System.err.println("ERRO: Carta é null em obterInformacoesCarta");
            return new InformacoesCarta("?", 0, Card.OperationType.SOMA, false);
        }
        
        String idCarta = carta.getId();
        if (idCarta == null || idCarta.isEmpty()) {
            System.err.println("ERRO: ID da carta é null ou vazio");
            return new InformacoesCarta("?", 0, Card.OperationType.SOMA, false);
        }
        
        String[] partes = idCarta.split("-");
        if (partes.length < 4) {
            System.err.println("ERRO: ID da carta inválido: " + idCarta);
            return new InformacoesCarta("?", 0, Card.OperationType.SOMA, false);
        }
        
        try {
            int linha = Integer.parseInt(partes[2]);
            int coluna = Integer.parseInt(partes[3]);
            
            if (isCartaOperacao(carta)) {
                if (gerador == null) {
                    System.err.println("ERRO: Gerador é null");
                    return new InformacoesCarta("?", 0, Card.OperationType.SOMA, true);
                }
                
                int operando1 = gerador.getMatrizOperandos1()[linha][coluna];
                int operando2 = gerador.getMatrizOperandos2()[linha][coluna];
                int resultado = gerador.getMatrizResultados()[linha][coluna];
                Card.OperationType tipoOperacao = obterTipoOperacao(linha, coluna);
                
                String textoExibicao = operando1 + obterSimboloOperacao(tipoOperacao) + operando2;
                return new InformacoesCarta(textoExibicao, resultado, tipoOperacao, true);
            } else {
                if (resultadosEmbaralhados == null) {
                    System.err.println("ERRO: resultadosEmbaralhados é null");
                    return new InformacoesCarta("?", 0, Card.OperationType.SOMA, false);
                }
                
                int resultado = resultadosEmbaralhados[linha][coluna];
                Card.OperationType tipoOperacao = obterTipoOperacaoParaResultado(linha, coluna);
                return new InformacoesCarta(String.valueOf(resultado), resultado, tipoOperacao, false);
            }
        } catch (NumberFormatException e) {
            System.err.println("ERRO ao parsear linha/coluna: " + e.getMessage());
            return new InformacoesCarta("?", 0, Card.OperationType.SOMA, false);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("ERRO: Índice fora dos limites: " + e.getMessage());
            return new InformacoesCarta("?", 0, Card.OperationType.SOMA, false);
        } catch (Exception e) {
            System.err.println("ERRO inesperado em obterInformacoesCarta: " + e.getMessage());
            return new InformacoesCarta("?", 0, Card.OperationType.SOMA, false);
        }
    }

    /**
     * Obtém o tipo de operação baseado na posição da matriz.
     * @param linha Linha da matriz
     * @param coluna Coluna da matriz
     * @return Tipo de operação
     */
    private Card.OperationType obterTipoOperacao(int linha, int coluna) {
        int operando1 = gerador.getMatrizOperandos1()[linha][coluna];
        int operando2 = gerador.getMatrizOperandos2()[linha][coluna];
        int resultado = gerador.getMatrizResultados()[linha][coluna];
        
        if (operando1 + operando2 == resultado) return Card.OperationType.SOMA;
        if (operando1 - operando2 == resultado) return Card.OperationType.SUBTRACAO;
        if (operando1 * operando2 == resultado) return Card.OperationType.MULTIPLICACAO;
        if (operando2 != 0 && operando1 / operando2 == resultado) return Card.OperationType.DIVISAO;
        
        return Card.OperationType.SOMA;
    }

    /**
     * Obtém o tipo de operação para cartas de resultado baseado na posição.
     * Para cartas de resultado, precisamos determinar qual operação gerou aquele resultado.
     * @param linha Linha da matriz
     * @param coluna Coluna da matriz
     * @return Tipo de operação
     */
    private Card.OperationType obterTipoOperacaoParaResultado(int linha, int coluna) {
        int resultado = resultadosEmbaralhados[linha][coluna];

        for (int i = 0; i < NUMERO_LINHAS; i++) {
            for (int j = 0; j < NUMERO_COLUNAS; j++) {
                int operando1 = gerador.getMatrizOperandos1()[i][j];
                int operando2 = gerador.getMatrizOperandos2()[i][j];
                int resultadoOperacao = gerador.getMatrizResultados()[i][j];
                
                if (resultadoOperacao == resultado) {
                    if (operando1 + operando2 == resultadoOperacao) return Card.OperationType.SOMA;
                    if (operando1 - operando2 == resultadoOperacao) return Card.OperationType.SUBTRACAO;
                    if (operando1 * operando2 == resultadoOperacao) return Card.OperationType.MULTIPLICACAO;
                    if (operando2 != 0 && operando1 / operando2 == resultadoOperacao) return Card.OperationType.DIVISAO;
                }
            }
        }

        int indice = linha * NUMERO_COLUNAS + coluna;
        switch (indice % 4) {
            case 0: return Card.OperationType.SOMA;
            case 1: return Card.OperationType.SUBTRACAO;
            case 2: return Card.OperationType.MULTIPLICACAO;
            case 3: return Card.OperationType.DIVISAO;
            default: return Card.OperationType.SOMA;
        }
    }

    /**
     * Obtém o símbolo da operação.
     * @param tipoOperacao Tipo de operação
     * @return Símbolo da operação
     */
    private String obterSimboloOperacao(Card.OperationType tipoOperacao) {
        switch (tipoOperacao) {
            case SOMA: return "+";
            case SUBTRACAO: return "-";
            case MULTIPLICACAO: return "×";
            case DIVISAO: return "÷";
            default: return "+";
        }
    }

    /**
     * Esconde o conteúdo de uma carta.
     * @param carta Carta a ser escondida
     */
    private void esconderCarta(StackPane carta) {
        if (carta.getChildren().size() >= 3) {
            carta.getChildren().get(0).setVisible(true);

            carta.getChildren().get(1).setVisible(false);

            carta.getChildren().get(2).setVisible(false);
        }
    }

    /**
     * Classe interna para armazenar informações de uma carta.
     */
    private static class InformacoesCarta {
        final String textoExibicao;
        final int resultado;
        final Card.OperationType tipoOperacao;
        final boolean isOperacao;

        InformacoesCarta(String textoExibicao, int resultado, Card.OperationType tipoOperacao, boolean isOperacao) {
            this.textoExibicao = textoExibicao;
            this.resultado = resultado;
            this.tipoOperacao = tipoOperacao;
            this.isOperacao = isOperacao;
        }
    }
}