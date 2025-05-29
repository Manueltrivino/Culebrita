import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class JuegoCulebrita extends JFrame {

    public JuegoCulebrita() {
        // Configuración de la ventana principal del juego
        this.add(new PanelJuego()); // Añade el panel del juego a la ventana
        this.setTitle("Juego de la Culebrita"); // Título de la ventana
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Acción al cerrar la ventana
        this.setResizable(false); // Evita que se redimensione la ventana
        this.pack(); // Ajusta el tamaño de la ventana al contenido
        this.setVisible(true); // Hace visible la ventana
        this.setLocationRelativeTo(null); // Centra la ventana en la pantalla
    }

    public static void main(String[] args) {
        // Crea una instancia del juego para iniciarlo
        new JuegoCulebrita();
    }
}

class PanelJuego extends JPanel implements ActionListener, KeyListener {

    // Constantes para el tamaño del juego y unidades
    static final int ANCHO_PANTALLA = 600;
    static final int ALTO_PANTALLA = 600;
    static final int TAMANO_UNIDAD = 25; // Tamaño de cada celda y parte de la culebra
    static final int UNIDADES_JUEGO = (ANCHO_PANTALLA * ALTO_PANTALLA) / (TAMANO_UNIDAD * TAMANO_UNIDAD); // Máximo número de unidades en el juego
    static final int RETRASO = 100; // Velocidad del juego (milisegundos)

    // Arreglos para las coordenadas X e Y de la culebra
    final int x[] = new int[UNIDADES_JUEGO];
    final int y[] = new int[UNIDADES_JUEGO];

    // Estado del juego
    int partesCuerpo = 6; // Longitud inicial de la culebra
    int manzanasComidas = 0;
    int manzanaX; // Coordenada X de la manzana
    int manzanaY; // Coordenada Y de la manzana
    char direccion = 'R'; // Dirección inicial: 'R' (Derecha), 'L' (Izquierda), 'U' (Arriba), 'D' (Abajo)
    boolean corriendo = false; // Indica si el juego está en curso
    Timer timer;
    Random random;

    // Constructor del panel del juego
    PanelJuego() {
        random = new Random();
        this.setPreferredSize(new Dimension(ANCHO_PANTALLA, ALTO_PANTALLA));
        this.setBackground(Color.black); // Color de fondo del panel
        this.setFocusable(true); // Permite que el panel reciba eventos de teclado
        this.addKeyListener(this); // Añade el listener para las teclas
        iniciarJuego();
    }

    // Método para iniciar o reiniciar el juego
    public void iniciarJuego() {
        nuevaManzana(); // Coloca la primera manzana
        partesCuerpo = 6; // Reinicia la longitud de la culebra
        manzanasComidas = 0; // Reinicia el contador de manzanas
        direccion = 'R'; // Dirección inicial
        // Posición inicial de la culebra (centrada)
        for (int i = 0; i < partesCuerpo; i++) {
            x[i] = ANCHO_PANTALLA / 2 - i * TAMANO_UNIDAD;
            y[i] = ALTO_PANTALLA / 2;
        }
        corriendo = true;
        if (timer != null) {
            timer.stop(); // Detiene el timer anterior si existe
        }
        timer = new Timer(RETRASO, this); // Crea un nuevo timer para el bucle del juego
        timer.start(); // Inicia el timer
    }

    // Método para dibujar los componentes del juego
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        dibujar(g);
    }

    // Método auxiliar para dibujar
    public void dibujar(Graphics g) {
        if (corriendo) {
            // Dibuja la cuadrícula (opcional, para visualización)
            /*
            for (int i = 0; i < ANCHO_PANTALLA / TAMANO_UNIDAD; i++) {
                g.drawLine(i * TAMANO_UNIDAD, 0, i * TAMANO_UNIDAD, ALTO_PANTALLA);
                g.drawLine(0, i * TAMANO_UNIDAD, ANCHO_PANTALLA, i * TAMANO_UNIDAD);
            }
            */

            // Dibuja la manzana
            g.setColor(Color.red);
            g.fillOval(manzanaX, manzanaY, TAMANO_UNIDAD, TAMANO_UNIDAD);

            // Dibuja la culebra
            for (int i = 0; i < partesCuerpo; i++) {
                if (i == 0) { // Cabeza de la culebra
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], TAMANO_UNIDAD, TAMANO_UNIDAD);
                } else { // Cuerpo de la culebra
                    g.setColor(new Color(45, 180, 0));
                    // g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255))); // Culebra multicolor (opcional)
                    g.fillRect(x[i], y[i], TAMANO_UNIDAD, TAMANO_UNIDAD);
                }
            }
            // Dibuja el puntaje
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free", Font.BOLD, 30));
            FontMetrics metrics = getFontMetrics(g.getFont());
            String textoPuntaje = "Puntaje: " + manzanasComidas;
            g.drawString(textoPuntaje, (ANCHO_PANTALLA - metrics.stringWidth(textoPuntaje)) / 2, g.getFont().getSize());

        } else {
            gameOver(g); // Muestra la pantalla de Game Over
        }
    }

    // Coloca una nueva manzana en una posición aleatoria
    public void nuevaManzana() {
        manzanaX = random.nextInt((int) (ANCHO_PANTALLA / TAMANO_UNIDAD)) * TAMANO_UNIDAD;
        manzanaY = random.nextInt((int) (ALTO_PANTALLA / TAMANO_UNIDAD)) * TAMANO_UNIDAD;
    }

    // Mueve la culebra
    public void mover() {
        // Mueve el cuerpo de la culebra (desde la cola hacia la cabeza)
        for (int i = partesCuerpo; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        // Mueve la cabeza de la culebra según la dirección
        switch (direccion) {
            case 'U': // Arriba
                y[0] = y[0] - TAMANO_UNIDAD;
                break;
            case 'D': // Abajo
                y[0] = y[0] + TAMANO_UNIDAD;
                break;
            case 'L': // Izquierda
                x[0] = x[0] - TAMANO_UNIDAD;
                break;
            case 'R': // Derecha
                x[0] = x[0] + TAMANO_UNIDAD;
                break;
        }
    }

    // Verifica si la culebra ha comido la manzana
    public void verificarManzana() {
        if ((x[0] == manzanaX) && (y[0] == manzanaY)) {
            partesCuerpo++; // Aumenta la longitud de la culebra
            manzanasComidas++; // Aumenta el puntaje
            nuevaManzana(); // Coloca una nueva manzana
        }
    }

    // Verifica si hay colisiones (con el cuerpo o con los bordes)
    public void verificarColisiones() {
        // Verifica si la cabeza colisiona con el cuerpo
        for (int i = partesCuerpo; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                corriendo = false; // Termina el juego
            }
        }
        // Verifica si la cabeza colisiona con el borde izquierdo
        if (x[0] < 0) {
            corriendo = false;
        }
        // Verifica si la cabeza colisiona con el borde derecho
        if (x[0] >= ANCHO_PANTALLA) {
            corriendo = false;
        }
        // Verifica si la cabeza colisiona con el borde superior
        if (y[0] < 0) {
            corriendo = false;
        }
        // Verifica si la cabeza colisiona con el borde inferior
        if (y[0] >= ALTO_PANTALLA) {
            corriendo = false;
        }

        if (!corriendo) {
            timer.stop(); // Detiene el timer si hay colisión
        }
    }

    // Muestra la pantalla de Game Over
    public void gameOver(Graphics g) {
        // Texto de Puntaje Final
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metricsPuntaje = getFontMetrics(g.getFont());
        String textoPuntajeFinal = "Puntaje Final: " + manzanasComidas;
        g.drawString(textoPuntajeFinal, (ANCHO_PANTALLA - metricsPuntaje.stringWidth(textoPuntajeFinal)) / 2, ALTO_PANTALLA / 2 - 50);

        // Texto de Game Over
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsGameOver = getFontMetrics(g.getFont());
        g.drawString("Game Over", (ANCHO_PANTALLA - metricsGameOver.stringWidth("Game Over")) / 2, ALTO_PANTALLA / 2);

        // Texto para reiniciar
        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.BOLD, 25));
        FontMetrics metricsReiniciar = getFontMetrics(g.getFont());
        g.drawString("Presiona 'R' para Reiniciar", (ANCHO_PANTALLA - metricsReiniciar.stringWidth("Presiona 'R' para Reiniciar")) / 2, ALTO_PANTALLA / 2 + 50);
    }

    // Método que se ejecuta con cada "tick" del timer (bucle del juego)
    @Override
    public void actionPerformed(ActionEvent e) {
        if (corriendo) {
            mover();
            verificarManzana();
            verificarColisiones();
        }
        repaint(); // Vuelve a dibujar el panel
    }

    // Métodos del KeyListener para manejar la entrada del teclado
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (direccion != 'R') { // Evita que la culebra se mueva en dirección opuesta instantáneamente
                    direccion = 'L';
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (direccion != 'L') {
                    direccion = 'R';
                }
                break;
            case KeyEvent.VK_UP:
                if (direccion != 'D') {
                    direccion = 'U';
                }
                break;
            case KeyEvent.VK_DOWN:
                if (direccion != 'U') {
                    direccion = 'D';
                }
                break;
            case KeyEvent.VK_R: // Tecla 'R' para reiniciar
                if (!corriendo) {
                    iniciarJuego();
                }
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No se necesita implementar para este juego
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No se necesita implementar para este juego
    }
}
