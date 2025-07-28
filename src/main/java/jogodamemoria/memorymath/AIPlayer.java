package jogodamemoria.memorymath;

import java.util.*;

/**
 * Classe que representa um jogador controlado por IA.
 * Implementa diferentes níveis de dificuldade baseados na memória de cartas.
 * 
 * @author MemoryMath Team
 * @version 2.0
 */
public class AIPlayer extends Player {
    
    /**
     * Enumeração dos níveis de dificuldade da IA.
     */
    public enum Difficulty {
        FACIL("Fácil", 0),
        MEDIO("Médio", 5),
        DIFICIL("Difícil", Integer.MAX_VALUE);
        
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
    private final Queue<CardMemory> cardMemory;
    private final Map<String, CardInfo> revealedCards;
    private final Random random;
    
    /**
     * Construtor da IA.
     * @param difficulty Nível de dificuldade da IA
     */
    public AIPlayer(Difficulty difficulty) {
        super("IA - " + difficulty.getDisplayName());
        this.difficulty = difficulty;
        this.cardMemory = new LinkedList<>();
        this.revealedCards = new HashMap<>();
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
        
        // Adiciona à memória de tamanho limitado
        CardMemory memory = new CardMemory(idCarta, infoCarta);
        cardMemory.offer(memory);
        
        // Remove cartas antigas se exceder o limite de memória
        while (cardMemory.size() > difficulty.getMemorySize()) {
            CardMemory removed = cardMemory.poll();
            if (removed != null) {
                revealedCards.remove(removed.getCardId());
            }
        }
        
        System.out.println("IA registrou carta: " + idCarta + " - " + infoCarta.getDisplayText());
    }

    /**
     * Remove uma carta da memória quando ela é encontrada (matched).
     * @param idCarta ID da carta
     */
    public void removerCartaDaMemoria(String idCarta) {
        revealedCards.remove(idCarta);
        cardMemory.removeIf(memory -> memory.getCardId().equals(idCarta));
        System.out.println("IA removeu carta da memória: " + idCarta);
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
        
        // Estratégia baseada na dificuldade
        switch (difficulty) {
            case FACIL:
                return calcularJogadaFacil(cartasDisponiveis);
            case MEDIO:
                return calcularJogadaMedia(cartasDisponiveis);
            case DIFICIL:
                return calcularJogadaDificil(cartasDisponiveis);
            default:
                return calcularJogadaFacil(cartasDisponiveis);
        }
    }
    
        /**
     * Calcula jogada para IA fácil (aleatória).
     * @param cartasDisponiveis Cartas disponíveis
     * @return ID da carta escolhida
     */
    private String calcularJogadaFacil(List<String> cartasDisponiveis) {
        // IA fácil: escolhe aleatoriamente
        int randomIndex = random.nextInt(cartasDisponiveis.size());
        String cartaEscolhida = cartasDisponiveis.get(randomIndex);
        System.out.println("IA Fácil escolheu aleatoriamente: " + cartaEscolhida);
        return cartaEscolhida;
    }

    /**
     * Calcula jogada para IA média (memória limitada).
     * @param cartasDisponiveis Cartas disponíveis
     * @return ID da carta escolhida
     */
    private String calcularJogadaMedia(List<String> cartasDisponiveis) {
        // Primeiro, tenta encontrar um par conhecido
        String cartaCorrespondente = encontrarCartaCorrespondente(cartasDisponiveis);
        if (cartaCorrespondente != null) {
            System.out.println("IA Média encontrou par conhecido: " + cartaCorrespondente);
            return cartaCorrespondente;
        }
        
        // Se não encontrar par, escolhe uma carta que ainda não viu
        String cartaNaoVista = encontrarCartaNaoVista(cartasDisponiveis);
        if (cartaNaoVista != null) {
            System.out.println("IA Média escolheu carta não vista: " + cartaNaoVista);
            return cartaNaoVista;
        }
        
        // Fallback: escolhe aleatoriamente
        return calcularJogadaFacil(cartasDisponiveis);
    }

    /**
     * Calcula jogada para IA difícil (memória completa).
     * @param cartasDisponiveis Cartas disponíveis
     * @return ID da carta escolhida
     */
    private String calcularJogadaDificil(List<String> cartasDisponiveis) {
        // IA difícil: sempre tenta encontrar o melhor par
        String cartaCorrespondente = encontrarCartaCorrespondente(cartasDisponiveis);
        if (cartaCorrespondente != null) {
            System.out.println("IA Difícil encontrou par ótimo: " + cartaCorrespondente);
            return cartaCorrespondente;
        }
        
        // Se não há par conhecido, escolhe a carta que pode revelar mais informações
        String cartaEstrategica = encontrarCartaEstrategica(cartasDisponiveis);
        if (cartaEstrategica != null) {
            System.out.println("IA Difícil escolheu carta estratégica: " + cartaEstrategica);
            return cartaEstrategica;
        }
        
        // Fallback: escolhe aleatoriamente
        return calcularJogadaFacil(cartasDisponiveis);
    }
    
        /**
     * Encontra uma carta que forma par com uma carta já conhecida.
     * @param cartasDisponiveis Cartas disponíveis
     * @return ID da carta que forma par, ou null se não encontrar
     */
    private String encontrarCartaCorrespondente(List<String> cartasDisponiveis) {
        // Procura por pares conhecidos
        for (String idCarta : cartasDisponiveis) {
            CardInfo infoCarta = obterInfoCartaPorId(idCarta);
            if (infoCarta != null) {
                // Procura por uma carta que forme par com esta
                for (Map.Entry<String, CardInfo> entry : revealedCards.entrySet()) {
                    if (!entry.getKey().equals(idCarta) && 
                        entry.getValue().getResult() == infoCarta.getResult() &&
                        saoTiposDiferentes(entry.getKey(), idCarta)) {
                        return idCarta;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Encontra uma carta que ainda não foi vista pela IA.
     * @param cartasDisponiveis Cartas disponíveis
     * @return ID da carta não vista, ou null se todas foram vistas
     */
    private String encontrarCartaNaoVista(List<String> cartasDisponiveis) {
        for (String idCarta : cartasDisponiveis) {
            if (!revealedCards.containsKey(idCarta)) {
                return idCarta;
            }
        }
        return null;
    }

    /**
     * Encontra uma carta estratégica para revelar (para IA difícil).
     * @param cartasDisponiveis Cartas disponíveis
     * @return ID da carta estratégica, ou null se não encontrar
     */
    private String encontrarCartaEstrategica(List<String> cartasDisponiveis) {
        // Para IA difícil, escolhe uma carta que pode revelar informações úteis
        // Prioriza cartas de operação sobre cartas de resultado
        List<String> cartasOperacao = new ArrayList<>();
        List<String> cartasResultado = new ArrayList<>();
        
        for (String idCarta : cartasDisponiveis) {
            if (idCarta.contains("op")) {
                cartasOperacao.add(idCarta);
            } else if (idCarta.contains("re")) {
                cartasResultado.add(idCarta);
            }
        }
        
        // Prefere cartas de operação para obter mais informações
        if (!cartasOperacao.isEmpty()) {
            return cartasOperacao.get(random.nextInt(cartasOperacao.size()));
        } else if (!cartasResultado.isEmpty()) {
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
     * Obtém informações de uma carta baseada no ID.
     * Este método deve ser implementado para acessar as informações das cartas.
     * @param idCarta ID da carta
     * @return Informações da carta, ou null se não encontrada
     */
    private CardInfo obterInfoCartaPorId(String idCarta) {
        // Por enquanto, retorna informações básicas baseadas no ID
        // Em uma implementação completa, isso deveria acessar o GameController
        if (idCarta.contains("op")) {
            // Carta de operação
            return new CardInfo("?", 0, true);
        } else if (idCarta.contains("re")) {
            // Carta de resultado
            return new CardInfo("?", 0, false);
        }
        return null;
    }
    
        /**
     * Limpa a memória da IA.
     */
    public void limparMemoria() {
        cardMemory.clear();
        revealedCards.clear();
        System.out.println("Memória da IA foi limpa");
    }

    /**
     * Obtém estatísticas da memória da IA.
     * @return String com estatísticas
     */
    public String obterEstatisticasMemoria() {
        return String.format("IA %s - Cartas na memória: %d/%d", 
                           difficulty.getDisplayName(), 
                           revealedCards.size(), 
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
     * Classe interna para armazenar informações de memória de cartas.
     */
    private static class CardMemory {
        private final String cardId;
        private final CardInfo cardInfo;
        private final long timestamp;
        
        public CardMemory(String cardId, CardInfo cardInfo) {
            this.cardId = cardId;
            this.cardInfo = cardInfo;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getCardId() {
            return cardId;
        }
        
        public CardInfo getCardInfo() {
            return cardInfo;
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