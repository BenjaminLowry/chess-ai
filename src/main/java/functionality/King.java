package functionality;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class representing the king piece in chess.
 */
public class King extends ChessPiece {
    // The valid relative moves for a king piece (all surrounding squares).
    private static final Set<RelativeMove> REL_MOVES = new HashSet<>(List.of(
            new RelativeMove(1, 0, RelativeMove.MoveRequirement.NONE),
            new RelativeMove(1, 1, RelativeMove.MoveRequirement.NONE),
            new RelativeMove(0, 1, RelativeMove.MoveRequirement.NONE),
            new RelativeMove(-1, 1, RelativeMove.MoveRequirement.NONE),
            new RelativeMove(-1, 0, RelativeMove.MoveRequirement.NONE),
            new RelativeMove(-1, -1, RelativeMove.MoveRequirement.NONE),
            new RelativeMove(0, -1, RelativeMove.MoveRequirement.NONE),
            new RelativeMove(1, -1, RelativeMove.MoveRequirement.NONE)
    ));

    /**
     * Constructs a King chess piece with the specified color and the short name 'k'.
     * @param color color of the king
     */
    public King(TeamColor color) {
        super(color);
        this.setShortName('k');
    }

    /**
     * Returns the relative moves for a king piece which is limited to the squares immediately
     * surrounding the king, and potentially castling movements if the king has not moved yet.
     * @return set of relative moves.
     */
    @Override
    public Set<RelativeMove> relativeMoves() {
        // Add castling relative moves if this king has not been moved yet.
        if (!this.getHasBeenMoved()) {
            Set<RelativeMove> movesPlusCastling = new HashSet<>(REL_MOVES);
            movesPlusCastling.add(new RelativeMove(0, 2, RelativeMove.MoveRequirement.CASTLING));
            movesPlusCastling.add(new RelativeMove(0, -2, RelativeMove.MoveRequirement.CASTLING));
            return movesPlusCastling;
        }
        return REL_MOVES;
    }

    /**
     * Returns a copy of this king chess piece.
     * @return king copy
     */
    @Override
    public ChessPiece copy() {
        ChessPiece piece = new King(this.getColor());
        piece.setHasBeenMoved(this.getHasBeenMoved());
        return piece;
    }
}