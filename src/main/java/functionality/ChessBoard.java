package functionality;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * Class representing the board in chess. The board is responsible for knowing the positions
 * of all pieces present on the board.
 */
public class ChessBoard implements Iterable<ChessPiece> {
    private static final int LENGTH = 8;

    // board[0] is top-left corner (black side). board[7] is top-right corner (black side).
    // board[56] is bottom-left corner (white side). board[63] is bottom-right corner (white side).
    private final ChessPiece[] board = new ChessPiece[LENGTH * LENGTH];

    /**
     * Constructs a ChessBoard with an empty board (i.e. no pieces on the board).
     */
    public ChessBoard() { }

    /**
     * Constructs a ChessBoard from a "boardFile" which contains 8 rows of 8 space-separated
     * strings representing pieces on a board. The ChessPiece.generatePieceFrom method parses
     * these strings into either their corresponding pieces or null if the string is not a
     * valid piece id (as defined by the method).
     * @param boardFile boardFile to parse
     */
    public ChessBoard(String boardFile) {
        try {
            File file = new File(boardFile);
            Scanner fileScan = new Scanner(file);
            int i = 0;
            while (fileScan.hasNext()) {
                board[i] = ChessPiece.generatePieceFrom(fileScan.next());
                i += 1;
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Returns a String representation of the state of the board.
     *
     * Returns 8 lines with 8 tokens on each line where each token
     * is either "--" to indicate an empty square or the corresponding 2-character
     * representation of the chess piece that occupies that space.
     *
     * @return a String representing current state of the board.
     */
    public String getBoardState() {
        String state = "";
        for (int i = 0; i < board.length; i++) {
            ChessPiece piece = board[i];
            if (piece == null || piece.getColor() == null) {
                state += "--";
            } else {
                state += piece.getShortName();
                state += piece.getColor() == TeamColor.BLACK ? "b" : "w";
            }
            if (i % 8 == 7) {
                if (i != 63) {
                    state += "\n";
                }
            } else {
                state += " ";
            }
        }
        return state;
    }

    /**
     * Returns iterator for the ChessBoard.
     * @return Iterator over this ChessBoard.
     */
    @Override
    public Iterator<ChessPiece> iterator() {
        return new Iterator<>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < board.length;
            }

            @Override
            public ChessPiece next() {
                return board[i++];
            }
        };
    }

    /**
     * Moves piece at the source coordinate to the destination coordinate.
     * @param src source coordinate
     * @param dest destination coordinate.
     */
    public void movePiece(BoardCoordinate src, BoardCoordinate dest) {
        ChessPiece srcPiece = getPieceAt(src);

        clearSquare(src);
        placePiece(srcPiece, dest);
    }

    /**
     * Places a chess piece on the board
     * @param p piece to place on the board
     * @param coord where to place the piece on the board
     */
    public void placePiece(ChessPiece p, BoardCoordinate coord) {
        board[coord.toIndex()] = p;
    }

    /**
     * Clears the square at the provided coordinate.
     * @param coord square to clear.
     */
    public void clearSquare(BoardCoordinate coord) {
        board[coord.toIndex()] = null;
    }

    /**
     * Returns whether or not the piece at the given coordinate is a pawn at its eighth rank.
     * @param coord coordinate of piece to check
     * @return true if piece at coord is a pawn at its eighth rank and false otherwise.
     */
    public boolean isEighthRankPawn(BoardCoordinate coord) {
        ChessPiece p = getPieceAt(coord);
        if (p instanceof Pawn) {
            if (p.getColor() == TeamColor.BLACK) {
                return coord.r == 7;
            } else {
                return coord.r == 0;
            }
        }
        return false;
    }

    /**
     * Returns whether the path between two coordinates is clear, meaning that a piece
     * at the start coordinate could theoretically move to the end coordinate.
     * @param c1 start coordinate
     * @param c2 end coordinate
     * @return true if the path between c1 and c2 is clear and false otherwise.
     */
    public boolean isPathClear(BoardCoordinate c1, BoardCoordinate c2) {
        List<BoardCoordinate> pathCoordinates = c1.getPathCoordinates(c2);
        // Iterate over the path coordinates, excluding the first since the piece itself
        // is not an obstruction.
        for (int i = 1; i < pathCoordinates.size(); i++) {
            if (board[pathCoordinates.get(i).toIndex()] != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the coordinates of the king piece for a given team.
     * @param team team of the king to return the coordinates of
     * @throws RuntimeException if the king for this team cannot be found on the board
     * @return BoardCoordinate of the king piece for the given team.
     */
    public BoardCoordinate getKingCoord(TeamColor team) {
        BoardCoordinate kingCoord = null;
        for (int i = 0; i < board.length; i++) {
            ChessPiece p = board[i];
            if (p != null && p.getClass() == King.class && p.getColor() == team) {
                kingCoord = new BoardCoordinate(i);
            }
        }
        if (kingCoord == null) {
            throw new RuntimeException("King cannot be located");
        }
        return kingCoord;
    }

    /**
     * Returns the piece at the provided board coordinate and null if there is no piece at that coordinate.
     * @param coord Board coordinate of piece to return. coord != null
     * @return functionality.ChessPiece at provided coordinate or null if no piece exists at the coordinate.
     */
    public ChessPiece getPieceAt(BoardCoordinate coord) {
        return board[coord.toIndex()];
    }

    /**
     * Returns whether the provided BoardCoordinate is within the bounds of the board.
     * @param coord queried coordinate
     * @return true if coord is within bounds of board and false otherwise.
     */
    public static boolean isOnBoard(BoardCoordinate coord) {
        return coord.r >= 0 && coord.r < LENGTH && coord.c >= 0 && coord.c < LENGTH;
    }

    /**
     * Return a copy of this ChessBoard with deep copies of all of the pieces.
     * @return ChessBoard copy
     */
    public ChessBoard copy() {
        ChessBoard boardCopy = new ChessBoard();
        for (int i = 0; i < boardCopy.board.length; i++) {
            boardCopy.board[i] = board[i] == null ? null : board[i].copy();
        }
        return boardCopy;
    }
}