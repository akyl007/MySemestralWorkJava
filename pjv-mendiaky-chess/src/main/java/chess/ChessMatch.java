package chess;

import boardgame.Pieces;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class ChessMatch {
    
    private int[][] board;
    private int numTurns;
    private boolean white;
    private boolean gameOver;
    private boolean mode; //True pokud je kus již vybrán, a false, pokud není vybrán
    private List<Move> selection;
    private LinkedList<Move> moveHistory;
    private LinkedList<Move> redoMoves;
    
    public ChessMatch() {
        reset();
    }

    //kopírovací konstruktor pro účely ladění / testování
    public ChessMatch(ChessMatch c) {
        this.board = c.getBoard();
        this.numTurns = c.getNumTurns();
        this.white = c.isWhite();
        this.gameOver = c.isGameOver();
        this.mode = c.getMode();
        this.selection = c.getSelection();

        this.moveHistory = c.moveHistory;
        this.redoMoves = c.redoMoves;
    }
    public ChessMatch clone(ChessMatch c) {
        ChessMatch newC = new ChessMatch();
        newC.board = c.getBoard();
        newC.numTurns = c.getNumTurns();
        newC.white = c.isWhite();
        newC.gameOver = c.isGameOver();
        newC.mode = c.getMode();
        newC.selection = c.getSelection();
        newC.moveHistory = c.getMoveHistory();
        newC.redoMoves = c.getRedoMoves();
        return newC;
    }
    
    private boolean inBounds(int c, int r) {
        boolean col = (c < 8) && (c >= 0);
        boolean row = (r < 8) && (r >= 0);
        return col && row;
    }
    
    interface getMovesInterface {
        boolean run(int c, int r, PieceType p);
    }
    
    public List<Move> getMoves(int c, int r) {
        int piece = getCell(c, r);
        List<Move> moves = new LinkedList<Move>();
        PieceType p;

        // metoda pro každý kousek kromě pěšce, která se zbaví opakování
        // Tato metoda je určena k použití ve smyčce, která iteruje možnými pohyby.
        getMovesInterface checkAddCell = (int cMove, int rMove, PieceType pMove) -> 
        {
            if (inBounds(cMove, rMove) && getCell(cMove, rMove) == 0) {
                Move m = new Move(c, r, cMove, rMove, pMove, -1, -1, PieceType.EMPTY, false);
                moves.add(m);
            } else {
                //zkontroluje, zda je jiný hráč
                if ( inBounds(cMove, rMove) && (pMove.isBlack()  != PieceType.isBlack(getCell(cMove, rMove))) ) {
                    Move m = new Move(c, r, cMove, rMove, pMove, cMove, rMove, PieceType.valueOf(getCell(cMove, rMove)), false );
                    moves.add(m);
                }
                return true;
            }
            return false;
        };
        switch(piece) {
        //PAWN
        case 1:
            p = PieceType.PAWN_W;
            //pohyby vpřed
            // zkontrolovat, zda v mezeře již nějaký kousek není
            if (inBounds(c, r+1) && getCell(c, r+1) == 0) {
                Move m1 = new Move(c, r, c, r+1, p, -1, -1, PieceType.EMPTY, false);
                moves.add(m1);
            }
            
            if (r == 1) {
                if (inBounds(c, r+2) && getCell(c, r+2) == 0) {
                    Move m2 = new Move(c, r, c, r+2, p, -1, -1, PieceType.EMPTY, false);
                    moves.add(m2);
                }
            }
            //Diagonální pohyby
            if (inBounds(c+1, r+1) && getCell(c+1, r+1) > 6) {
                Move m3 = new Move(c, r, c+1, r+1, p, c+1, r+1, PieceType.valueOf(getCell(c+1, r+1)), false );
                moves.add(m3);
            }
            
            if (inBounds(c-1, r+1) && getCell(c-1, r+1) > 6) {
                Move m3 = new Move(c, r, c-1, r+1, p, c-1, r+1, PieceType.valueOf(getCell(c-1, r+1)), false );
                moves.add(m3);
            }
            
            Move mW;
            if (!moveHistory.isEmpty()) {
                mW = moveHistory.getLast();
            } else {
                mW = new Move(-1, -1, -1, -1, PieceType.EMPTY, -1, -1, PieceType.EMPTY, false);
            }
            if (mW.getP() == PieceType.PAWN_B) {
                if (mW.getR1()-2 == mW.getR2()) {
                    if (r == mW.getR2() && c == mW.getC2()+1) {
                        if (inBounds(c-1, r+1) && getCell(c-1, r+1) == 0) {
                            Move m3 = new Move(c, r, c-1, r+1, p, c-1, r, PieceType.valueOf(getCell(c-1, r)), false );
                            moves.add(m3);
                        }
                    } else if (r == mW.getR2() && c == mW.getC2()-1) {
                        if (inBounds(c+1, r+1) && getCell(c+1, r+1) == 0) {
                            Move m3 = new Move(c, r, c+1, r+1, p, c+1, r, PieceType.valueOf(getCell(c+1, r)), false );
                            moves.add(m3);
                        }
                    }
                }
            }
            
            break;
            
        case 7:
            p = PieceType.PAWN_B;
            //pohyby vpřed
            //zkontroluje, zda v mezeře již nějaký kousek není
            if (inBounds(c, r-1) && getCell(c, r-1) == 0) {
                Move m1 = new Move(c, r, c, r-1, p, -1, -1, PieceType.EMPTY, false);
                moves.add(m1);
            }
            
            if (r == 6) {
                if (inBounds(c, r-2) && getCell(c, r-2) == 0) {
                    Move m2 = new Move(c, r, c, r-2, p, -1, -1, PieceType.EMPTY, false);
                    moves.add(m2);
                }
            }
            //Diagonal moves
            if (inBounds(c+1, r-1) && getCell(c+1, r-1) < 7 && getCell(c+1, r-1) != 0) {
                Move m3 = new Move(c, r, c+1, r-1, p, c+1, r-1, PieceType.valueOf(getCell(c+1, r-1)), false );
                moves.add(m3);
            }
            
            if (inBounds(c-1, r-1) && getCell(c-1, r-1) < 7 && getCell(c-1, r-1) != 0) {
                Move m3 = new Move(c, r, c-1, r-1, p, c-1, r-1, PieceType.valueOf(getCell(c-1, r-1)), false );
                moves.add(m3);
            }
            //en passant
            Move mB;
            if (!moveHistory.isEmpty()) {
                mB = moveHistory.getLast();
            } else {
                mB = new Move(-1, -1, -1, -1, PieceType.EMPTY, -1, -1, PieceType.EMPTY, false);
            }
            if (mB.getP() == PieceType.PAWN_W) {
                if (mB.getR1()+2 == mB.getR2()) {
                    if (r == mB.getR2() && c == mB.getC2()+1) {
                        if (inBounds(c-1, r-1) && getCell(c-1, r-1) == 0) {
                            Move m3 = new Move(c, r, c-1, r-1, p, c-1, r, PieceType.valueOf(getCell(c-1, r)), false );
                            moves.add(m3);
                        }
                    } else if (r == mB.getR2() && c == mB.getC2()-1) {
                        if (inBounds(c+1, r-1) && getCell(c+1, r-1) == 0) {
                            Move m3 = new Move(c, r, c+1, r-1, p, c+1, r, PieceType.valueOf(getCell(c+1, r)), false );
                            moves.add(m3);
                        }
                    }
                }
            }
            
            break;
            
        //ROOK
        case 2:
            p = PieceType.ROOK_W;
            //nahoru
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c, r-i, p)) {
                    break;
                }
            }
            //dolu
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c, r+i, p)) {
                    break;
                }
            }
            //vlevo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c-i, r, p)) {
                    break;
                }
            }
            //vpravo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c+i, r, p)) {
                    break;
                }
            }
            break;
        case 8:
            p = PieceType.ROOK_B;
            //nahoru
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c, r-i, p)) {
                    break;
                }
            }
            //dolu
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c, r+i, p)) {
                    break;
                }
            }
            //vlevo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c-i, r, p)) {
                    break;
                }
            }
            //vpravo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c+i, r, p)) {
                    break;
                }
            }
            break;
        //bishops
        case 3:
            p = PieceType.BISHOP_W;
            //nahoru vlevo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c-i, r-i, p)) {
                    break;
                }
            }
            //nahoru vpravo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c+i, r-i, p)) {
                    break;
                }
            }
            //dolu vlevo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c-i, r+i, p)) {
                    break;
                }
            }
            //dolu vpravo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c+i, r+i, p)) {
                    break;
                }
            }
            break;
        case 9:
            p = PieceType.BISHOP_B;
            //nahoru vlevo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c-i, r-i, p)) {
                    break;
                }
            }
            //nahoru vpravo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c+i, r-i, p)) {
                    break;
                }
            }
            //vlevo dolu
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c-i, r+i, p)) {
                    break;
                }
            }
            //vpravo dolu
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c+i, r+i, p)) {
                    break;
                }
            }
            break;
        //knights
        case 4:
            p = PieceType.KNIGHT_W;
            //nahoru vlevo
            checkAddCell.run(c-1, r-2, p);
            //nahoru vpravo
            checkAddCell.run(c+1, r-2, p);
            //dolu vlevo
            checkAddCell.run(c-1, r+2, p);
            //dolu vpravo
            checkAddCell.run(c+1, r+2, p);
            //vlevo nahoru
            checkAddCell.run(c-2, r-1, p);
            //vlevo dolu
            checkAddCell.run(c-2, r+1, p);
            //vpravo nahoru
            checkAddCell.run(c+2, r-1, p);
            //vpravo dolu
            checkAddCell.run(c+2, r+1, p);
            break;
        case 10:
            p = PieceType.KNIGHT_B;
            //nahoru vlevo
            checkAddCell.run(c-1, r-2, p);
            //nahoru vpravo
            checkAddCell.run(c+1, r-2, p);
            //dolu vlevo
            checkAddCell.run(c-1, r+2, p);
            //dolu vpravo
            checkAddCell.run(c+1, r+2, p);
            //vlevo nahoru
            checkAddCell.run(c-2, r-1, p);
            //vlevo dolu
            checkAddCell.run(c-2, r+1, p);
            //vpravo nahoru
            checkAddCell.run(c+2, r-1, p);
            //vpravo dolu
            checkAddCell.run(c+2, r+1, p);
            break;
        case 5:
            p = PieceType.QUEEN_W;
            //nahoru
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c, r-i, p)) {
                    break;
                }
            }
            //dolu
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c, r+i, p)) {
                    break;
                }
            }
            //vlevo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c-i, r, p)) {
                    break;
                }
            }
            //vpravo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c+i, r, p)) {
                    break;
                }
            }
            //nahoru vlevo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c-i, r-i, p)) {
                    break;
                }
            }
            //nahoru vpravo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c+i, r-i, p)) {
                    break;
                }
            }
            //dolu vlevo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c-i, r+i, p)) {
                    break;
                }
            }
            //dolu vpravo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c+i, r+i, p)) {
                    break;
                }
            }
            break;
        case 11:
            p = PieceType.QUEEN_B;
            //nahoru
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c, r-i, p)) {
                    break;
                }
            }
            //dolu
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c, r+i, p)) {
                    break;
                }
            }
            //vlevo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c-i, r, p)) {
                    break;
                }
            }
            //vpravo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c+i, r, p)) {
                    break;
                }
            }
            //nahoru vlevo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c-i, r-i, p)) {
                    break;
                }
            }
            //nahoru vpravo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c+i, r-i, p)) {
                    break;
                }
            }
            //dolu vlevo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c-i, r+i, p)) {
                    break;
                }
            }
            //dolu vpravo
            for (int i = 1; i < 8; i++) {
                if (checkAddCell.run(c+i, r+i, p)) {
                    break;
                }
            }
            break;
        case 6:
            p = PieceType.KING_W;
            //nahoru vlevo
            checkAddCell.run(c-1, r-1, p);
            //nahoru vpravo
            checkAddCell.run(c+1, r-1, p);
            //dolu vlevo
            checkAddCell.run(c-1, r+1, p);
            //dolu vpravo
            checkAddCell.run(c+1, r+1, p);
            //nahoru
            checkAddCell.run(c, r-1, p);
            //dolu
            checkAddCell.run(c, r+1, p);
            //vlevo
            checkAddCell.run(c-1, r, p);
            //vpravo
            checkAddCell.run(c+1, r, p);
            //rošáda
            if (check() == 0 || check() == 1) {
                //zkontroluje, zda se král nehnul a zda se pohnuly věže
                boolean king = false;
                boolean rookR = false;
                boolean rookL = false;
                for (Move m : moveHistory) {
                    if (m.getP() == p) {
                        king = true;
                    }
                    if (m.getC1() == 7 && m.getR1() == 0) {
                        rookR = true;
                    }
                    if (m.getC1() == 0 && m.getR1() == 0) {
                        rookL = true;
                    }
                    
                    if (!king) {
                        if (!rookR) {
                            boolean clear = true;
                            // zkontroluje, zda je volný prostor
                            for (int i = 1; i < 4; i++) {
                                if (inBounds(c+i, r) && board[r][c+i] != 0) {
                                    clear = false;
                                }
                            }
                            if (clear) {
                                // K uložení typu věže používáme data mrtvého kusu
                                // Můžeme to udělat, protože stejně nemůžete zabít kousek rošádováním
                                moves.add(new Move(c, r, c+2, r, p, c+4, r, PieceType.ROOK_W, true));
                            }
                        }
                        if (!rookL) {
                            boolean clear = true;
                            // zkontroluje, zda je volný prostor
                            for (int i = 1; i < 3; i++) {
                                if (inBounds(c-i, r) && board[r][c-i] != 0) {
                                    clear = false;
                                }
                            }
                            if (clear) {
                                moves.add(new Move(c, r, c-2, r, p, c-3, r, PieceType.ROOK_W, true));
                            }
                        }
                    }
                }
            }
            break;
        case 12:
            p = PieceType.KING_B;
            //nahoru vlevo
            checkAddCell.run(c-1, r-1, p);
            //nahoru vpravo
            checkAddCell.run(c+1, r-1, p);
            //dolu vlevo
            checkAddCell.run(c-1, r+1, p);
            //dolu vpravo
            checkAddCell.run(c+1, r+1, p);
            //nahoru
            checkAddCell.run(c, r-1, p);
            //dolu
            checkAddCell.run(c, r+1, p);
            //vlevo
            checkAddCell.run(c-1, r, p);
            //vpravo
            checkAddCell.run(c+1, r, p);
            //rošáda
            if (check() == 0 || check() == 2) {
                //zkontroluje, zda se král nehnul a zda se pohnuly věže
                boolean king = false;
                boolean rookR = false;
                boolean rookL = false;
                for (Move m : moveHistory) {
                    if (m.getP() == p) {
                        king = true;
                    }
                    if (m.getC1() == 7 && m.getR1() == 7) {
                        rookR = true;
                    }
                    if (m.getC1() == 0 && m.getR1() == 7) {
                        rookL = true;
                    }
                    
                    if (!king) {
                        if (!rookR) {
                            boolean clear = true;
                            // zkontroluje, zda je volný prostor
                            for (int i = 1; i < 4; i++) {
                                if (inBounds(c+i, r) && board[r][c+i] != 0) {
                                    clear = false;
                                }
                            }
                            if (clear) {
                                moves.add(new Move(c, r, c+2, r, p, c+4, r, PieceType.ROOK_B, true));
                            }
                        }
                        if (!rookL) {
                            boolean clear = true;
                            // zkontroluje, zda je volný prostor
                            for (int i = 1; i < 3; i++) {
                                if (inBounds(c-i, r) && board[r][c-i] != 0) {
                                    clear = false;
                                }
                            }
                            if (clear) {
                                moves.add(new Move(c, r, c-2, r, p, c-3, r, PieceType.ROOK_B, true));
                            }
                        }
                    }
                }
            }
            break;
        }
        
        
        return moves;
    }
    
    private List<Move> removeCheckMoves(List<Move> l) {
        List<Move> newL = new LinkedList<Move>(l);
        for (Move m : l) {
            ChessMatch clone;
            if (board != null) {
                clone = clone(this);
            } else {
                throw new NullPointerException();
            }
            clone.executeMove(m);
            if (white && (clone.check() == 2 || clone.check() == 3)) {
                newL.remove(m);
            }
            if (!white && (clone.check() == 1 || clone.check() == 3)) {
                newL.remove(m);
            }
        }
        return newL;
    }
    
    
    public boolean executeMove(Move m) {
        if (!m.getCastle()) {
            if (m.getDead() != PieceType.EMPTY) {
                board[m.getR3()][m.getC3()] = 0;
            }
            board[m.getR2()][m.getC2()] = m.getP().getPeice();
            board[m.getR1()][m.getC1()] = 0;
            moveHistory.add(m);
            white = !white;
            mode = false;
            numTurns++;
            return true;
        } else {
            board[m.getR2()][m.getC2()] = m.getP().getPeice();
            board[m.getR1()][m.getC1()] = 0;
            if (m.getC1() < m.getC2()) {
                board[m.getR2()][m.getC2()-1] = m.getDead().getPeice();
            } else {
                board[m.getR2()][m.getC2()+1] = m.getDead().getPeice();
            }
            board[m.getR3()][m.getC3()] = 0;
            moveHistory.add(m);
            white = !white;
            mode = false;
            numTurns++;
            return true;
        }
    }
    
    public boolean playTurn(int c, int r) {
        PieceType p = PieceType.valueOf(board[r][c]);
        // Použitelné bez ohledu na to, zda je proveden výběr:
        if (gameOver) {
            return false;
        }
        
        if ((white && p.isWhite()) || (!white && p.isBlack())) {
            selection = getMoves(c, r);
            selection = removeCheckMoves(selection);
            mode = true;
            return true;
        }

        if (!mode) {
        }
        //vyber:
        else {
            for (Move m : selection) {
                if (c == m.getC2() && r == m.getR2()) {
                    executeMove(m);
                    redoMoves.clear();
                    return true;
                }
            }
            mode = false;
            return false;
        }
        
        return false;
    }
    
    //0: pokráčení hry
    //1: bilý vyhravá
    //2: černý vyhravá
    //3: stalemate
    public int checkWinner() {
        boolean wMoves = false;
        boolean bMoves = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                PieceType p = PieceType.valueOf(board[i][j]);
                System.out.println(removeCheckMoves(getMoves(j, i)).isEmpty());
                if (p != PieceType.EMPTY && !removeCheckMoves(getMoves(j, i)).isEmpty()) {

                    if (Pieces.isWhite(p)) {
                        System.out.println(p);
                        wMoves = true;
                    } else {
                        bMoves = true;
                    }
                }
            }
        }
        System.out.println(wMoves);
        if (!wMoves && white) {
            if (check() == 2) {
                return 2;
            } else {
                return 3;
            }
        } else if (!bMoves && !white) {
            if (check() == 1) {
                return 1;
            } else {
                return 3;
            }
        } else {
            return 0;
        }
    }
    
    //0: žádný šah
    //1: černý v šahu
    //2: bilý v šahu
    //3: oba jsou v šahu
    public int check() {
        boolean w = false;
        boolean b = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != 0 && board[i][j] != 6 && board[i][j] != 12) {
                    List<Move> moves = getMoves(j, i);
                    for (Move m : moves) {
                        if (m.getDead() == PieceType.KING_B) {
                            w = true;
                        } else if (m.getDead() == PieceType.KING_W) {
                            b = true;
                        }
                    }
                }
            }
        }
        
        
        if (w && b) {
            return 3;
        } else if (w && !b) {
            return 1;
        } else if (b && !w) {
            return 2;
        } else {
            return 0;
        }
    }
    
    public boolean undo() {
        if (moveHistory.isEmpty()) {
            return false;
        }else {
            Move m = moveHistory.removeLast();
            redoMoves.add(m);
            if (m.getCastle()) {
                board[m.getR1()][m.getC1()] = m.getP().getPeice();
                board[m.getR2()][m.getC2()] = 0;
                if (m.getC1() < m.getC2()) {
                    board[m.getR2()][m.getC2()-1] = 0;
                    board[m.getR3()][m.getC3()] = m.getDead().getPeice();
                } else {
                    board[m.getR2()][m.getC2()+1] = 0;
                    board[m.getR3()][m.getC3()] = m.getDead().getPeice();
                }
                return true;
            } else {
                board[m.getR1()][m.getC1()] = m.getP().getPeice();
                board[m.getR2()][m.getC2()] = 0;
                if (m.getDead() != PieceType.EMPTY) {
                    board[m.getR3()][m.getC3()] = m.getDead().getPeice();
                }
                white = !white;
                mode = false;
                numTurns--;
                return true;
            }
        }
    }
    
    public boolean redo() {
        if (redoMoves.isEmpty()) {
            return false;
        } else {
            Move m = redoMoves.removeLast();
            moveHistory.add(m);
            executeMove(m);
            return true;
        }
    }
    
    public void reset() {
        board = new int[8][8];
        moveHistory = new LinkedList<Move>();
        redoMoves = new LinkedList<Move>();
        selection = new LinkedList<Move>();
        //pawn
        for (int i = 0; i < 8; i++) {
            board[1][i] = 1;
            board[6][i] = 7;
        }
        //rook
        board[0][0]=2;
        board[0][7]=2;
        board[7][0]=8;
        board[7][7]=8;
        //bishop
        board[0][2]=3;
        board[0][5]=3;
        board[7][2]=9;
        board[7][5]=9;
        //knight
        board[0][1]=4;
        board[0][6]=4;
        board[7][1]=10;
        board[7][6]=10;
        //queen
        board[0][4]=5;
        board[7][4]=11;
        //king
        board[0][3]=6;
        board[7][3]=12;
        
        
        numTurns = 0;
        mode = false;
        white = true;
        gameOver = false;
    }
    
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(board);
        result = prime * result + (gameOver ? 1231 : 1237);
        result = prime * result + (mode ? 1231 : 1237);
        result = prime * result + ((moveHistory == null) ? 0 : moveHistory.hashCode());
        result = prime * result + numTurns;
        result = prime * result + ((redoMoves == null) ? 0 : redoMoves.hashCode());
        result = prime * result + ((selection == null) ? 0 : selection.hashCode());
        result = prime * result + (white ? 1231 : 1237);
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChessMatch other = (ChessMatch) obj;
        if (!Arrays.deepEquals(board, other.board))
            return false;
        if (gameOver != other.gameOver)
            return false;
        if (mode != other.mode)
            return false;
        if (moveHistory == null) {
            if (other.moveHistory != null)
                return false;
        } else if (!moveHistory.equals(other.moveHistory)) {
            System.out.println("FALSE");
            System.out.println(moveHistory.toString());
            System.out.println(other.moveHistory.toString());
            return false;
        }
        if (numTurns != other.numTurns)
            return false;
        if (redoMoves == null) {
            if (other.redoMoves != null)
                return false;
        } else if (!redoMoves.equals(other.redoMoves))
            return false;
        if (selection == null) {
            if (other.selection != null)
                return false;
        } else if (!selection.equals(other.selection))
            return false;
        if (white != other.white)
            return false;
        return true;
    }

    //gettery a settery
    public boolean getCurrentPlayer() {
        return new Boolean(white);
    }
    
    public void swapCurrentPlayer() {
        white = !white;
    }
    
    public boolean getMode() {
        return new Boolean(mode);
    }
    
    public List<Move> getSelection() {
        return new LinkedList<Move>(selection);
    }
    
    public int getCell(int c, int r) {
        return board[r][c];
    }
    
    public int[][] getBoard() {
        if (board == null) {
            board = new int[8][8];
        }
        int[][] b = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                b[i][j] = new Integer(board[i][j]);
            }
        }
        return b;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public int getNumTurns() {
        return new Integer(numTurns);
    }

    public void setNumTurns(int numTurns) {
        this.numTurns = numTurns;
    }

    public boolean isWhite() {
        return new Boolean(white);
    }

    public void setWhite(boolean white) {
        this.white = white;
    }

    public boolean isGameOver() {
        return new Boolean(gameOver);
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public LinkedList<Move> getMoveHistory() {
        return new LinkedList<Move>(moveHistory);
    }

    public void setMoveHistory(LinkedList<Move> moveHistory) {
        this.moveHistory = moveHistory;
    }

    public LinkedList<Move> getRedoMoves() {
        return new LinkedList<Move>(redoMoves);
    }

    public void setRedoMoves(LinkedList<Move> redoMoves) {
        this.redoMoves = redoMoves;
    }

    public void setMode(boolean mode) {
        this.mode = mode;
    }

    public void setSelection(List<Move> selection) {
        this.selection = selection;
    }
    
}
