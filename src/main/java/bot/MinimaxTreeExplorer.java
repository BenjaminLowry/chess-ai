package bot;

import functionality.TeamColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that explores a minimax tree for a given game and is able to return the "best" move
 * based on a provided utility function.
 * @param <T> game to be explored
 */
public class MinimaxTreeExplorer<T> {
    private int maxDepth;
    private UtilityEvaluator<T> utilityEval;
    private MoveEnumerator<T> mEnum;
    private TeamColor team;
    private T root;

    /**
     * Create a new minimax tree explorer.
     *
     * @param eval utility function for this game
     * @param mEnum object that gives the next possible states in this game given the current state
     * @param team team in the game that this explorer is helping
     * @param root main game object
     * @param maxDepth farthest moves ahead that explorer will look
     */
    public MinimaxTreeExplorer(UtilityEvaluator<T> eval, MoveEnumerator<T> mEnum, TeamColor team,
                               T root, int maxDepth) {
        this.utilityEval = eval;
        this.mEnum = mEnum;
        this.team = team;
        this.root = root;
        this.maxDepth = maxDepth;
    }

    /**
     * Set the max number of moves the explorer will look ahead.
     * @param maxDepth max number of moves
     */
    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    /**
     * Get up to the next 3 best moves given the current state of the game.
     * @return list of next best game nodes-value pairs
     */
    public List<NodeValue> getNextBestNodes() {
        List<T> nextStates = mEnum.nextStates(root);
        List<NodeValue> bestNodes = new ArrayList<>(3);
        bestNodes.add(new NodeValue(null, Double.NEGATIVE_INFINITY));
        bestNodes.add(new NodeValue(null, Double.NEGATIVE_INFINITY));
        bestNodes.add(new NodeValue(null, Double.NEGATIVE_INFINITY));
        double maxValue = Double.NEGATIVE_INFINITY;

        for (T nextState : nextStates) {
            double v = minValue(nextState, maxValue, Double.POSITIVE_INFINITY, 1);
            if (v > bestNodes.get(2).value) {
                bestNodes.remove(2);
                bestNodes.add(new NodeValue(nextState, v));
                bestNodes.sort((nv1, nv2) -> Double.compare(nv2.value, nv1.value));
                maxValue = bestNodes.get(0).value;
            }
        }

        return bestNodes;
    }

    /**
     * Get the next best node in the game given the current state of the game.
     * @return next best node
     */
    public T getNextBestNode() {
        List<T> nextStates = mEnum.nextStates(root);
        double maxValue = Double.NEGATIVE_INFINITY;
        T maxNextState = null;

        for (T nextState : nextStates) {
            double v = minValue(nextState, maxValue, Double.POSITIVE_INFINITY, 1);
            if (v > maxValue) {
                maxValue = v;
                maxNextState = nextState;
            }
        }
        return maxNextState;
    }

    // Max node in the minimax tree.
    private double maxValue(T state, double alpha, double beta, int depth) {
        if (depth >= maxDepth) {
            return utilityEval.utility(state, team, depth);
        }
        List<T> nextStates = mEnum.nextStates(state);
        if (nextStates.size() == 0) {
            return utilityEval.utility(state, team, depth);
        }
        double v = Double.NEGATIVE_INFINITY;

        for (T nextState : nextStates) {
            double vPrime = minValue(nextState, alpha, beta, depth + 1);
            v = Math.max(v, vPrime);

            // (alpha-beta) Prune.
            if (vPrime > beta) {
                return v;
            }

            alpha = Math.max(alpha, vPrime);
        }
        return v;
    }

    // Min node in the minimax tree.
    private double minValue(T state, double alpha, double beta, int depth) {
        if (depth >= maxDepth) {
            return utilityEval.utility(state, team, depth);
        }
        List<T> nextStates = mEnum.nextStates(state);
        if (nextStates.size() == 0) {
            return utilityEval.utility(state, team, depth);
        }
        double v = Double.POSITIVE_INFINITY;

        for (T nextState : nextStates) {
            double vPrime = maxValue(nextState, alpha, beta, depth + 1);
            v = Math.min(v, vPrime);

            // (alpha-beta) Prune.
            if (vPrime < alpha) {
                return v;
            }

            beta = Math.min(beta, vPrime);
        }
        return v;
    }

    /**
     * Class representing a node-value pair where the value is the utility of visiting this
     * node during the game.
     */
    public class NodeValue {
        public T node;
        public double value;

        public NodeValue(T node, double value) {
            this.node = node;
            this.value = value;
        }
    }
}