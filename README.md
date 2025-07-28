# 🧮 Memory Math - Jogo de Memória Matemático

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.13.0-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

## 📖 Descrição

**Memory Math** é um jogo educativo de memória que combina o clássico jogo da memória com operações matemáticas. Os jogadores devem encontrar pares de cartas onde uma carta contém uma operação matemática e a outra contém o resultado correspondente.

O jogo foi desenvolvido em **Java** utilizando **JavaFX** para a interface gráfica, oferecendo uma experiência interativa e educativa para crianças e adultos que desejam praticar matemática de forma divertida.

## 🎮 Características Principais

### ✨ Funcionalidades
- **Modo PvP (Player vs Player)**: Jogo entre dois jogadores humanos
- **Modo PvE (Player vs Environment)**: Jogo contra IA com três níveis de dificuldade
- **Operações Matemáticas**: Soma, subtração, multiplicação e divisão
- **Sistema de Pontuação**: Controle de pontuação por jogador
- **Interface Responsiva**: Design adaptável e moderno
- **Efeitos Sonoros**: Sons para ações do jogo (virar carta, acerto, erro, vitória)
- **Música de Fundo**: Trilha sonora durante o jogo

### 🎯 Níveis de Dificuldade da IA
- **Fácil**: IA com memória limitada (0 cartas)
- **Médio**: IA com memória moderada (5 cartas)
- **Difícil**: IA com memória perfeita (todas as cartas)

### 🎨 Interface Gráfica
- Design moderno e intuitivo
- Animações suaves de transição
- Cores diferenciadas por tipo de operação
- Layout responsivo que se adapta ao tamanho da janela

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 21**: Linguagem principal
- **JavaFX 21**: Framework para interface gráfica
- **Maven**: Gerenciador de dependências e build

### Frontend
- **FXML**: Definição de layouts
- **CSS**: Estilização da interface
- **JavaFX Controls**: Componentes da interface

### Recursos
- **Imagens**: PNG e JPG para cartas e elementos visuais
- **Áudio**: MP3 para efeitos sonoros e música

## 📁 Estrutura do Projeto

```
memoryMath/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── jogodamemoria/
│   │   │       └── memorymath/
│   │   │           ├── controllers/          # Controladores FXML
│   │   │           ├── model/                # Modelos de dados
│   │   │           ├── transitions/          # Gerenciamento de cenas
│   │   │           ├── util/                 # Utilitários
│   │   │           ├── AIPlayer.java         # Jogador IA
│   │   │           ├── GameManager.java      # Gerenciador do jogo
│   │   │           ├── HumanPlayer.java      # Jogador humano
│   │   │           ├── Main.java             # Classe principal
│   │   │           └── Player.java           # Classe base do jogador
│   │   └── resources/
│   │       ├── css/                          # Estilos CSS
│   │       ├── fxml/                         # Layouts FXML
│   │       ├── images/                       # Imagens do jogo
│   │       └── sounds/                       # Arquivos de áudio
│   └── test/                                 # Testes unitários
├── pom.xml                                   # Configuração Maven
└── README.md                                 # Este arquivo
```

## 🚀 Como Executar

### Pré-requisitos
- **Java 21** ou superior
- **Maven 3.6** ou superior

### Instalação e Execução

1. **Clone o repositório**
   ```bash
   git clone https://github.com/seu-usuario/memoryMath.git
   cd memoryMath
   ```

2. **Compile o projeto**
   ```bash
   mvn clean compile
   ```

3. **Execute o jogo**
   ```bash
   mvn javafx:run
   ```

### Execução Alternativa
```bash
# Compilar e executar em um comando
mvn clean javafx:run
```

## 🎮 Como Jogar

### Iniciando uma Partida
1. Execute o jogo
2. Clique em "Nova Partida"
3. Escolha o modo de jogo (PvP ou PvE)
4. Configure os jogadores e operações
5. Clique em "Iniciar Jogo"

### Regras do Jogo
- O tabuleiro possui duas grades: **Operações** e **Resultados**
- Cada carta de operação tem um par correspondente na grade de resultados
- Clique em uma carta para revelá-la
- Clique em uma segunda carta para tentar formar um par
- Se as cartas formarem um par correto, você ganha pontos e pode jogar novamente
- Se não formarem um par, elas voltam a ficar ocultas e o turno passa para o outro jogador
- O jogo termina quando todos os pares forem encontrados
- O jogador com mais pontos vence

### Pontuação
- **Par correto**: +1 ponto
- **Par incorreto**: 0 pontos (turno passa para o outro jogador)

## 🔧 Configuração

### Personalização de Operações
O jogo permite selecionar quais tipos de operações matemáticas serão utilizadas:
- ✅ **Soma**: Operações de adição
- ✅ **Subtração**: Operações de subtração  
- ✅ **Multiplicação**: Operações de multiplicação
- ✅ **Divisão**: Operações de divisão

### Configuração da IA
- **Fácil**: IA com comportamento aleatório
- **Médio**: IA com memória limitada de 5 cartas
- **Difícil**: IA com memória perfeita de todas as cartas

## 🎨 Personalização

### Modificando Estilos
Os estilos visuais podem ser personalizados editando o arquivo:
```
src/main/resources/css/styles.css
```

### Adicionando Novos Sons
Para adicionar novos efeitos sonoros:
1. Adicione o arquivo de áudio em `src/main/resources/sounds/`
2. Modifique a classe `AudioManager` para incluir o novo som

### Alterando Imagens
Para modificar as imagens das cartas:
1. Substitua os arquivos em `src/main/resources/images/`
2. Mantenha os mesmos nomes de arquivo ou atualize as referências no código

## 🧪 Testes

### Executando Testes
```bash
mvn test
```

### Cobertura de Testes
```bash
mvn jacoco:report
```

## 📦 Build

### Criando JAR Executável
```bash
mvn clean package
```

### Criando Distribuição
```bash
mvn javafx:jlink
```

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

### Padrões de Código
- Siga as convenções de nomenclatura Java
- Adicione comentários Javadoc para métodos públicos
- Mantenha a cobertura de testes acima de 80%
- Use nomes descritivos para variáveis e métodos

## 📝 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

## 👥 Autores

- **Memory Math Team** - *Desenvolvimento inicial* - [GitHub](https://github.com/seu-usuario)

## 🙏 Agradecimentos

- JavaFX Community
- Maven Community
- Contribuidores e testadores do projeto

## 📞 Suporte

Se você encontrar algum problema ou tiver sugestões:

1. Verifique se há uma [issue](https://github.com/seu-usuario/memoryMath/issues) relacionada
2. Crie uma nova issue com detalhes do problema
3. Para dúvidas gerais, abra uma [discussion](https://github.com/seu-usuario/memoryMath/discussions)

---

**Divirta-se jogando Memory Math! 🎮🧮** 