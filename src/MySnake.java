import java.util.ArrayList;

public class MySnake extends Snake implements SnakeTactics{
    public static int[] nApple, pApple;

    int circleAppleCount = 0;
    ArrayList<int[]> snakeSegments;

    public MySnake(){
        nApple = globalMembers.nApple;
        pApple = globalMembers.pApple;
    }

    /**
     * updateSnake: updates mySnake
     * @param dex
     * @param snakeInfo
     */
    @Override
    public void updateSnake(int dex, String[] snakeInfo) {
        snakeDex = dex;

        /// 'reset' snake
        if (status == -1)
        {
            updateSnakePosition(5, currSnakeInfo, false); // remove snake from board
            globalMembers.invisibleDex = snakeDex;
        }
        else if (status == 1)
        {
            updateSnakePosition(3, currSnakeInfo, false); // remove snake from board
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
                updateSnakePosition(3, snakeInfo, true);//
                break;
            }

            case -1: {
                updateLength(Integer.parseInt(snakeInfo[1]));
                kills = Integer.parseInt(snakeInfo[2]);
                invisibleSteps = Integer.parseInt(snakeInfo[3]);

                globalMembers.invisibleDex = snakeDex;

                formSegments(5, snakeInfo);
                updateSnakePosition(5, snakeInfo, true);//
                break;

                // TODO: CONSIDER surrounding an apple
            }
        }


    }

    /**
     * formSegments: Makes an array of all positions the snake occupies
     * @param startDex
     * @param snakeInfo
     */
    private void formSegments(int startDex, String[] snakeInfo) {
        int adj,
                oldX, oldY,
                newX, newY;
        String[] segSplit;

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
                //
                System.err.println("~~~~~~~~~~~ snakeSegments ~~~~~~~~~~~~");
                System.err.println("size: " + snakeSegments.size());
                System.err.println("length: " + length);
                System.err.println("~~~~~~~~~~~ ~~~~~~~~~~~~~~ ~~~~~~~~~~");

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
        updateCoordinates(tail, snakeSegments.get(snakeSegments.size() - 1));

        System.err.println("**** snakeSegments ****");
        for (int[] seg : snakeSegments)
            System.err.println("\t" + seg[0] + "\t" + seg[1]);
        System.err.println("****  ****");
    }

    @Override
    public int makeMove() {
// TODO: Be able to make every move a sure one, that there are no compromises required...!!!

        int nextMove = 5; // Should never go straight, so something's wrong if it does
        int[] currApple = pApple[0] == -1 ? nApple : pApple;

        if (status == 0)
            return nextMove;


        // invisible snake existing?
        if (globalMembers.invisibleDex != -1)
        {
            if (globalMembers.invisibleDex != snakeDex)
                nextMove = goForTail();
            else
                nextMove = goForApple();
        }
        // unable to get to apple?
        else if (globalMembers.board.getBoard()[currApple[0]][currApple[1]])
        {
            nextMove = goForTail();
        }
        // normal game of chase...
        else
        {
            nextMove = goForApple();
        }

        if (nextMove == 5)
        {
            nextMove = goForTail();
            System.err.println("CORRECTION MOVE: " + nextMove);
        }

        return nextMove;
    }

    private int goForApple() {
        int myDistance, compDistance;
        int[] closestCompetitionHead;

        int nextMove = 5;
        int[] currApple = pApple[0] == -1 ? nApple : pApple;

        closestCompetitionHead = getClosestSnake(snakeDex, currApple);

        myDistance = manhattanDistance(head, currApple);
        compDistance = manhattanDistance(closestCompetitionHead, currApple);

        // try going for priority apple
        if (myDistance < compDistance)
            nextMove = aStar(head, currApple, snakeSegments);
        else if (myDistance < 0.7 * compDistance && compDistance > 20)
            nextMove = aStar(head, currApple, snakeSegments);
        else if (pApple[0] != -1)
        {
            closestCompetitionHead = getClosestSnake(snakeDex, nApple);

            myDistance = manhattanDistance(head, nApple);
            compDistance = manhattanDistance(closestCompetitionHead, nApple);

            if (myDistance < compDistance || (myDistance < 0.7 * compDistance && compDistance > 20))
                nextMove = aStar(head, currApple, snakeSegments);
        }

        return nextMove;
    }

    private int goTo(int[] destination) {
        return aStar(head, destination, snakeSegments);
    }

    // TODO: HERE... WORK ON TODO BELOW...
    private int goForTail() {
        int scale;
        int[] tempPos;
        ArrayList<int[]> tailCorners = new ArrayList<>();

        int width = GlobalMembers.width, height = GlobalMembers.height;
        int[] currTail = snakeSegments.get(length - 1), preTail = snakeSegments.get(length - 2);
        boolean[][] board = GlobalMembers.board.getBoard();

        // find 4 corners around snakeTail that isn't occupied
        for (int spaces = 4; spaces < moveSpace.length; ++spaces)
        {
            tempPos = new int[2];

            scale = 1;
            tempPos[0] = tail[0] + scale * moveSpace[spaces][0];
            tempPos[1] = tail[1] + scale * moveSpace[spaces][1];

            // while not out of bounds and the space is occupied...
            while (!outOfBounds(tempPos) && (board[tempPos[0]][tempPos[1]] || false)) // TODO: need to check that i'm not closing myself in... that a *simple* astar to head exists?
            {
                ++scale;
                tempPos[0] = tail[0] + scale * moveSpace[spaces][0];
                tempPos[1] = tail[1] + scale * moveSpace[spaces][1];
            }

            if (!outOfBounds(tempPos))
                tailCorners.add(tempPos);
        }

        tempPos = getClosest_FurthestPos(tailCorners, head, true);

        return aStar(head, tempPos, snakeSegments);
    }

    /**
     * updateSnakePosition: Marks off parts on the board occupied by a snake
     * @param startDex
     * @param snakeInfo
     * @param occupied
     */
    private void updateSnakePosition(int startDex, String[] snakeInfo, boolean occupied)
    {
        String[] segSplit;
        int[] oldPos = new int[2],
                newPos = new int[2];

        int size = snakeInfo.length;

        segSplit = snakeInfo[startDex + 1].split(",");
        updateCoordinates(body, segSplit);

        for (int segDex = startDex; segDex < size; ++segDex)
        {
            segSplit = snakeInfo[segDex].split(",");

            // record head and tail positions when necessary
            if (segDex == startDex)
            {
                updateCoordinates(head, segSplit);

                oldPos = head;

                continue;
            }
            else if (segDex == size - 1)
            {
                updateCoordinates(tail, segSplit);
            }

            newPos[0] = Integer.parseInt(segSplit[0]);
            newPos[1] = Integer.parseInt(segSplit[1]);

//            System.err.println("newPos: " + newPos[0] + " " + newPos[1]);
//            System.err.println("oldPos: " + oldPos[0] + " " + oldPos[1]);

            togglePositions(oldPos[0], oldPos[1], newPos[0], newPos[1], occupied);

            oldPos = newPos.clone();

        }

    }

    /**
     * togglePositions: Determines and marks off in-between segments of the board occupied by a snake
     * @param oldX
     * @param oldY
     * @param newX
     * @param newY
     * @param occupied
     */
    private void togglePositions(int oldX, int oldY, int newX, int newY, boolean occupied)
    {
        int adj;

        boolean[][] board = globalMembers.board.getBoard();

        adj = (oldX > newX || oldY > newY) ? -1 : 1;

        for (int xPos = oldX; xPos != newX; xPos += adj)
        {
            board[xPos][newY] = occupied;
        }
        for (int yPos = oldY; yPos != newY; yPos += adj)
        {
            board[newX][yPos] = occupied;
        }

        board[newX][newY] = occupied;
    }
}
