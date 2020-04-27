package functionality;

import java.util.HashSet;
import java.util.Set;

/**
 * Class representing the rook piece in chess.
 */
public class Rook extends ChessPiece {
    private static final Set<RelativeMove> REL_MOVES = initializeRelativeMoves();

    /**
     * Constructs a Rook chess piece with the specified color and the short name 'r'.
     * @param color color of the rook
     */
    public Rook(TeamColor color) {
        super(color);
        this.setShortName('r');
    }

    /**
     * Returns the relative moves for a rook piece which includes horizontal and vertical,
     * movement across a chess board.
     * @return set of relative moves.
     */
    @Override
    public Set<RelativeMove> relativeMoves() {
        return REL_MOVES;
    }

    /**
     * Returns a copy of this rook chess piece.
     * @return rook copy
     */
    @Override
    public ChessPiece copy() {
        ChessPiece piece = new Rook(this.getColor());
        piece.setHasBeenMoved(this.getHasBeenMoved());
        return piece;
    }

    // Static initializer for the relative moves that a rook can make.
    private static Set<RelativeMove> initializeRelativeMoves() {
        Set<RelativeMove> moves = new HashSet<>();
        for (int i = 1; i < 8; i++) {
            moves.add(new RelativeMove(i, 0, RelativeMove.MoveRequirement.NONE));
            moves.add(new RelativeMove(-i, 0, RelativeMove.MoveRequirement.NONE));
            moves.add(new RelativeMove(0, i, RelativeMove.MoveRequirement.NONE));
            moves.add(new RelativeMove(0, -i, RelativeMove.MoveRequirement.NONE));
        }
        return moves;
    }
}