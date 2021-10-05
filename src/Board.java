public class Board {
    int width, height;
    boolean[][] mainBoard, headSpaceBoard;

    //
    int printCount = 0;

    public Board(){}
    public Board(int width, int height) {
        this.width = width;
        this.height = height;

        mainBoard = new boolean[width][height];
        headSpaceBoard = new boolean[width][height];
    }

    public boolean isOccupied(int x, int y)
    {
        return mainBoard[x][y];
    }

    public boolean[][] getMainBoard() {
        return mainBoard;
    }
    public void setPosition(int x, int y, boolean occupied)
    {
        mainBoard[x][y] = occupied;
    }

    public void print() {
        //if (printCount <= 6)
        {
            for (int yVals = 0; yVals < height; ++yVals )
            {
                for (int xVals = 0; xVals < width; ++xVals)
                {
                    System.err.print("[" + xVals + " " + yVals + "] = " + mainBoard[xVals][yVals] + "\t");
                }
                System.err.println();
            }
        }

        ++printCount;
    }

    public boolean[][] getHeadSpaceBoard() {
        return headSpaceBoard;
    }
}
