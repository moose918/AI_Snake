import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import za.ac.wits.snake.DevelopmentAgent;

public class MyAgent extends DevelopmentAgent implements Coordinates {

    public static void main(String args[]) {
        MyAgent mooseAgent = new MyAgent();
        MyAgent.start(mooseAgent, args);
    }

    @Override
    public void run() {
        String line, snakeLine, initString, nApple, pApple;
        int mySnakeDex;

        boolean bInitialized = false;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            initString = br.readLine();

            // initialize global members
            new GlobalMembers(initString.split(" "));

            while (true) {
                line = br.readLine();
                System.err.println(line);
                if (line.contains("Game Over")) {
                    break;
                }

                // get apple info
                pApple = line;
                System.err.println("pApple: " + pApple);
                nApple = br.readLine();
                System.err.println("nApple: " + nApple);
                updateCoordinates(GlobalMembers.nApple, nApple.split(" "));
                updateCoordinates(GlobalMembers.pApple, pApple.split(" "));

                // get my snake pointer
                mySnakeDex = Integer.parseInt(br.readLine());
                System.err.println(mySnakeDex);

                // reset invisible snake index
                GlobalMembers.invisibleDex = -1;

                for (int snakeDex = 0; snakeDex < GlobalMembers.numSnakes; snakeDex++) {
                    snakeLine = br.readLine();
                    System.err.println("Snake " + snakeDex + ": " + snakeLine);

                    // initialize the snakes
                    if (!bInitialized )
                    {
                        if (snakeDex == mySnakeDex)
                        {
                            GlobalMembers.mySnake = new MySnake();
                            GlobalMembers.snakes[snakeDex] = GlobalMembers.mySnake;
                        }
                        else{
                            GlobalMembers.snakes[snakeDex] = new Snake();

                        }
                    }

                    // update the snakes
                    if (snakeDex != mySnakeDex)
                        GlobalMembers.snakes[snakeDex].updateSnake(snakeDex, snakeLine.split(" "));
                    else
                        GlobalMembers.mySnake.updateSnake(snakeDex, snakeLine.split(" "));

                }
                bInitialized = true;

                System.err.println("\n~~~~~~~~ALGO START~~~~~~~~~~~~~~~\n");
                // begin strategy
                long startTime = System.currentTimeMillis();
                int move = GlobalMembers.mySnake.makeMove();
                long endTime = System.currentTimeMillis();
                long length = endTime-startTime, delay = 500 - length;
               // Thread.sleep(delay);
                System.err.println("\n~~~~~~~~~~~~ " + (length) + " ms ~~~~~~~");

                System.out.println(move);

                System.err.println("\n~~~~~~~~~~~DONE~~~~~~~~~~~~\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}