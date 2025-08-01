package jogodamemoria.memorymath.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;

/**
 * Gerenciador de áudio para o jogo Memory Math.
 * Controla todos os sons do jogo incluindo música de fundo e efeitos sonoros.
 * Implementa o padrão Singleton para garantir uma única instância.
 * 
 * @author Renan Amancio
 * @version 1.0
 */
public class AudioManager {
    
    private static AudioManager instance;
    private MediaPlayer musicaFundo;
    private MediaPlayer somRevelarCarta;
    private MediaPlayer somMatch;
    private MediaPlayer somNaoMatch;
    private MediaPlayer somVitoria;
    private MediaPlayer somDerrota;
    
    private boolean somAtivado = true;
    private boolean musicaAtivada = true;
    private double volumeMusica = 0.3;
    private double volumeEfeitos = 0.7;
    
    private AudioManager() {
        inicializarSons();
    }
    
    /**
     * Obtém a instância única do AudioManager (Singleton).
     * 
     * @return Instância do AudioManager
     */
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    /**
     * Inicializa todos os sons do jogo.
     */
    private void inicializarSons() {
        try {
            URL musicaUrl = getClass().getResource("/sounds/music-background.mp3");
            if (musicaUrl != null) {
                Media musica = new Media(musicaUrl.toExternalForm());
                musicaFundo = new MediaPlayer(musica);
                musicaFundo.setVolume(volumeMusica);
                musicaFundo.setCycleCount(MediaPlayer.INDEFINITE);
            }

            URL somRevelarUrl = getClass().getResource("/sounds/flip-card.mp3");
            if (somRevelarUrl != null) {
                Media mediaRevelar = new Media(somRevelarUrl.toExternalForm());
                somRevelarCarta = new MediaPlayer(mediaRevelar);
                somRevelarCarta.setVolume(volumeEfeitos);
            }

            URL somMatchUrl = getClass().getResource("/sounds/card-correto.mp3");
            if (somMatchUrl != null) {
                Media mediaMatch = new Media(somMatchUrl.toExternalForm());
                somMatch = new MediaPlayer(mediaMatch);
                somMatch.setVolume(volumeEfeitos);
            }

            URL somErroUrl = getClass().getResource("/sounds/card-erro.mp3");
            if (somErroUrl != null) {
                Media mediaErro = new Media(somErroUrl.toExternalForm());
                somNaoMatch = new MediaPlayer(mediaErro);
                somNaoMatch.setVolume(volumeEfeitos);
            } else {
                somNaoMatch = new MediaPlayer(somRevelarCarta.getMedia());
                somNaoMatch.setVolume(volumeEfeitos);
            }

            URL somVitoriaUrl = getClass().getResource("/sounds/vitoria.mp3");
            if (somVitoriaUrl != null) {
                Media mediaVitoria = new Media(somVitoriaUrl.toExternalForm());
                somVitoria = new MediaPlayer(mediaVitoria);
                somVitoria.setVolume(volumeEfeitos);
            } else {
                somVitoria = new MediaPlayer(somMatch.getMedia());
                somVitoria.setVolume(volumeEfeitos);
            }

            URL somDerrotaUrl = getClass().getResource("/sounds/derrota.mp3");
            if (somDerrotaUrl != null) {
                Media mediaDerrota = new Media(somDerrotaUrl.toExternalForm());
                somDerrota = new MediaPlayer(mediaDerrota);
                somDerrota.setVolume(volumeEfeitos);
            } else {
                somDerrota = new MediaPlayer(somNaoMatch.getMedia());
                somDerrota.setVolume(volumeEfeitos);
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao carregar sons: " + e.getMessage());
        }
    }
    
    /**
     * Toca o som de revelar carta.
     */
    public void tocarSomRevelarCarta() {
        if (somAtivado && somRevelarCarta != null) {
            somRevelarCarta.stop();
            somRevelarCarta.seek(Duration.ZERO);
            somRevelarCarta.play();
        }
    }
    
    /**
     * Toca o som de match (carta encontrada).
     */
    public void tocarSomMatch() {
        if (somAtivado && somMatch != null) {
            somMatch.stop();
            somMatch.seek(Duration.ZERO);
            somMatch.play();
        }
    }
    
    /**
     * Toca o som de não match.
     */
    public void tocarSomNaoMatch() {
        if (somAtivado && somNaoMatch != null) {
            somNaoMatch.stop();
            somNaoMatch.seek(Duration.ZERO);
            somNaoMatch.play();
        }
    }
    
    /**
     * Toca o som de vitória.
     */
    public void tocarSomVitoria() {
        if (somAtivado && somVitoria != null) {
            somVitoria.stop();
            somVitoria.seek(Duration.ZERO);
            somVitoria.play();
        }
    }
    
    /**
     * Toca o som de derrota.
     */
    public void tocarSomDerrota() {
        if (somAtivado && somDerrota != null) {
            somDerrota.stop();
            somDerrota.seek(Duration.ZERO);
            somDerrota.play();
        }
    }
    
    /**
     * Inicia a música de fundo.
     */
    public void iniciarMusicaFundo() {
        if (musicaAtivada && musicaFundo != null) {
            musicaFundo.play();
        }
    }
    
    /**
     * Para a música de fundo.
     */
    public void pararMusicaFundo() {
        if (musicaFundo != null) {
            musicaFundo.stop();
        }
    }
    
    /**
     * Pausa a música de fundo.
     */
    public void pausarMusicaFundo() {
        if (musicaFundo != null) {
            musicaFundo.pause();
        }
    }
    
    /**
     * Retoma a música de fundo.
     */
    public void retomarMusicaFundo() {
        if (musicaAtivada && musicaFundo != null) {
            musicaFundo.play();
        }
    }
    
    /**
     * Define se o som está ativado.
     * 
     * @param ativado true para ativar, false para desativar
     */
    public void setSomAtivado(boolean ativado) {
        this.somAtivado = ativado;
    }
    
    /**
     * Define se a música está ativada.
     * 
     * @param ativada true para ativar, false para desativar
     */
    public void setMusicaAtivada(boolean ativada) {
        this.musicaAtivada = ativada;
        if (!ativada) {
            pararMusicaFundo();
        } else if (musicaFundo != null && musicaFundo.getStatus() == MediaPlayer.Status.PAUSED) {
            retomarMusicaFundo();
        }
    }
    
    /**
     * Define o volume da música.
     * 
     * @param volume Volume entre 0.0 e 1.0
     */
    public void setVolumeMusica(double volume) {
        this.volumeMusica = Math.max(0.0, Math.min(1.0, volume));
        if (musicaFundo != null) {
            musicaFundo.setVolume(volumeMusica);
        }
    }
    
    /**
     * Define o volume dos efeitos sonoros.
     * 
     * @param volume Volume entre 0.0 e 1.0
     */
    public void setVolumeEfeitos(double volume) {
        this.volumeEfeitos = Math.max(0.0, Math.min(1.0, volume));
        if (somRevelarCarta != null) {
            somRevelarCarta.setVolume(volumeEfeitos);
        }
        if (somMatch != null) {
            somMatch.setVolume(volumeEfeitos);
        }
        if (somNaoMatch != null) {
            somNaoMatch.setVolume(volumeEfeitos);
        }
        if (somVitoria != null) {
            somVitoria.setVolume(volumeEfeitos);
        }
        if (somDerrota != null) {
            somDerrota.setVolume(volumeEfeitos);
        }
    }
    
    /**
     * Verifica se o som está ativado.
     * 
     * @return true se o som está ativado
     */
    public boolean isSomAtivado() {
        return somAtivado;
    }
    
    /**
     * Verifica se a música está ativada.
     * 
     * @return true se a música está ativada
     */
    public boolean isMusicaAtivada() {
        return musicaAtivada;
    }
    
    /**
     * Obtém o volume da música.
     * 
     * @return Volume da música (0.0 a 1.0)
     */
    public double getVolumeMusica() {
        return volumeMusica;
    }
    
    /**
     * Obtém o volume dos efeitos.
     * 
     * @return Volume dos efeitos (0.0 a 1.0)
     */
    public double getVolumeEfeitos() {
        return volumeEfeitos;
    }
    
    /**
     * Libera recursos de áudio.
     */
    public void liberarRecursos() {
        if (musicaFundo != null) {
            musicaFundo.dispose();
        }
        if (somRevelarCarta != null) {
            somRevelarCarta.dispose();
        }
        if (somMatch != null) {
            somMatch.dispose();
        }
        if (somNaoMatch != null) {
            somNaoMatch.dispose();
        }
        if (somVitoria != null) {
            somVitoria.dispose();
        }
        if (somDerrota != null) {
            somDerrota.dispose();
        }
    }
} 