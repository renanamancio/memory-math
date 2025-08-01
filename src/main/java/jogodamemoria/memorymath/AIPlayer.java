package jogodamemoria.memorymath;

import java.util.*;

/**
 * Classe que representa um jogador controlado por IA.
 * Implementa diferentes níveis de dificuldade baseados na memória de cartas abertas.
 * 
 * @author MemoryMath Team
 * @version 2.0
 */
public class AIPlayer extends Player {
    
    /**
     * Enumeração dos níveis de dificuldade da IA.
     */
    public enum Difficulty {
        FACIL("Fácil", 2),      // Lembra dos 2 últimos pares abertos
        MEDIO("Médio", 6),      // Lembra dos 6 últimos pares abertos
        DIFICIL("Difícil", Integer.MAX_VALUE); // Lembra de todos os pares abertos
        
        private final String displayName;
        private final int memorySize;
        
        Difficulty(String displayName, int memorySize) {
            this.displayName = displayName;
            this.memorySize = memorySize;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public int getMemorySize() {
            return memorySize;
        }
    }
    
    private final Difficulty difficulty;
    private final Map<String, CardInfo> revealedCards; // Cartas abertas por qualquer jogador
    private final Queue<CardPair> cardMemory; // Memória de pares de cartas abertas
    private final Random random;
    
    /**
     * Construtor da IA.
     * @param difficulty Nível de dificuldade da IA
     */
    public AIPlayer(Difficulty difficulty) {
        super("IA - " + difficulty.getDisplayName());
        this.difficulty = difficulty;
        this.revealedCards = new HashMap<>();
        this.cardMemory = new LinkedList<>();
        this.random = new Random();
    }
    
    /**
     * Obtém o nível de dificuldade da IA.
     * @return Nível de dificuldade
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }
    
    /**
     * Registra uma carta revelada na memória da IA.
     * @param idCarta ID da carta
     * @param infoCarta Informações da carta
     */
    public void registrarCartaRevelada(String idCarta, CardInfo infoCarta) {
        // Adiciona à memória principal
        revealedCards.put(idCarta, infoCarta);
    }

    /**
     * Remove uma carta da memória quando ela é encontrada (matched).
     * @param idCarta ID da carta
     */
    public void removerCartaDaMemoria(String idCarta) {
        revealedCards.remove(idCarta);
    }
    
    /**
     * Registra um par de cartas abertas na memória.
     * @param carta1 ID da primeira carta
     * @param carta2 ID da segunda carta
     */
    public void registrarParAberto(String carta1, String carta2) {
        CardPair pair = new CardPair(carta1, carta2);
        cardMemory.offer(pair);
        
        // Remove pares antigos se exceder o limite de memória
        while (cardMemory.size() > difficulty.getMemorySize()) {
            CardPair removed = cardMemory.poll();
            if (removed != null) {
                // Remove as cartas do par antigo da memória principal
                revealedCards.remove(removed.getCarta1());
                revealedCards.remove(removed.getCarta2());
            }
        }
    }
    
    /**
     * Calcula a melhor jogada para a IA.
     * @param cartasDisponiveis Lista de cartas disponíveis para jogar
     * @return ID da carta escolhida, ou null se não houver jogada ótima
     */
    public String calcularMelhorJogada(List<String> cartasDisponiveis) {
        if (cartasDisponiveis.isEmpty()) {
            return null;
        }
        
        // Primeiro, tenta encontrar um match conhecido
        String matchConhecido = encontrarMatchConhecido(cartasDisponiveis);
        if (matchConhecido != null) {
            return matchConhecido;
        }
        
        // Se não encontrar match, escolhe uma carta estratégica
        String cartaEstrategica = escolherCartaEstrategica(cartasDisponiveis);
        if (cartaEstrategica != null) {
            return cartaEstrategica;
        }
        
        // Fallback: escolhe aleatoriamente
        int randomIndex = random.nextInt(cartasDisponiveis.size());
        return cartasDisponiveis.get(randomIndex);
    }
    
    /**
     * Encontra uma carta que forma match com uma carta já conhecida.
     * @param cartasDisponiveis Cartas disponíveis
     * @return ID da carta que forma match, ou null se não encontrar
     */
    private String encontrarMatchConhecido(List<String> cartasDisponiveis) {
        for (String idCarta : cartasDisponiveis) {
            CardInfo infoCarta = revealedCards.get(idCarta);
            if (infoCarta != null) {
                // Procura por uma carta que forme match com esta
                for (Map.Entry<String, CardInfo> entry : revealedCards.entrySet()) {
                    String outraCarta = entry.getKey();
                    CardInfo infoOutraCarta = entry.getValue();
                    
                    // Verifica se são cartas diferentes e formam um match
                    if (!outraCarta.equals(idCarta) && 
                        infoCarta.getResult() == infoOutraCarta.getResult() &&
                        saoTiposDiferentes(idCarta, outraCarta)) {
                        return idCarta;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Escolhe uma carta estratégica para revelar.
     * @param cartasDisponiveis Cartas disponíveis
     * @return ID da carta estratégica, ou null se não encontrar
     */
    private String escolherCartaEstrategica(List<String> cartasDisponiveis) {
        // Prioriza cartas que ainda não foram vistas
        List<String> cartasNaoVistas = new ArrayList<>();
        List<String> cartasOperacao = new ArrayList<>();
        List<String> cartasResultado = new ArrayList<>();
        
        for (String idCarta : cartasDisponiveis) {
            if (!revealedCards.containsKey(idCarta)) {
                cartasNaoVistas.add(idCarta);
            } else {
                if (idCarta.contains("op")) {
                    cartasOperacao.add(idCarta);
                } else if (idCarta.contains("re")) {
                    cartasResultado.add(idCarta);
                }
            }
        }
        
        // Prioriza cartas não vistas
        if (!cartasNaoVistas.isEmpty()) {
            return cartasNaoVistas.get(random.nextInt(cartasNaoVistas.size()));
        }
        
        // Se não há cartas não vistas, prioriza cartas de operação
        if (!cartasOperacao.isEmpty()) {
            return cartasOperacao.get(random.nextInt(cartasOperacao.size()));
        }
        
        // Fallback: qualquer carta de resultado
        if (!cartasResultado.isEmpty()) {
            return cartasResultado.get(random.nextInt(cartasResultado.size()));
        }
        
        return null;
    }
    
    /**
     * Verifica se duas cartas são de tipos diferentes (operação vs resultado).
     * @param idCarta1 ID da primeira carta
     * @param idCarta2 ID da segunda carta
     * @return true se são de tipos diferentes
     */
    private boolean saoTiposDiferentes(String idCarta1, String idCarta2) {
        boolean ehOp1 = idCarta1.contains("op");
        boolean ehOp2 = idCarta2.contains("op");
        return ehOp1 != ehOp2;
    }
    
    /**
     * Limpa a memória da IA.
     */
    public void limparMemoria() {
        cardMemory.clear();
        revealedCards.clear();
    }

    /**
     * Obtém estatísticas da memória da IA.
     * @return String com estatísticas
     */
    public String obterEstatisticasMemoria() {
        return String.format("IA %s - Cartas na memória: %d, Pares lembrados: %d/%d", 
                           difficulty.getDisplayName(), 
                           revealedCards.size(),
                           cardMemory.size(),
                           difficulty.getMemorySize());
    }
    
    /**
     * Executa a lógica do turno da IA.
     */
    @Override
    public void jogarTurno() {
        // Implementação da IA para jogar o turno
        // (pode ser deixado vazio se o controle for feito pelo GameController)
    }
    
    /**
     * Classe interna para armazenar informações de um par de cartas.
     */
    private static class CardPair {
        private final String carta1;
        private final String carta2;
        private final long timestamp;
        
        public CardPair(String carta1, String carta2) {
            this.carta1 = carta1;
            this.carta2 = carta2;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getCarta1() {
            return carta1;
        }
        
        public String getCarta2() {
            return carta2;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
    }
    
    /**
     * Classe interna para armazenar informações de uma carta.
     */
    public static class CardInfo {
        private final String displayText;
        private final int result;
        private final boolean isOperation;
        
        public CardInfo(String displayText, int result, boolean isOperation) {
            this.displayText = displayText;
            this.result = result;
            this.isOperation = isOperation;
        }
        
        public String getDisplayText() {
            return displayText;
        }
        
        public int getResult() {
            return result;
        }
        
        public boolean isOperation() {
            return isOperation;
        }
    }
}