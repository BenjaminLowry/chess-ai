package bot;

import functionality.ChessGame;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChessDelegateTest {
    private static final String TEST_FILE_DIR = "src/test/testFiles/";
    private static final String STANDARD_BOARD_PATH = TEST_FILE_DIR + "defaultLayout.txt";

    @Test
    public void testNextStates() {
        ChessDelegate delegate = new ChessDelegate();
        ChessGame game = new ChessGame(STANDARD_BOARD_PATH);
        List<ChessGame> nextStates = delegate.nextStates(game);
        Set<String> actual = new HashSet<>();
        for (ChessGame g : nextStates) {
            actual.add(g.getBoardState());
        }

        Set<String> expected = new HashSet<>();
        expected.add(
                        "rb nb bb qb kb bb nb rb\n" +
                        "pb pb pb pb pb pb pb pb\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "pw -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- pw pw pw pw pw pw pw\n" +
                        "rw nw bw qw kw bw nw rw"
        );
        expected.add(
                        "rb nb bb qb kb bb nb rb\n" +
                        "pb pb pb pb pb pb pb pb\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "pw -- -- -- -- -- -- --\n" +
                        "-- pw pw pw pw pw pw pw\n" +
                        "rw nw bw qw kw bw nw rw"
        );
        expected.add(
                        "rb nb bb qb kb bb nb rb\n" +
                        "pb pb pb pb pb pb pb pb\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- pw -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "pw -- pw pw pw pw pw pw\n" +
                        "rw nw bw qw kw bw nw rw"
        );
        expected.add(
                        "rb nb bb qb kb bb nb rb\n" +
                        "pb pb pb pb pb pb pb pb\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- pw -- -- -- -- -- --\n" +
                        "pw -- pw pw pw pw pw pw\n" +
                        "rw nw bw qw kw bw nw rw"
        );
        expected.add(
                        "rb nb bb qb kb bb nb rb\n" +
                        "pb pb pb pb pb pb pb pb\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- pw -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "pw pw -- pw pw pw pw pw\n" +
                        "rw nw bw qw kw bw nw rw"
        );
        expected.add(
                        "rb nb bb qb kb bb nb rb\n" +
                        "pb pb pb pb pb pb pb pb\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- pw -- -- -- -- --\n" +
                        "pw pw -- pw pw pw pw pw\n" +
                        "rw nw bw qw kw bw nw rw"
        );
        expected.add(
                        "rb nb bb qb kb bb nb rb\n" +
                        "pb pb pb pb pb pb pb pb\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- pw -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "pw pw pw -- pw pw pw pw\n" +
                        "rw nw bw qw kw bw nw rw"
        );
        expected.add(
                        "rb nb bb qb kb bb nb rb\n" +
                        "pb pb pb pb pb pb pb pb\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- pw -- -- -- --\n" +
                        "pw pw pw -- pw pw pw pw\n" +
                        "rw nw bw qw kw bw nw rw"
        );
        expected.add(
                        "rb nb bb qb kb bb nb rb\n" +
                        "pb pb pb pb pb pb pb pb\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- pw -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "pw pw pw pw -- pw pw pw\n" +
                        "rw nw bw qw kw bw nw rw"
        );
        expected.add(
                        "rb nb bb qb kb bb nb rb\n" +
                        "pb pb pb pb pb pb pb pb\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- pw -- -- --\n" +
                        "pw pw pw pw -- pw pw pw\n" +
                        "rw nw bw qw kw bw nw rw"
        );
        expected.add(
                        "rb nb bb qb kb bb nb rb\n" +
                        "pb pb pb pb pb pb pb pb\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- pw -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "pw pw pw pw pw -- pw pw\n" +
                        "rw nw bw qw kw bw nw rw"
        );
        expected.add(
                        "rb nb bb qb kb bb nb rb\n" +
                        "pb pb pb pb pb pb pb pb\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- pw -- --\n" +
                        "pw pw pw pw pw -- pw pw\n" +
                        "rw nw bw qw kw bw nw rw"
        );
        expected.add(
                        "rb nb bb qb kb bb nb rb\n" +
                        "pb pb pb pb pb pb pb pb\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- pw --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "pw pw pw pw pw pw -- pw\n" +
                        "rw nw bw qw kw bw nw rw"
        );
        expected.add(
                        "rb nb bb qb kb bb nb rb\n" +
                        "pb pb pb pb pb pb pb pb\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- pw --\n" +
                        "pw pw pw pw pw pw -- pw\n" +
                        "rw nw bw qw kw bw nw rw"
        );
        expected.add(
                        "rb nb bb qb kb bb nb rb\n" +
                        "pb pb pb pb pb pb pb pb\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- pw\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "pw pw pw pw pw pw pw --\n" +
                        "rw nw bw qw kw bw nw rw"
        );
        expected.add(
                        "rb nb bb qb kb bb nb rb\n" +
                        "pb pb pb pb pb pb pb pb\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- pw\n" +
                        "pw pw pw pw pw pw pw --\n" +
                        "rw nw bw qw kw bw nw rw"
        );
        expected.add(
                        "rb nb bb qb kb bb nb rb\n" +
                        "pb pb pb pb pb pb pb pb\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "nw -- -- -- -- -- -- --\n" +
                        "pw pw pw pw pw pw pw pw\n" +
                        "rw -- bw qw kw bw nw rw"
        );
        expected.add(
                        "rb nb bb qb kb bb nb rb\n" +
                        "pb pb pb pb pb pb pb pb\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- nw -- -- -- -- --\n" +
                        "pw pw pw pw pw pw pw pw\n" +
                        "rw -- bw qw kw bw nw rw"
        );
        expected.add(
                        "rb nb bb qb kb bb nb rb\n" +
                        "pb pb pb pb pb pb pb pb\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- nw -- --\n" +
                        "pw pw pw pw pw pw pw pw\n" +
                        "rw nw bw qw kw bw -- rw"
        );
        expected.add(
                        "rb nb bb qb kb bb nb rb\n" +
                        "pb pb pb pb pb pb pb pb\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- --\n" +
                        "-- -- -- -- -- -- -- nw\n" +
                        "pw pw pw pw pw pw pw pw\n" +
                        "rw nw bw qw kw bw -- rw"
        );

        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

}
