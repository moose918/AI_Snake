import java.util.ArrayList;

/**
 * Coordinates: Processes coordinate related functions
 */
public interface Coordinates {
    int[][] moveSpace = {
            {0, -1}, {0, 1}, {-1, 0}, {1, 0},
            {-1, -1}, {1, -1}, {-1, 1}, {1, 1}
    };
    int[][] opposites = {
        {0, 1}, {2, 3}
    };

    default int[] getArray(String[] p)
    {
        return new int[]{Integer.parseInt(p[0]), Integer.parseInt(p[1])};
    }

    /**
     * Updates co-ordinates with given split string
     * @param pos
     * @param p
     */
    default void updateCoordinates(int[] pos, String[] p)
    {
        pos[0] = Integer.parseInt(p[0]);
        pos[1] = Integer.parseInt(p[1]);
    }

    /**
     * Updates co-ordinates with a given int array
     * @param pos
     * @param p
     */
    default void updateCoordinates(int[] pos, int[] p)
    {
        pos[0] = p[0];
        pos[1] = p[1];
    }

    /**
     * Adds the two co-ordinates to sum
     * @param sum
     * @param a
     * @param b
     */
    default void addCoordinates(int[] sum, int[] a, int[] b)
    {
        sum[0] = a[0] + b[0];
        sum[1] = a[1] + b[1];
    }

    /**
     * Calculates the manhattan distance
     * @param a
     * @param b
     * @return
     */
    default int manhattanDistance(int[] a, int[] b)
    {
        return Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]);
    }

    /**
     * Determines the direction towards destination
     * @param destPos
     * @param srcPos
     * @return
     */
    default int determineDirection(int[] destPos, int[] srcPos){
        int d = -1;
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
        {
            d = 5; // IMPOSSIBLE
            System.err.println("xDiff: " + xDiff);
            System.err.println("yDiff: " + yDiff);
        }

        return d;
    }

    /**
     * Indicates if two directions are opposite each other, checks if they're not the same direction, that they are
     * both found in separate containers (bFoundA and bFoundB)
     * @param a
     * @param b
     * @return
     */
    default boolean isOppositeDirection(int a, int b)
    {
        boolean bFoundA = false, bFoundB = false;

        if (opposites[0][0] == a || opposites[0][0] == b ||
                opposites[0][1] == a || opposites[0][1] == b)
            bFoundA = true;
        if (opposites[1][0] == a || opposites[1][0] == b ||
                opposites[1][1] == a || opposites[1][1] == b)
            bFoundB = true;

        return a != b && (!bFoundA || !bFoundB);
    }

    default int getOppositeDirection(int d)
    {
        int oppDirection = 5;

        switch (d)
        {
            case 0 : {
                oppDirection = 1;
                break;
            }
            case 1 :{
                oppDirection = 0;
                break;
            }
            case  2:{
                oppDirection = 3;
                break;
            }
            case  3:{
                oppDirection = 2;
                break;
            }

        }

        return oppDirection;
    }

    /**
     * Indicates if a position is out of bounds
     * @param pos
     * @return
     */
    default boolean outOfBounds(int[] pos)
    {
        return pos[0] < 0 || pos[0] >= GlobalMembers.width || pos[1] < 0 || pos[1] >= GlobalMembers.height;
    }

    /**
     * Indicates if two int arrays are the same position
     * @param a
     * @param b
     * @return
     */
    default boolean isSamePosition(int[] a, int[] b)
    {
        return a[0] == b[0] && a[1] == b[1];
    }

    default void printPos(int[] pos)
    {
        System.err.println(pos[0] + " " + pos[1]);
    }
    default void printArray(ArrayList<int[]> positions)
    {
        for (int[] pos : positions)
        {
            System.err.print("{" + pos[0] +" , "+pos[1] +"}\t");
        }
        System.err.println();
    }
}
