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

    // Constantes do jogo
    private static final int NUMERO_COLUNAS = 3;
    private static final int NUMERO_LINHAS = 4;
    private static final int TOTAL_PARES = 12; // 12 pares = 24 cartas individuais

    // Caminhos das imagens das cartas
    private static final String CAMINHO_IMAGEM_VERSO_CARTA = "/images/verso-carta.png";
    private static final String CAMINHO_IMAGEM_REVELACAO_SOMA = "/images/amareloEstimulo.jpg";
    private static final String CAMINHO_IMAGEM_REVELACAO_SUBTRACAO = "/images/azulCerebro.png";
    private static final String CAMINHO_IMAGEM_REVELACAO_MULTIPLICACAO = "/images/roxoDesafio.png";
    private static final String CAMINHO_IMAGEM_REVELACAO_DIVISAO = "/images/verdeLogico.jpg";

    // Elementos da interface
    @FXML private GridPane gradeOperacoes;
    @FXML private GridPane gradeResultados;
    @FXML private Label rotuloIndicadorTurno;
    @FXML private Label nomeJogador1;
    @FXML private Label pontuacaoJogador1;
    @FXML private Label nomeJogador2;
    @FXML private Label pontuacaoJogador2;

    // Imagens das cartas
    private Image imagemVersoCarta;
    private Image imagemRevelacaoSoma;
    private Image imagemRevelacaoSubtracao;
    private Image imagemRevelacaoMultiplicacao;
    private Image imagemRevelacaoDivisao;

    // Estado do jogo
    private Gerador gerador;
    private StackPane primeiraCartaSelecionada;
    private StackPane segundaCartaSelecionada;
    private boolean aguardandoSegundaCarta = false;
    private int paresEncontrados = 0;
    private final int TOTAL_CARTAS = NUMERO_LINHAS * NUMERO_COLUNAS * 2; // 24 cartas individuais (12 pares)

    private Integer[][] resultadosEmbaralhados;

    private boolean turnoIA = false;
    private PauseTransition atrasoIA;
    
    // Constantes para o sistema de pontuação
    private static final int PONTOS_ACERTO = 5;
    private static final int PONTOS_ERRO = -1;

    /**
     * Inicializa o controlador do jogo, carregando recursos e configurando o tabuleiro.
     */
    @FXML
    public void inicializar() {
        paresEncontrados = 0;
        
        carregarTodasImagensCartas();
        configurarJogo();
        configurarLayoutResponsivo();
        popularGrades();
        configurarInformacoesJogadores();
        atualizarIndicadorTurno();
    }

    /**
     * Encerra a partida e retorna ao menu principal.
     */
    @FXML
    private void encerrarPartida() {
        SceneManager.getInstance().carregarCena("/fxml/menu-view.fxml");
    }

    /**
     * Configura o layout responsivo para diferentes tamanhos de tela.
     */
    private void configurarLayoutResponsivo() {
        gradeOperacoes.setHgap(8);
        gradeOperacoes.setVgap(8);
        gradeResultados.setHgap(8);
        gradeResultados.setVgap(8);

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
    }

    /**
     * Configura o jogo inicializando o gerador e as operações.
     */
    private void configurarJogo() {
        GameManager gerenciadorJogo = GameManager.getInstance();
        List<Card.OperationType> operacoesSelecionadas = gerenciadorJogo.getSelectedOperations();
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

        gerador = new Gerador(NUMERO_LINHAS, NUMERO_COLUNAS, arrayOperacoes);
        embaralharResultados();
    }

    /**
     * Embaralha os resultados para criar posições aleatórias no grid de resultados.
     */
    private void embaralharResultados() {
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
    }

    /**
     * Carrega todas as imagens das cartas (verso e faces coloridas).
     */
    private void carregarTodasImagensCartas() {
        carregarImagemVersoCarta();
        carregarImagensRevelacao();
    }

    /**
     * Carrega a imagem do verso da carta com tratamento de erro.
     */
    private void carregarImagemVersoCarta() {
        try {
            imagemVersoCarta = ImageUtils.carregarImagem(CAMINHO_IMAGEM_VERSO_CARTA);

            if (imagemVersoCarta.isError()) {
                throw new RuntimeException("Erro ao carregar a imagem do verso da carta");
            }
        } catch (ResourceLoadException e) {
            AlertUtils.mostrarErro("Erro ao carregar recursos do jogo", e.getMessage());
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
                return imagemVersoCarta; // Fallback
        }
    }

    /**
     * Popula os grids de operações e resultados.
     */
    private void popularGrades() {
        popularGrade(gradeOperacoes, "op");
        popularGrade(gradeResultados, "re");
    }

    /**
     * Configura as informações dos jogadores na interface.
     */
    private void configurarInformacoesJogadores() {
        GameManager gerenciadorJogo = GameManager.getInstance();
        nomeJogador1.setText(gerenciadorJogo.getPlayer1().getName());
        pontuacaoJogador1.setText(String.valueOf(gerenciadorJogo.getPlayer1().getScore()));
        nomeJogador2.setText(gerenciadorJogo.getPlayer2().getName());
        pontuacaoJogador2.setText(String.valueOf(gerenciadorJogo.getPlayer2().getScore()));
    }

    /**
     * Atualiza o indicador de turno.
     */
    private void atualizarIndicadorTurno() {
        GameManager gerenciadorJogo = GameManager.getInstance();
        rotuloIndicadorTurno.setText("Vez de: " + gerenciadorJogo.getCurrentPlayer().getName());

        if (gerenciadorJogo.getCurrentPlayer() instanceof AIPlayer) {
            turnoIA = true;
            if (!aguardandoSegundaCarta) {
                iniciarTurnoIA();
            }
        } else {
            turnoIA = false;
        }
    }

    /**
     * Inicia o turno da IA.
     */
    private void iniciarTurnoIA() {
        if (!turnoIA) return;

        atrasoIA = new PauseTransition(Duration.seconds(1.0));
        atrasoIA.setOnFinished(event -> {
            executarJogadaIA();
        });
        atrasoIA.play();
    }

    /**
     * Executa a jogada da IA.
     */
    private void executarJogadaIA() {
        if (!turnoIA) return;
        
        GameManager gerenciadorJogo = GameManager.getInstance();
        AIPlayer jogadorIA = (AIPlayer) gerenciadorJogo.getCurrentPlayer();

        List<String> cartasDisponiveis = new ArrayList<>();
        for (int i = 0; i < gradeOperacoes.getChildren().size(); i++) {
            StackPane carta = (StackPane) gradeOperacoes.getChildren().get(i);
            if (!isCartaRevelada(carta) && !isCartaEncontrada(carta)) {
                cartasDisponiveis.add(carta.getId());
            }
        }
        
        if (cartasDisponiveis.isEmpty()) {
            return;
        }

        String melhorJogada = jogadorIA.calcularMelhorJogada(cartasDisponiveis);
        
        if (melhorJogada != null) {
            StackPane cartaParaClicar = encontrarCartaPorId(melhorJogada);
            if (cartaParaClicar != null) {
                if (!aguardandoSegundaCarta) {
                    primeiraCartaSelecionada = cartaParaClicar;
                    revelarCarta(cartaParaClicar);
                    registrarCartaParaIA(cartaParaClicar);
                    aguardandoSegundaCarta = true;

                    PauseTransition atrasoSegundaCarta = new PauseTransition(Duration.seconds(1.0));
                    atrasoSegundaCarta.setOnFinished(event -> executarSegundaJogadaIA());
                    atrasoSegundaCarta.play();
                }
            }
        }
    }

    /**
     * Executa a segunda jogada da IA.
     */
    private void executarSegundaJogadaIA() {
        if (!turnoIA || !aguardandoSegundaCarta) return;
        
        GameManager gerenciadorJogo = GameManager.getInstance();
        AIPlayer jogadorIA = (AIPlayer) gerenciadorJogo.getCurrentPlayer();

        List<String> cartasDisponiveis = new ArrayList<>();
        for (int i = 0; i < gradeResultados.getChildren().size(); i++) {
            StackPane carta = (StackPane) gradeResultados.getChildren().get(i);
            if (!isCartaRevelada(carta) && !isCartaEncontrada(carta) && (primeiraCartaSelecionada == null || !carta.getId().equals(primeiraCartaSelecionada.getId()))) {
                cartasDisponiveis.add(carta.getId());
            }
        }
        
        if (cartasDisponiveis.isEmpty()) {
            return;
        }

        String melhorJogada = jogadorIA.calcularMelhorJogada(cartasDisponiveis);
        
        if (melhorJogada != null) {
            StackPane cartaParaClicar = encontrarCartaPorId(melhorJogada);
            if (cartaParaClicar != null) {
                segundaCartaSelecionada = cartaParaClicar;
                revelarCarta(cartaParaClicar);
                registrarCartaParaIA(cartaParaClicar);

                PauseTransition atrasoVerificacao = new PauseTransition(Duration.seconds(0.5));
                atrasoVerificacao.setOnFinished(event -> verificarPar());
                atrasoVerificacao.play();
            }
        }
    }

    /**
     * Encontra uma carta pelo ID.
     * @param idCarta ID da carta
     * @return StackPane da carta, ou null se não encontrada
     */
    private StackPane encontrarCartaPorId(String idCarta) {
        for (int i = 0; i < gradeOperacoes.getChildren().size(); i++) {
            StackPane carta = (StackPane) gradeOperacoes.getChildren().get(i);
            if (carta.getId().equals(idCarta)) {
                return carta;
            }
        }

        for (int i = 0; i < gradeResultados.getChildren().size(); i++) {
            StackPane carta = (StackPane) gradeResultados.getChildren().get(i);
            if (carta.getId().equals(idCarta)) {
                return carta;
            }
        }
        
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
            System.err.println("Grid é null para prefixo: " + prefixo);
            return;
        }
        
        for (int linha = 0; linha < NUMERO_LINHAS; linha++) {
            for (int coluna = 0; coluna < NUMERO_COLUNAS; coluna++) {
                StackPane painelCarta = criarPainelCarta(prefixo, linha, coluna);
                grade.add(painelCarta, coluna, linha);
            }
        }
    }

    /**
     * Cria um painel de carta individual.
     * @param prefixo Prefixo da carta
     * @param linha Linha da carta
     * @param coluna Coluna da carta
     * @return StackPane representando a carta
     */
    private StackPane criarPainelCarta(String prefixo, int linha, int coluna) {
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
        painelCarta.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");

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
        if (carta.getChildren().size() >= 3) {
            carta.getChildren().get(0).setVisible(false);

            ImageView frenteCarta = (ImageView) carta.getChildren().get(1);
            frenteCarta.setVisible(true);

            InformacoesCarta informacoesCarta = obterInformacoesCarta(carta);

            if (informacoesCarta.tipoOperacao != null) {
                frenteCarta.setImage(obterImagemRevelacaoParaOperacao(informacoesCarta.tipoOperacao));
            } else {
                frenteCarta.setImage(imagemRevelacaoSoma);
            }

            Label rotuloCarta = (Label) carta.getChildren().get(2);
            rotuloCarta.setText(informacoesCarta.textoExibicao);
            rotuloCarta.setVisible(true);

            AudioManager.getInstance().tocarSomRevelarCarta();
        }
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
        String idCarta = carta.getId();
        String[] partes = idCarta.split("-");
        int linha = Integer.parseInt(partes[2]);
        int coluna = Integer.parseInt(partes[3]);
        
        if (isCartaOperacao(carta)) {
            int operando1 = gerador.getMatrizOperandos1()[linha][coluna];
            int operando2 = gerador.getMatrizOperandos2()[linha][coluna];
            int resultado = gerador.getMatrizResultados()[linha][coluna];
            Card.OperationType tipoOperacao = obterTipoOperacao(linha, coluna);
            
            String textoExibicao = operando1 + obterSimboloOperacao(tipoOperacao) + operando2;
            return new InformacoesCarta(textoExibicao, resultado, tipoOperacao, true);
        } else {
            int resultado = resultadosEmbaralhados[linha][coluna];
            Card.OperationType tipoOperacao = obterTipoOperacaoParaResultado(linha, coluna);
            return new InformacoesCarta(String.valueOf(resultado), resultado, tipoOperacao, false);
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