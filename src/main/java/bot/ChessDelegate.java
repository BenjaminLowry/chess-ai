package bot;

import functionality.ChessGame;
import functionality.Move;
import java.util.LinkedList;
import java.util.List;

/**
 * Class responsible for interacting with the ChessGame class and using its public methods to synthesize
 * information.
 */
public class ChessDelegate implements MoveEnumerator<ChessGame> {

    /**
     * Returns a set of all possible states of the game after one move by the given team.
     * @param state current game
     * @throws RuntimeException if any of the possible moves provided by the game cannot be executed
     * @return set of all possible next states of the game
     */
    @Override
    public List<ChessGame> nextStates(ChessGame state) {
        List<ChessGame> states = new LinkedList<>();
        for (Move move : state.getPossibleMoves()) {
            ChessGame newGame = state.copy();
            ChessGame.MoveOutcome outcome = newGame.attemptMove(move.getFirst(), move.getSecond());
            if (outcome == ChessGame.MoveOutcome.PAWN_PROMOTION) {
                char[] pieces = {'q', 'r', 'b', 'n'};
                for (char p : pieces) {
                    ChessGame newNewGame = newGame.copy();
                    newNewGame.promotePawn(p);
                    states.add(newNewGame);
                }
            } else if (outcome != ChessGame.MoveOutcome.FAILURE) {
                states.add(newGame);
            } else {
                throw new RuntimeException("Move failed.");
            }
        }
        return states;
    }
}
