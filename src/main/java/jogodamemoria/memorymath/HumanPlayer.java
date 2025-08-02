package jogodamemoria.memorymath;

/**
 * Classe que representa um jogador humano do jogo.
 * Herda da classe Player e implementa a lógica específica para jogadores humanos.
 * 
 * @author Renan Amancio
 * @version 1.0
 */
public class HumanPlayer extends Player {
    
    /**
     * Construtor da classe HumanPlayer.
     * 
     * @param name Nome do jogador humano
     */
    public HumanPlayer(String name) {
        super(name);
    }
    
    /**
     * Implementação do método abstrato jogarTurno().
     * Para jogadores humanos, a lógica do turno é controlada pelo GameController
     * através da interface gráfica.
     */
    @Override
    public void jogarTurno() {
    }
}