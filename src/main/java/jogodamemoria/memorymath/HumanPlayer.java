package jogodamemoria.memorymath;

/**
 * Representa um jogador humano do jogo.
 */
public class HumanPlayer extends Player {
    public HumanPlayer(String name) {
        super(name);
    }
    @Override
    public void jogarTurno() {
        // A lógica do turno do humano é controlada pelo GameController
    }
}