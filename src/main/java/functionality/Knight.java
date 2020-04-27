package functionality;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class representing the knight piece in chess.
 */
public class Knight extends ChessPiece {
    // The valid relative moves for a knight piece (l-shaped moves).
    private static final Set<RelativeMove> REL_MOVES = new HashSet<>(List.of(
            new RelativeMove(2, 1, RelativeMove.MoveRequirement.NONE),
            new RelativeMove(1, 2, RelativeMove.MoveRequirement.NONE),
            new RelativeMove(-1, 2, RelativeMove.MoveRequirement.NONE),
            new RelativeMove(-2, 1, RelativeMove.MoveRequirement.NONE),
            new RelativeMove(-2, -1, RelativeMove.MoveRequirement.NONE),
            new RelativeMove(-1, -2, RelativeMove.MoveRequirement.NONE),
            new RelativeMove(1, -2, RelativeMove.MoveRequirement.NONE),
            new RelativeMove(2, -1, RelativeMove.MoveRequirement.NONE)
    ));

    /**
     * Constructs a Knight chess piece with the specified color and the short name 'n'.
     * @param color color of the knight
     */
    public Knight(TeamColor color) {
        super(color);
        this.setShortName('n');
    }

    /**
     * Returns the relative moves for a knight piece which is limited to its l-shaped
     * moves.
     * @return set of relative moves.
     */
    @Override
    public Set<RelativeMove> relativeMoves() {
        return REL_MOVES;
    }

    /**
     * Returns a copy of this knight chess piece.
     * @return knight copy
     */
    @Override
    public ChessPiece copy() {
        ChessPiece piece = new Knight(this.getColor());
        piece.setHasBeenMoved(this.getHasBeenMoved());
        return piece;
    }
}