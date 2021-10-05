import java.util.ArrayList;

public class MySnake extends Snake implements SnakeTactics{
    public static int[] nApple, pApple;

    ArrayList<int[]> snakeSegments;

    int[] nextTail = {-1, -1};

    public MySnake(){
        nApple = GlobalMembers.nApple;
        pApple = GlobalMembers.pApple;
    }

    /**
     * Updates mySnake stats and snakeSegments
     * @param dex
     * @param snakeInfo
     */
    @Override
    public void updateSnake(int dex, String[] snakeInfo) {
        snakeDex = dex;

        /// 'reset' snake
        if (status == -1)
        {
//            updateSnakePosition(5, currSnakeInfo, false); // remove snake from board
            GlobalMembers.invisibleDex = snakeDex;
        }
        else if (status == 1)
        {
//            updateSnakePosition(3, currSnakeInfo, false); // remove snake from board
        }

        currSnakeInfo = snakeInfo;
        setStatus(snakeInfo[0]);

        switch (status)
        {
            case 0:
                return; //nothing to do

            case 1: {
                updateLength(Integer.parseInt(snakeInfo[1]));
                kills = Integer.parseInt(snakeInfo[2]);

                formSegments(3, snakeInfo);
//                updateSnakePosition(3, snakeInfo, true);//
                break;
            }

            case -1: {
                updateLength(Integer.parseInt(snakeInfo[1]));
                kills = Integer.parseInt(snakeInfo[2]);
                invisibleSteps = Integer.parseInt(snakeInfo[3]);

                GlobalMembers.invisibleDex = snakeDex;

                formSegments(5, snakeInfo);
//                updateSnakePosition(5, snakeInfo, true);//
                break;
            }
        }


    }

    /**
     * Makes an array of all positions the snake occupies, used for path finding
     * @param startDex
     * @param snakeInfo
     */
    private void formSegments(int startDex, String[] snakeInfo) {
        int adj,
                oldX, oldY,
                newX, newY;
        String[] segSplit;

        // You died
        if (head[0] != -1 && manhattanDistance(head, getArray(snakeInfo[startDex].split(","))) > 1)
        {
            System.err.println("========== HERE'S WHY YOU DIED ================");
            System.err.println("SnakeHead: " + head[0] + "\t" + head[1]);
            System.err.println("SnakeTail: " + tail[0] + "\t" + tail[1]);
            System.err.print("SnakeSegments: ");printArray(snakeSegments);
            System.err.println("================================");
        }

        snakeSegments = new ArrayList<>();

        segSplit = snakeInfo[startDex].split(",");
        oldX = Integer.parseInt(segSplit[0]);
        oldY = Integer.parseInt(segSplit[1]);

        for (int dex = startDex + 1; dex < snakeInfo.length; ++dex)
        {
            segSplit = snakeInfo[dex].split(",");
            newX = Integer.parseInt(segSplit[0]);
            newY = Integer.parseInt(segSplit[1]);

            adj = (oldX > newX || oldY > newY) ? -1 : 1;

            for (int xPos = oldX; xPos != newX; xPos += adj)
            {
//                System.err.println("~~~~~~~~~~~ snakeSegments ~~~~~~~~~~~~");
//                System.err.println("size: " + snakeSegments.size());
//                System.err.println("length: " + length);
//                System.err.println("~~~~~~~~~~~ ~~~~~~~~~~~~~~ ~~~~~~~~~~");

                snakeSegments.add(new int[]{xPos, newY});
            }
            for (int yPos = oldY; yPos != newY; yPos += adj)
            {
                snakeSegments.add(new int[]{newX, yPos});
            }

            // don't forget to add the tail into snakeSegs
            if (dex == snakeInfo.length -1)
                snakeSegments.add(new int[]{newX, newY});

            oldX = newX;
            oldY = newY;
        }

        updateCoordinates(head, snakeSegments.get(0));
        updateCoordinates(prevHead, snakeSegments.get(1));
        updateCoordinates(tail, snakeSegments.get(snakeSegments.size() - 1));
        updateCoordinates(nextTail, snakeSegments.get(snakeSegments.size() - 2));

//        System.err.println("**** snakeSegments ****");
//        for (int[] seg : snakeSegments)
//            System.err.println("\t" + seg[0] + "\t" + seg[1]);
//        System.err.println("****  ****");
    }

    /**
     * Determines what move to make
     * @return
     */
    public int makeMove() {
        // TODO HERE... FIGURE OUT WHY BAD DESTINATIONS ARE CALCULATED
// TODO: Be able to make every move a sure one, that there are no compromises required...!!!
        int nextMove = 5; // Should never go straight, so something's wrong if it does
        int[] currApple = pApple[0] == -1 ? nApple : pApple;

        // begin strategy
        long startTime = System.currentTimeMillis();

        if (status == 0)
        {
            return nextMove;
        }

        // already at position or unable to get apple?
        if (isSamePosition(head, currApple) || GlobalMembers.boards.getHeadSpaceBoard()[currApple[0]][currApple[1]])
        {
            nextMove = goForTail();
            System.err.println("-------------------------- Normal tail travel travel " + nextMove + " - -----------------------");
        }
        // invisible snake existing?
        else if (GlobalMembers.invisibleDex != -1)
        {
            if (GlobalMembers.invisibleDex != snakeDex)
            {
                nextMove = goForTail();
                System.err.println("-------------------------- Invisible tail travel " + nextMove + " - -----------------------");
            }
            else
            {
                nextMove = goForApple(false);
                System.err.println("-------------------------- Invisible apple chase " + nextMove + " - -----------------------");
            }
        }
        // normal game of chase...
        else
        {
            nextMove = goForApple(true);
            System.err.println("-------------------------- Normal apple chase " + nextMove + " -----------------------");
        }

        if (nextMove == 5)
        {
            System.err.println("-------------------------- No possible move -----------------------");
            // probably enabling the headspace check caused the snake to not know what to do
            Cell.adj = -1;
            nextMove = goForTail();
            Cell.adj = 1;
            System.err.println("CORRECTION MOVE: " + nextMove);
        }
        long endTime = System.currentTimeMillis();
        long length = endTime-startTime;
        System.err.println("-------------------------- " + (length) + " ms -----------------------");

        return nextMove;
    }

    /**
     * Snake attempts to go for the best apple, or next best if not possible
     * @param chickenOut : indicates when the snake should not bother going for an impossible apple, unless invisible for disruption
     * @return
     */
    private int goForApple(boolean chickenOut) {
        int myDistance, compDistance;
        int[] closestCompetitionHead;

        int nextMove = 5;
        int[] currApple = pApple[0] == -1 ? nApple : pApple;

        closestCompetitionHead = getClosestSnake(snakeDex, currApple);

        myDistance = manhattanDistance(head, currApple);
        compDistance = manhattanDistance(closestCompetitionHead, currApple);

        // try going for priority apple, if possible
        if (myDistance < compDistance)
        {
            nextMove = goTo(currApple, false);
        }
        // don't go for it if competition is close, but don't chickenOut if invisible
        else if (!chickenOut || compDistance > 15)
            nextMove = goTo(currApple, true);
        // if we were attempting to go to a power apple, try going for the normal one
        else if (pApple[0] != -1)
        {
            closestCompetitionHead = getClosestSnake(snakeDex, nApple);

            compDistance = manhattanDistance(closestCompetitionHead, nApple);

            if (compDistance > 15)
                nextMove = goTo(nApple, true);
        }

        return nextMove;
    }

    /**
     * Simple go-to location, checks if there is any collision with a headspace area
     * @param destination
     * @return
     */
    private int goTo(int[] destination, boolean bScenicRoute) {
        int nextMove;

        int surroundCount = 0;
        int[] tempPos = new int[2];
        boolean[][] headSpaceBoard = GlobalMembers.boards.getHeadSpaceBoard();

        System.err.println("+++++++++++++++++++++++");
        System.err.print("headStart: ");
        printPos(head);
        System.err.print("destination: ");
        printPos(destination);
        System.err.println("+++++++++++++++++++++++");

        for (int dex = 0; dex < 4; ++ dex)
        {
            addCoordinates(tempPos, head, moveSpace[dex]);

            if (!outOfBounds(tempPos) && headSpaceBoard[tempPos[0]][tempPos[1]])
            {
                ++surroundCount;
            }
        }

        // if surrounded or unable to move because of headspace position
        if (surroundCount >= 3 || headSpaceBoard[head[0]][head[1]])
        {
            nextMove = aStar(head, destination, snakeSegments, bScenicRoute);
        }
        else
        {
            nextMove = aStar(head, destination, snakeSegments, bScenicRoute);
        }

        return nextMove;
    }

    /**
     * Determines which part near its tail it should go to
     * @return
     */
    // TODO: GO TO BACK CORNER OF TAIL, NOT INNER CORNERchoose best side of tail to wrap around
    private int goForTail() {
        int scale, nextMove = 5;

        int[] tempPos;
        ArrayList<int[]> tailMoves = new ArrayList<>();

        boolean[][] headSpaceBoard = GlobalMembers.boards.getHeadSpaceBoard(),
                mainBoard = GlobalMembers.boards.getMainBoard();

        // find 4 positions (left/right/up/down/diagonals) around snakeTail that isn't occupied
        //for (int spaces = startSpace; spaces < endSpace; ++spaces)
        for (int spaces = 4; spaces < moveSpace.length; ++spaces)
        {
            tempPos = new int[2];

            scale = 0;

            // while not out of bounds and the space is occupied, increase the 'scale' distance from cornerTail, and that there is no path to the position
            do{
                ++scale;

                tempPos[0] = tail[0] + scale * moveSpace[spaces][0];
                tempPos[1] = tail[1] + scale * moveSpace[spaces][1];

            }while (!outOfBounds(tempPos) && (isSamePosition(head, tempPos) || mainBoard[tempPos[0]][tempPos[1]] ||
                    headSpaceBoard[tempPos[0]][tempPos[1]] || bodyCollision(tempPos) || aStar(head, tempPos, snakeSegments, false) == 5));

            if (!outOfBounds(tempPos))
            {
                tailMoves.add(tempPos);
            }
        }

        tempPos = getClosest_FurthestPos(tailMoves, head, false);

        if (tempPos != null)
            nextMove = goTo(tempPos, false);

        return nextMove;
    }

    /**
     * Returns if the snake will collide with itself
     * @param pos
     * @return
     */
    private boolean bodyCollision(int[] pos)
    {
        boolean bCollision = false;

        for (int[] tempPos : snakeSegments)
        {
            if (isSamePosition(pos, tempPos))
            {
                bCollision = true;
                break;
            }
        }

        return bCollision;
    }
}
