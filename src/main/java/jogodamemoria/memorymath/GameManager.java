package jogodamemoria.memorymath;

import jogodamemoria.memorymath.model.Card;
import java.util.List;
import jogodamemoria.memorymath.Player;
import jogodamemoria.memorymath.HumanPlayer;
import jogodamemoria.memorymath.AIPlayer;

public class GameManager {

    private static GameManager instance;
    private GameManager() {}
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public enum GameMode { PVP, PVE }

    private GameMode gameMode;
    private Player player1, player2, currentPlayer, vencedor;
    private List<Card.OperationType> selectedOperations;
    private AIPlayer.Difficulty aiDifficulty;

    public void setGameMode(GameMode gameMode) { this.gameMode = gameMode; }

    public void configurarJogoPvP(String p1Name, String p2Name, List<Card.OperationType> ops) {
        this.player1 = new HumanPlayer(p1Name);
        this.player2 = new HumanPlayer(p2Name);
        this.selectedOperations = ops;
        this.currentPlayer = this.player1;
        this.vencedor = null;
    }

    public void configurarJogoPvE(String p1Name, AIPlayer.Difficulty difficulty, List<Card.OperationType> ops) {
        this.player1 = new HumanPlayer(p1Name);
        this.player2 = new AIPlayer(difficulty);
        this.aiDifficulty = difficulty;
        this.selectedOperations = ops;
        this.currentPlayer = this.player1;
        this.vencedor = null;
    }

    public void trocarTurno() {
        this.currentPlayer = (this.currentPlayer == player1) ? player2 : player1;
    }

    public void setVencedor(Player vencedor) { this.vencedor = vencedor; }
    public Player getVencedor() { return vencedor; }
    public GameMode getGameMode() { return gameMode; }
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public Player getCurrentPlayer() { return currentPlayer; }
    public List<Card.OperationType> getSelectedOperations() { return selectedOperations; }

    // Adiciona ponto ao jogador atual
    public void adicionarPontoJogadorAtual() {
        currentPlayer.adicionarPonto();
    }

    // Verifica se o jogo terminou
    public boolean jogoTerminou(int totalCartas, int cartasEncontradas) {
        return cartasEncontradas == totalCartas;
    }

    // Determina o vencedor
    public Player determinarVencedor() {
        if (player1.getScore() > player2.getScore()) return player1;
        if (player2.getScore() > player1.getScore()) return player2;
        return null; // empate
    }
}