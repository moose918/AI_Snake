import java.util.ArrayList;

public class Cell implements Comparable<Cell> {
    int fCost, gCost, hCost, moveDex, currLength;
    int[] pos;

    static int adj = 1;

    Cell parentCell = null;

    public Cell(int g, int h, int l, int mDex, int[] p) {
        gCost = g;
        hCost = h;
        fCost = g + h;

        currLength = l;

        moveDex = mDex;

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
            return adj*(this.hCost - o.hCost);
        else
            return adj*(this.fCost - o.fCost);
    }

    /**
     * update: Sets the cell's heuristic, previous move to get there, position and parentCell
     * @param h
     * @param mDex
     * @param p
     * @param parent
     */
    public void update(int h, int mDex, int l, int[] p, Cell parent) {
        gCost = parent.gCost + 1;
        hCost = h;
        fCost = gCost + h;

        currLength = l;

        moveDex = mDex;

        // update the position
        pos = p.clone();

        parentCell = parent;
    }

    public void updateSafe(int h, int l, int[] p, Cell parent) {
        gCost = parent.gCost + 1;
        hCost = h;
        fCost = gCost + h;

        currLength = l;

        // update the position
        pos = p.clone();

        parentCell = parent;
    }

    public ArrayList<int[]> getCurrentPath()
    {
        Cell tempCell = this;
        ArrayList<int[]> currPath = new ArrayList<>();

        while (tempCell.parentCell != null)
        {
            currPath.add(tempCell.pos);
            tempCell = tempCell.parentCell;
        }

        return currPath;
    }

    /**
     * Prepares the Cell for the next path find
     */
    public void reset() {
        fCost = 0;
        gCost = 0;
        hCost = 0;
        moveDex = 0;
        currLength = 0;

        pos = null;

        parentCell = null;
    }

    /**
     * Debugging...
     */
    public void print() {
        System.err.println("\n~~~~~~~~~~~~~~");
        System.err.println("Cell @ pos: " + pos[0] + " " + pos[1]);
        System.err.println("Parent @ pos: " + (parentCell == null ? "null" : parentCell.pos[0] + " " + parentCell.pos[1]));
        System.err.println("moveDex: " + moveDex);
        System.err.println("length: " + currLength);
        System.err.println("fCost: " + fCost);
        System.err.println("gCost: " + gCost);
        System.err.println("hCost: " + hCost);
        System.err.println("\n~~~~~~~~~~~~~~");
    }

    public void clearPath() {
        if (parentCell.parentCell != null)
            parentCell.clearPath();

        GlobalMembers.closed[pos[0]][pos[1]] = false;
        GlobalMembers.explored[pos[0]][pos[1]] = false;
        reset();
    }


}