package boardgame;

import application.UI;
import chess.ChessMatch;
import chess.ChessPosition;
import static org.junit.Assert.*;

import boardgame.BoardException;
import boardgame.Position;
import org.junit.Test;

public class BoardTest {
    @Test
    public void removePieceCheck(){
        /**
         * Kontrola odstranění figurek z desky
         */
        ChessMatch cm = new ChessMatch();
        ChessPosition chessPosition = UI.readChessPositionString("b2");
        Position pos = chessPosition.toPosition();
        cm.board.removePiece(pos);
        UI.printBoard(cm.getPieces());
        assertFalse(cm.board.thereIsAPiece(pos));
    }

    @Test
    public void positionExistsTest(){
        /**
         * Kontrola funkce position exists
         */
        ChessMatch cm = new ChessMatch();
        ChessPosition chessPosition = UI.readChessPositionString("a2");
        Position pos = chessPosition.toPosition();
        assertTrue(cm.board.positionExists(pos));

    }
}
