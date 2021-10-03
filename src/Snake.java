public abstract class Snake implements Coordinates{
    protected int status;
    protected int length, maxSize, kills; // alive or invisible
    protected int invisibleSteps; // invisible
    protected int snakeDex;
    protected int direction; // resp board

    protected int[] head = {-1, -1}, body = {-1, -1}, tail = {-1, -1};
    protected String[] currSnakeInfo = null;

    protected GlobalMembers globalMembers;

    public Snake(){}

    public abstract int makeMove();

    /**
     * updateSnake: Resets snake stats and its position on the board
     * @param dex
     * @param snakeInfo
     */
    public void updateSnake(int dex, String[] snakeInfo) {
        snakeDex = dex;
        // 'reset' snake
        if (status == -1)
        {
            globalMembers.invisibleDex = snakeDex;
            toggleInvisible(currSnakeInfo, false);
        }
        else if (status == 1)
        {
            updateSnakePosition(3, currSnakeInfo, false); // remove snake from board
            toggleHeadSpace(false);
        }
        setStatus(snakeInfo[0]);

        currSnakeInfo = snakeInfo;
        direction = determineDirection(head, body);

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

                globalMembers.invisibleDex = snakeDex;

                toggleInvisible(snakeInfo, true);   // update invisibility
                break;
            }
        }
    }

    /**
     * toggleHeadSpace: Creates additional boundaries to avoid head-on collision
     */
    private void toggleHeadSpace(boolean occupied) {
        int[] pos;

        int[] tempPos = new int[2];
        boolean[][] board = globalMembers.board.getBoard();

        for (int dex = 0; dex < moveSpace.length; ++dex)
        {
            pos = moveSpace[dex];

            for (int radius = 0; radius < 3; ++radius)
            {
                if (radius > 0 && dex > 3)
                    break;

                tempPos[0] = head[0] + radius * pos[0];
                tempPos[1] = head[1] + radius * pos[1];

                if (tempPos[0] < 0 || tempPos[1] < 0 || tempPos[0] >= globalMembers.width || tempPos[1] >= globalMembers.height)
                    continue;

                board[tempPos[0]][tempPos[1]] = occupied;
            }

        }
    }

    /**
     * setStatus: Determines the snake's current status
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
     * updateLength: Sets the snake's high score
     * @param l
     */
    protected void updateLength(int l) {
        if (l > maxSize)
            maxSize = l;

        length = l;
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

    /**
     * toggleInvisible: For invisible snakes, update the last known info
     * @param snakeInfo
     * @param occupied
     */
    private void toggleInvisible(String[] snakeInfo, boolean occupied) {
        // we're either enabling/disabling

        // TODO: Accounting for indexing, remove the tail if necessary
        // includes if it is out-of-place

        int size = snakeInfo.length;
        boolean[][] board = globalMembers.board.getBoard();

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
