package functionality;

import java.util.HashSet;
import java.util.Set;

/**
 * Class representing the queen piece in chess.
 */
public class Queen extends ChessPiece {
    private static final Set<RelativeMove> REL_MOVES = initializeRelativeMoves();

    /**
     * Constructs a Queen chess piece with the specified color and the short name 'q'.
     * @param color color of the queen
     */
    public Queen(TeamColor color) {
        super(color);
        this.setShortName('q');
    }

    /**
     * Returns the relative moves for a queen piece which includes horizontal, vertical,
     * and diagonal movement across a chess board.
     * @return set of relative moves.
     */
    @Override
    public Set<RelativeMove> relativeMoves() {
        return REL_MOVES;
    }

    /**
     * Returns a copy of this queen chess piece.
     * @return queen copy
     */
    @Override
    public ChessPiece copy() {
        ChessPiece piece = new Queen(this.getColor());
        piece.setHasBeenMoved(this.getHasBeenMoved());
        return piece;
    }

    // Return all of the relative moves for a queen piece.
    private static Set<RelativeMove> initializeRelativeMoves() {
        Set<RelativeMove> moves = new HashSet<>();
        for (int i = 1; i < 8; i++) {
            moves.add(new RelativeMove(-i, 0, RelativeMove.MoveRequirement.NONE));
            moves.add(new RelativeMove(-i, i, RelativeMove.MoveRequirement.NONE));
            moves.add(new RelativeMove(0, i, RelativeMove.MoveRequirement.NONE));
            moves.add(new RelativeMove(i, i, RelativeMove.MoveRequirement.NONE));
            moves.add(new RelativeMove(i,0, RelativeMove.MoveRequirement.NONE));
            moves.add(new RelativeMove(i, -i, RelativeMove.MoveRequirement.NONE));
            moves.add(new RelativeMove(0, -i, RelativeMove.MoveRequirement.NONE));
            moves.add(new RelativeMove(-i, -i, RelativeMove.MoveRequirement.NONE));
        }
        return moves;
    }
}