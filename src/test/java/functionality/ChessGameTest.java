package functionality;

import java.util.Set;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ChessGameTest {
    private static final String TEST_FILE_DIR = "src/test/testFiles/";
    private static final String STANDARD_BOARD_PATH = TEST_FILE_DIR + "defaultLayout.txt";

    @Test
    public void testMovePawn() {
        ChessGame testGame = new ChessGame(STANDARD_BOARD_PATH);
        
        // Illegal diagonal move for non-attack.
        assertFailure(testGame.attemptMove(new BoardCoordinate(6, 0), new BoardCoordinate(5, 1)));
        // Legal one space forward move.
        assertSuccess(testGame.attemptMove(new BoardCoordinate(6, 0), new BoardCoordinate(5, 0)));

        // Illegal diagonal move for non-attack.
        assertFailure(testGame.attemptMove(new BoardCoordinate(1, 1), new BoardCoordinate(2, 0)));
        // Legal two space forward move.
        assertSuccess(testGame.attemptMove(new BoardCoordinate(1, 1), new BoardCoordinate(3, 1)));

        // Illegal two space forward move as a second move.
        assertFailure(testGame.attemptMove(new BoardCoordinate(5, 0), new BoardCoordinate(3, 0)));

        assertSuccess(testGame.attemptMove(new BoardCoordinate(5, 0), new BoardCoordinate(4, 0)));
        assertSuccess(testGame.attemptMove(new BoardCoordinate(3, 1), new BoardCoordinate(4, 0)));
    }

    @Test
    public void testPawnPromotion() {
        ChessGame testGame = new ChessGame(TEST_FILE_DIR + "onlyPawnsAndKing.txt");

        assertSuccess(testGame.attemptMove(new BoardCoordinate(6, 1), new BoardCoordinate(4, 1)));
        assertSuccess(testGame.attemptMove(new BoardCoordinate(1, 0), new BoardCoordinate(3, 0)));
        assertSuccess(testGame.attemptMove(new BoardCoordinate(4, 1), new BoardCoordinate(3, 0)));
        assertSuccess(testGame.attemptMove(new BoardCoordinate(1, 6), new BoardCoordinate(2, 6)));
        assertSuccess(testGame.attemptMove(new BoardCoordinate(3, 0), new BoardCoordinate(2, 0)));
        assertSuccess(testGame.attemptMove(new BoardCoordinate(2, 6), new BoardCoordinate(3, 6)));
        assertSuccess(testGame.attemptMove(new BoardCoordinate(2, 0), new BoardCoordinate(1, 0)));
        assertSuccess(testGame.attemptMove(new BoardCoordinate(1, 7), new BoardCoordinate(2, 7)));

        assertFailure(testGame.promotePawn('b'));
        assertPawnPromotion(testGame.attemptMove(new BoardCoordinate(1, 0), new BoardCoordinate(0, 0)));
        assertSuccess(testGame.promotePawn('b'));

        assertSuccess(testGame.attemptMove(new BoardCoordinate(2, 7), new BoardCoordinate(3, 7)));
        assertSuccess(testGame.attemptMove(new BoardCoordinate(6, 5), new BoardCoordinate(4, 5)));
        assertSuccess(testGame.attemptMove(new BoardCoordinate(3, 6), new BoardCoordinate(4, 5)));
        assertSuccess(testGame.attemptMove(new BoardCoordinate(6, 7), new BoardCoordinate(5, 7)));
        assertSuccess(testGame.attemptMove(new BoardCoordinate(4, 5), new BoardCoordinate(5, 5)));
        assertSuccess(testGame.attemptMove(new BoardCoordinate(5, 7), new BoardCoordinate(4, 7)));
        assertCheck(testGame.attemptMove(new BoardCoordinate(5, 5), new BoardCoordinate(6, 5)));
        assertSuccess(testGame.attemptMove(new BoardCoordinate(7, 4), new BoardCoordinate(7, 3)));

        assertFailure(testGame.promotePawn('q'));
        assertPawnPromotion(testGame.attemptMove(new BoardCoordinate(6, 5), new BoardCoordinate(7, 5)));
        assertCheckmate(testGame.promotePawn('q'));
    }

    @Test
    public void testEnPassantRule() {
        ChessGame simpleScenario = new ChessGame(TEST_FILE_DIR + "enPassantExample1.txt", TeamColor.BLACK);
        assertSuccess(simpleScenario.attemptMove(new BoardCoordinate(1, 5), new BoardCoordinate(3, 5)));
        assertSuccess(simpleScenario.attemptMove(new BoardCoordinate(3, 4), new BoardCoordinate(2, 5)));
        assertEquals("kw -- -- -- -- -- -- --\n" + "-- -- -- -- -- -- -- --\n" + "-- -- -- -- -- pw -- --\n" +
                "-- -- -- -- -- -- -- --\n" + "-- -- -- -- -- -- -- --\n" + "-- -- -- -- -- -- -- --\n" +
                "-- -- -- -- -- -- -- --\n" + "kb -- -- -- -- -- -- --", simpleScenario.getBoardState());

        // Trying to do en passant with a horizontal attack instead of diagonal.
        ChessGame simpleScenario2 = new ChessGame(TEST_FILE_DIR + "enPassantExample1.txt", TeamColor.WHITE);
        assertFailure(simpleScenario2.attemptMove(new BoardCoordinate(3, 4), new BoardCoordinate(2, 5)));
        assertSuccess(simpleScenario2.attemptMove(new BoardCoordinate(3, 4), new BoardCoordinate(2, 4)));
        assertSuccess(simpleScenario2.attemptMove(new BoardCoordinate(1, 5), new BoardCoordinate(3, 5)));
        assertFailure(simpleScenario2.attemptMove(new BoardCoordinate(2, 4), new BoardCoordinate(2, 5)));

        ChessGame scenario2a = new ChessGame(TEST_FILE_DIR + "enPassantExample2.txt", TeamColor.BLACK);
        assertSuccess(scenario2a.attemptMove(new BoardCoordinate(1, 3), new BoardCoordinate(3, 3)));
        assertSuccess(scenario2a.attemptMove(new BoardCoordinate(3, 4), new BoardCoordinate(2, 3)));

        // Trying to do en passant not immediately after.
        ChessGame scenario2b = new ChessGame(TEST_FILE_DIR + "enPassantExample2.txt", TeamColor.BLACK);
        assertSuccess(scenario2b.attemptMove(new BoardCoordinate(1, 3), new BoardCoordinate(3, 3)));
        assertSuccess(scenario2b.attemptMove(new BoardCoordinate(6, 7), new BoardCoordinate(5, 7)));
        assertSuccess(scenario2b.attemptMove(new BoardCoordinate(0, 1), new BoardCoordinate(2, 0)));
        assertFailure(scenario2b.attemptMove(new BoardCoordinate(3, 4), new BoardCoordinate(2, 3)));

        ChessGame scenario3 = new ChessGame(TEST_FILE_DIR + "enPassantExample3.txt", TeamColor.BLACK);
        assertCheck(scenario3.attemptMove(new BoardCoordinate(1, 6), new BoardCoordinate(3, 6)));
        assertCheckmate(scenario3.attemptMove(new BoardCoordinate(3, 5), new BoardCoordinate(2, 6)));
    }

    @Test
    public void testMoveRook() {
        ChessGame defaultBoard = new ChessGame(STANDARD_BOARD_PATH);

        // Illegal hopping over pawn.
        assertFailure(defaultBoard.attemptMove(new BoardCoordinate(7, 0), new BoardCoordinate(5, 0)));
        assertFailure(defaultBoard.attemptMove(new BoardCoordinate(7, 0), new BoardCoordinate(2, 0)));

        // Illegal move onto a pawn.
        assertFailure(defaultBoard.attemptMove(new BoardCoordinate(7, 0), new BoardCoordinate(6, 0)));

        // Illegal knight-like move.
        assertFailure(defaultBoard.attemptMove(new BoardCoordinate(7, 0), new BoardCoordinate(5, 1)));

        ChessGame cornerPawnsRemovedBoard = new ChessGame(TEST_FILE_DIR + "cornerPawnsRemoved.txt");

        // Various legal moves.
        assertSuccess(cornerPawnsRemovedBoard.attemptMove(
                new BoardCoordinate(7, 0), new BoardCoordinate(6, 0)));
        assertSuccess(cornerPawnsRemovedBoard.attemptMove(
                new BoardCoordinate(0, 0), new BoardCoordinate(2, 0)));
        assertSuccess(cornerPawnsRemovedBoard.attemptMove(
                new BoardCoordinate(7, 7), new BoardCoordinate(5, 7)));
        assertSuccess(cornerPawnsRemovedBoard.attemptMove(
                new BoardCoordinate(2, 0), new BoardCoordinate(2, 3)));
        assertSuccess(cornerPawnsRemovedBoard.attemptMove(
                new BoardCoordinate(5, 7), new BoardCoordinate(7, 7)));
        assertSuccess(cornerPawnsRemovedBoard.attemptMove(
                new BoardCoordinate(2, 3), new BoardCoordinate(6, 3)));

        // Illegal diagonal moves.
        assertFailure(cornerPawnsRemovedBoard.attemptMove(
                new BoardCoordinate(6, 0), new BoardCoordinate(5, 1)));
        assertFailure(cornerPawnsRemovedBoard.attemptMove(
                new BoardCoordinate(7, 7), new BoardCoordinate(5, 5)));
    }

    @Test
    public void testMoveKing() {
        ChessGame withoutPawns = new ChessGame(TEST_FILE_DIR + "withoutPawns.txt");

        assertFailure(withoutPawns.attemptMove(new BoardCoordinate(7, 4), new BoardCoordinate(7, 3)));
        assertFailure(withoutPawns.attemptMove(new BoardCoordinate(7, 4), new BoardCoordinate(7, 5)));
        assertFailure(withoutPawns.attemptMove(new BoardCoordinate(7, 4), new BoardCoordinate(5, 4)));

        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(7, 4), new BoardCoordinate(6, 4)));
        assertFailure(withoutPawns.attemptMove(new BoardCoordinate(0, 4), new BoardCoordinate(1, 3)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(0, 4), new BoardCoordinate(1, 5)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(6, 4), new BoardCoordinate(5, 4)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(1, 5), new BoardCoordinate(1, 6)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(5, 4), new BoardCoordinate(5, 5)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(1, 6), new BoardCoordinate(2, 6)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(5, 5), new BoardCoordinate(4, 4)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(2, 6), new BoardCoordinate(2, 5)));
    }

    @Test
    public void testMoveKnight() {
        ChessGame withoutPawns = new ChessGame(TEST_FILE_DIR + "withoutPawns.txt");

        assertFailure(withoutPawns.attemptMove(new BoardCoordinate(7, 1), new BoardCoordinate(6, 1)));
        assertFailure(withoutPawns.attemptMove(new BoardCoordinate(7, 1), new BoardCoordinate(6, 0)));
        assertFailure(withoutPawns.attemptMove(new BoardCoordinate(7, 1), new BoardCoordinate(6, 2)));
        assertFailure(withoutPawns.attemptMove(new BoardCoordinate(7, 1), new BoardCoordinate(7, 0)));
        assertFailure(withoutPawns.attemptMove(new BoardCoordinate(7, 1), new BoardCoordinate(5, 1)));

        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(7, 1), new BoardCoordinate(5, 0)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(0, 6), new BoardCoordinate(1, 4)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(5, 0), new BoardCoordinate(7, 1)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(0, 1), new BoardCoordinate(1, 3)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(7, 6), new BoardCoordinate(6, 4)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(1, 3), new BoardCoordinate(3, 4)));
    }

    @Test
    public void testMoveBishop() {
        ChessGame withoutPawns = new ChessGame(TEST_FILE_DIR + "withoutPawns.txt");

        assertFailure(withoutPawns.attemptMove(new BoardCoordinate(7, 2), new BoardCoordinate(7, 1)));
        assertFailure(withoutPawns.attemptMove(new BoardCoordinate(7, 2), new BoardCoordinate(6, 2)));
        assertFailure(withoutPawns.attemptMove(new BoardCoordinate(7, 2), new BoardCoordinate(5, 1)));
        assertFailure(withoutPawns.attemptMove(new BoardCoordinate(7, 5), new BoardCoordinate(5, 5)));
        assertFailure(withoutPawns.attemptMove(new BoardCoordinate(7, 5), new BoardCoordinate(6, 3)));

        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(7, 5), new BoardCoordinate(4, 2)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(0, 2), new BoardCoordinate(1, 1)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(4, 2), new BoardCoordinate(2, 4)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(1, 1), new BoardCoordinate(2, 0)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(2, 4), new BoardCoordinate(3, 3)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(0, 5), new BoardCoordinate(5, 0)));
    }

    @Test
    public void testMoveQueen() {
        ChessGame withoutPawns = new ChessGame(TEST_FILE_DIR + "withoutPawns.txt");

        assertFailure(withoutPawns.attemptMove(new BoardCoordinate(7, 3), new BoardCoordinate(7, 1)));
        assertFailure(withoutPawns.attemptMove(new BoardCoordinate(7, 3), new BoardCoordinate(6, 1)));
        assertFailure(withoutPawns.attemptMove(new BoardCoordinate(7, 3), new BoardCoordinate(5, 2)));

        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(7, 3), new BoardCoordinate(2, 3)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(0, 3), new BoardCoordinate(1, 3)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(2, 3), new BoardCoordinate(5, 3)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(1, 3), new BoardCoordinate(1, 2)));
        assertCheck(withoutPawns.attemptMove(new BoardCoordinate(5, 3), new BoardCoordinate(6, 4)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(1, 2), new BoardCoordinate(1, 4)));
        assertSuccess(withoutPawns.attemptMove(new BoardCoordinate(6, 4), new BoardCoordinate(5, 4)));
        assertCheck(withoutPawns.attemptMove(new BoardCoordinate(1, 4), new BoardCoordinate(5, 4)));
    }

    @Test
    public void testCheckmate() {
        assertTrue(ChessGame.verifyGameCheckmate(TEST_FILE_DIR + "checkmateExample1.txt", TeamColor.BLACK));
        assertTrue(ChessGame.verifyGameCheckmate(TEST_FILE_DIR + "checkmateExample2.txt", TeamColor.BLACK));
        assertTrue(ChessGame.verifyGameCheckmate(TEST_FILE_DIR + "checkmateExample3.txt", TeamColor.BLACK));
        assertTrue(ChessGame.verifyGameCheckmate(TEST_FILE_DIR + "checkmateExample4.txt", TeamColor.WHITE));
        assertTrue(ChessGame.verifyGameCheckmate(TEST_FILE_DIR + "checkmateExample5.txt", TeamColor.WHITE));
        assertTrue(ChessGame.verifyGameCheckmate(TEST_FILE_DIR + "checkmateExample6.txt", TeamColor.BLACK));
        assertTrue(ChessGame.verifyGameCheckmate(TEST_FILE_DIR + "checkmateExample7.txt", TeamColor.BLACK));
        assertTrue(ChessGame.verifyGameCheckmate(TEST_FILE_DIR + "checkmateExample8.txt", TeamColor.BLACK));
        assertTrue(ChessGame.verifyGameCheckmate(TEST_FILE_DIR + "checkmateExample9.txt", TeamColor.WHITE));

        assertFalse(ChessGame.verifyGameCheckmate(TEST_FILE_DIR + "notCheckmateExample1.txt", TeamColor.WHITE));
        assertFalse(ChessGame.verifyGameCheckmate(TEST_FILE_DIR + "notCheckmateExample2.txt", TeamColor.BLACK));
        assertFalse(ChessGame.verifyGameCheckmate(TEST_FILE_DIR + "notCheckmateExample3.txt", TeamColor.BLACK));
        assertFalse(ChessGame.verifyGameCheckmate(TEST_FILE_DIR + "notCheckmateExample4.txt", TeamColor.BLACK));
        assertFalse(ChessGame.verifyGameCheckmate(TEST_FILE_DIR + "notCheckmateExample5.txt", TeamColor.WHITE));
        assertFalse(ChessGame.verifyGameCheckmate(TEST_FILE_DIR + "notCheckmateExample6.txt", TeamColor.WHITE));
    }

    @Test
    public void testStalemate() {
        assertTrue(ChessGame.verifyGameStalemate(TEST_FILE_DIR + "stalemateExample1.txt", TeamColor.BLACK));
        assertTrue(ChessGame.verifyGameStalemate(TEST_FILE_DIR + "stalemateExample2.txt", TeamColor.BLACK));
        assertTrue(ChessGame.verifyGameStalemate(TEST_FILE_DIR + "stalemateExample3.txt", TeamColor.BLACK));
        assertTrue(ChessGame.verifyGameStalemate(TEST_FILE_DIR + "stalemateExample4.txt", TeamColor.BLACK));
        assertTrue(ChessGame.verifyGameStalemate(TEST_FILE_DIR + "stalemateExample5.txt", TeamColor.WHITE));
        assertTrue(ChessGame.verifyGameStalemate(TEST_FILE_DIR + "stalemateExample6.txt", TeamColor.BLACK));
        assertTrue(ChessGame.verifyGameStalemate(TEST_FILE_DIR + "stalemateExample7.txt", TeamColor.BLACK));
        assertTrue(ChessGame.verifyGameStalemate(TEST_FILE_DIR + "stalemateExample8.txt", TeamColor.BLACK));
        assertTrue(ChessGame.verifyGameStalemate(TEST_FILE_DIR + "stalemateExample9.txt", TeamColor.WHITE));

        assertFalse(ChessGame.verifyGameStalemate(TEST_FILE_DIR + "notStalemateExample1.txt", TeamColor.BLACK));
        assertFalse(ChessGame.verifyGameStalemate(TEST_FILE_DIR + "notStalemateExample2.txt", TeamColor.BLACK));
        assertFalse(ChessGame.verifyGameStalemate(TEST_FILE_DIR + "notStalemateExample3.txt", TeamColor.BLACK));
        assertFalse(ChessGame.verifyGameStalemate(TEST_FILE_DIR + "notStalemateExample4.txt", TeamColor.BLACK));
        assertFalse(ChessGame.verifyGameStalemate(TEST_FILE_DIR + "notStalemateExample5.txt", TeamColor.WHITE));
        assertFalse(ChessGame.verifyGameStalemate(TEST_FILE_DIR + "notStalemateExample9.txt", TeamColor.WHITE));
    }

    @Test
    public void testCastling() {
        ChessGame castling1a = new ChessGame(TEST_FILE_DIR + "castlingExample1.txt");
        assertSuccess(castling1a.attemptMove(new BoardCoordinate(7, 4), new BoardCoordinate(7, 2)));
        assertEquals("rb -- -- -- kb -- -- rb\n" + "-- -- -- -- -- -- -- --\n" + "-- -- -- -- -- -- -- --\n" +
                "-- -- -- -- -- -- -- --\n" + "-- -- -- -- -- -- -- --\n" + "-- -- -- -- -- -- -- --\n" +
                "-- -- -- -- -- -- -- --\n" + "-- -- kw rw -- -- -- rw", castling1a.getBoardState());

        assertSuccess(castling1a.attemptMove(new BoardCoordinate(0, 4), new BoardCoordinate(0, 6)));
        assertEquals("rb -- -- -- -- rb kb --\n" + "-- -- -- -- -- -- -- --\n" + "-- -- -- -- -- -- -- --\n" +
                "-- -- -- -- -- -- -- --\n" + "-- -- -- -- -- -- -- --\n" + "-- -- -- -- -- -- -- --\n" +
                "-- -- -- -- -- -- -- --\n" + "-- -- kw rw -- -- -- rw", castling1a.getBoardState());

        // Make sure that castling cannot occur after the king has moved.
        ChessGame castling1b = new ChessGame(TEST_FILE_DIR + "castlingExample1.txt");
        assertSuccess(castling1b.attemptMove(new BoardCoordinate(7, 4), new BoardCoordinate(7, 5)));
        assertSuccess(castling1b.attemptMove(new BoardCoordinate(0, 4), new BoardCoordinate(0, 3)));
        assertSuccess(castling1b.attemptMove(new BoardCoordinate(7, 5), new BoardCoordinate(7, 4)));
        assertSuccess(castling1b.attemptMove(new BoardCoordinate(0, 3), new BoardCoordinate(0, 4)));
        assertFailure(castling1b.attemptMove(new BoardCoordinate(7, 4), new BoardCoordinate(7, 6)));
        assertFailure(castling1b.attemptMove(new BoardCoordinate(0, 4), new BoardCoordinate(0, 2)));

        // Make sure that castling cannot occur after the rook has moved.
        ChessGame castling1c = new ChessGame(TEST_FILE_DIR + "castlingExample1.txt");
        assertSuccess(castling1c.attemptMove(new BoardCoordinate(7, 0), new BoardCoordinate(7, 1)));
        assertSuccess(castling1c.attemptMove(new BoardCoordinate(0, 7), new BoardCoordinate(0, 6)));
        assertSuccess(castling1c.attemptMove(new BoardCoordinate(7, 1), new BoardCoordinate(7, 0)));
        assertSuccess(castling1c.attemptMove(new BoardCoordinate(0, 6), new BoardCoordinate(0, 7)));
        assertFailure(castling1c.attemptMove(new BoardCoordinate(7, 4), new BoardCoordinate(7, 2)));
        assertFailure(castling1c.attemptMove(new BoardCoordinate(0, 4), new BoardCoordinate(0, 6)));
        assertSuccess(castling1c.attemptMove(new BoardCoordinate(7, 4), new BoardCoordinate(7, 6)));
        assertSuccess(castling1c.attemptMove(new BoardCoordinate(0, 4), new BoardCoordinate(0, 2)));
        assertEquals("-- -- kb rb -- -- -- rb\n" + "-- -- -- -- -- -- -- --\n" + "-- -- -- -- -- -- -- --\n" +
                "-- -- -- -- -- -- -- --\n" + "-- -- -- -- -- -- -- --\n" + "-- -- -- -- -- -- -- --\n" +
                "-- -- -- -- -- -- -- --\n" + "rw -- -- -- -- rw kw --", castling1c.getBoardState());

        // Make sure that castling cannot occur if there is a piece between the king and the rook.
        ChessGame castling2 = new ChessGame(TEST_FILE_DIR + "castlingExample2.txt");
        assertFailure(castling2.attemptMove(new BoardCoordinate(7, 4), new BoardCoordinate(7, 2)));
        assertFailure(castling2.attemptMove(new BoardCoordinate(0, 4), new BoardCoordinate(0, 6)));

        // Make sure that castling cannot occur if the king is in check.
        ChessGame castling3 = new ChessGame(TEST_FILE_DIR + "castlingExample3.txt");
        assertCheck(castling3.attemptMove(new BoardCoordinate(4, 2), new BoardCoordinate(3, 1)));
        assertFailure(castling3.attemptMove(new BoardCoordinate(0, 4), new BoardCoordinate(0, 6)));
        assertFailure(castling3.attemptMove(new BoardCoordinate(0, 4), new BoardCoordinate(0, 2)));

        // Make sure that castling cannot occur if it passes through a check.
        ChessGame castling4 = new ChessGame(TEST_FILE_DIR + "castlingExample4.txt", TeamColor.BLACK);
        assertFailure(castling4.attemptMove(new BoardCoordinate(0, 4), new BoardCoordinate(0, 6)));
        assertFailure(castling4.attemptMove(new BoardCoordinate(0, 4), new BoardCoordinate(0, 2)));

        // Make sure that castling cannot occur if the king will be in check after the castling.
        ChessGame castling5 = new ChessGame(TEST_FILE_DIR + "castlingExample5.txt");
        assertFailure(castling5.attemptMove(new BoardCoordinate(7, 4), new BoardCoordinate(7, 6)));
        assertSuccess(castling5.attemptMove(new BoardCoordinate(7, 4), new BoardCoordinate(7, 2)));
        assertEquals("-- -- -- -- kb -- -- --\n" + "-- -- -- -- -- -- qb --\n" + "-- -- -- -- -- -- -- --\n" +
                "-- -- -- -- -- -- -- --\n" + "-- -- -- -- -- -- -- --\n" + "-- -- -- -- -- -- -- --\n" +
                "-- -- -- -- -- -- -- --\n" + "-- -- kw rw -- -- -- rw", castling5.getBoardState());
    }

    @Test
    public void testGetPossibleMoves() {
        ChessGame standard = new ChessGame();
        Set<Move> expected = Set.of(
                new Move(new BoardCoordinate(6, 0), new BoardCoordinate(5, 0)),
                new Move(new BoardCoordinate(6, 0), new BoardCoordinate(4, 0)),
                new Move(new BoardCoordinate(6, 1), new BoardCoordinate(5, 1)),
                new Move(new BoardCoordinate(6, 1), new BoardCoordinate(4, 1)),
                new Move(new BoardCoordinate(6, 2), new BoardCoordinate(5, 2)),
                new Move(new BoardCoordinate(6, 2), new BoardCoordinate(4, 2)),
                new Move(new BoardCoordinate(6, 3), new BoardCoordinate(5, 3)),
                new Move(new BoardCoordinate(6, 3), new BoardCoordinate(4, 3)),
                new Move(new BoardCoordinate(6, 4), new BoardCoordinate(5, 4)),
                new Move(new BoardCoordinate(6, 4), new BoardCoordinate(4, 4)),
                new Move(new BoardCoordinate(6, 5), new BoardCoordinate(5, 5)),
                new Move(new BoardCoordinate(6, 5), new BoardCoordinate(4, 5)),
                new Move(new BoardCoordinate(6, 6), new BoardCoordinate(5, 6)),
                new Move(new BoardCoordinate(6, 6), new BoardCoordinate(4, 6)),
                new Move(new BoardCoordinate(6, 7), new BoardCoordinate(5, 7)),
                new Move(new BoardCoordinate(6, 7), new BoardCoordinate(4, 7)),
                new Move(new BoardCoordinate(7, 1), new BoardCoordinate(5, 0)),
                new Move(new BoardCoordinate(7, 1), new BoardCoordinate(5, 2)),
                new Move(new BoardCoordinate(7, 6), new BoardCoordinate(5, 5)),
                new Move(new BoardCoordinate(7, 6), new BoardCoordinate(5, 7))
        );

        assertEquals(expected, standard.getPossibleMoves());

        ChessGame almostCheck = new ChessGame(TEST_FILE_DIR + "notCheckmateExample2.txt", TeamColor.BLACK);
        Set<Move> expected2 = Set.of(
                new Move(new BoardCoordinate(0, 7), new BoardCoordinate(1, 7))
        );

        assertEquals(expected2, almostCheck.getPossibleMoves());
        assertSuccess(almostCheck.attemptMove(new BoardCoordinate(0, 7), new BoardCoordinate(1, 7)));

        Set<Move> expected3 = Set.of(
                new Move(new BoardCoordinate(1, 3), new BoardCoordinate(0, 3)),
                new Move(new BoardCoordinate(1, 3), new BoardCoordinate(2, 3)),
                new Move(new BoardCoordinate(1, 3), new BoardCoordinate(3, 3)),
                new Move(new BoardCoordinate(1, 3), new BoardCoordinate(4, 3)),
                new Move(new BoardCoordinate(1, 3), new BoardCoordinate(5, 3)),
                new Move(new BoardCoordinate(1, 3), new BoardCoordinate(6, 3)),
                new Move(new BoardCoordinate(1, 3), new BoardCoordinate(7, 3)),
                new Move(new BoardCoordinate(1, 3), new BoardCoordinate(1, 2)),
                new Move(new BoardCoordinate(1, 4), new BoardCoordinate(0, 4)),
                new Move(new BoardCoordinate(6, 4), new BoardCoordinate(5, 4)),
                new Move(new BoardCoordinate(6, 4), new BoardCoordinate(4, 4)),
                new Move(new BoardCoordinate(1, 5), new BoardCoordinate(0, 7)),
                new Move(new BoardCoordinate(1, 5), new BoardCoordinate(0, 3)),
                new Move(new BoardCoordinate(1, 5), new BoardCoordinate(2, 7)),
                new Move(new BoardCoordinate(1, 5), new BoardCoordinate(3, 6)),
                new Move(new BoardCoordinate(1, 5), new BoardCoordinate(3, 4)),
                new Move(new BoardCoordinate(1, 5), new BoardCoordinate(2, 3)),
                new Move(new BoardCoordinate(6, 5), new BoardCoordinate(5, 5)),
                new Move(new BoardCoordinate(6, 5), new BoardCoordinate(4, 5)),
                new Move(new BoardCoordinate(7, 6), new BoardCoordinate(7, 5)),
                new Move(new BoardCoordinate(7, 6), new BoardCoordinate(6, 6)),
                new Move(new BoardCoordinate(7, 6), new BoardCoordinate(7, 7)),
                new Move(new BoardCoordinate(6, 7), new BoardCoordinate(5, 7)),
                new Move(new BoardCoordinate(6, 7), new BoardCoordinate(4, 7))
        );

        assertEquals(expected3, almostCheck.getPossibleMoves());
    }
    
    private static void assertSuccess(ChessGame.MoveOutcome outcome) {
        assertEquals(ChessGame.MoveOutcome.SUCCESS, outcome);
    }

    private static void assertCheck(ChessGame.MoveOutcome outcome) {
        assertEquals(ChessGame.MoveOutcome.CHECK, outcome);
    }
    
    private static void assertFailure(ChessGame.MoveOutcome outcome) {
        assertEquals(ChessGame.MoveOutcome.FAILURE, outcome);
    }

    private static void assertPawnPromotion(ChessGame.MoveOutcome outcome) {
        assertEquals(ChessGame.MoveOutcome.PAWN_PROMOTION, outcome);
    }

    private static void assertCheckmate(ChessGame.MoveOutcome outcome) {
        assertEquals(ChessGame.MoveOutcome.CHECKMATE, outcome);
    }
}
