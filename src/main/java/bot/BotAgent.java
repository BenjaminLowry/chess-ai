package bot;

import functionality.ChessGame;
import functionality.Move;
import functionality.TeamColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for acting as the robot player.
 */
public class BotAgent {
    private ChessGame game;
    private TeamColor team;
    private MinimaxTreeExplorer<ChessGame> explorer;

    /**
     * Create a new bot that will act a player in the provided game on the specified team.
     * @param game chess game for the bot to play in
     * @param team the bot's team
     */
    public BotAgent(ChessGame game, TeamColor team) {
        this.game = game;
        this.team = team;
        this.explorer = new MinimaxTreeExplorer<>(new NaiveUtilityEvaluator(), new ChessDelegate(), team, game, 4);
    }

    /**
     * Get the next best move for the bot's team given the current state of the chess game.
     * @throws IllegalStateException if it is not the bot's turn when this method is called
     * @return Move object representing the next best move for the bot
     */
    public Move getNextMove() {
        if (game.currentTurn() != team) {
            throw new IllegalStateException("Not the bot's turn.");
        }
        ChessGame nextState = explorer.getNextBestNode();
        return nextState.getLastMove();
    }

    /**
     * Get a list of up to three of the next best moves for the bot's team.
     * @return a list of MoveUtility objects which contain the moves and their relative
     *         utility for the bot's team
     */
    public List<MoveUtility> getNextMoves() {
        if (game.totalPiecesLeft() < 8) {
            explorer.setMaxDepth(6);
        } else if (game.totalPiecesLeft() < 12) {
            explorer.setMaxDepth(5);
        }

        if (game.currentTurn() != team) {
            throw new IllegalStateException("Not the bot's turn.");
        }
        List<MinimaxTreeExplorer<ChessGame>.NodeValue> nextStates = explorer.getNextBestNodes();
        List<MoveUtility> nextMoves = new ArrayList<>(3);
        for (MinimaxTreeExplorer<ChessGame>.NodeValue nv : nextStates) {
            // If we find a null node then there are less than three moves the bot can make.
            if (nv.node != null) {
                nextMoves.add(new MoveUtility(nv.node.getLastMove(), nv.value));
            }
        }
        return nextMoves;
    }

    /**
     * Class representing a move and the utility such a move may provide for a team in chess.
     */
    public class MoveUtility {
        public Move move;
        public double utility;

        public MoveUtility(Move move, double utility) {
            this.move = move;
            this.utility = utility;
        }
    }
}