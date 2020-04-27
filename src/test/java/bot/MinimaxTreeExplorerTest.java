package bot;

import functionality.ChessGame;
import functionality.TeamColor;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MinimaxTreeExplorerTest {
    private static final String TEST_FILE_DIR = "src/test/testFiles/";

    @Test
    public void testGetNextBestMoveSimple() {
        UtilityEvaluator<String> eval = new UtilityEvaluator<>() {
            private int count = 0;
            private final List<Double> utilities = List.of(4.0, 8.0, 9.0, 2.0, -2.0, 9.0, -1.0);

            @Override
            public double utility(String state, TeamColor team, int depth) {
                return utilities.get(count++);
            }
        };

        MoveEnumerator<String> mEnum = (state) -> {
            List<String> nextStates = new ArrayList<>(2);
            char c = state.charAt(state.length() - 1);
            int n = c - 'a';
            nextStates.add(indexToStr(2 * n + 1));
            nextStates.add(indexToStr(2 * n + 2));
            return nextStates;
        };

        MinimaxTreeExplorer<String> mt = new MinimaxTreeExplorer<>(eval, mEnum, TeamColor.WHITE, "a", 3);
        assertEquals("b", mt.getNextBestNode());
    }

    @Test
    public void testTest() {
        ChessDelegate delegate = new ChessDelegate();
        MoveEnumerator<ChessGame> gameEnum = new MoveEnumerator<>() {
            @Override
            public List<ChessGame> nextStates(ChessGame state) {
                List<ChessGame> nextStates = delegate.nextStates(state);
                return nextStates;
            }
        };

        ChessGame startGame = new ChessGame(TEST_FILE_DIR + "notCheckmateExample2.txt");
        MinimaxTreeExplorer<ChessGame> mt = new MinimaxTreeExplorer<>(new NaiveUtilityEvaluator(),
                gameEnum, TeamColor.WHITE, startGame, 4);
        System.out.println(mt.getNextBestNode().getBoardState());
    }

    public static String indexToStr(int i) {
        return String.valueOf((char) ((int) 'a' + i));
    }
}
