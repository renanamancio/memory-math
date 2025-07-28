package jogodamemoria.memorymath;

/**
 * Representa um jogador do jogo (humano ou IA).
 */
public abstract class Player {
    protected String name;
    protected int score;

    public Player(String name) {
        this.name = (name == null || name.trim().isEmpty()) ? "Jogador" : name;
        this.score = 0;
    }

    public String getName() { return name; }
    public int getScore() { return score; }

    public void adicionarPonto() {
        this.score++;
    }

    /**
     * Executa a lógica do turno do jogador (humano ou IA).
     */
    public abstract void jogarTurno();
} 