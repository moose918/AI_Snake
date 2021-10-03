import java.util.ArrayList;

public class Cell implements Comparable<Cell> {
    int fCost, gCost, hCost, moveDex;
    int[] pos;

    Cell parentCell = null;
    ArrayList<int[]> addedSnake = new ArrayList<>();

    public Cell(int g, int h, int[] p) {
        gCost = g;
        hCost = h;
        fCost = g + h;

        pos = p;
    }

    public Cell() {}

    /**
     * compareTo: Sort by fCost, hCost in ascending order
     * @param o
     * @return
     */
    @Override
    public int compareTo(Cell o) {
        if (this.fCost == o.fCost)
            return this.hCost - o.hCost;
        else
            return this.fCost - o.fCost;
    }

    /**
     * update: Sets the cell's heuristic, previous move to get there, position and parentCell
     * @param h
     * @param mDex
     * @param p
     * @param parent
     */
    public void update(int h, int mDex, int[] p, Cell parent) {
        gCost = parent.gCost + 1;
        hCost = h;
        fCost = gCost + h;

        moveDex = mDex;
        pos = p.clone();

        addedSnake = parent.addedSnake;
        addedSnake.add(pos);

        parentCell = parent;
    }

    public void print() {
        System.err.println("\n~~~~~~~~~~~~~~");
        System.err.println("Cell @ pos: " + pos[0] + " " + pos[1]);
        System.err.println("Parent @ pos: " + (parentCell == null ? "null" : parentCell.pos[0] + " " + parentCell.pos[1]));
        System.err.println("moveDex: " + moveDex);
        System.err.println("fCost: " + fCost);
        System.err.println("gCost: " + gCost);
        System.err.println("hCost: " + hCost);
        System.err.println("\n~~~~~~~~~~~~~~");
    }
}