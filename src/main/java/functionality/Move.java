package functionality;

/**
 * Class representing a move of a piece in chess.
 */
public class Move {
    private BoardCoordinate c1;
    private BoardCoordinate c2;
    private char pawnPromotion;

    /**
     * Construct a Move representing moving from one coordinate on the board to another.
     * @param c1 source coordinate
     * @param c2 destination coordinate
     */
    public Move(BoardCoordinate c1, BoardCoordinate c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    /**
     * Construct a Move representing moving a pawn from one coordinate to another
     * and then promoting this pawn to a piece as specified by its short name.
     *
     * @param c1 source coordinate
     * @param c2 destination coordinate
     * @param pawnPromotion short name of piece to promote pawn to
     */
    public Move(BoardCoordinate c1, BoardCoordinate c2, char pawnPromotion) {
        this(c1, c2);
        this.pawnPromotion = pawnPromotion;
    }

    /**
     * Get the source coordinate of this move.
     * @return source BoardCoordinate
     */
    public BoardCoordinate getFirst() {
        return this.c1;
    }

    /**
     * Get the destination coordinate of this move.
     * @return destination BoardCoordinate
     */
    public BoardCoordinate getSecond() {
        return this.c2;
    }

    /**
     * Get the short name of the piece that the pawn was promoted to in this move.
     * @return short name of piece promoted to
     */
    public char getPawnPromotion() {
        return this.pawnPromotion;
    }

    /**
     * Returns true iff another object is equal to this Move.
     *
     * Move equality is defined as two objects both being Move instances
     * and having equal c1, c2, and pawnPromotion components.
     *
     * @param obj other object
     * @return true if this other object is equal to this Move, and false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Move)) {
            return false;
        }
        Move other = (Move) obj;
        return this.c1.equals(other.c1) && this.c2.equals(other.c2) && this.pawnPromotion == other.pawnPromotion;
    }

    /**
     * Returns a hash code for this Move.
     * @return hash code
     */
    @Override
    public int hashCode() {
        return 17 * this.c1.hashCode() + 3 * this.c2.hashCode() + 7 * this.pawnPromotion;
    }
}
