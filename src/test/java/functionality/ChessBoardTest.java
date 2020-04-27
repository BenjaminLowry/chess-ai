package functionality;

import java.util.Iterator;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChessBoardTest {

    @Test
    public void testBoardIterator() {
        ChessBoard board = new ChessBoard("src/main/resources/standardLayout.txt");
        Iterator<ChessPiece> itr = board.iterator();

        assertTrue(itr.hasNext());
        assertTrue(itr.next() instanceof Rook);
        assertTrue(itr.next() instanceof Knight);
        assertTrue(itr.next() instanceof Bishop);
        assertTrue(itr.next() instanceof Queen);
        assertTrue(itr.next() instanceof King);
        assertTrue(itr.next() instanceof Bishop);
        assertTrue(itr.next() instanceof Knight);
        assertTrue(itr.next() instanceof Rook);

        for (int i = 0; i < 8; i++) {
            assertTrue(itr.next() instanceof Pawn);
        }

        for (int i = 0; i < 32; i++) {
            assertTrue(itr.next() == null);
        }

        for (int i = 0; i < 8; i++) {
            assertTrue(itr.next() instanceof Pawn);
        }

        assertTrue(itr.next() instanceof Rook);
        assertTrue(itr.next() instanceof Knight);
        assertTrue(itr.next() instanceof Bishop);
        assertTrue(itr.next() instanceof Queen);
        assertTrue(itr.next() instanceof King);
        assertTrue(itr.next() instanceof Bishop);
        assertTrue(itr.next() instanceof Knight);
        assertTrue(itr.next() instanceof Rook);
        assertFalse(itr.hasNext());
    }


}