package bot;

import java.util.List;

/**
 * Interface that defines objects that can provide the next states in a game given the current
 * state of the game.
 * @param <T>
 */
public interface MoveEnumerator<T> {

    /**
     * Returns a list of game states given the current state of the game.
     * @param state current game state
     * @return list of all possible game states after one move
     */
    public List<T> nextStates(T state);
}