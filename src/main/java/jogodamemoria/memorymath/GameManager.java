package jogodamemoria.memorymath;

import jogodamemoria.memorymath.model.Card;
import java.util.List;
import jogodamemoria.memorymath.Player;
import jogodamemoria.memorymath.HumanPlayer;
import jogodamemoria.memorymath.AIPlayer;

/**
 * Gerenciador principal do jogo Memory Math.
 * Implementa o padrão Singleton para garantir uma única instância.
 * Responsável por gerenciar o estado do jogo, jogadores e configurações.
 * 
 * @author Renan Amancio
 * @version 1.0
 */
public class GameManager {

    private static GameManager instance;
    private GameManager() {}
    
    /**
     * Obtém a instância única do GameManager (Singleton).
     * 
     * @return Instância do GameManager
     */
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    /**
     * Enum que define os modos de jogo disponíveis.
     */
    public enum GameMode { PVP, PVE }

    private GameMode gameMode;
    private Player player1, player2, currentPlayer, vencedor;
    private List<Card.OperationType> selectedOperations;
    private AIPlayer.Difficulty aiDifficulty;

    /**
     * Define o modo de jogo.
     * 
     * @param gameMode Modo de jogo (PVP ou PVE)
     */
    public void setGameMode(GameMode gameMode) { this.gameMode = gameMode; }

    /**
     * Configura o jogo para modo PvP (Player vs Player).
     * 
     * @param p1Name Nome do primeiro jogador
     * @param p2Name Nome do segundo jogador
     * @param ops Lista de operações selecionadas
     */
    public void configurarJogoPvP(String p1Name, String p2Name, List<Card.OperationType> ops) {
        this.player1 = new HumanPlayer(p1Name);
        this.player2 = new HumanPlayer(p2Name);
        this.selectedOperations = ops;
        this.currentPlayer = this.player1;
        this.vencedor = null;
    }

    /**
     * Configura o jogo para modo PvE (Player vs Environment/IA).
     * 
     * @param p1Name Nome do jogador humano
     * @param difficulty Dificuldade da IA
     * @param ops Lista de operações selecionadas
     */
    public void configurarJogoPvE(String p1Name, AIPlayer.Difficulty difficulty, List<Card.OperationType> ops) {
        this.player1 = new HumanPlayer(p1Name);
        this.player2 = new AIPlayer(difficulty);
        this.aiDifficulty = difficulty;
        this.selectedOperations = ops;
        this.currentPlayer = this.player1;
        this.vencedor = null;
    }

    /**
     * Troca o turno entre os jogadores.
     */
    public void trocarTurno() {
        this.currentPlayer = (this.currentPlayer == player1) ? player2 : player1;
    }

    /**
     * Define o vencedor do jogo.
     * 
     * @param vencedor Jogador vencedor
     */
    public void setVencedor(Player vencedor) { this.vencedor = vencedor; }
    
    /**
     * Obtém o vencedor do jogo.
     * 
     * @return Jogador vencedor ou null se não houver vencedor
     */
    public Player getVencedor() { return vencedor; }
    
    /**
     * Obtém o modo de jogo atual.
     * 
     * @return Modo de jogo
     */
    public GameMode getGameMode() { return gameMode; }
    
    /**
     * Obtém o primeiro jogador.
     * 
     * @return Primeiro jogador
     */
    public Player getPlayer1() { return player1; }
    
    /**
     * Obtém o segundo jogador.
     * 
     * @return Segundo jogador
     */
    public Player getPlayer2() { return player2; }
    
    /**
     * Obtém o jogador atual.
     * 
     * @return Jogador atual
     */
    public Player getCurrentPlayer() { return currentPlayer; }
    
    /**
     * Obtém as operações selecionadas para o jogo.
     * 
     * @return Lista de operações selecionadas
     */
    public List<Card.OperationType> getSelectedOperations() { return selectedOperations; }

    /**
     * Adiciona um ponto ao jogador atual.
     */
    public void adicionarPontoJogadorAtual() {
        currentPlayer.adicionarPonto();
    }

    /**
     * Verifica se o jogo terminou baseado no número de cartas encontradas.
     * 
     * @param totalCartas Total de cartas no jogo
     * @param cartasEncontradas Número de cartas já encontradas
     * @return true se o jogo terminou, false caso contrário
     */
    public boolean jogoTerminou(int totalCartas, int cartasEncontradas) {
        return cartasEncontradas == totalCartas;
    }

    /**
     * Determina o vencedor baseado na pontuação dos jogadores.
     * 
     * @return Jogador vencedor, null em caso de empate
     */
    public Player determinarVencedor() {
        if (player1.getScore() > player2.getScore()) return player1;
        if (player2.getScore() > player1.getScore()) return player2;
        return null; // empate
    }
}