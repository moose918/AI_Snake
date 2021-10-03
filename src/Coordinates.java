/**
 * Coordinates: Processes coordinate related functions
 */
public interface Coordinates {
    int[][] moveSpace = {
            {0, -1}, {0, 1}, {-1, 0}, {1, 0},
            {-1, -1}, {1, -1}, {-1, 1}, {1, 1}
    };

    default int[] getArray(String[] p)
    {
        return new int[]{Integer.parseInt(p[0]), Integer.parseInt(p[1])};
    }
    default void updateCoordinates(int[] pos, String[] p)
    {
        pos[0] = Integer.parseInt(p[0]);
        pos[1] = Integer.parseInt(p[1]);
    }
    default void updateCoordinates(int[] pos, int[] p)
    {
        pos[0] = p[0];
        pos[1] = p[1];
    }
    default int manhattanDistance(int[] a, int[] b)
    {
        return Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]);
    }
    default int determineDirection(int[] destPos, int[] srcPos){
        int d = 0;
        int xDiff = destPos[0] - srcPos[0],
                yDiff = destPos[1] - srcPos[1];

        // UP!!
        if ( yDiff < 0)
            d = 0;
        else if ( yDiff > 0) // DOWN
            d = 1;
        else if ( xDiff < 0) // LEFT
            d = 2;
        else if ( xDiff > 0) // RIGHT
            d = 3;
        else
            d = 5; // IMPOSSIBLE

        return d;
    }
    default boolean isOppositeDirection(int a, int b)
    {
        return Math.abs(a - b) == 1 && ( a % 2 == 0 && b % 2 != 0 ||  a % 2 != 0 && b % 2 == 0);
    }
    default boolean outOfBounds(int[] pos)
    {
        return pos[0] < 0 || pos[0] >= GlobalMembers.width || pos[1] < 0 || pos[1] >= GlobalMembers.height;
    }
}
