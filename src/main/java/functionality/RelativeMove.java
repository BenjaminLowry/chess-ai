package functionality;

/**
 * Class representing a valid move for a chess piece relative to their current position.
 * A valid move may require that the move is an attacking move (e.g. pawn diagonal move) or
 * another special situation.
 */
public class RelativeMove extends BoardCoordinate {
    /**
     * Requirement for this relative coordinate in order for it to valid for a piece.
     */
    public MoveRequirement req;

    /**
     * Create a RelativeMove with a relative row and column change and a move requirment.
     * @param row row change for relative move
     * @param col column change for relative move
     * @param req move requirement
     */
    public RelativeMove(int row, int col, MoveRequirement req) {
        super(row, col);
        this.req = req;
    }

    /**
     * Returns whether or not another object is equal to this relative move.
     *
     * RelativeMove equality is defined as two objects both being RelativeMove
     * instances and having equal row, col, and req components.
     *
     * @param o other object
     * @return true if the other object is equal to this, and false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RelativeMove)) {
            return false;
        }
        RelativeMove other = (RelativeMove) o;
        return this.r == other.r && this.c == other.c && this.req == other.req;
    }

    /**
     * Returns a hash code for this RelativeMove.
     * @return hash code
     */
    @Override
    public int hashCode() {
        return 7 * this.r + 19 * this.c + 5 * this.req.hashCode();
    }

    /**
     * Enum for the requirements imposed on certain moves.
     */
    public enum MoveRequirement {
        NONE, ATTACK_ONLY, NON_ATTACK_ONLY, EN_PASSANT, CASTLING;
    }
}