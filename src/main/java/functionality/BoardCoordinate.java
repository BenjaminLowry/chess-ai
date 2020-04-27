package functionality;

import java.util.LinkedList;
import java.util.List;

/**
 * Class representing a coordinate on the chess board in terms of a row and column.
 */
public class BoardCoordinate {
    private static final int LENGTH = 8;

    /**
     * Row of this coordinate, with r = 0 representing the top row of the board
     * and r = 7 representing the bottom row of the board.
     */
    public int r;
    /**
     * Column of this coordinate, with c = 0 representing the left-most column of the board
     * and c = 7 representing the right-most column of the board.
     */
    public int c;

    /**
     * Constructs a BoardCoordinate with a specific row and column.
     * @param row row of coordinate
     * @param col column of coordinate
     */
    public BoardCoordinate(int row, int col) {
        this.r = row;
        this.c = col;
    }

    /**
     * Constructs a BoardCoordinate from a board square index.
     *
     * The board squares are numbered from 0 to 63 going left to right across a row
     * and then down to the next row. For example, index 7 represents row 0 and column 7
     * and index 35 represents row 4 and column 3.
     *
     * @param index board square index
     */
    public BoardCoordinate(int index) {
        this.r = index / LENGTH;
        this.c = index % LENGTH;
    }

    /**
     * Returns the board square index of this coordinate as defined above.
     * @return board square index
     */
    public int toIndex() {
        return r * LENGTH + c;
    }

    /**
     * Return the algebraic square identifier commonly used in chess where
     * rows are numbered 1 - 8 and columns are lettered a - h.
     * @return algebraic square identifier of this board coordinate
     */
    public String toAlgebraic() {
        return ((char) (97 + c)) + String.valueOf(8 - r);
    }

    /**
     * Creates a new BoardCoordinate by adding another BoardCoordinate to this.
     *
     * BoardCoordinates are added by adding their row components and column components with
     * the resulting row and column defining the new BoardCoordinate.
     * @param other other BoardCoordinate to add to this
     * @return summed BoardCoordinate as defined above
     */
    public BoardCoordinate add(BoardCoordinate other) {
        return new BoardCoordinate(r + other.r, c + other.c);
    }

    /**
     * Creates a new BoardCoordinate by subtracting another BoardCoordinate from this.
     *
     * BoardCoordinates are subtracted by subtracting their row components and column components
     * with the resulting row and column defining the new BoardCoordinate.
     * @param other other BoardCoordinate to subtract from this
     * @return BoardCoordinate representing the result of the subtraction
     */
    public BoardCoordinate subtract(BoardCoordinate other) {
        return new BoardCoordinate(r - other.r, c - other.c);
    }

    /**
     * Return a List of all BoardCoordinates on the path between this (inclusive) and other (exclusive).
     * @param other Path destination coordinate
     * @return List containing path. If the path between this and other is not vertical, horizontal or diagonal,
     *         returns an empty List.
     */
    public List<BoardCoordinate> getPathCoordinates(BoardCoordinate other) {
        List<BoardCoordinate> coords = new LinkedList<>();
        coords.add(this);

        int rowDiff = other.r - r;
        int colDiff = other.c - c;

        // If the coordinates do not represent a vertical, horizontal, or diagonal path.
        if (rowDiff != 0 && colDiff != 0 && Math.abs(rowDiff) != Math.abs(colDiff)) {
            return coords;
        }

        int rInc = rowDiff > 0 ? 1 : (rowDiff < 0 ? -1 : 0);
        int cInc = colDiff > 0 ? 1 : (colDiff < 0 ? -1 : 0);

        int ri = this.r + rInc;
        int ci = this.c + cInc;

        while (ri != other.r || ci != other.c) {
            coords.add(new BoardCoordinate(ri, ci));
            ri += rInc;
            ci += cInc;
        }
        return coords;
    }

    /**
     * Returns whether or not another object is equal to this BoardCoordinate.
     *
     * BoardCoordinate equality is defined as two objects both being BoardCoordinate
     * instances and having the same row and column fields.
     *
     * @param o other object
     * @return true if the other object is equal to this, and false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BoardCoordinate)) {
            return false;
        }
        BoardCoordinate other = (BoardCoordinate) o;
        return this.r == other.r && this.c == other.c;
    }

    /**
     * Returns a hash code for this BoardCoordinate.
     * @return hash code
     */
    @Override
    public int hashCode() {
        return 17 * r + 7 * c;
    }
}
