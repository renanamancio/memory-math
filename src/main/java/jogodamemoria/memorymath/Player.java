package jogodamemoria.memorymath;

/**
 * Classe abstrata que representa um jogador do jogo (humano ou IA).
 * Define a estrutura básica para todos os tipos de jogadores.
 * 
 * @author Renan Amancio
 * @version 1.0
 */
public abstract class Player {
    protected String name;
    protected int score;

    /**
     * Construtor da classe Player.
     * 
     * @param name Nome do jogador
     */
    public Player(String name) {
        this.name = (name == null || name.trim().isEmpty()) ? "Jogador" : name;
        this.score = 0;
    }

    /**
     * Obtém o nome do jogador.
     * 
     * @return Nome do jogador
     */
    public String getName() { return name; }
    
    /**
     * Obtém a pontuação atual do jogador.
     * 
     * @return Pontuação atual
     */
    public int getScore() { return score; }

    /**
     * Adiciona um ponto à pontuação do jogador.
     */
    public void adicionarPonto() {
        this.score++;
    }
    
    /**
     * Adiciona uma quantidade específica de pontos à pontuação do jogador.
     * Garante que a pontuação não fique negativa.
     * 
     * @param pontos Quantidade de pontos a adicionar
     */
    public void adicionarPontos(int pontos) {
        this.score += pontos;
        // Garante que a pontuação não fique negativa
        if (this.score < 0) {
            this.score = 0;
        }
    }

    /**
     * Método abstrato que executa a lógica do turno do jogador.
     * Deve ser implementado pelas classes filhas (HumanPlayer e AIPlayer).
     */
    public abstract void jogarTurno();
} 