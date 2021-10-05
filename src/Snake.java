public class Snake implements Coordinates{
    protected int status;
    protected int length, maxSize, kills; // alive or invisible
    protected int invisibleSteps; // invisible
    protected int snakeDex;
    protected int direction; // resp board

    protected int[] head = {-1, -1}, prevHead = {-1, -1}, tail = {-1, -1};
    protected String[] currSnakeInfo = null;

    public Snake(){}

    /**
     * Resets snake stats and its position on the board
     * @param dex
     * @param snakeInfo
     */
    public void updateSnake(int dex, String[] snakeInfo) {
        snakeDex = dex;

        // 'reset' snake
        if (status == -1)
        {
            GlobalMembers.invisibleDex = snakeDex;
            toggleInvisible(currSnakeInfo, false);
        }
        else if (status == 1)
        {
            updateSnakePosition(3, currSnakeInfo, false); // remove snake from board
            toggleHeadSpace(false);
        }
        setStatus(snakeInfo[0]);

        currSnakeInfo = snakeInfo;
        direction = determineDirection(head, prevHead);

        switch (status)
        {
            case 0: {
                System.err.println("DEAD");
                break;
            }

            case 1: {
                updateLength(Integer.parseInt(snakeInfo[1]));
                kills = Integer.parseInt(snakeInfo[2]);

                updateSnakePosition(3, snakeInfo, true);
                toggleHeadSpace(true);
                break;
            }

            case -1: {
                updateLength(Integer.parseInt(snakeInfo[1]));
                kills = Integer.parseInt(snakeInfo[2]);
                invisibleSteps = Integer.parseInt(snakeInfo[3]);

                GlobalMembers.invisibleDex = snakeDex;

                toggleInvisible(snakeInfo, true);   // update invisibility
                break;
            }
        }
    }

    /**
     * Creates additional boundaries around enemy snake heads to avoid head-on collision
     * @param occupied
     */
    private void toggleHeadSpace(boolean occupied) {
        // TODO: toggle in specific conditions, double if closer to head (~10)
        //

        int[] movePos = new int[2];
        boolean[][] mainBoard = GlobalMembers.boards.getMainBoard(),
                headSpaceBoard = GlobalMembers.boards.getHeadSpaceBoard();

        // mandatory headspace for mainBoard
        for (int moveDex = 0; moveDex < 4; ++moveDex)
        {
            // mark off this spot
            movePos[0] = head[0] + moveSpace[moveDex][0];
            movePos[1] = head[1] + moveSpace[moveDex][0];

            if (outOfBounds(movePos))
            {
                continue;
            }

            mainBoard[movePos[0]][movePos[1]] = occupied;
        }

        // additional headspaces to mark off
        for (int scale = 1; scale <= 2; ++scale)
        {
            // mark off the surrounding area of this diagonal
            for (int moveDex = 0; moveDex < 4; ++moveDex)
            {
                movePos[0] = head[0] + scale*moveSpace[moveDex][0];
                movePos[1] = head[1] + scale*moveSpace[moveDex][1];

                if (outOfBounds(movePos))
                {
                    continue;
                }

                headSpaceBoard[movePos[0]][movePos[1]] = occupied;
            }
        }
    }

    /**
     * Determines the snake's current status
     * @param s
     */
    protected void setStatus(String s) {
        int snakeStatus;
        if (s.equals("alive"))
            snakeStatus = 1;
        else if (s.equals("dead"))
            snakeStatus = 0;
        else
            snakeStatus = -1;

        status = snakeStatus;
    }

    /**
     * Sets the snake's high score
     * @param l
     */
    protected void updateLength(int l) {
        if (l > maxSize)
            maxSize = l;

        length = l;
    }

    /**
     * Marks off parts on the board occupied by a snake
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

        // retrieve coordinates of each segment
        segSplit = snakeInfo[startDex + 1].split(",");
        updateCoordinates(prevHead, segSplit);

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

            // mark the ranges between segments as occupied/unoccupied on the board
            togglePositions(oldPos[0], oldPos[1], newPos[0], newPos[1], occupied);

            oldPos = newPos.clone();

        }

    }

    /**
     * Determines and marks off in-between segments of the board occupied by a snake
     * @param oldX
     * @param oldY
     * @param newX
     * @param newY
     * @param occupied
     */
    private void togglePositions(int oldX, int oldY, int newX, int newY, boolean occupied)
    {
        int adj;

        boolean[][] board = GlobalMembers.boards.getMainBoard();

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

    /**
     * For invisible snakes, update the last known info
     * @param snakeInfo
     * @param occupied
     */
    private void toggleInvisible(String[] snakeInfo, boolean occupied) {
        // we're either enabling/disabling

        // TODO: Accounting for indexing, remove the tail if necessary
        // includes if it is out-of-place

        int size = snakeInfo.length;
        boolean[][] board = GlobalMembers.boards.getMainBoard();

        // TODO: Is the size not given when invisible and > 5?

        if (size > 5)
        {
            // check if the last 2 are the same...
            // nothing we can do... we're blind :`(
            // might as well toggle the last tail
            if (snakeInfo[size - 1].equals(snakeInfo[size - 2]))
            {
                updateCoordinates(tail, snakeInfo[size - 1].split(","));
                board[tail[0]][tail[1]] = occupied;
            }
            else
                updateSnakePosition(5, snakeInfo, occupied);
        }
        // we're disabling the last seen one...
        else
        {
            updateCoordinates(tail, snakeInfo[size - 1].split(","));
            board[tail[0]][tail[1]] = occupied;
        }
    }

}
