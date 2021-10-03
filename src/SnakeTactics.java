import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

public interface SnakeTactics extends Coordinates {

    /**
     * getClosestSnake: finds the closest snake (not ours) to a given point
     * @param mySnakeDex
     * @param destination
     * @return
     */
    default int[] getClosestSnake(int mySnakeDex, int[] destination)
    {
        Snake s;
        int distance = -1;
        int[] closestSnakeHead = new int[2];

        for (int snakeDex = 0; snakeDex < GlobalMembers.numSnakes; ++snakeDex)
        {
            if (snakeDex == mySnakeDex)
                continue;

            s = GlobalMembers.snakes[snakeDex];

            if (distance == -1 || manhattanDistance(s.head, destination) < distance);
                closestSnakeHead = s.head;

        }

        return closestSnakeHead.clone();
    }

    /**
     * getClosestPos: returns the closest position to the snakeHead
     */
    default int[] getClosest_FurthestPos(ArrayList<int[]> positions, int[] start_endPos, boolean bNearest)
    {
        int distance;
        int[] pos;

        int priorityDistance = -1;
        int[] nearest_furthestPos = {-1, -1};

        for (int dex = 0; dex < positions.size(); ++dex)
        {
            pos = positions.get(dex);

            distance = manhattanDistance(pos, start_endPos);

            if (bNearest)
            {
                if (priorityDistance == -1 || distance < priorityDistance)
                {
                    priorityDistance = distance;
                    nearest_furthestPos = pos;
                }
            }
            else
            {
                if (priorityDistance == -1 || distance > priorityDistance);
                {
                    priorityDistance = distance;
                    nearest_furthestPos = pos;
                }
            }
        }


        return nearest_furthestPos;
    }

    /**
     * reset: Refreshes the traversing board once the A* is complete
     */
    default void reset() {
        int w = GlobalMembers.width, h = GlobalMembers.height;
        GlobalMembers.explored = new boolean[w][h];
        GlobalMembers.closed = new boolean[w][h];
    }


    // TODO: FIX HERE...
    default int aStar(int[] start, int[] goal, ArrayList<int[]> mySnakeSegments)
    {
        int nextMove = 5, newGCost, movePosDex;
        int[] parentPos;
        boolean bFound = false;
        Cell parentCell, tempCell = null;

        int count = 0;
        int[] tempPos = {-1, -1};
        int[][] movePos = moveSpace;
        boolean[][] board = GlobalMembers.board.getBoard();
        PriorityBlockingQueue<Cell> cellQueue = new PriorityBlockingQueue<>();
        Cell rootCell = new Cell(0, manhattanDistance(start, goal), start);

        //
        System.err.println("\n~~~~~~~~~~~~~~~ A* BEGINING ~~~~~~~~~~~~~~~~~~~~~~~");
        System.err.println("Start: " + start[0] + " " + start[1]);
        System.err.println("Goal: " + goal[0] + " " + goal[1]);


        GlobalMembers.nodes[start[0]][start[1]] = rootCell;
        cellQueue.add(rootCell);

        while (!cellQueue.isEmpty() && !bFound)
        {
            // remove the next definite path...
            parentCell = cellQueue.remove();

//            System.err.println("\nparentCell...");
//            parentCell.print();

            parentPos = parentCell.pos;
            GlobalMembers.closed[parentPos[0]][parentPos[1]] = true;

            for (movePosDex = 0; movePosDex < 4; ++movePosDex)
            {
                tempPos[0] = parentPos[0] + movePos[movePosDex][0];
                tempPos[1] = parentPos[1] + movePos[movePosDex][1];

                // if unexplorable or already part of the final path...
                if (tempPos[0] < 0 || tempPos[0] >= GlobalMembers.width ||
                        tempPos[1] < 0 || tempPos[1] >= GlobalMembers.height ||
                        board[tempPos[0]][tempPos[1]] ||
                        GlobalMembers.closed[tempPos[0]][tempPos[1]])
                {
                    continue;
                }

                // otherwise retrieve the Cell reference
                tempCell = GlobalMembers.nodes[tempPos[0]][tempPos[1]];

                newGCost = parentCell.gCost + 1;

                // check if recently explored
                if (GlobalMembers.explored[tempPos[0]][tempPos[1]])
                {
                    // update this cell if its fPath is better
                    if (newGCost < tempCell.gCost)
                    {
                        cellQueue.remove(tempCell);
                        tempCell.update(manhattanDistance(tempPos, goal), movePosDex, tempPos, parentCell);
                        cellQueue.add(tempCell);
                    }
                }
                // or calculate the new path's costs and add to queue
                else
                {
                    tempCell.update(manhattanDistance(tempPos, goal), movePosDex, tempPos, parentCell);
                    cellQueue.add(tempCell);
                    GlobalMembers.explored[tempPos[0]][tempPos[1]] = true;
                }

                // have we found our goal?
                if (goal[0] == tempPos[0] && goal[1] == tempPos[1])
                {
                    bFound = true;
                    //break; // out of for loop
                }
            }

            ++count;
        }

        System.err.println("Iterations: " + count);

        tempCell = GlobalMembers.nodes[goal[0]][goal[1]];

        if (!cellQueue.isEmpty())
        {
            System.err.println("Root cell move: " + rootCell.moveDex);

            while (tempCell != rootCell)
            {
                nextMove = tempCell.moveDex;
                tempCell = tempCell.parentCell;

                /*System.err.println("cellPath...");
                tempCell.print();*/

            }
        }
        else
        {
            nextMove = 5;
        }


        System.err.println("Next Move: " + nextMove);
        System.err.println("\n~~~~~~~~~~~~~~~ A* COMPLETED~~~~~~~~~~~~~~~~~~~~~~~");


        reset();

        return nextMove;
    }


}
