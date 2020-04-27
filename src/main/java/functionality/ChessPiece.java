package functionality;

import java.util.Set;

/**
 * Abstract class representing a piece in the game of chess.
 */
public abstract class ChessPiece {
    private TeamColor color;
    private boolean hasBeenMoved;
    private char shortName;

    /**
     * Constructs a chess piece with a given color.
     * @param color piece color
     */
    public ChessPiece(TeamColor color) {
        this.hasBeenMoved = false;
        this.color = color;
    }

    /**
     * Returns a set of moves that this piece could theoretically make based off of the type of piece
     * relative to the piece's current position.
     * @return a Set of RelativeMove objects representing all possible relative moves
     */
    public abstract Set<RelativeMove> relativeMoves();

    /**
     * Returns a copy of this ChessPiece object.
     * @return ChessPiece copy
     */
    public abstract ChessPiece copy();

    /**
     * Creates a ChessPiece object based off of a string id of size 2.
     *
     * The first character in the id determines the type of piece:
     *   - 'r' == rook
     *   - 'n' == knight
     *   - 'b' == bishop
     *   - 'q' == queen
     *   - 'k' == king
     *   - 'p' == pawn
     *
     * The second character in the id determines the color of the piece:
     *   - 'b' == black
     *   - 'w' == white
     *
     * For example, if the id was "qw" then this method would generate a black queen piece.
     *
     * @param id piece id
     * @return newly generated ChessPiece of the specified type and color, or null if the
     *         id does not match the pattern outlined above.
     */
    public static ChessPiece generatePieceFrom(String id) {
        ChessPiece p;
        char type = id.charAt(0);
        char colorChar = id.charAt(1);
        TeamColor color = colorChar == 'b' ? TeamColor.BLACK : TeamColor.WHITE;
        switch (type) {
            case 'r':
                p = new Rook(color);
                break;
            case 'n':
                p = new Knight(color);
                break;
            case 'b':
                p = new Bishop(color);
                break;
            case 'q':
                p = new Queen(color);
                break;
            case 'k':
                p = new King(color);
                break;
            case 'p':
                p = new Pawn(color);
                break;
            default:
                return null;
        }
        return p;
    }

    /**
     * Returns the color of this chess piece.
     * @return piece color
     */
    public TeamColor getColor() {
        return this.color;
    }

    /**
     * Returns whether or not this piece has been moved yet.
     * @return true if the piece has been moved before, false otherwise.
     */
    public boolean getHasBeenMoved() {
        return this.hasBeenMoved;
    }

    /**
     * Set whether or not this piece has been moved before.
     * @param hasBeenMoved whether this piece has been moved before
     */
    public void setHasBeenMoved(boolean hasBeenMoved) {
        this.hasBeenMoved = hasBeenMoved;
    }

    /**
     * Return the specified short name of this chess piece in the form of a single character.
     * @return
     */
    public char getShortName() {
        return this.shortName;
    }

    /**
     * Set the short name of this chess piece.
     * @param shortName name for this chess piece.
     */
    public void setShortName(char shortName) {
        this.shortName = shortName;
    }
}