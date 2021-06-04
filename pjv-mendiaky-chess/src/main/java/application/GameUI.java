package application;

import boardgame.GameBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

/**
 * Tato třída nastavuje frame nejvyšší úrovně a widgety pro GUI.
 *
 * Hra inicializuje pohled, implementuje řadiče
 * funkce pomocí tlačítka reset
 */
public class GameUI implements Runnable {
    private final Logger logger = Logger.getGlobal();
    public void run() {
        // Klíčové slovo "final" označuje neměnnost i pro lokální proměnné.

        // Frame nejvyšší úrovně, ve kterém žijí herní komponenty
        final JFrame frame = new JFrame("Chess");
        frame.setLocation(400, 400);
        logger.config("JFrame added");

        // Stavový panel
        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.SOUTH);
        final JLabel status = new JLabel("Setting up...");
        status_panel.add(status);

        logger.config("Status panel added");

        // GameBoard
        final GameBoard board = new GameBoard(status);
        frame.add(board, BorderLayout.CENTER);

        // tlačítko Reset
        final JPanel control_panel = new JPanel();
        frame.add(control_panel, BorderLayout.NORTH);

        // Všimněte si, že když do tlačítka reset přidáme posluchače akcí, definujeme jej jako
        // anonymní vnitřní třída, která je příkladem ActionListener s jeho actionPerformed()
        // metoda přepsána. Po stisknutí tlačítka se vyvolá akce ().
        final JButton reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.reset();
            }
        });
        control_panel.add(reset);
        logger.config("added Reset button");
        final JButton load = new JButton("Load");
        load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.load();
            }
        });
        control_panel.add(load);

        final JButton save = new JButton("Save");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.save();
            }
        });
        control_panel.add(save);

        final JButton undo = new JButton("Undo");
        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.undo();
            }
        });
        control_panel.add(undo);

        final JButton redo = new JButton("Redo");
        redo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.redo();
            }
        });
        control_panel.add(redo);


        // Umístění frame na obrazovku
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start
        board.reset();
    }

    /**
     * Main method run to start and run the game. Initializes the GUI elements specified in Game and
     * runs it. IMPORTANT: Do NOT delete! You MUST include this in your final submission.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new GameUI());
    }
}