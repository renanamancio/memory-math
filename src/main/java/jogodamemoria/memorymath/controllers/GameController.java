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
 * @author MemoryMath Team
 * @version 2.0
 */
public class GameController {

    private static final int NUM_COLS = 3;
    private static final int NUM_ROWS = 4;
    private static final int TOTAL_PAIRS = 12;

    private static final String CARD_BACK_IMAGE_PATH = "/images/verso-carta.png";
    private static final String CARD_REVEL_IMAGE_SOMA = "/images/amareloEstimulo.jpg";
    private static final String CARD_REVEL_IMAGE_SUBTRACAO = "/images/azulCerebro.png";
    private static final String CARD_REVEL_IMAGE_MULTIPLICACAO = "/images/roxoDesafio.png";
    private static final String CARD_REVEL_IMAGE_DIVISAO = "/images/verdeLogico.jpg";

    @FXML private GridPane operacoesGrid;
    @FXML private GridPane resultadosGrid;
    @FXML private Label turnIndicatorLabel;
    @FXML private Label jogador1Nome;
    @FXML private Label jogador1Score;
    @FXML private Label jogador2Nome;
    @FXML private Label jogador2Score;

    private Image cardBackImage;
    private Image cardRevelImageSoma;
    private Image cardRevelImageSubtracao;
    private Image cardRevelImageMultiplicacao;
    private Image cardRevelImageDivisao;

    private Gerador gerador;
    private StackPane primeiraCartaSelecionada;
    private StackPane segundaCartaSelecionada;
    private boolean aguardandoSegundaCarta = false;
    private int cartasEncontradas = 0;
    private final int TOTAL_CARTAS = NUM_ROWS * NUM_COLS;

    private Integer[][] resultadosEmbaralhados;

    private boolean turnoIA = false;
    private PauseTransition delayIA;
    
    // Constantes para o novo sistema de pontuação
    private static final int PONTOS_ACERTO = 5;
    private static final int PONTOS_ERRO = -1;

    /**
     * Inicializa o controlador do jogo, carregando recursos e configurando o tabuleiro.
     */
    @FXML
    public void initialize() {
        // Garante que a contagem seja inicializada corretamente
        cartasEncontradas = 0;
        
        loadAllCardImages();
        setupGame();
        setupResponsiveLayout();
        populateGrids();
        setupPlayerInfo();
        updateTurnIndicator();
        
        System.out.println("Jogo inicializado! Total de cartas: " + TOTAL_CARTAS + " (12 pares)");
        System.out.println("Contagem inicial de cartas encontradas: " + cartasEncontradas);
        System.out.println("NUM_ROWS = " + NUM_ROWS + ", NUM_COLS = " + NUM_COLS);
        System.out.println("TOTAL_CARTAS = " + TOTAL_CARTAS + " (deve ser 12)");
    }

    @FXML
    private void encerraPartida() {
        SceneManager.getInstance().carregarCena("/fxml/menu-view.fxml");
    }

    /**
     * Configura o layout responsivo para diferentes tamanhos de tela.
     */
    private void setupResponsiveLayout() {
        operacoesGrid.setHgap(8);
        operacoesGrid.setVgap(8);
        resultadosGrid.setHgap(8);
        resultadosGrid.setVgap(8);

        for (int i = 0; i < NUM_COLS; i++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setPercentWidth(100.0 / NUM_COLS);
            colConstraints.setHgrow(Priority.ALWAYS);
            operacoesGrid.getColumnConstraints().add(colConstraints);
            resultadosGrid.getColumnConstraints().add(colConstraints);
        }
        
        for (int i = 0; i < NUM_ROWS; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100.0 / NUM_ROWS);
            rowConstraints.setVgrow(Priority.ALWAYS);
            operacoesGrid.getRowConstraints().add(rowConstraints);
            resultadosGrid.getRowConstraints().add(rowConstraints);
        }
    }

    /**
     * Configura o jogo inicializando o gerador e as operações.
     */
    private void setupGame() {
        GameManager gm = GameManager.getInstance();
        List<Card.OperationType> operacoesSelecionadas = gm.getSelectedOperations();
        int[] operacoesArray = new int[4];
        
        // Configura array de operações selecionadas
        for (Card.OperationType op : operacoesSelecionadas) {
            switch (op) {
                case SOMA:
                    operacoesArray[Gerador.SOMA] = 1;
                    break;
                case SUBTRACAO:
                    operacoesArray[Gerador.SUBTRACAO] = 1;
                    break;
                case MULTIPLICACAO:
                    operacoesArray[Gerador.MULTIPLICACAO] = 1;
                    break;
                case DIVISAO:
                    operacoesArray[Gerador.DIVISAO] = 1;
                    break;
            }
        }

        gerador = new Gerador(NUM_ROWS, NUM_COLS, operacoesArray);
        embaralharResultados();

        System.out.println("=== CONFIGURAÇÃO DO JOGO ===");
        System.out.println("Operações selecionadas: " + operacoesSelecionadas);
        System.out.println("Grid: " + NUM_ROWS + "x" + NUM_COLS + " (" + TOTAL_PAIRS + " pares)");
        System.out.println("=== FIM DA CONFIGURAÇÃO ===\n");
    }

    /**
     * Embaralha os resultados para criar posições aleatórias no grid de resultados.
     */
    private void embaralharResultados() {
        resultadosEmbaralhados = new Integer[NUM_ROWS][NUM_COLS];

        List<Integer> todosResultados = new ArrayList<>();
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                todosResultados.add(gerador.getMatrizResultados()[i][j]);
            }
        }

        Collections.shuffle(todosResultados);

        int index = 0;
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                resultadosEmbaralhados[i][j] = todosResultados.get(index++);
            }
        }
    }

    /**
     * Carrega todas as imagens das cartas (verso e faces coloridas).
     */
    private void loadAllCardImages() {
        loadCardBackImage();
        loadCardRevelImages();
    }

    /**
     * Carrega a imagem do verso da carta com tratamento de erro.
     */
    private void loadCardBackImage() {
        try {
            cardBackImage = ImageUtils.carregarImagem(CARD_BACK_IMAGE_PATH);

            if (cardBackImage.isError()) {
                throw new RuntimeException("Erro ao carregar a imagem do verso da carta");
            }
            
            System.out.println("Imagem do verso da carta carregada com sucesso");
        } catch (ResourceLoadException e) {
            AlertUtils.mostrarErro("Erro ao carregar recursos do jogo", e.getMessage());
            cardBackImage = null;
        }
    }

    /**
     * Carrega as imagens de revelação das cartas (faces coloridas).
     */
    private void loadCardRevelImages() {
        try {
            cardRevelImageSoma = ImageUtils.carregarImagem(CARD_REVEL_IMAGE_SOMA);
            if (cardRevelImageSoma.isError()) {
                throw new RuntimeException("Erro ao carregar imagem de soma");
            }

            cardRevelImageSubtracao = ImageUtils.carregarImagem(CARD_REVEL_IMAGE_SUBTRACAO);
            if (cardRevelImageSubtracao.isError()) {
                throw new RuntimeException("Erro ao carregar imagem de subtração");
            }

            cardRevelImageMultiplicacao = ImageUtils.carregarImagem(CARD_REVEL_IMAGE_MULTIPLICACAO);
            if (cardRevelImageMultiplicacao.isError()) {
                throw new RuntimeException("Erro ao carregar imagem de multiplicação");
            }

            cardRevelImageDivisao = ImageUtils.carregarImagem(CARD_REVEL_IMAGE_DIVISAO);
            if (cardRevelImageDivisao.isError()) {
                throw new RuntimeException("Erro ao carregar imagem de divisão");
            }
            
            System.out.println("Todas as imagens de revelação carregadas com sucesso");
        } catch (ResourceLoadException e) {
            AlertUtils.mostrarErro("Erro ao carregar recursos do jogo", e.getMessage());
            cardRevelImageSoma = cardRevelImageSubtracao = cardRevelImageMultiplicacao = cardRevelImageDivisao = null;
        }
    }

    /**
     * Obtém a imagem de revelação baseada no tipo de operação.
     * @param operationType tipo de operação matemática
     * @return Image correspondente à operação
     */
    private Image getRevelImageForOperation(Card.OperationType operationType) {
        switch (operationType) {
            case SOMA:
                return cardRevelImageSoma;
            case SUBTRACAO:
                return cardRevelImageSubtracao;
            case MULTIPLICACAO:
                return cardRevelImageMultiplicacao;
            case DIVISAO:
                return cardRevelImageDivisao;
            default:
                return cardBackImage; // Fallback
        }
    }



    /**
     * Popula os grids de operações e resultados.
     */
    private void populateGrids() {
        populateGrid(operacoesGrid, "op");
        populateGrid(resultadosGrid, "re");
    }

    /**
     * Configura as informações dos jogadores na interface.
     */
    private void setupPlayerInfo() {
        GameManager gm = GameManager.getInstance();
        jogador1Nome.setText(gm.getPlayer1().getName());
        jogador1Score.setText(String.valueOf(gm.getPlayer1().getScore()));
        jogador2Nome.setText(gm.getPlayer2().getName());
        jogador2Score.setText(String.valueOf(gm.getPlayer2().getScore()));
    }

    /**
     * Atualiza o indicador de turno.
     */
    private void updateTurnIndicator() {
        GameManager gm = GameManager.getInstance();
        turnIndicatorLabel.setText("Vez de: " + gm.getCurrentPlayer().getName());

        if (gm.getCurrentPlayer() instanceof AIPlayer) {
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

        delayIA = new PauseTransition(Duration.seconds(1.0));
        delayIA.setOnFinished(event -> {
            executarJogadaIA();
        });
        delayIA.play();
    }

    /**
     * Executa a jogada da IA.
     */
    private void executarJogadaIA() {
        if (!turnoIA) return;
        
        GameManager gm = GameManager.getInstance();
        AIPlayer aiPlayer = (AIPlayer) gm.getCurrentPlayer();

        List<String> availableCards = new ArrayList<>();
        for (int i = 0; i < operacoesGrid.getChildren().size(); i++) {
            StackPane card = (StackPane) operacoesGrid.getChildren().get(i);
            if (!isCardRevealed(card) && !isCardMatched(card)) {
                availableCards.add(card.getId());
            }
        }
        
        if (availableCards.isEmpty()) {
            return;
        }

        String bestMove = aiPlayer.calcularMelhorJogada(availableCards);
        
        if (bestMove != null) {
            StackPane cardToClick = findCardById(bestMove);
            if (cardToClick != null) {
                if (!aguardandoSegundaCarta) {
                    primeiraCartaSelecionada = cardToClick;
                    revealCard(cardToClick);
                    registerCardForAI(cardToClick);
                    aguardandoSegundaCarta = true;

                    PauseTransition secondCardDelay = new PauseTransition(Duration.seconds(1.0));
                    secondCardDelay.setOnFinished(event -> executarSegundaJogadaIA());
                    secondCardDelay.play();
                }
            }
        }
    }

    /**
     * Executa a segunda jogada da IA.
     */
    private void executarSegundaJogadaIA() {
        if (!turnoIA || !aguardandoSegundaCarta) return;
        
        GameManager gm = GameManager.getInstance();
        AIPlayer aiPlayer = (AIPlayer) gm.getCurrentPlayer();

        List<String> availableCards = new ArrayList<>();
        for (int i = 0; i < resultadosGrid.getChildren().size(); i++) {
            StackPane card = (StackPane) resultadosGrid.getChildren().get(i);
            if (!isCardRevealed(card) && !isCardMatched(card) && (primeiraCartaSelecionada == null || !card.getId().equals(primeiraCartaSelecionada.getId()))) {
                availableCards.add(card.getId());
            }
        }
        
        if (availableCards.isEmpty()) {
            return;
        }

        String bestMove = aiPlayer.calcularMelhorJogada(availableCards);
        
        if (bestMove != null) {
            StackPane cardToClick = findCardById(bestMove);
            if (cardToClick != null) {
                segundaCartaSelecionada = cardToClick;
                revealCard(cardToClick);
                registerCardForAI(cardToClick);

                PauseTransition checkDelay = new PauseTransition(Duration.seconds(0.5));
                checkDelay.setOnFinished(event -> checkMatch());
                checkDelay.play();
            }
        }
    }



    /**
     * Encontra uma carta pelo ID.
     * @param cardId ID da carta
     * @return StackPane da carta, ou null se não encontrada
     */
    private StackPane findCardById(String cardId) {
        for (int i = 0; i < operacoesGrid.getChildren().size(); i++) {
            StackPane card = (StackPane) operacoesGrid.getChildren().get(i);
            if (card.getId().equals(cardId)) {
                return card;
            }
        }

        for (int i = 0; i < resultadosGrid.getChildren().size(); i++) {
            StackPane card = (StackPane) resultadosGrid.getChildren().get(i);
            if (card.getId().equals(cardId)) {
                return card;
            }
        }
        
        return null;
    }

    /**
     * Registra carta revelada na memória da IA.
     * @param card Carta revelada
     */
    private void registerCardForAI(StackPane card) {
        GameManager gm = GameManager.getInstance();
        // Registra a carta para todas as IAs no jogo (se houver)
        if (gm.getPlayer1() instanceof AIPlayer) {
            AIPlayer aiPlayer = (AIPlayer) gm.getPlayer1();
            CardInfo cardInfo = getCardInfo(card);

            AIPlayer.CardInfo aiCardInfo = new AIPlayer.CardInfo(
                cardInfo.displayText, 
                cardInfo.result, 
                cardInfo.isOperation
            );
            
            aiPlayer.registrarCartaRevelada(card.getId(), aiCardInfo);
        }
        
        if (gm.getPlayer2() instanceof AIPlayer) {
            AIPlayer aiPlayer = (AIPlayer) gm.getPlayer2();
            CardInfo cardInfo = getCardInfo(card);

            AIPlayer.CardInfo aiCardInfo = new AIPlayer.CardInfo(
                cardInfo.displayText, 
                cardInfo.result, 
                cardInfo.isOperation
            );
            
            aiPlayer.registrarCartaRevelada(card.getId(), aiCardInfo);
        }
    }

    /**
     * Remove carta da memória da IA quando encontrada.
     * @param card Carta encontrada
     */
    private void removeCardFromAI(StackPane card) {
        GameManager gm = GameManager.getInstance();
        // Remove a carta de todas as IAs no jogo (se houver)
        if (gm.getPlayer1() instanceof AIPlayer) {
            AIPlayer aiPlayer = (AIPlayer) gm.getPlayer1();
            aiPlayer.removerCartaDaMemoria(card.getId());
        }
        
        if (gm.getPlayer2() instanceof AIPlayer) {
            AIPlayer aiPlayer = (AIPlayer) gm.getPlayer2();
            aiPlayer.removerCartaDaMemoria(card.getId());
        }
    }
    
    /**
     * Registra um par de cartas abertas na memória das IAs.
     * @param card1 Primeira carta do par
     * @param card2 Segunda carta do par
     */
    private void registerPairForAI(StackPane card1, StackPane card2) {
        GameManager gm = GameManager.getInstance();
        // Registra o par para todas as IAs no jogo (se houver)
        if (gm.getPlayer1() instanceof AIPlayer) {
            AIPlayer aiPlayer = (AIPlayer) gm.getPlayer1();
            aiPlayer.registrarParAberto(card1.getId(), card2.getId());
        }
        
        if (gm.getPlayer2() instanceof AIPlayer) {
            AIPlayer aiPlayer = (AIPlayer) gm.getPlayer2();
            aiPlayer.registrarParAberto(card1.getId(), card2.getId());
        }
    }

    /**
     * Popula um grid com cartas.
     * @param grid GridPane a ser populado
     * @param prefix Prefixo para identificação das cartas
     */
    private void populateGrid(GridPane grid, String prefix) {
        if (grid == null) {
            System.err.println("Grid é null para prefixo: " + prefix);
            return;
        }
        
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                StackPane cardPane = createCardPane(prefix, row, col);
                grid.add(cardPane, col, row);
            }
        }
    }

    /**
     * Cria um painel de carta individual.
     * @param prefix Prefixo da carta
     * @param row Linha da carta
     * @param col Coluna da carta
     * @return StackPane representando a carta
     */
    private StackPane createCardPane(String prefix, int row, int col) {
        ImageView cardBack = new ImageView(cardBackImage);
        cardBack.setFitWidth(90);
        cardBack.setFitHeight(90);
        cardBack.setPreserveRatio(false);
        cardBack.setSmooth(true);
        cardBack.setCache(true);

        ImageView cardFront = new ImageView();
        cardFront.setFitWidth(90);
        cardFront.setFitHeight(90);
        cardFront.setPreserveRatio(false);
        cardFront.setVisible(false);

        Label cardLabel = new Label("?");
        cardLabel.setVisible(false);
        cardLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; " +
                          "-fx-padding: 10px; -fx-alignment: center;");

        StackPane cardPane = new StackPane(cardBack, cardFront, cardLabel);
        cardPane.setId("card-" + prefix + "-" + row + "-" + col);
        cardPane.getStyleClass().add("card");
        cardPane.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-radius: 5; -fx-border-radius: 5;");

        cardPane.setOnMouseClicked(event -> handleCardClick(cardPane));
        
        return cardPane;
    }

    /**
     * Manipula o clique em uma carta.
     * @param clickedCard Carta que foi clicada
     */
    private void handleCardClick(StackPane clickedCard) {
        if (turnoIA) {
            return;
        }
        
        if (clickedCard == null || isCardRevealed(clickedCard) || isCardMatched(clickedCard)) {
            return;
        }

        registerCardForAI(clickedCard);

        if (!aguardandoSegundaCarta) {
            if (!isOperationCard(clickedCard)) {
                AlertUtils.mostrarErro("Jogada inválida", "A primeira carta deve ser do grid de operações.");
                return;
            }
            primeiraCartaSelecionada = clickedCard;
            revealCard(clickedCard);
            aguardandoSegundaCarta = true;
        } else {
            if (!isResultCard(clickedCard)) {
                AlertUtils.mostrarErro("Jogada inválida", "A segunda carta deve ser do grid de resultados.");
                return;
            }
            if (clickedCard != primeiraCartaSelecionada) {
                segundaCartaSelecionada = clickedCard;
                revealCard(clickedCard);
                registerCardForAI(clickedCard);
                checkMatch();
            }
        }
    }

    /**
     * Verifica se uma carta está revelada.
     * @param card Carta a ser verificada
     * @return true se a carta está revelada
     */
    private boolean isCardRevealed(StackPane card) {
        if (card.getChildren().size() >= 2) {
            return !card.getChildren().get(0).isVisible();
        }
        return false;
    }

    /**
     * Verifica se uma carta já foi encontrada (matched).
     * @param card Carta a ser verificada
     * @return true se a carta foi encontrada
     */
    private boolean isCardMatched(StackPane card) {
        return card.getStyleClass().contains("matched");
    }

    /**
     * Revela o conteúdo de uma carta.
     * @param card Carta a ser revelada
     */
    private void revealCard(StackPane card) {
        if (card.getChildren().size() >= 3) {
            card.getChildren().get(0).setVisible(false);

            ImageView cardFront = (ImageView) card.getChildren().get(1);
            cardFront.setVisible(true);

            CardInfo cardInfo = getCardInfo(card);

            if (cardInfo.operationType != null) {
                cardFront.setImage(getRevelImageForOperation(cardInfo.operationType));
            } else {
                cardFront.setImage(cardRevelImageSoma);
            }

            Label cardLabel = (Label) card.getChildren().get(2);
            cardLabel.setText(cardInfo.displayText);
            cardLabel.setVisible(true);

            AudioManager.getInstance().tocarSomRevelarCarta();
        }
    }

    /**
     * Verifica se as duas cartas selecionadas formam um par.
     */
    private void checkMatch() {
        CardInfo info1 = getCardInfo(primeiraCartaSelecionada);
        CardInfo info2 = getCardInfo(segundaCartaSelecionada);
        
        boolean isMatch = false;

        if (isOperationCard(primeiraCartaSelecionada) && isResultCard(segundaCartaSelecionada)) {
            isMatch = info1.result == info2.result;
            System.out.println("Verificando match: " + info1.displayText + " (" + info1.result + ") == " + info2.displayText + " (" + info2.result + ") = " + isMatch);
        } else if (isResultCard(primeiraCartaSelecionada) && isOperationCard(segundaCartaSelecionada)) {
            isMatch = info2.result == info1.result;
            System.out.println("Verificando match: " + info2.displayText + " (" + info2.result + ") == " + info1.displayText + " (" + info1.result + ") = " + isMatch);
        }
        
        if (isMatch) {
            System.out.println("MATCH ENCONTRADO!");
            handleMatch();
        } else {
            System.out.println("NÃO É MATCH!");
            handleNoMatch();
        }
    }

    /**
     * Manipula quando um par é encontrado.
     */
    private void handleMatch() {
        System.out.println("=== HANDLE MATCH INICIADO ===");
        System.out.println("Antes: cartasEncontradas = " + cartasEncontradas);
        
        primeiraCartaSelecionada.getStyleClass().removeAll("erro-match");
        segundaCartaSelecionada.getStyleClass().removeAll("erro-match");
        primeiraCartaSelecionada.getStyleClass().add("matched");
        segundaCartaSelecionada.getStyleClass().add("matched");

        removeCardFromAI(primeiraCartaSelecionada);
        removeCardFromAI(segundaCartaSelecionada);

        cartasEncontradas += 2;
        System.out.println("Depois: cartasEncontradas = " + cartasEncontradas);

        GameManager gm = GameManager.getInstance();
        Player player = gm.getCurrentPlayer();
        player.adicionarPontos(PONTOS_ACERTO);

        updatePlayerScores();

        AudioManager.getInstance().tocarSomMatch();

        System.out.println("Par encontrado! Cartas encontradas: " + cartasEncontradas + "/" + TOTAL_CARTAS);
        System.out.println("Condição para terminar: " + cartasEncontradas + " >= " + TOTAL_CARTAS + " = " + (cartasEncontradas >= TOTAL_CARTAS));

        // SÓ TERMINA O JOGO SE TODAS AS 12 CARTAS FOREM ENCONTRADAS
        if (cartasEncontradas >= TOTAL_CARTAS) {
            System.out.println("TODAS AS 12 CARTAS FORAM ENCONTRADAS! Finalizando jogo...");
            handleGameEnd();
        } else {
            System.out.println("Jogo continua... Próximo turno.");
            resetCardSelection();

            if (gm.getCurrentPlayer() instanceof AIPlayer) {
                PauseTransition aiDelay = new PauseTransition(Duration.seconds(0.5));
                aiDelay.setOnFinished(event -> iniciarTurnoIA());
                aiDelay.play();
            }
        }
        System.out.println("=== HANDLE MATCH FINALIZADO ===");
    }

    /**
     * Manipula quando um par não é encontrado.
     */
    private void handleNoMatch() {
        AudioManager.getInstance().tocarSomNaoMatch();
        
        // Registra o par aberto na memória das IAs
        registerPairForAI(primeiraCartaSelecionada, segundaCartaSelecionada);
        
        // Aplica penalidade ao jogador atual
        GameManager gm = GameManager.getInstance();
        Player player = gm.getCurrentPlayer();
        player.adicionarPontos(PONTOS_ERRO);
        updatePlayerScores();
        
        primeiraCartaSelecionada.getStyleClass().removeAll("matched");
        segundaCartaSelecionada.getStyleClass().removeAll("matched");
        primeiraCartaSelecionada.getStyleClass().add("erro-match");
        segundaCartaSelecionada.getStyleClass().add("erro-match");
        PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
        delay.setOnFinished(event -> {
            hideCard(primeiraCartaSelecionada);
            hideCard(segundaCartaSelecionada);
            resetCardSelection();
            gm.trocarTurno();
            updateTurnIndicator();
        });
        delay.play();
    }

    /**
     * Reseta a seleção de cartas.
     */
    private void resetCardSelection() {
        primeiraCartaSelecionada = null;
        segundaCartaSelecionada = null;
        aguardandoSegundaCarta = false;
    }

    /**
     * Verifica se todas as cartas foram encontradas.
     * @return true se todas as cartas foram encontradas
     */
    private boolean verificarTodasCartasEncontradas() {
        int cartasMatched = 0;
        int cartasOperacoes = 0;
        int cartasResultados = 0;
        
        // Conta cartas matched no grid de operações
        for (int i = 0; i < operacoesGrid.getChildren().size(); i++) {
            StackPane card = (StackPane) operacoesGrid.getChildren().get(i);
            if (isCardMatched(card)) {
                cartasMatched++;
                cartasOperacoes++;
            }
        }
        
        // Conta cartas matched no grid de resultados
        for (int i = 0; i < resultadosGrid.getChildren().size(); i++) {
            StackPane card = (StackPane) resultadosGrid.getChildren().get(i);
            if (isCardMatched(card)) {
                cartasMatched++;
                cartasResultados++;
            }
        }
        
        System.out.println("=== VERIFICAÇÃO DE CARTAS ===");
        System.out.println("Cartas matched (operações): " + cartasOperacoes);
        System.out.println("Cartas matched (resultados): " + cartasResultados);
        System.out.println("Total matched: " + cartasMatched + "/" + TOTAL_CARTAS);
        System.out.println("Variável cartasEncontradas: " + cartasEncontradas);
        System.out.println("================================");
        
        return cartasMatched >= TOTAL_CARTAS;
    }

    /**
     * Manipula o fim do jogo.
     */
    private void handleGameEnd() {
        System.out.println("FINALIZANDO JOGO - Cartas encontradas: " + cartasEncontradas + "/" + TOTAL_CARTAS);
        
        GameManager gm = GameManager.getInstance();
        Player vencedor = null;
        boolean empate = false;
        
        // Determina o vencedor baseado na pontuação
        if (gm.getPlayer1().getScore() == gm.getPlayer2().getScore()) {
            empate = true;
        } else {
            vencedor = gm.getPlayer1().getScore() > gm.getPlayer2().getScore() ? gm.getPlayer1() : gm.getPlayer2();
        }
        gm.setVencedor(empate ? null : vencedor);
        
        // Toca som apropriado baseado no resultado
        if (empate) {
            AudioManager.getInstance().tocarSomVitoria(); // Som neutro para empate
        } else if (gm.getGameMode() == GameManager.GameMode.PVE) {
            if (vencedor instanceof AIPlayer) {
                AudioManager.getInstance().tocarSomDerrota(); // Som de derrota quando IA vence
            } else {
                AudioManager.getInstance().tocarSomVitoria(); // Som de vitória quando jogador vence
            }
        } else {
            AudioManager.getInstance().tocarSomVitoria(); // Som de vitória para PvP
        }
        
        System.out.println("JOGO FINALIZADO COM SUCESSO! Todas as " + TOTAL_CARTAS + " cartas foram encontradas.");
        jogodamemoria.memorymath.transitions.SceneManager.getInstance().carregarCena("/fxml/vitoria-view.fxml");
    }

    /**
     * Atualiza os scores dos jogadores na interface.
     */
    private void updatePlayerScores() {
        GameManager gm = GameManager.getInstance();
        jogador1Score.setText(String.valueOf(gm.getPlayer1().getScore()));
        jogador2Score.setText(String.valueOf(gm.getPlayer2().getScore()));
    }

    /**
     * Verifica se uma carta é de operação.
     * @param card Carta a ser verificada
     * @return true se é carta de operação
     */
    private boolean isOperationCard(StackPane card) {
        return card.getId().contains("op");
    }

    /**
     * Verifica se uma carta é de resultado.
     * @param card Carta a ser verificada
     * @return true se é carta de resultado
     */
    private boolean isResultCard(StackPane card) {
        return card.getId().contains("re");
    }

    /**
     * Obtém as informações de uma carta baseada em sua posição.
     * @param card Carta para obter informações
     * @return CardInfo com as informações da carta
     */
    private CardInfo getCardInfo(StackPane card) {
        String cardId = card.getId();
        String[] parts = cardId.split("-");
        int row = Integer.parseInt(parts[2]);
        int col = Integer.parseInt(parts[3]);
        
        if (isOperationCard(card)) {
            int op1 = gerador.getMatrizOperandos1()[row][col];
            int op2 = gerador.getMatrizOperandos2()[row][col];
            int result = gerador.getMatrizResultados()[row][col];
            Card.OperationType operationType = getOperationType(row, col);
            
            String displayText = op1 + getOperationSymbol(operationType) + op2;
            return new CardInfo(displayText, result, operationType, true);
        } else {
            int result = resultadosEmbaralhados[row][col];
            Card.OperationType operationType = getOperationTypeForResult(row, col);
            return new CardInfo(String.valueOf(result), result, operationType, false);
        }
    }

    /**
     * Obtém o tipo de operação baseado na posição da matriz.
     * @param row Linha da matriz
     * @param col Coluna da matriz
     * @return Tipo de operação
     */
    private Card.OperationType getOperationType(int row, int col) {
        int op1 = gerador.getMatrizOperandos1()[row][col];
        int op2 = gerador.getMatrizOperandos2()[row][col];
        int result = gerador.getMatrizResultados()[row][col];
        
        if (op1 + op2 == result) return Card.OperationType.SOMA;
        if (op1 - op2 == result) return Card.OperationType.SUBTRACAO;
        if (op1 * op2 == result) return Card.OperationType.MULTIPLICACAO;
        if (op2 != 0 && op1 / op2 == result) return Card.OperationType.DIVISAO;
        
        return Card.OperationType.SOMA;
    }

    /**
     * Obtém o tipo de operação para cartas de resultado baseado na posição.
     * Para cartas de resultado, precisamos determinar qual operação gerou aquele resultado.
     * @param row Linha da matriz
     * @param col Coluna da matriz
     * @return Tipo de operação
     */
    private Card.OperationType getOperationTypeForResult(int row, int col) {
        int result = resultadosEmbaralhados[row][col];

        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                int op1 = gerador.getMatrizOperandos1()[i][j];
                int op2 = gerador.getMatrizOperandos2()[i][j];
                int opResult = gerador.getMatrizResultados()[i][j];
                
                if (opResult == result) {
                    if (op1 + op2 == opResult) return Card.OperationType.SOMA;
                    if (op1 - op2 == opResult) return Card.OperationType.SUBTRACAO;
                    if (op1 * op2 == opResult) return Card.OperationType.MULTIPLICACAO;
                    if (op2 != 0 && op1 / op2 == opResult) return Card.OperationType.DIVISAO;
                }
            }
        }

        int index = row * NUM_COLS + col;
        switch (index % 4) {
            case 0: return Card.OperationType.SOMA;
            case 1: return Card.OperationType.SUBTRACAO;
            case 2: return Card.OperationType.MULTIPLICACAO;
            case 3: return Card.OperationType.DIVISAO;
            default: return Card.OperationType.SOMA;
        }
    }

    /**
     * Obtém o símbolo da operação.
     * @param operationType Tipo de operação
     * @return Símbolo da operação
     */
    private String getOperationSymbol(Card.OperationType operationType) {
        switch (operationType) {
            case SOMA: return "+";
            case SUBTRACAO: return "-";
            case MULTIPLICACAO: return "×";
            case DIVISAO: return "÷";
            default: return "+";
        }
    }

    /**
     * Esconde o conteúdo de uma carta.
     * @param card Carta a ser escondida
     */
    private void hideCard(StackPane card) {
        if (card.getChildren().size() >= 3) {
            card.getChildren().get(0).setVisible(true);

            card.getChildren().get(1).setVisible(false);

            card.getChildren().get(2).setVisible(false);
        }
    }



    /**
     * Classe interna para armazenar informações de uma carta.
     */
    private static class CardInfo {
        final String displayText;
        final int result;
        final Card.OperationType operationType;
        final boolean isOperation;

        CardInfo(String displayText, int result, Card.OperationType operationType, boolean isOperation) {
            this.displayText = displayText;
            this.result = result;
            this.operationType = operationType;
            this.isOperation = isOperation;
        }
    }
}