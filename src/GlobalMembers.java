public class GlobalMembers implements Coordinates{
    public static int collisionCheckCount = 0;
    public static boolean dieded = false;

    public static int numSnakes, width, height, mode;
    public static Board boards;
    public static Snake[] snakes;
    public static MySnake mySnake;

    public static Cell[][] nodes, safeCheckNodes;
    public static boolean[][] explored, closed, safeExplored, safeClosed;

    public static int invisibleDex = -1;
    public static int[] nApple = new int[2],
            pApple = new int[2],
            apple = new int[2];

    public GlobalMembers(String[] initSplit) {
        numSnakes = Integer.parseInt(initSplit[0]);
        width = Integer.parseInt(initSplit[1]);
        height = Integer.parseInt(initSplit[2]);
        mode = Integer.parseInt(initSplit[3]);

        boards = new Board(width, height);
        snakes = new Snake[numSnakes];

        explored = new boolean[width][height];
        closed = new boolean[width][height];
        nodes = new Cell[width][height];

        safeExplored = new boolean[width][height];
        safeClosed = new boolean[width][height];
        safeCheckNodes = new Cell[width][height];

        for (int x = 0; x < width; ++x)
            for (int y = 0; y < height; ++y)
            {
                nodes[x][y] = new Cell();
                safeCheckNodes[x][y] = new Cell();
            }

        //MySnake.snakeTactics = new SnakeTactics(width, height);
    }

}
