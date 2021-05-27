package gui;
import application.UI;
import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPosition;




import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.List;
import java.util.*;

/**
 * Table class for chess board.
 *
 * @author frogp
 */
public class Table extends Observable {

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;

    private static ChessMatch chessMatch;
    private static String defaultPieceIconPath = "graphic/PieceIcons/";
    private final Game game;

    private ChessPosition sourcePosition;
    private ChessPosition targetPosition;
    private Piece humanMovedPiece;

    private boolean highlightValidMoves;

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private final static Dimension SQUARE_PANEL_DIMENSION = new Dimension(10, 10);

    public static final Table INSTANCE = new Table();


    private Table() {
        this.gameFrame = new JFrame("PJV-MENDIAKY");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessMatch = new ChessMatch();
        this.boardPanel = new BoardPanel();
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.setVisible(true);
        this.highlightValidMoves = false;
        this.game = new Game(this.gameFrame, true);
        this.gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JMenuBar createTableMenuBar() {
        JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("Soubor");

        final JMenuItem newGame = new JMenuItem("Nová hra");
        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int reply = JOptionPane.showConfirmDialog(Table.get().getBoardPanel(),
                        "Tím se vytvoří nová hra, aktuální progres budou ztraceny. Chcete pokračovat?");
                if (reply == 0) {
                    System.out.println("Spuštění nové hry!");
                    chessMatch = new ChessMatch();
                    Table.get().getBoardPanel().drawBoard(chessMatch.board);
                }
            }
        });
        fileMenu.add(newGame);

        final JMenuItem exit = new JMenuItem("Výstup");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Výstup!");
                System.exit(0);
            }
        });
        fileMenu.add(exit);

        return fileMenu;
    }

    public static Table get() {
        return INSTANCE;
    }

    public void show() {
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }

    private Board getGameBoard() {
        return this.chessMatch.board;
    }

    private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    private Game getGameSetup() {
        return this.game;
    }

    private JMenu createPreferencesMenu() {
        final JMenu prefMenu = new JMenu("Preference");
        final JCheckBoxMenuItem highlightValidMovesCheckBox
                = new JCheckBoxMenuItem("Zvýraznout platné pohyby", false);

        highlightValidMovesCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightValidMoves = highlightValidMovesCheckBox.isSelected();
            }
        });

        prefMenu.add(highlightValidMovesCheckBox);
        return prefMenu;
    }

    private JMenu createOptionMenu() {
        final JMenu optionMenu = new JMenu("Nastávení");

        final JMenuItem setupGameMenuItem = new JMenuItem("Hrá");
        setupGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });

        optionMenu.add(setupGameMenuItem);

        return optionMenu;
    }

    private void setupUpdate(Game game) {
        setChanged();
        notifyObservers(game);
    }

    public void updateGameBoard(Board board) {
        this.chessMatch.board = board;
    }








    private class BoardPanel extends JPanel {

        final List<SquarePanel> boardSquares;

        public BoardPanel() {
            super(new GridLayout(8, 8));
            this.boardSquares = new ArrayList<>();
            for (int i = 0; i < 64; i++) {
                final SquarePanel squarePanel = new SquarePanel(this, new Position(i,i));
                this.boardSquares.add(squarePanel);
                add(squarePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setBackground(Color.decode("#8B4726"));
            validate();
        }

        private void drawBoard(Board board) {
            removeAll();
            for (SquarePanel squarePanel : boardSquares) {
                squarePanel.drawSquare(board);
                add(squarePanel);
            }
            validate();
            repaint();
        }
    }

    private class SquarePanel extends JPanel {

        private final Position position;
        private Piece King;

        public SquarePanel(final BoardPanel boardPanel, final Position position) {
            super(new GridBagLayout());
            this.position = position;
            setPreferredSize(SQUARE_PANEL_DIMENSION);
            assignSquareColour();

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        sourcePosition = null;
                        targetPosition = null;
                        humanMovedPiece = null;
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        if (sourcePosition == null) {
                            // first click
                            sourcePosition = UI.getChessPosition(position);
                            humanMovedPiece = chessMatch.board.piece(position);
                            if (humanMovedPiece == null) {
                                sourcePosition = null;
                            }
                        } else {
                            // second click
                            targetPosition = UI.getChessPosition(position);
                            chessMatch.performChessMove(sourcePosition,targetPosition);

                            if (true) {
                                updateGameBoard(chessMatch.board);
                                System.out.println("Making new move!");
                                System.out.println(chessMatch);
                            }
                            sourcePosition = null;
                            targetPosition = null;
                            humanMovedPiece = null;
                        }

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                boardPanel.drawBoard(chessMatch.board);
                                if (chessMatch.getCheckMate()) {
                                    JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                                            "Game Over: " + Table.get().chessMatch.getCurrentPlayer()
                                            + " Player is in checkmate!", "Game Over",
                                            JOptionPane.INFORMATION_MESSAGE);
                                    System.out.println("Game Over!" + Table.get().chessMatch.getCurrentPlayer()
                                            + " is in Checkmate!");
                                }

                                if (chessMatch.getCheck()) {
                                    JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                                            "Game Over: "
                                            + " Player is in stalemate!", "Game Over",
                                            JOptionPane.INFORMATION_MESSAGE);
                                    System.out.println("GameOver!"
                                            + " is in Stalemate!");
                                }
                            }
                        });
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });
            validate();
        }

        public void drawSquare(final Board board) {
            assignSquareColour();

            highlightSourceSquare();
            highlightCheck();
            validate();
        }


        private void highlightCheck() {
            if (Table.get().chessMatch.getCheck()) {
                if (this.position == position) {
                    setBackground(Color.RED);
                }
            }
        }

        private void highlightSourceSquare() {
            if (sourcePosition != null && this.position == sourcePosition.toPosition()) {
                setBackground(Color.YELLOW);
            }
        }

        private void assignSquareColour() {
            final int squareX = this.position.getRow();
            final int squareY = this.position.getColumn() ;
            setBackground((squareX + squareY) % 2 == 0 ? Color.decode("#FFFACD") : Color.decode("#593324"));
        }
    }

}
