package application;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.pieces.FigureColors;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gui.Table;

public class Program {
    private static final Logger logger = Logger.getGlobal();
    public static void main(String[] args) {

        final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

        Scanner sc = new Scanner(System.in);
        ChessMatch chessMatch = new ChessMatch();
        List<ChessPiece> captured = new ArrayList<>();
        logger.info("Match started!");
        while (!chessMatch.getCheckMate()) {
            try {
                UI.whitePlayerfiguresColor = FigureColors.ANSI_BLUE;
                UI.blackPlayerfiguresColor = FigureColors.ANSI_GREEN;
                Table.get().show();
                UI.clearScreen();
                UI.printMatch(chessMatch, captured);
                System.out.println();
                System.out.print("Source: ");
                ChessPosition source = UI.readChessPosition(sc);
                logger.config("Getting source position successful: " + source.getColumn() + source.getRow());


                boolean[][] possibleMoves = chessMatch.possibleMoves(source);
                UI.clearScreen();
                UI.printBoard(chessMatch.getPieces(), possibleMoves);
                System.out.println();
                System.out.print("Target: ");
                ChessPosition target = UI.readChessPosition(sc);
                logger.config("Getting source position successful: " + target.getColumn() + target.getRow());

                ChessPiece capturedPiece = chessMatch.performChessMove(source, target);


                if (capturedPiece != null) {
                    captured.add(capturedPiece);
                }

                if (chessMatch.getPromoted() != null) {
                    System.out.print("Enter piece for promotion (B/N/R/Q): ");
                    String type = sc.nextLine().toUpperCase();
                    while (!type.equals("B") && !type.equals("N") && !type.equals("R") & !type.equals("Q")) {
                        System.out.print("Invalid value! Enter piece for promotion (B/N/R/Q): ");
                        type = sc.nextLine().toUpperCase();
                    }
                    logger.info("Improving pawn to: " + type);
                    chessMatch.replacePromotedPiece(type);
                    logger.info("Improving success!");
                }
            }
            catch (ChessException e) {
                System.out.println(e.getMessage());
                sc.nextLine();
            }
            catch (InputMismatchException e) {
                System.out.println(e.getMessage());
                sc.nextLine();
            }
        }
        UI.clearScreen();
        UI.printMatch(chessMatch, captured);
    }

}
