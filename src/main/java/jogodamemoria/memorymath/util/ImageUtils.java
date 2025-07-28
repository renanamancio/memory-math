package jogodamemoria.memorymath.util;

import javafx.scene.image.Image;
import jogodamemoria.memorymath.util.ResourceLoadException;

public class ImageUtils {
    public static Image carregarImagem(String caminho) throws ResourceLoadException {
        try {
            Image img = new Image(ImageUtils.class.getResourceAsStream(caminho));
            if (img.isError()) {
                throw new ResourceLoadException("Erro ao carregar imagem: " + caminho);
            }
            return img;
        } catch (Exception e) {
            throw new ResourceLoadException("Erro ao carregar imagem: " + caminho + " - " + e.getMessage());
        }
    }
} 