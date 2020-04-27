package functionality;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Class responsible for the mechanics of operating a game of chess including validating
 * attempted moves by players and determining game outcomes.
 */
public class ChessGame implements Iterable<ChessPiece> {
    private TeamColor turn;
    private ChessBoard board;
    private BoardCoordinate pawnPromotionCoord;
    private BoardCoordinate twoSpaceMovedPawn;
    private ChessPiece lastRemoved;
    private Move lastMove;
    private boolean isCheck = false;
    private boolean isOver = false;

    /**
     * Creates a new chess game with the standard chess board and with white being the first
     * team to play, as per traditional rules.
     */
    public ChessGame() {
        this("src/main/resources/standardLayout.txt", TeamColor.WHITE);
    }

    /**
     * Creates a new chess game with the starting board configuration set by the file at the provided
     * path and with white playing first.
     * @param boardFile path to the board configuration file
     */
    public ChessGame(String boardFile) {
        this(boardFile, TeamColor.WHITE);
    }

    /**
     * Creates a new chess game with the starting board configuration set by the file at the provided
     * path and with the given team starting first.
     * @param boardFile path to the board configuration file
     * @param turn team to start first
     */
    public ChessGame(String boardFile, TeamColor turn) {
        this.board = new ChessBoard(boardFile);
        this.turn = turn;
    }

    /**
     * Returns true if the provided chess game is in checkmate.
     * @param game chess game to check
     * @return true if the game is in checkmate, and false otherwise.
     */
    public static boolean verifyGameCheckmate(ChessGame game) {
        return game.postMoveOutcome() == MoveOutcome.CHECKMATE;
    }

    /**
     * Constructs a new ChessGame with the given board and specified team that just moved in this
     * hypothetical scenario and returns whether the game is now in checkmate.
     * @param boardFile file path to board state
     * @param toMove team to move in this scenario
     * @return true if game is now in checkmate and false otherwise.
     */
    public static boolean verifyGameCheckmate(String boardFile, TeamColor toMove) {
        ChessGame game = new ChessGame(boardFile, toMove);
        return game.postMoveOutcome() == MoveOutcome.CHECKMATE;
    }

    /**
     * Returns true if the provided chess game is in stalemate.
     * @param game chess game to check
     * @return true if the game is in stalemate, and false otherwise.
     */
    public static boolean verifyGameStalemate(ChessGame game) {
        return game.postMoveOutcome() == MoveOutcome.STALEMATE;
    }

    /**
     * Constructs a new ChessGame with the given board and specified team that just moved in this
     * hypothetical scenario and returns whether the game is now in stalemate.
     * @param boardFile file path to board state
     * @param toMove team to move in this scenario
     * @return true if game is now in stalemate and false otherwise.
     */
    public static boolean verifyGameStalemate(String boardFile, TeamColor toMove) {
        ChessGame game = new ChessGame(boardFile, toMove);
        return game.postMoveOutcome() == MoveOutcome.STALEMATE;
    }

    /**
     * Returns a string representation of the current state of the chess board.
     * @return String containing the current board state.
     */
    public String getBoardState() {
        return board.getBoardState();
    }

    /**
     * Return all moves possible for the current turn.
     * @return a set of all possible moves.
     */
    public Set<Move> getPossibleMoves() {
        Set<Move> moves = new HashSet<>();
        int i = 0;
        for (ChessPiece p : board) {
            if (p != null && p.getColor() == turn) {
                BoardCoordinate src = new BoardCoordinate(i);
                for (int j = 0; j < 64; j++) {
                    BoardCoordinate dest = new BoardCoordinate(j);
                    if (isValidMove(src, dest)) {
                        moves.add(new Move(src, dest));
                    }
                }
            }
            i += 1;
        }
        return moves;
    }

    /**
     * Attempts to move a piece from one coordinate to another.
     * @param src source coordinate
     * @param dest destination coordinate
     * @throws RuntimeException if 'promotePawn' was not called between the previous call to 'attemptMove'
     *         that returned MoveOutcome.PAWN_PROMOTION and this call to 'attemptMove'.
     * @return MoveOutcome.FAILURE if the attempted move was invalid,
     *         MoveOutcome.CHECK if the move caused check but not checkmate,
     *         MoveOutcome.CHECKMATE if the move caused checkmate,
     *         MoveOutcome.STALEMATE if the move caused stalemate,
     *         MoveOutcome.INSUF_MAT_DRAW if the move caused a draw due to insufficient material,
     *         MoveOutcome.PAWN_PROMOTION if the move caused a pawn to get promoted which means
     *             that 'promotePawn' needs to be immediately called with the chosen piece to
     *             promote the pawn to,
     *         and MoveOutcome.SUCCESS for an otherwise successful move.
     */
    public MoveOutcome attemptMove(BoardCoordinate src, BoardCoordinate dest) {
        if (pawnPromotionCoord != null) {
            throw new RuntimeException("Pawn needs to be promoted before further moves can be made.");
        }

        // If the move is not valid, return failure.
        if (!isValidMove(src, dest)) {
            return MoveOutcome.FAILURE;
        }

        ChessPiece srcPiece = board.getPieceAt(src);
        performMove(src, dest);
        lastMove = new Move(src, dest);

        // If a pawn was moved that needs to be promoted, indicate this.
        // The player will have to indicate their decision before looking for checkmate
        // and stalemate and such.
        if (board.isEighthRankPawn(dest)) {
            pawnPromotionCoord = dest;
            return MoveOutcome.PAWN_PROMOTION;
        }

        // Keep track of any pawns that move two spaces in one turn so we know whether
        // an en passant attack can occur.
        if (srcPiece instanceof Pawn && !srcPiece.getHasBeenMoved() && Math.abs(dest.r - src.r) == 2) {
            twoSpaceMovedPawn = dest;
        } else {
            twoSpaceMovedPawn = null;
        }

        changeTurn();

        if (!srcPiece.getHasBeenMoved()) {
            srcPiece.setHasBeenMoved(true);
        }

        return postMoveOutcome();
    }

    /**
     * Method to be called immediately after receiving an outcome equal to MoveOutcome.PAWN_PROMOTION
     * by 'attemptMove' to indicate the piece that the respective pawn should be promoted to.
     * @param pieceChoice character representing the piece to promote the pawn to with 'q' == queen,
     *                    'r' == rook, 'n' == knight, 'b' == bishop.
     * @throws IllegalArgumentException if piece choice is not one of the following: 'q', 'r', 'n', or 'b'
     * @return MoveOutcome.CHECK if the move caused check but not checkmate,
     *         MoveOutcome.CHECKMATE if the move caused checkmate,
     *         MoveOutcome.STALEMATE if the move caused stalemate,
     *         MoveOutcome.INSUF_MAT_DRAW if the move caused a draw due to insufficient material,
     *         MoveOutcome.SUCCESS for an otherwise successful move.
     */
    public MoveOutcome promotePawn(char pieceChoice) {
        if (pawnPromotionCoord != null) {
            if (pieceChoice != 'q' && pieceChoice != 'r' && pieceChoice != 'n' && pieceChoice != 'b') {
                throw new IllegalArgumentException("Invalid piece selection.");
            }
            ChessPiece p;
            switch (pieceChoice) {
                case 'q':
                    p = new Queen(turn);
                    break;
                case 'r':
                    p = new Rook(turn);
                    break;
                case 'n':
                    p = new Knight(turn);
                    break;
                case 'b':
                    p = new Bishop(turn);
                    break;
                default:
                    p = null;
            }
            board.placePiece(p, pawnPromotionCoord);
            pawnPromotionCoord = null;

            changeTurn();

            lastMove = new Move(lastMove.getFirst(), lastMove.getSecond(), pieceChoice);

            return postMoveOutcome();
        }
        return MoveOutcome.FAILURE;
    }

    // Returns the outcome of a given successful move.
    private MoveOutcome postMoveOutcome() {
        MoveOutcome outcome = MoveOutcome.SUCCESS;
        if (isCheck()) {
            isCheck = true;
            if (isTeamStuck(turn)) {
                outcome = MoveOutcome.CHECKMATE;
                isOver = true;
            } else {
                outcome = MoveOutcome.CHECK;
            }
        } else {
            isCheck = false;
            if (isTeamStuck(turn)) {
                outcome = MoveOutcome.STALEMATE;
                isOver = true;
            } else if (isInsufficientMaterialDraw()) {
                outcome = MoveOutcome.INSUF_MAT_DRAW;
                isOver = true;
            }
        }
        return outcome;
    }

    // Move a piece from src to dest accounting for various chess edge-cases.
    private void performMove(BoardCoordinate src, BoardCoordinate dest) {
        ChessPiece srcPiece = board.getPieceAt(src);
        ChessPiece destPiece = board.getPieceAt(dest);
        BoardCoordinate relative = dest.subtract(src);

        // If this was a valid en passant attack, remove the fleeing pawn.
        if (srcPiece instanceof Pawn && destPiece == null && relative.c != 0) {
            lastRemoved = board.getPieceAt(twoSpaceMovedPawn);
            board.clearSquare(twoSpaceMovedPawn);
        } else if (srcPiece instanceof King && Math.abs(relative.c) == 2) {  // Castling.
            BoardCoordinate rookCoord;
            if (relative.c < 0) {
                rookCoord = new BoardCoordinate(src.r, src.c - 4);
            } else {
                rookCoord = new BoardCoordinate(src.r, src.c + 3);
            }
            board.movePiece(rookCoord, new BoardCoordinate(src.r, src.c + relative.c / 2));
        } else {
            lastRemoved = destPiece;
        }

        board.movePiece(src, dest);
    }

    // Undo the previous move accounting for various chess edge-cases.
    private void undoMove(BoardCoordinate src, BoardCoordinate dest) {
        ChessPiece destPiece = board.getPieceAt(dest);
        BoardCoordinate relative = dest.subtract(src);

        BoardCoordinate adj = src.add(new BoardCoordinate(0, relative.c));
        // If an en passant move occurred.
        if (destPiece instanceof Pawn && adj.equals(twoSpaceMovedPawn)) {
            board.clearSquare(dest);
            board.placePiece(lastRemoved, adj);
        } else if (destPiece instanceof King && Math.abs(relative.c) == 2) {  // Castling.
            board.clearSquare(dest);
            BoardCoordinate rookCoord;
            if (relative.c < 0) {
                rookCoord = new BoardCoordinate(src.r, src.c - 4);
            } else {
                rookCoord = new BoardCoordinate(src.r, src.c + 3);
            }
            board.movePiece(new BoardCoordinate(src.r, src.c + relative.c / 2), rookCoord);
        } else {
            board.placePiece(lastRemoved, dest);
        }

        board.placePiece(destPiece, src);
    }

    // Returns true iff moving the piece at src to dest is a valid chess move.
    private boolean isValidMove(BoardCoordinate src, BoardCoordinate dest) {
        ChessPiece srcPiece = board.getPieceAt(src);
        ChessPiece destPiece = board.getPieceAt(dest);

        // If the player tries to move the opponent's piece.
        if (srcPiece.getColor() != turn) {
            return false;
        }

        // If the piece is being moved to the position of another piece on its team, this is not a valid move.
        if (destPiece != null && destPiece.getColor() == srcPiece.getColor()) {
            return false;
        }

        // In any situation in which a King is the destination piece, this is an invalid move
        // and prevents capture of King.
        if (destPiece instanceof King) {
            return false;
        }

        // If the dest coordinate is not in the set of valid moves for this piece.
        if (!isValidRelativeMove(src, dest)) {
            return false;
        }

        // If the path between the selected piece and the destination is obstructed, then this is not a valid move.
        if (!board.isPathClear(src, dest)) {
            return false;
        }

        return !doesMoveCauseCheck(src, dest);
    }

    // Returns whether the proposed move is one of the valid relative moves for the selected piece.
    private boolean isValidRelativeMove(BoardCoordinate src, BoardCoordinate dest) {
        ChessPiece srcPiece = board.getPieceAt(src);
        ChessPiece destPiece = board.getPieceAt(dest);
        BoardCoordinate relative = dest.subtract(src);
        for (RelativeMove rm : srcPiece.relativeMoves()) {
            if (rm.r == relative.r && rm.c == relative.c) {
                // If the destination coord is empty and the relative move allows this,
                // then this is a valid relative move.
                if (destPiece == null &&
                        (rm.req == RelativeMove.MoveRequirement.NONE ||
                                rm.req == RelativeMove.MoveRequirement.NON_ATTACK_ONLY)) {
                    return true;
                }
                // If the destination coord is occupied by an opponent and the relative move allows an
                // attack then this is a valid relative move.
                if (destPiece != null &&
                        (rm.req == RelativeMove.MoveRequirement.NONE ||
                                rm.req == RelativeMove.MoveRequirement.ATTACK_ONLY)) {
                    return true;
                }
                // If this is a valid en passant attack.
                if (destPiece == null && rm.req == RelativeMove.MoveRequirement.EN_PASSANT &&
                        twoSpaceMovedPawn != null &&
                        (src.add(new BoardCoordinate(0, relative.c)).equals(twoSpaceMovedPawn))) {
                    return true;
                }
                // If the king is attempting to castle.
                if (rm.req == RelativeMove.MoveRequirement.CASTLING && src.c == 4 && (src.r == 0 || src.r == 7)) {
                    return isValidCastlingScenario(src, dest);
                }
            }
        }
        return false;
    }

    // Returns true iff moving the chess piece from src to dest is a valid scenario in which castling
    // may occur. This means that the piece at src must be a king.
    private boolean isValidCastlingScenario(BoardCoordinate src, BoardCoordinate dest) {
        BoardCoordinate relative = dest.subtract(src);
        if (isCheck) {
            return false;
        }

        BoardCoordinate rookCoord;
        if (relative.c < 0) {
            rookCoord = new BoardCoordinate(src.r, src.c - 4);
        } else {
            rookCoord = new BoardCoordinate(src.r, src.c + 3);
        }
        ChessPiece rook = board.getPieceAt(rookCoord);
        // If the rook isn't in the correct spot or has been moved before, then castling
        // is invalid.
        if (!(rook instanceof Rook) || rook.getHasBeenMoved()) {
            return false;
        }
        // If the path between the rook and the king is not clear, then castling is invalid.
        if (!board.isPathClear(src, rookCoord)) {
            return false;
        }
        // If moving to the intermediary square between the king's two space move would
        // cause check then castling is invalid.
        if (doesMoveCauseCheck(src, new BoardCoordinate(src.r, src.c + relative.c / 2))) {
            return false;
        }
        return true;
    }

    // Returns true iff the current player is in check.
    private boolean isCheck() {
        BoardCoordinate kingCoord = board.getKingCoord(turn);
        return getSquareThreateners(kingCoord, turn).size() > 0;
    }

    // Returns true iff a team has no possible valid moves with the current board state.
    private boolean isTeamStuck(TeamColor team) {
        int i = 0;
        for (ChessPiece p : board) {
            if (p != null && p.getColor() == team) {
                BoardCoordinate pCoord = new BoardCoordinate(i);
                for (RelativeMove rm : p.relativeMoves()) {
                    BoardCoordinate dest = pCoord.add(rm);
                    if (ChessBoard.isOnBoard(dest)) {
                        if (isValidMove(pCoord, dest)) {
                            return false;
                        }
                    }
                }
            }
            i += 1;
        }
        return true;
    }

    // Returns true iff the result of the game is a draw due to insufficient material.
    private boolean isInsufficientMaterialDraw() {
        Set<Character> blackPieces = new HashSet<>();
        Set<Character> whitePieces = new HashSet<>();

        for (ChessPiece p : board) {
            if (p != null && p.getColor() == TeamColor.BLACK) {
                blackPieces.add(p.getShortName());
            } else if (p != null && p.getColor() == TeamColor.WHITE) {
                whitePieces.add(p.getShortName());
            }
        }

        if (blackPieces.size() == 1 && whitePieces.size() == 1) {
            return true;
        } else if (blackPieces.size() == 1 && whitePieces.size() == 2) {
            if (whitePieces.contains('n') || whitePieces.contains('b')) {
                return true;
            }
        } else if (whitePieces.size() == 1 && blackPieces.size() == 2) {
            if (blackPieces.contains('n') || blackPieces.contains('b')) {
                return true;
            }
        } else if (blackPieces.size() == 2 && whitePieces.size() == 2 &&
                blackPieces.contains('b') && whitePieces.contains('b')) {
            int i = 0;
            int blackIdx = -1;
            int whiteIdx = -1;
            for (ChessPiece p : board) {
                if (p instanceof Bishop) {
                    if (p.getColor() == TeamColor.BLACK) {
                        blackIdx = i;
                    } else {
                        whiteIdx = i;
                    }
                }
            }
            // If the bishops are on the same color square then this is an insufficient material draw.
            return (blackIdx + i / 8) % 2 == (whiteIdx + i / 8) % 2;
        }
        return false;
    }

    // Returns true iff moving the piece at src to dest would cause the srcPiece's team to
    // be in check (indicating an invalid move).
    private boolean doesMoveCauseCheck(BoardCoordinate src, BoardCoordinate dest) {
        ChessPiece srcPiece = board.getPieceAt(src);
        performMove(src, dest);

        BoardCoordinate kingCoord = board.getKingCoord(srcPiece.getColor());
        Set<BoardCoordinate> kingCheckers = getSquareThreateners(kingCoord, srcPiece.getColor());

        undoMove(src, dest);
        return kingCheckers.size() > 0;
    }

    // Returns a set of all board coordinates containing pieces that could attack the given coordinate
    // on the board if it were occupied by a dummy piece that belongs to the 'threatened' team.
    private Set<BoardCoordinate> getSquareThreateners(BoardCoordinate pieceCoord, TeamColor threatened) {
        Set<BoardCoordinate> threateners = new HashSet<>();
        // Place a dummy piece on the board at the given coord.
        ChessPiece curPiece = board.getPieceAt(pieceCoord);
        board.placePiece(new DummyPiece(threatened), pieceCoord);
        int i = 0;
        for (ChessPiece threat : board) {
            // We want to inspect the possible moves of the enemy team.
            if (threat != null && threat.getColor() != threatened) {
                BoardCoordinate threatCoord = new BoardCoordinate(i);
                // Check that the threatening piece could attack this piece.
                if (isValidRelativeMove(threatCoord, pieceCoord) &&
                        board.isPathClear(threatCoord, pieceCoord)) {
                    threateners.add(new BoardCoordinate(i));
                }
            }
            i += 1;
        }
        // Replace the dummy piece with the correct current piece.
        board.placePiece(curPiece, pieceCoord);
        return threateners;
    }

    /**
     * Returns the total number of chess pieces left in the game.
     * @return number of chess pieces left in the game
     */
    public int totalPiecesLeft() {
        int count = 0;
        for (ChessPiece p : board) {
            if (p != null) {
                count += 1;
            }
        }
        return count;
    }

    /**
     * Returns an iterator over the chess pieces in this game.
     * @return Iterator over the current chess pieces in the game
     */
    public Iterator<ChessPiece> iterator() {
        return board.iterator();
    }

    /**
     * Returns the color of the team whose turn it is.
     * @return color of team to move
     */
    public TeamColor currentTurn() {
        return turn;
    }

    // Advance to the next player's turn.
    private void changeTurn() {
        turn = TeamColor.oppositeTeam(turn);
    }

    /**
     * Return the most recent move executed in this game.
     * @return most recent move
     */
    public Move getLastMove() {
        return lastMove;
    }

    /**
     * Return true iff the game is now over.
     * @return true if the game is now over, and false otherwise.
     */
    public boolean isGameOver() {
        return isOver;
    }

    /**
     * Returns a copy of this ChessGame
     * @return ChessGame copy
     */
    public ChessGame copy() {
        ChessGame copy = new ChessGame();
        copy.turn = this.turn;
        copy.board = this.board.copy();
        copy.pawnPromotionCoord = this.pawnPromotionCoord;
        copy.twoSpaceMovedPawn = this.twoSpaceMovedPawn;
        copy.lastRemoved = this.lastRemoved == null ? null : this.lastRemoved.copy();
        copy.lastMove = this.lastMove;
        copy.isCheck = this.isCheck;
        copy.isOver = this.isOver;
        return copy;
    }

    /**
     * Enum for the various outcomes possible after attempting a move in chess.
     */
    public enum MoveOutcome {
        SUCCESS, FAILURE, PAWN_PROMOTION, CHECK, CHECKMATE, STALEMATE, INSUF_MAT_DRAW
    }
}