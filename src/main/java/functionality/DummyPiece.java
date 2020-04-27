package functionality;

import java.util.HashSet;
import java.util.Set;

/**
 * ChessPiece that does not represent an actual chess piece but just a theoretical piece that could
 * occupy a square on the board for evaluative purposes.
 */
public class DummyPiece extends ChessPiece {

    /**
     * Constructs a DummyPiece with the specific color and with short name 'd'.
     * @param color dummy piece color
     */
    public DummyPiece(TeamColor color) {
        super(color);
        this.setShortName('d');
    }

    /**
     * Return the relative moves for a dummy piece which includes no moves since the dummy piece
     * is not an actual piece in chess.
     * @return an empty set of relative moves
     */
    @Override
    public Set<RelativeMove> relativeMoves() {
        return new HashSet<>();
    }

    /**
     * Returns a copy of this dummy piece.
     * @return dummy piece copy
     */
    @Override
    public ChessPiece copy() {
        return new DummyPiece(this.getColor());
    }
}