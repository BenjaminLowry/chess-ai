package functionality;

/**
 * Enum for the team colors in chess.
 */
public enum TeamColor {
    BLACK, WHITE;

    /**
     * Returns the opposite team color to the color provided.
     * @param c provided color
     * @return if c == BLACK, returns WHITE, and vice versa.
     */
    public static TeamColor oppositeTeam(TeamColor c) {
        return c == BLACK ? WHITE : BLACK;
    }
}
