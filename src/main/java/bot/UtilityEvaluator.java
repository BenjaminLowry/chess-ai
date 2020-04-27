package bot;

import functionality.TeamColor;

/**
 * Interface for classes that can be used to calculated the utility of different game states.
 */
public interface UtilityEvaluator<T> {

    /**
     * Returns the utility of the current state for a given team as a quantitative figure.
     * @param state current game state
     * @param team team to determine the utility for
     * @return a decimal number representing the utility of this game state for the given team.
     */
    public double utility(T state, TeamColor team, int depth);
}
