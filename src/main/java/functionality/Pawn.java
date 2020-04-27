package functionality;

import java.util.HashSet;
import java.util.Set;

/**
 * Class representing the pawn piece in chess.
 */
public class Pawn extends ChessPiece {
    /**
     * Constructs a Pawn chess piece with the specified color and the short name 'p'.
     * @param color color of the pawn
     */
    public Pawn(TeamColor color) {
        super(color);
        this.setShortName('p');
    }

    /**
     * Returns the relative moves for a pawn piece which is normally limited to advancing
     * one square directly ahead, but can also include advancing two squares as the pawn's
     * first move, capturing diagonally one square away, and an "en passant" attack.
     * @return set of relative moves.
     */
    @Override
    public Set<RelativeMove> relativeMoves() {
        Set<RelativeMove> moves = new HashSet<>();
        int directionScalar = this.getColor() == TeamColor.BLACK ? 1 : -1;

        // One space forward move.
        moves.add(new RelativeMove(directionScalar, 0, RelativeMove.MoveRequirement.NON_ATTACK_ONLY));
        // Diagonal attacking moves.
        moves.add(new RelativeMove(directionScalar, -1, RelativeMove.MoveRequirement.ATTACK_ONLY));
        moves.add(new RelativeMove(directionScalar, 1, RelativeMove.MoveRequirement.ATTACK_ONLY));
        // Diagonal en passant attacking moves.
        moves.add(new RelativeMove(directionScalar, -1, RelativeMove.MoveRequirement.EN_PASSANT));
        moves.add(new RelativeMove(directionScalar, 1, RelativeMove.MoveRequirement.EN_PASSANT));
        // If it hasn't moved yet, can do two space forward move.
        if (!this.getHasBeenMoved()) {
            moves.add(new RelativeMove(2 * directionScalar, 0, RelativeMove.MoveRequirement.NON_ATTACK_ONLY));
        }

        return moves;
    }

    /**
     * Returns a copy of this pawn chess piece.
     * @return pawn copy
     */
    @Override
    public ChessPiece copy() {
        ChessPiece piece = new Pawn(this.getColor());
        piece.setHasBeenMoved(this.getHasBeenMoved());
        return piece;
    }
}