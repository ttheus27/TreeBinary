import javafx.application.Application;
import javafx.scene.Group;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

// Classe principal que contém o menu e o ponto de entrada do programa (main).
public class TDE2_CodigoMorse {

    // A árvore é declarada como 'static' para que possa ser acessada
    // tanto pelo menu no 'main' quanto pela classe JavaFX 'TreeVisualizer'.
    public static MorseBST morseTree = new MorseBST();

    public static final CountDownLatch latch = new CountDownLatch(1);
    // Referência para nossa aplicação JavaFX para podermos chamá-la depois
    public static TreeVisualizer fxApp;

    // Método auxiliar que espera a aplicação FX estar pronta e a retorna.
    public static TreeVisualizer waitForFxApp() {
        try {
            // Espera até que o latch seja liberado (pela thread da GUI)
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return fxApp;
    }

    private static final char[] ALPHABET = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'

    };

    // Array com os códigos Morse correspondentes. A ORDEM DEVE SER A MESMA!
    private static final String[] MORSE_CODES = {
            ".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--",
            "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--.."
    };


    // Classe interna para representar um Nó da árvore.
    static class Node {
        char letter; // Armazena o caractere (ex: 'A', 'B')
        Node left;   // Filho da esquerda (caminho do '.')
        Node right;  // Filho da direita (caminho do '-')

        // Construtor do Nó.
        public Node() {
            this.letter = '\0'; // '\0' representa um caractere nulo, para nós intermediários.
            this.left = null;
            this.right = null;
        }
    }

    // Classe interna para a Árvore Binária de Busca Morse.
    static class MorseBST {
        private Node root;

        public MorseBST() {
            // A raiz é um nó "vazio" que serve como ponto de partida.
            root = new Node();
        }

        public boolean isEmpty() {
            // A árvore está vazia se a raiz não tiver filhos.
            return root.left == null && root.right == null;
        }

        // Método público que inicia a inserção.
        public void insert(char letter, String morseCode) {
            // A chamada recursiva começa na raiz e no início do código morse (índice 0).
            root = insertRecursive(root, letter, morseCode, 0);
        }

        // Método privado recursivo para inserir um caractere.
        private Node insertRecursive(Node current, char letter, String morseCode, int index) {
            // Se o nó atual não existe, cria um novo.
            if (current == null) {
                current = new Node();
            }

            // Caso base: Se já percorremos todo o código Morse, chegamos ao nó de destino.
            if (index == morseCode.length()) {
                current.letter = letter; // Armazena a letra neste nó.
                return current;
            }

            // Passo recursivo: Decide se vai para a esquerda ou direita.
            char direction = morseCode.charAt(index);
            if (direction == '.') {
                // Se for ponto, continua a inserção pela subárvore esquerda.
                current.left = insertRecursive(current.left, letter, morseCode, index + 1);
            } else if (direction == '-') {
                // Se for traço, continua a inserção pela subárvore direita.
                current.right = insertRecursive(current.right, letter, morseCode, index + 1);
            }
            return current;
        }

        // --- DECODIFICAÇÃO ---
        // Decodifica uma palavra inteira em código Morse (ex: ".- -... -.-.")
        public String decode(String morsePhrase) {
            StringBuilder decodedMessage = new StringBuilder();
            // Divide a frase morse pelos espaços para obter o código de cada letra.
            String[] morseLetters = morsePhrase.trim().split(" ");

            for (String morseLetter : morseLetters) {
                char letter = decodeLetter(morseLetter);
                decodedMessage.append(letter);
            }
            return decodedMessage.toString();
        }

        // Método público que inicia a decodificação de uma única letra em morse.
        private char decodeLetter(String morseCode) {
            return decodeRecursive(root, morseCode, 0);
        }

        // Método privado recursivo para encontrar uma letra a partir do seu código morse.
        private char decodeRecursive(Node current, String morseCode, int index) {
            // Caso base 1: Se o nó é nulo, o código morse é inválido.
            if (current == null) {
                return '?'; // Retorna '?' para indicar um erro.
            }

            // Caso base 2: Se percorremos todo o código, encontramos o nó.
            if (index == morseCode.length()) {
                return current.letter;
            }

            // Passo recursivo: Navega para a esquerda ou direita.
            char direction = morseCode.charAt(index);
            if (direction == '.') {
                return decodeRecursive(current.left, morseCode, index + 1);
            } else {
                return decodeRecursive(current.right, morseCode, index + 1);
            }
        }

        // --- CODIFICAÇÃO ---
        // Codifica uma palavra inteira em texto para código Morse.
        public String encode(String text) {
            StringBuilder encodedMessage = new StringBuilder();
            text = text.toUpperCase(); // Converte para maiúsculas para corresponder à árvore.

            for (int i = 0; i < text.length(); i++) {
                char letter = text.charAt(i);
                // Busca o código morse para cada letra.
                String morseCode = findMorseCode(root, letter, "");
                if (morseCode != null) {
                    encodedMessage.append(morseCode).append(" ");
                } else {
                    encodedMessage.append("? "); // Se a letra não for encontrada.
                }
            }
            return encodedMessage.toString().trim();
        }

        // Método recursivo para buscar uma letra na árvore e retornar seu código Morse.
        private String findMorseCode(Node current, char letterToFind, String currentPath) {
            // Caso base 1: Se o nó é nulo, a letra não está neste caminho.
            if (current == null) {
                return null;
            }

            // Caso base 2: Se encontramos a letra no nó atual.
            if (current.letter == letterToFind) {
                return currentPath;
            }

            // Passo recursivo: Busca na subárvore esquerda.
            String foundPath = findMorseCode(current.left, letterToFind, currentPath + ".");
            // Se encontrou na esquerda, retorna o caminho.
            if (foundPath != null) {
                return foundPath;
            }

            // Se não encontrou na esquerda, busca na subárvore direita.
            foundPath = findMorseCode(current.right, letterToFind, currentPath + "-");
            return foundPath;
        }

        // --- MÉTODOS PARA VISUALIZAÇÃO ---
        public Node getRoot() {
            return root;
        }

        public int getHeight() {
            return getHeight(root);
        }

        private int getHeight(Node node) {
            if (node == null) return 0;
            return 1 + Math.max(getHeight(node.left), getHeight(node.right));
        }
    }




    // --- LÓGICA DO MENU E EXECUÇÃO PRINCIPAL ---
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("\n--- MENU CÓDIGO MORSE ---");
            System.out.println("1. Inserir um novo caractere");
            System.out.println("2. Codificar (Texto para Morse)");
            System.out.println("3. Decodificar (Morse para Texto)");
            System.out.println("4. Exibir Árvore Binária");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                option = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                option = -1; // Opção inválida
            }

            switch (option) {
                case 1:
                    handleInsert(scanner);
                    break;
                case 2:
                    handleEncode(scanner);
                    break;
                case 3:
                    handleDecode(scanner);
                    break;
                case 4:
                    // LÓGICA ATUALIZADA PARA EXIBIR A ÁRVORE
                    if (morseTree.isEmpty()) {
                        System.out.println("ERRO: A árvore está vazia. Insira caracteres antes de exibi-la.");
                    } else {
                        // Se a aplicação FX ainda não foi iniciada...
                        if (fxApp == null) {
                            System.out.println("Iniciando a interface gráfica...");
                            // ...inicia ela em uma NOVA THREAD para não bloquear o console.
                            new Thread(() -> Application.launch(TreeVisualizer.class, args)).start();
                            // Espera a aplicação sinalizar que está pronta.
                            fxApp = waitForFxApp();
                        }
                        // Pede para a thread da GUI (que já está rodando) mostrar a janela.
                        Platform.runLater(() -> fxApp.showStage());
                    }
                    break;
                    case 0:
                    System.out.println("Saindo do programa...");
                    // --- MUDANÇA: Garante que a thread da GUI seja encerrada também ---
                    if (fxApp != null) {
                        Platform.exit();
                    }
                    // --- FIM DA MUDANÇA ---
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
                    break;
            }
        } while (option != 0);

        scanner.close();
    }

    private static String getMorseCodeForLetter(char letter) {
        // Percorre o nosso array de alfabeto.
        for (int i = 0; i < ALPHABET.length; i++) {
            // Se encontrar a letra que estamos procurando...
            if (ALPHABET[i] == letter) {
                // ...retorna o código que está na mesma posição no outro array.
                return MORSE_CODES[i];
            }
        }
        // Se o loop terminar e não encontrar a letra, retorna null.
        return null;
    }

    private static void handleInsert(Scanner scanner) {
        // MUDANÇA: O texto do prompt foi melhorado para incluir a nova opção.
        System.out.print("Digite o caractere a ser inserido (ou 'TUDO' para popular a árvore): ");
        String input = scanner.nextLine();

        // MUDANÇA: Verifica se o usuário digitou a palavra-chave "TUDO".
        // Usamos equalsIgnoreCase para aceitar "tudo", "TUDO", "Tudo", etc.
        if (input.equalsIgnoreCase("TUDO")) {
            System.out.println("Populando a árvore com todos os caracteres suportados...");

            // Loop que percorre nossos arrays de dicionário
            for (int i = 0; i < ALPHABET.length; i++) {
                char letter = ALPHABET[i];
                String morseCode = MORSE_CODES[i];
                // Insere cada par de letra/código na árvore
                morseTree.insert(letter, morseCode);
            }

            System.out.println("Árvore populada com sucesso com " + ALPHABET.length + " caracteres!");
            return; // Retorna para o menu principal
        }

        // Se o input não foi "TUDO", o código abaixo (que você já tinha) é executado.
        String letterInput = input.toUpperCase();
        if (letterInput.length() != 1) {
            System.out.println("ERRO: Entrada inválida. Por favor, insira apenas um caractere ou a palavra 'TUDO'.");
            return;
        }

        char letter = letterInput.charAt(0);
        String morseCode = getMorseCodeForLetter(letter);

        if (morseCode == null) {
            System.out.println("ERRO: Caractere '" + letter + "' não é suportado ou é inválido.");
            return;
        }

        morseTree.insert(letter, morseCode);
        System.out.println("Caractere '" + letter + "' (código " + morseCode + ") foi inserido com sucesso!");
    }
    private static void handleEncode(Scanner scanner) {
        if (morseTree.isEmpty()) {
            System.out.println("ERRO: A árvore está vazia. Impossível codificar.");
            return;
        }
        System.out.print("Digite o texto para codificar: ");
        String text = scanner.nextLine();
        if (text == null || text.trim().isEmpty()) {
            System.out.println("ERRO: Entrada inválida.");
            return;
        }
        String encoded = morseTree.encode(text);
        System.out.println("Código Morse: " + encoded);
    }

    private static void handleDecode(Scanner scanner) {
        if (morseTree.isEmpty()) {
            System.out.println("ERRO: A árvore está vazia. Impossível decodificar.");
            return;
        }
        System.out.print("Digite o código Morse para decodificar (letras separadas por espaço): ");
        String morse = scanner.nextLine();
        if (morse == null || morse.trim().isEmpty() || !morse.matches("^[\\.\\-\\s]+$")) {
            System.out.println("ERRO: Entrada inválida. Use apenas '.', '-' e espaços.");
            return;
        }
        String decoded = morseTree.decode(morse);
        System.out.println("Texto decodificado: " + decoded);
    }

//    // Método para popular a árvore com o alfabeto Morse padrão.
//    public static void populateTree() {
//        morseTree.insert('E', ".");
//        morseTree.insert('T', "-");
//        morseTree.insert('I', "..");
//        morseTree.insert('A', ".-");
//        morseTree.insert('N', "-.");
//        morseTree.insert('M', "--");
//        morseTree.insert('S', "...");
//        morseTree.insert('U', "..-");
//        morseTree.insert('R', ".-.");
//        morseTree.insert('W', ".--");
//        morseTree.insert('D', "-..");
//        morseTree.insert('K', "-.-");
//        morseTree.insert('G', "--.");
//        morseTree.insert('O', "---");
//        morseTree.insert('H', "....");
//        morseTree.insert('V', "...-");
//        morseTree.insert('F', "..-.");
//        morseTree.insert('L', ".-..");
//        morseTree.insert('P', ".--.");
//        morseTree.insert('J', ".---");
//        morseTree.insert('B', "-...");
//        morseTree.insert('X', "-..-");
//        morseTree.insert('C', "-.-.");
//        morseTree.insert('Y', "-.--");
//        morseTree.insert('Z', "--..");
//        morseTree.insert('Q', "--.-");
//    }

    // --- CLASSE DE VISUALIZAÇÃO GRÁFICA (JAVAFX) ---
    // Esta classe é separada da lógica principal e é responsável apenas por desenhar.
    public static class TreeVisualizer extends Application {
        private Stage stage;

        // MUDANÇA: Construtor para guardar a referência da aplicação
        public TreeVisualizer() {
            TDE2_CodigoMorse.fxApp = this;
        }

        @Override
        public void start(Stage primaryStage) {
            this.stage = primaryStage;
            primaryStage.setTitle("Visualizador de Árvore Binária Morse");

            // MUDANÇA: Configura a GUI para não encerrar o programa ao ser fechada
            Platform.setImplicitExit(false);
            primaryStage.setOnCloseRequest(event -> {
                primaryStage.hide(); // Apenas esconde a janela
                event.consume();
            });

            // Prepara a cena, mas sem desenhar nada ainda
            Group rootGroup = new Group();
            Scene scene = new Scene(rootGroup);
            primaryStage.setScene(scene);

            // MUDANÇA: Sinaliza que a inicialização terminou e o console pode continuar
            TDE2_CodigoMorse.latch.countDown();
        }

        // MUDANÇA: Método para ser chamado de fora para mostrar e redesenhar a janela
        public void showStage() {
            if (stage != null) {
                drawTreeOnCanvas(); // Redesenha a árvore com os dados mais recentes
                stage.show();
                stage.toFront(); // Traz a janela para a frente
            }
        }

        // MUDANÇA: Lógica de desenho movida para seu próprio método
        private void drawTreeOnCanvas() {
            MorseBST bst = TDE2_CodigoMorse.morseTree;
            int height = bst.getHeight();
            int canvasHeight = 100 + height * 100;
            int canvasWidth = Math.max(800, (int) Math.pow(2, height) * 50);

            Canvas canvas = new Canvas(canvasWidth, canvasHeight);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            drawNode(gc, bst.getRoot(), canvasWidth / 2, 40, canvasWidth / 4);

            // Atualiza o conteúdo da cena com o novo canvas desenhado
            ((Group)stage.getScene().getRoot()).getChildren().clear();
            ((Group)stage.getScene().getRoot()).getChildren().add(canvas);
        }

        private void drawNode(GraphicsContext gc, Node node, double x, double y, double xOffset) {
            if (node == null) {
                return;
            }
            gc.setStroke(Color.BLACK);
            gc.strokeOval(x - 15, y - 15, 30, 30);
            char letter = node.letter;
            if (letter != '\0') {
                gc.fillText(String.valueOf(letter), x - 4, y + 4);
            }
            if (node.left != null) {
                double newX = x - xOffset;
                double newY = y + 80;
                gc.strokeLine(x - 10, y + 10, newX + 10, newY - 10);
                drawNode(gc, node.left, newX, newY, xOffset / 2);
            }
            if (node.right != null) {
                double newX = x + xOffset;
                double newY = y + 80;
                gc.strokeLine(x + 10, y + 10, newX - 10, newY - 10);
                drawNode(gc, node.right, newX, newY, xOffset / 2);
            }
        }
    }
}