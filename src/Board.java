public class Board {
    int width, height;
    boolean[][] board;

    //
    int printCount = 0;

    public Board(){}
    public Board(int width, int height) {
        this.width = width;
        this.height = height;

        board = new boolean[width][height];
    }

    public boolean isOccupied(int x, int y)
    {
        return board[x][y];
    }

    public boolean[][] getBoard() {
        return board;
    }
    public void setPosition(int x, int y, boolean occupied)
    {
        board[x][y] = occupied;
    }

    public void print() {
        //if (printCount <= 6)
        {
            for (int yVals = 0; yVals < height; ++yVals )
            {
                for (int xVals = 0; xVals < width; ++xVals)
                {
                    System.err.print("[" + xVals + " " + yVals + "] = " + board[xVals][yVals] + "\t");
                }
                System.err.println();
            }
        }

        ++printCount;
    }
}
