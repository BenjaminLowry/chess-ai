package functionality;

import java.util.HashSet;
import java.util.Set;

/**
 * Class representing the bishop piece in chess.
 */
public class Bishop extends ChessPiece {
    private static final Set<RelativeMove> REL_MOVES = initializeRelativeMoves();

    /**
     * Constructs a Bishop chess piece with the specified color and the short name 'b'.
     * @param color color of the bishop
     */
    public Bishop(TeamColor color) {
        super(color);
        this.setShortName('b');
    }

    /**
     * Returns the relative moves for a bishop piece which is limited to diagonal
     * movements.
     * @return set of relative moves.
     */
    @Override
    public Set<RelativeMove> relativeMoves() {
        return REL_MOVES;
    }

    /**
     * Returns a copy of this bishop chess piece.
     * @return bishop copy
     */
    @Override
    public ChessPiece copy() {
        ChessPiece piece = new Bishop(this.getColor());
        piece.setHasBeenMoved(this.getHasBeenMoved());
        return piece;
    }

    // Return all possible diagonal relative moves for a bishop, from one space away to seven
    // spaces away.
    private static Set<RelativeMove> initializeRelativeMoves() {
        Set<RelativeMove> moves = new HashSet<>();
        for (int i = 1; i < 8; i++) {
            moves.add(new RelativeMove(-i, i, RelativeMove.MoveRequirement.NONE));
            moves.add(new RelativeMove(i, i, RelativeMove.MoveRequirement.NONE));
            moves.add(new RelativeMove(i, -i, RelativeMove.MoveRequirement.NONE));
            moves.add(new RelativeMove(-i, -i, RelativeMove.MoveRequirement.NONE));
        }
        return moves;
    }
}
