import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;

public interface SnakeTactics extends Coordinates {
    //TODO: HEEEEEREEEE
    // MANDATORY HEADSPACE ,GO LEFT OR RIGHT OF SNAKE IF NOT POSSIBLE
// TODO: MAKE CHICKEN RADIUS SMALLER
    //TODO: INDIIDUAL BFS CHECK FOR CHECKING 80% FILLER
    //TODO:


    /**
     * Finds the closest snake (not ours) to a given point
     * @param mySnakeDex
     * @param destination
     * @return
     */
    default int[] getClosestSnake(int mySnakeDex, int[] destination)
    {
        int distance;
        Snake s;

        int shortestDistance = -1;
        int[] closestSnakeHead = new int[2];

        for (int snakeDex = 0; snakeDex < GlobalMembers.numSnakes; ++snakeDex)
        {
            if (snakeDex == mySnakeDex)
                continue;

            s = GlobalMembers.snakes[snakeDex];
            distance = manhattanDistance(s.head, destination);

            if (shortestDistance == -1 || distance < shortestDistance)
            {
                shortestDistance = distance;
                closestSnakeHead = s.head;
            }

        }

        return closestSnakeHead.clone();
    }

    /**
     * Returns the closest position to the snakeHead
     */
    default int[] getClosest_FurthestPos(ArrayList<int[]> positions, int[] start_endPos, boolean bNearest)
    {
        int distance, nearest_furthestDex = 5;
        int[] tempPos;

        int priorityDistance = -1;

        for (int dex = 0; dex < positions.size(); ++dex)
        {
            tempPos = positions.get(dex);
            distance = manhattanDistance(tempPos, start_endPos);

            if (bNearest)
            {
                if (priorityDistance == -1 || distance < priorityDistance)
                {
                    priorityDistance = distance;
                    nearest_furthestDex = dex;
                }
            } else
            {
                if (priorityDistance == -1 || distance > priorityDistance)
                {
                    priorityDistance = distance;
                    nearest_furthestDex = dex;
                }
            }
        }

        if (priorityDistance == -1)
            return null;
        else
            return positions.get(nearest_furthestDex);
    }

    /**
     * reset: Refreshes the traversing board and cells once the A* is complete
     */
    default void reset() {
        int w = GlobalMembers.width, h = GlobalMembers.height;

        Cell.adj = 1;
        GlobalMembers.explored = new boolean[w][h];
        GlobalMembers.closed = new boolean[w][h];

        for (int x = 0; x < w; ++x)
            for (int y = 0; y < h; ++y)
            {
                GlobalMembers.nodes[x][y].reset();
            }
    }

    default void resetSafeCheck()
    {
        int w = GlobalMembers.width, h = GlobalMembers.height;
        GlobalMembers.safeExplored = new boolean[w][h];
        GlobalMembers.safeClosed = new boolean[w][h];

        for (int x = 0; x < w; ++x)
            for (int y = 0; y < h; ++y)
            {
                GlobalMembers.safeCheckNodes[x][y].reset();
            }
    }


    // TODO: FIX HERE...
    // do the entrypath check, must be greater than 100% ahead of current path, then break, otherwise, reset/clear that path
    default int aStar(int[] start, int[] goal, ArrayList<int[]> mySnakeSegments, boolean bScenicRoute)
    {
        int newGCost, movePosDex, newDirection;
        int[] parentPos;
        boolean bAdditionalPath;
        Cell parentCell, tempCell = null;

        int count = 0, nextMove = 5, oldDirection = GlobalMembers.mySnake.direction,
            snakeLength = mySnakeSegments.size();
        boolean bFound = false;
        int[] tempPos = {-1, -1};
        int[][] movePos = moveSpace;
        boolean[][] mainBoard = GlobalMembers.boards.getMainBoard(),
            headSpaceBoard = GlobalMembers.boards.getHeadSpaceBoard();
        PriorityBlockingQueue<Cell> cellQueue = new PriorityBlockingQueue<>();
        Cell rootCell = new Cell(0, manhattanDistance(start, goal), snakeLength, oldDirection, start);

        Cell.adj = bScenicRoute ? -1 : 1;

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

            parentPos = parentCell.pos;
           // oldDirection = parentCell.moveDex;
            GlobalMembers.closed[parentPos[0]][parentPos[1]] = true;

            for (movePosDex = 0; movePosDex < 4; ++movePosDex)
            {
                tempPos[0] = parentPos[0] + movePos[movePosDex][0];
                tempPos[1] = parentPos[1] + movePos[movePosDex][1];

              //  newDirection = determineDirection(tempPos, parentPos);

                // if unexplorable, already part of the final path, or will eventually cause a collision with the snake's body...
                if (outOfBounds(tempPos) ||
                        mainBoard[tempPos[0]][tempPos[1]] ||
                        //(headSpaceCheck && headSpaceBoard[tempPos[0]][tempPos[1]]) ||
                       // isOppositeDirection(newDirection, oldDirection) ||
                        GlobalMembers.closed[tempPos[0]][tempPos[1]]  ||
                        potentialCollision(tempPos, parentCell, mySnakeSegments) ||
                        isSpaceUnsafe(tempPos, mySnakeSegments))
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
                    if (newGCost < tempCell.gCost && cellQueue.remove(tempCell))
                    {
                        tempCell.update(manhattanDistance(tempPos, goal), movePosDex, snakeLength, tempPos, parentCell);
                        cellQueue.add(tempCell);
                    }
                }
                // or calculate the new path's costs and add to queue
                else
                {
                    tempCell.update(manhattanDistance(tempPos, goal), movePosDex, snakeLength, tempPos, parentCell);
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

        System.err.println("A* Iterations: " + count);

        tempCell = GlobalMembers.nodes[goal[0]][goal[1]];

        if (!cellQueue.isEmpty())
        {
//            System.err.println("Root cell move: " + rootCell.moveDex);

            /*if (reverse)
            {
                while (tempCell.parentCell != rootCell)
                {
                    tempCell = tempCell.parentCell;

//                System.err.println("cellPath...");
//                tempCell.print();
                }

                nextMove = getOppositeDirection(tempCell.moveDex);
            }
            else {*/
                while (tempCell != rootCell)
                {
                    nextMove = tempCell.moveDex;
                    tempCell = tempCell.parentCell;

//                System.err.println("cellPath...");
//                tempCell.print();
                }
//            }
        }
        /*else if (!reverse)
        {
            reset();
            System.err.println("\n!!!!!!!!!!!!!REVERSE!!!!!!!!!!!!!!!!\n");
            nextMove = aStar(goal, start, mySnakeSegments, headSpaceCheck, true);
            System.err.println("\n!!!!!!!!!!!!!REVERSE complete!!!!!!!!!!!!!!!!\n");
        }*/



        System.err.println("Next Move: " + nextMove);
        System.err.println("\n~~~~~~~~~~~~~~~ A* COMPLETED~~~~~~~~~~~~~~~~~~~~~~~");


        reset();

        return nextMove;
    }

    /**
     * Checks if any potential addition to a path causes a collision
     * @param checkPos
     * @param currCell
     * @param oldSegSegments
     * @return
     */
    default boolean potentialCollision(int[] checkPos, Cell currCell, ArrayList<int[]> oldSegSegments)
    {
        int oldSegLength;
        int[] tempPos;
        Cell tempCell;

        boolean bCollision = false;
        int segCount = 0, currLength = currCell.currLength;

        /*System.err.println("||||||||||||||||||||||||COLLISION CHECK ||||||||||||||||||||||||");
        System.err.println("Snake length: " + currLength);
        System.err.println("Position check: " + checkPos[0] + " " + checkPos[1]);
        System.err.print("Added segments: ");*/



        // check if the provisional position collides with any  cell that may be present in that path's current time
        // while we've not collided or traversed far enough into the snake's new segments...
        tempCell = currCell;

        while (!bCollision && segCount < currLength && tempCell != null)
        {
            tempPos = tempCell.pos;

//            System.err.print("{" +tempPos[0] + " , " + tempPos[1] + "}\t");
            if (isSamePosition(tempPos, checkPos))
            {
                bCollision = true;
            }
            else
            {
                ++segCount;
            }

            tempCell = tempCell.parentCell;
        }

        oldSegLength = currLength - segCount;

        /*System.err.println("");
        System.err.print("Old segments: "); printArray(oldSegSegments);*/

        // similar to previous case, only check old segments, if necessary
        if (!bCollision && oldSegLength > 0)
        {
            for (int segDex = 1; !bCollision && segDex <= oldSegLength; ++segDex )
            {
                tempPos = oldSegSegments.get(segDex);
                if (isSamePosition(tempPos, checkPos))
                {
                    bCollision = true;
                }
            }
        }

        /*System.err.println("|||||||||||||||||||||||| Collision: " + bCollision + " ||||||||||||||||||||||||");
        System.err.println("||||||||||||||||||||||||  ||||||||||||||||||||||||");*/

        /*if (GlobalMembers.collisionCheckCount < 15)
            ++GlobalMembers.collisionCheckCount;
        else
            System.exit(0);*/

        return bCollision;
    }

    /**
     * Checks if the longest path in this area is at least 80% of the snake's length
     * @param checkHead
     * @param currSegments
     * @return
     */
    default boolean isSpaceUnsafe(int[] checkHead, ArrayList<int[]> currSegments)
    {
        int newGCost, movePosDex;
        int[] parentPos;
        Cell parentCell, tempCell;

        int count = 0, snakeLength = currSegments.size(), reqPathLength = (int)(0.8 * snakeLength);
        boolean bUnsafe = true;
        int[] tempPos = {-1, -1};
        int[][] movePos = moveSpace;
        boolean[][] mainBoard = GlobalMembers.boards.getMainBoard(),
                headSpaceBoard = GlobalMembers.boards.getHeadSpaceBoard();
        PriorityBlockingQueue<Cell> cellQueue = new PriorityBlockingQueue<>();
        Cell rootCell = new Cell(0, 0, snakeLength, 5, checkHead);

        /*//
        System.err.println("\n~~~~~~~~~~~~~~~ BFS/A* BEGINING ~~~~~~~~~~~~~~~~~~~~~~~");
        System.err.println("checkHead: " + checkHead[0] + " " + checkHead[1]);*/


        // adjust sorting to descending order of heuristic
        Cell.adj = -1;
        GlobalMembers.safeCheckNodes[checkHead[0]][checkHead[1]] = rootCell;
        cellQueue.add(rootCell);

        while (!cellQueue.isEmpty() && bUnsafe)
        {
            // remove the next definite path...
            parentCell = cellQueue.remove();

            //
//            System.err.println("\nparentCell...");
//            parentCell.print();

            parentPos = parentCell.pos;
            // oldDirection = parentCell.moveDex;
            GlobalMembers.safeClosed[parentPos[0]][parentPos[1]] = true;


            for (movePosDex = 0; movePosDex < 4; ++movePosDex)
            {
                tempPos[0] = parentPos[0] + movePos[movePosDex][0];
                tempPos[1] = parentPos[1] + movePos[movePosDex][1];

                // if unexplorable, already part of the final path, or will eventually cause a collision with the snake's body...
                if (outOfBounds(tempPos) ||
                        mainBoard[tempPos[0]][tempPos[1]] ||
                        (headSpaceBoard[tempPos[0]][tempPos[1]]) || /// headspacecheck not needed, as it's mandatory
                        GlobalMembers.safeClosed[tempPos[0]][tempPos[1]]  ||
                        potentialCollision(tempPos, parentCell, currSegments))
                {
                    continue;
                }


                // otherwise retrieve the Cell reference
                tempCell = GlobalMembers.safeCheckNodes[tempPos[0]][tempPos[1]];

                newGCost = parentCell.gCost + 1;

                // check if recently explored
                if (GlobalMembers.safeExplored[tempPos[0]][tempPos[1]])
                {
                    // update this cell if its fPath is longer
                    if (newGCost > tempCell.gCost)
                    {
                        if (cellQueue.remove(tempCell))
                        {
                            tempCell.updateSafe(newGCost, snakeLength, tempPos, parentCell);
                            cellQueue.add(tempCell);
                        }
                    }
                }
                // or calculate the new path's costs and add to queue
                else
                {
                    tempCell.updateSafe(newGCost, snakeLength, tempPos, parentCell);
                    cellQueue.add(tempCell);
                    GlobalMembers.safeExplored[tempPos[0]][tempPos[1]] = true;
                }

                // have we found our goal?
                if (tempCell.hCost > reqPathLength)
                {
                    bUnsafe = false;
                }
            }

            ++count;
        }

        /*System.err.println("Iterations on safecheck: " + count);
        System.err.println("Is path unsafe: " + bUnsafe);
        System.err.println("\n~~~~~~~~~~~~~~~ BFS/A* END ~~~~~~~~~~~~~~~~~~~~~~~");*/

        // adjust sorting to normal order for A*
        Cell.adj = 1;

        // needs a separate reset as it is used frequently
        resetSafeCheck();

        return bUnsafe;
    }


}
