package boardgame;
import application.SaveLoad;
import chess.ChessMatch;
import chess.Move;
import chess.PieceType;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Tato třída vytváří objekt TicTacToe, což je model hry.
 * Jak uživatel klikne na hrací desku, model je aktualizován.
 */
@SuppressWarnings("serial")
public class GameBoard extends JPanel {

    private ChessMatch c; // model for the game
    private JLabel status; // current status text
    private Pieces piece; //reusable piece which can be drawn in different forms for different pieces
    private SaveLoad sl;

    // herní konstánty
    public static final int BOARD_WIDTH = 800;
    public static final int BOARD_HEIGHT = 800;

    /**
     * Initializes the game board.
     */
    public GameBoard(JLabel statusInit) {
        // vytvoří hranici kolem oblasti soudu, metoda JComponent
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Povolí zaměření klávesnice na dvorní oblasti.
        setFocusable(true);
        
        c = new ChessMatch(); // initializuje model pro hru
        status = statusInit; // initializuje stáv JLabel
        piece = new Pieces(55, 55, 800, 800);
        sl = new SaveLoad();

        /*
         * Poslouchá mouseclicky. Aktualizuje model a poté aktualizuje herní desku
         * na základě aktualizovaného modelu.
         */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                
                // aktualizuje model vzhledem k souřadnicím myši
                c.playTurn(p.x / 100, p.y / 100);
                
                updateStatus(); // aktualizuje stáv JLabel
                repaint(); // překreslí hrací desku
            }
        });
    }

    /**
     * Resetování hry
     */
    public void reset() {
        c.reset();
        updateStatus();
        repaint();

        // Ujistěte se, že tato součást má zaměření klávesnice / myši
        requestFocusInWindow();
    }
    
    public void undo() {
        if (c.undo()) {
            updateStatus();
            repaint();
        }

        // Ujistěte se, že tato součást má zaměření klávesnice / myši
        requestFocusInWindow();
    }
    
    public void redo() {
        if (c.redo()) {
            updateStatus();
            repaint();
        }

        // Ujistěte se, že tato součást má zaměření klávesnice / myši
        requestFocusInWindow();
    }
    
    public void save() {
        String name = JOptionPane.showInputDialog("Name Save");
        if (name != null) {
            sl.save(name, c);
        }
    }
    
    public void load() {
        //kod z https://docs.oracle.com/javase/7/docs/api/javax/swing/JFileChooser.html
        JFileChooser chooser = new JFileChooser("saves/");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "txt Files", "txt");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(new JPanel());
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            c = sl.load(chooser.getSelectedFile());
            updateStatus();
            repaint();
        }
    }

    /**
     * Aktualizuje JLabel tak, aby odrážel aktuální stav hry.
     */
    private void updateStatus() {
        if (c.getCurrentPlayer()) {
            status.setText("White's Turn");
        } else {
            status.setText("Black's Turn");
        }
        
        int winner = c.checkWinner();
        System.out.println(winner);
        if (winner == 1) {
            status.setText("White wins!!!");
        } else if (winner == 2) {
            status.setText("Black wins!!!");
        } else if (winner == 3) {
            status.setText("Stalemate.");
        }
    }

    /**
     * Kreslí hrací desku.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Kreslí mřížku desky
        Color b = Color.getHSBColor(0.28f, 0.4f, 0.6f);
        Color w = Color.getHSBColor(0.14f, 0.17f, 0.85f);
        for (int i = 0; i < 8; i++) {
            if (g.getColor() == b) {
                g.setColor(w);
            } else {
                g.setColor(b);
            }
            
            for (int j = 0; j < 8; j++) {
                if (g.getColor() == b) {
                    g.setColor(w);
                } else {
                    g.setColor(b);
                }
                g.fillRect(i*100, j*100, 100, 100);
            }
        }
        
        // Kreslí figurky
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int state = c.getCell(j, i);
                switch(state) {
                    case 1:
                        piece.draw(g, 20 + 100*j, 20 + 100*i, PieceType.PAWN_W);
                        break;
                    case 2:
                        piece.draw(g, 20 + 100*j, 20 + 100*i, PieceType.ROOK_W);
                        break;
                    case 3:
                        piece.draw(g, 20 + 100*j, 20 + 100*i, PieceType.BISHOP_W);
                        break;
                    case 4:
                        piece.draw(g, 20 + 100*j, 20 + 100*i, PieceType.KNIGHT_W);
                        break;
                    case 5:
                        piece.draw(g, 20 + 100*j, 20 + 100*i, PieceType.QUEEN_W);
                        break;
                    case 6:
                        piece.draw(g, 20 + 100*j, 20 + 100*i, PieceType.KING_W);
                        break;
                    case 7:
                        piece.draw(g, 20 + 100*j, 20 + 100*i, PieceType.PAWN_B);
                        break;
                    case 8:
                        piece.draw(g, 20 + 100*j, 20 + 100*i, PieceType.ROOK_B);
                        break;
                    case 9:
                        piece.draw(g, 20 + 100*j, 20 + 100*i, PieceType.BISHOP_B);
                        break;
                    case 10:
                        piece.draw(g, 20 + 100*j, 20 + 100*i, PieceType.KNIGHT_B);
                        break;
                    case 11:
                        piece.draw(g, 20 + 100*j, 20 + 100*i, PieceType.QUEEN_B);
                        break;
                    case 12:
                        piece.draw(g, 20 + 100*j, 20 + 100*i, PieceType.KING_B);
                        break;
                
                }
            }
        }
        //Zvýraznuje možné pohyby
        if (c.getMode()) {
            for (Move m : c.getSelection()) {
                int col = m.getC2();
                int row = m.getR2();
                Color highlight = new Color(150, 150, 0, 127);
                g.setColor(highlight);
                g.fillRect(col*100, row*100, 100, 100);
            }
        }
        
        
    }

    /**
     * Vrátí velikost GameBoardu.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }
}