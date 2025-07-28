package jogodamemoria.memorymath.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import jogodamemoria.memorymath.AIPlayer;
import jogodamemoria.memorymath.GameManager;
import jogodamemoria.memorymath.model.Card;
import jogodamemoria.memorymath.transitions.SceneManager;
import jogodamemoria.memorymath.util.AlertUtils;
import jogodamemoria.memorymath.util.AudioManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador responsável pela tela de configuração do jogo.
 * Gerencia a configuração de jogadores, dificuldade da IA e tipos de operações.
 */
public class ConfigController {

    private static final String ERRO_NOME_VAZIO = "Nome do jogador não pode estar vazio";
    private static final String ERRO_NENHUMA_OPERACAO = "Por favor, selecione pelo menos um tipo de operação para continuar.";

    @FXML private TextField textFieldJogador1;
    @FXML private TextField textFieldJogador2;
    @FXML private Label labelJogador2;

    @FXML private Label labelDificuldadeAI;
    @FXML private HBox hBoxDificuldadeAI;
    @FXML private ToggleButton toggleButtonFacilAI;
    @FXML private ToggleButton toggleButtonMedioAI;
    @FXML private ToggleButton toggleButtonDificilAI;
    @FXML private ToggleGroup aiDifficultyGroup;

    @FXML private ToggleButton toggleSoma;
    @FXML private ToggleButton toggleSubtracao;
    @FXML private ToggleButton toggleMultiplicacao;
    @FXML private ToggleButton toggleDivisao;

    /**
     * Inicializa o controlador configurando a interface baseada no modo de jogo.
     */
    @FXML
    public void initialize() {
        configurarGrupoToggle();
        configurarInterfaceParaModoJogo();
    }

    /**
     * Configura o grupo de toggle buttons para dificuldade da IA.
     */
    private void configurarGrupoToggle() {
        aiDifficultyGroup = new ToggleGroup();
        toggleButtonFacilAI.setToggleGroup(aiDifficultyGroup);
        toggleButtonMedioAI.setToggleGroup(aiDifficultyGroup);
        toggleButtonDificilAI.setToggleGroup(aiDifficultyGroup);
        toggleButtonFacilAI.setSelected(true);
    }

    /**
     * Configura a interface baseada no modo de jogo (PVP ou PVE).
     */
    private void configurarInterfaceParaModoJogo() {
        GameManager gameManager = GameManager.getInstance();
        
        if (gameManager.getGameMode() == GameManager.GameMode.PVP) {
            ocultarElementosIA();
        } else {
            ocultarElementosJogador2();
        }
    }

    /**
     * Oculta elementos relacionados à IA (modo PVP).
     */
    private void ocultarElementosIA() {
        labelDificuldadeAI.setVisible(false);
        labelDificuldadeAI.setManaged(false);
        hBoxDificuldadeAI.setVisible(false);
        hBoxDificuldadeAI.setManaged(false);
    }

    /**
     * Oculta elementos relacionados ao jogador 2 (modo PVE).
     */
    private void ocultarElementosJogador2() {
        labelJogador2.setVisible(false);
        labelJogador2.setManaged(false);
        textFieldJogador2.setVisible(false);
        textFieldJogador2.setManaged(false);
    }

    /**
     * Manipula o clique no botão de iniciar jogo.
     * Valida as entradas e configura o jogo.
     */
    @FXML
    private void aoClicarIniciarJogo() {
        if (!validarEntradas()) {
            return;
        }

        try {
            configurarEIniciarJogo();
        } catch (Exception e) {
            AlertUtils.mostrarErro("Erro ao configurar o jogo", e.getMessage());
        }

    }

    @FXML
    private void voltarSelecao() {
        SceneManager.getInstance().carregarCena("/fxml/selecao-modo-view.fxml");
    }

    /**
     * Valida todas as entradas do usuário.
     * @return true se todas as validações passarem, false caso contrário
     */
    private boolean validarEntradas() {

        if (!nomeJogadorValido(textFieldJogador1.getText())) {
            AlertUtils.mostrarErro("Erro", ERRO_NOME_VAZIO);
            return false;
        }

        GameManager gameManager = GameManager.getInstance();
        if (gameManager.getGameMode() == GameManager.GameMode.PVP) {
            if (!nomeJogadorValido(textFieldJogador2.getText())) {
                AlertUtils.mostrarErro("Erro", "Nome do jogador 2 não pode estar vazio");
                return false;
            }
        }

        if (obterOperacoesSelecionadas().isEmpty()) {
            AlertUtils.mostrarAviso("Nenhuma Operação Selecionada", ERRO_NENHUMA_OPERACAO);
            return false;
        }

         return true;
    }

    /**
     * Verifica se o nome do jogador é válido.
     * @param nomeJogador nome do jogador
     * @return true se o nome for válido, false caso contrário
     */
    private boolean nomeJogadorValido(String nomeJogador) {
        return nomeJogador != null && !nomeJogador.trim().isEmpty();
    }

    /**
     * Obtém a lista de operações selecionadas.
     * @return lista de tipos de operação selecionados
     */
    private List<Card.OperationType> obterOperacoesSelecionadas() {
        List<Card.OperationType> operacoes = new ArrayList<>();
        
        if (toggleSoma.isSelected()) {
            operacoes.add(Card.OperationType.SOMA);
        }
        if (toggleSubtracao.isSelected()) {
            operacoes.add(Card.OperationType.SUBTRACAO);
        }
        if (toggleMultiplicacao.isSelected()) {
            operacoes.add(Card.OperationType.MULTIPLICACAO);
        }
        if (toggleDivisao.isSelected()) {
            operacoes.add(Card.OperationType.DIVISAO);
        }
        
        return operacoes;
    }

    /**
     * Obtém a dificuldade selecionada para a IA.
     * @return dificuldade da IA
     */
    private AIPlayer.Difficulty obterDificuldadeSelecionada() {
        if (toggleButtonMedioAI.isSelected()) {
            return AIPlayer.Difficulty.MEDIO;
        } else if (toggleButtonDificilAI.isSelected()) {
            return AIPlayer.Difficulty.DIFICIL;
        }
        return AIPlayer.Difficulty.FACIL;
    }

    /**
     * Configura e inicia o jogo baseado no modo selecionado.
     */
    private void configurarEIniciarJogo() {
        GameManager gameManager = GameManager.getInstance();
        String nomeJogador1 = textFieldJogador1.getText().trim();
        List<Card.OperationType> operacoes = obterOperacoesSelecionadas();

        if (gameManager.getGameMode() == GameManager.GameMode.PVP) {
            String nomeJogador2 = textFieldJogador2.getText().trim();
            gameManager.configurarJogoPvP(nomeJogador1, nomeJogador2, operacoes);
        } else {
            AIPlayer.Difficulty dificuldade = obterDificuldadeSelecionada();
            gameManager.configurarJogoPvE(nomeJogador1, dificuldade, operacoes);
        }

        AudioManager.getInstance().pausarMusicaFundo();
        
        SceneManager.getInstance().carregarCena("/fxml/game-view.fxml");
    }
}