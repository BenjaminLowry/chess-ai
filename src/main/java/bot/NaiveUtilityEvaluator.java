package bot;

import functionality.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Class representing a relatively naive chess game utility evaluator. Without any information
 * about previous or future moves, this evaluator provides quantitative utility estimates for
 * a state in chess.
 */
public class NaiveUtilityEvaluator implements UtilityEvaluator<ChessGame> {
    private static final double WIN_UTILITY = 1000000.0;
    private static final Random RAND = new Random();

    /**
     * Returns the utility of a given chess state for a specified team.
     *
     * @param state current game state
     * @param team team to determine the utility for
     * @param depth depth in a tree search that this utility is being retrieved in
     * @return a double representing the utility of this chess state for the given team with
     *         a larger number representing a more advantageous situation for the team
     */
    public double utility(ChessGame state, TeamColor team, int depth) {
        if (ChessGame.verifyGameCheckmate(state)) {
            if (team == state.currentTurn()) {
                return -WIN_UTILITY;
            } else {
                return WIN_UTILITY - depth;
            }
        } else if (ChessGame.verifyGameStalemate(state)) {
            return -WIN_UTILITY / 2;
        }

        Map<TeamColor, Double> teamStrengths = getTeamStrengths(state);
        double overallStrength;
        if (team == TeamColor.BLACK) {
            overallStrength = teamStrengths.get(TeamColor.BLACK) - teamStrengths.get(TeamColor.WHITE);
        } else {
            overallStrength = teamStrengths.get(TeamColor.WHITE) - teamStrengths.get(TeamColor.BLACK);
        }

        // Add small random fluctuations to avoid repeating moves.
        overallStrength += (RAND.nextInt(10) / 1000.0);

        return overallStrength;
    }

    // Get the relative strengths of each team's current pieces.
    private Map<TeamColor, Double> getTeamStrengths(ChessGame state) {
        Map<TeamColor, Double> strengthMap = new HashMap<>();
        double blackStrength = 0.0;
        double whiteStrength = 0.0;
        int i = 0;
        for (ChessPiece p : state) {
            if (p != null) {
                double pieceStrength = 0.0;
                int pawnRow = 0;
                if (p instanceof Pawn) {
                    pieceStrength = 1.0;
                    pawnRow += (i / 8);
                } else if (p instanceof Rook) {
                    pieceStrength = 1.8;
                } else if (p instanceof Knight) {
                    pieceStrength = 1.5;
                } else if (p instanceof Bishop) {
                    pieceStrength = 1.5;
                } else if (p instanceof Queen) {
                    pieceStrength = 2.5;
                }
                if (p.getColor() == TeamColor.BLACK) {
                    blackStrength += pieceStrength;
                    // Add utility for advancing pawns.
                    blackStrength += ((pawnRow - 1) * 0.005);
                } else {
                    whiteStrength += pieceStrength;
                    // Add utility for advancing pawns.
                    whiteStrength += ((6 - pawnRow) * 0.005);
                }
            }
            i += 1;
        }
        strengthMap.put(TeamColor.BLACK, blackStrength);
        strengthMap.put(TeamColor.WHITE, whiteStrength);
        return strengthMap;
    }
}