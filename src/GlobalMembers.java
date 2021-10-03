public class GlobalMembers implements Coordinates{
    public static int numSnakes, width, height, mode;
    public static Board board;
    public static Snake[] snakes;
    public static MySnake mySnake;

    public static Cell[][] nodes;
    public static boolean[][] explored, closed;

    public static int invisibleDex = -1;
    public static int[] nApple = new int[2],
            pApple = new int[2],
            apple = new int[2];

    public GlobalMembers(String[] initSplit) {
        numSnakes = Integer.parseInt(initSplit[0]);
        width = Integer.parseInt(initSplit[1]);
        height = Integer.parseInt(initSplit[2]);
        mode = Integer.parseInt(initSplit[3]);

        board = new Board(width, height);
        snakes = new Snake[numSnakes];

        explored = new boolean[width][height];
        closed = new boolean[width][height];
        nodes = new Cell[width][height];

        for (Cell[] cells : nodes)
            for (int dex = 0; dex < cells.length; ++dex)
                cells[dex] = new Cell();

        //MySnake.snakeTactics = new SnakeTactics(width, height);
    }

}
