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
    private static final int NUM_ROWS = 3;
    private static final int TOTAL_PAIRS = 9;

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

    private boolean continuarJogando = true;

    private boolean turnoIA = false;
    private PauseTransition delayIA;

    /**
     * Inicializa o controlador do jogo, carregando recursos e configurando o tabuleiro.
     */
    @FXML
    public void initialize() {
        loadAllCardImages();
        setupGame();
        setupResponsiveLayout();
        populateGrids();
        setupPlayerInfo();
        updateTurnIndicator();
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
        System.out.println("Array de operações: ");
        for (int i = 0; i < operacoesArray.length; i++) {
            System.out.println("  [" + i + "] = " + operacoesArray[i]);
        }
        System.out.println("Gerador inicializado com sucesso!");
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
        
        System.out.println("Resultados embaralhados:");
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                System.out.print(resultadosEmbaralhados[i][j] + "\t");
            }
            System.out.println();
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
     * Cria uma imagem padrão como fallback quando a imagem original não pode ser carregada.
     * @return Image padrão
     */
    private Image createDefaultCardImage() {
        return null;
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
            System.out.println("IA não encontrou cartas de operação disponíveis");
            return;
        }

        String bestMove = aiPlayer.calcularMelhorJogada(availableCards);
        
        if (bestMove != null) {
            StackPane cardToClick = findCardById(bestMove);
            if (cardToClick != null) {
                System.out.println("IA escolheu: " + bestMove);

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
            System.out.println("IA não encontrou segunda carta de resultado disponível");
            return;
        }

        String bestMove = aiPlayer.calcularMelhorJogada(availableCards);
        
        if (bestMove != null) {
            StackPane cardToClick = findCardById(bestMove);
            if (cardToClick != null) {
                System.out.println("IA escolheu segunda carta: " + bestMove);

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
     * Obtém lista de cartas disponíveis para jogar.
     * @return Lista de IDs de cartas disponíveis
     */
    private List<String> getAvailableCards() {
        List<String> availableCards = new ArrayList<>();

        for (int i = 0; i < operacoesGrid.getChildren().size(); i++) {
            StackPane card = (StackPane) operacoesGrid.getChildren().get(i);
            if (!isCardRevealed(card) && !isCardMatched(card)) {
                availableCards.add(card.getId());
            }
        }

        for (int i = 0; i < resultadosGrid.getChildren().size(); i++) {
            StackPane card = (StackPane) resultadosGrid.getChildren().get(i);
            if (!isCardRevealed(card) && !isCardMatched(card)) {
                availableCards.add(card.getId());
            }
        }
        
        return availableCards;
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
        if (gm.getCurrentPlayer() instanceof AIPlayer) {
            AIPlayer aiPlayer = (AIPlayer) gm.getCurrentPlayer();
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
        if (gm.getCurrentPlayer() instanceof AIPlayer) {
            AIPlayer aiPlayer = (AIPlayer) gm.getCurrentPlayer();
            aiPlayer.removerCartaDaMemoria(card.getId());
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

        System.out.println("Populando grid " + prefix + " com " + NUM_ROWS + "x" + NUM_COLS + " cartas");
        
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                StackPane cardPane = createCardPane(prefix, row, col);
                grid.add(cardPane, col, row);
                System.out.println("Carta criada: " + cardPane.getId() + " na posição [" + row + "," + col + "]");
            }
        }
        
        System.out.println("Grid " + prefix + " populado com " + grid.getChildren().size() + " cartas");
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
            System.out.println("Clique bloqueado - turno da IA");
            return;
        }
        
        if (clickedCard == null || isCardRevealed(clickedCard) || isCardMatched(clickedCard)) {
            return;
        }

        System.out.println("Clicou em: " + clickedCard.getId());

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

            System.out.println("Carta revelada: " + card.getId() + 
                             " - Texto: " + cardInfo.displayText + 
                             " - Resultado: " + cardInfo.result + 
                             " - Tipo: " + (cardInfo.operationType != null ? cardInfo.operationType : "null"));
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
        } else if (isResultCard(primeiraCartaSelecionada) && isOperationCard(segundaCartaSelecionada)) {
            isMatch = info2.result == info1.result;
        }
        
        if (isMatch) {
            handleMatch();
        } else {
            handleNoMatch();
        }
    }

    /**
     * Manipula quando um par é encontrado.
     */
    private void handleMatch() {
        primeiraCartaSelecionada.getStyleClass().removeAll("erro-match");
        segundaCartaSelecionada.getStyleClass().removeAll("erro-match");
        primeiraCartaSelecionada.getStyleClass().add("matched");
        segundaCartaSelecionada.getStyleClass().add("matched");

        removeCardFromAI(primeiraCartaSelecionada);
        removeCardFromAI(segundaCartaSelecionada);

        cartasEncontradas += 2;

        GameManager gm = GameManager.getInstance();
        Player player = gm.getCurrentPlayer();
        player.adicionarPonto();

        updatePlayerScores();

        AudioManager.getInstance().tocarSomMatch();

        if (cartasEncontradas >= TOTAL_CARTAS) {
            handleGameEnd();
        } else {
            resetCardSelection();

            if (continuarJogando) {
                System.out.println("Par encontrado! " + player.getName() + " continua jogando.");

                if (gm.getCurrentPlayer() instanceof AIPlayer) {
                    PauseTransition aiDelay = new PauseTransition(Duration.seconds(0.5));
                    aiDelay.setOnFinished(event -> iniciarTurnoIA());
                    aiDelay.play();
                }
            } else {
                gm.trocarTurno();
                updateTurnIndicator();
            }
        }
    }

    /**
     * Manipula quando um par não é encontrado.
     */
    private void handleNoMatch() {
        AudioManager.getInstance().tocarSomNaoMatch();
        
        primeiraCartaSelecionada.getStyleClass().removeAll("matched");
        segundaCartaSelecionada.getStyleClass().removeAll("matched");
        primeiraCartaSelecionada.getStyleClass().add("erro-match");
        segundaCartaSelecionada.getStyleClass().add("erro-match");
        PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
        delay.setOnFinished(event -> {
            hideCard(primeiraCartaSelecionada);
            hideCard(segundaCartaSelecionada);
            resetCardSelection();
            GameManager gm = GameManager.getInstance();
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
     * Manipula o fim do jogo.
     */
    private void handleGameEnd() {
        GameManager gm = GameManager.getInstance();
        Player vencedor = null;
        boolean empate = false;
        if (gm.getPlayer1().getScore() == gm.getPlayer2().getScore()) {
            empate = true;
        } else {
            vencedor = gm.getPlayer1().getScore() > gm.getPlayer2().getScore() ? gm.getPlayer1() : gm.getPlayer2();
        }
        gm.setVencedor(empate ? null : vencedor);
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
     * Exibe uma mensagem de erro.
     * @param title Título do erro
     * @param message Mensagem do erro
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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