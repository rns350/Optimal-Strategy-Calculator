/**
 * simple driver for creating and running the game.  This class reads the input to set up the game,
 * then plays it nGame times.
 * 
 * @author Reed N Schick
 */
public class Play{

    static Game game; //game object to play
    static int nGames = 0; //number of games to play
    public static void main(String [] Args){
        readInput(Args);
        for(int i = 0; i < nGames; i ++){
            game.play();
        }
        game.generateAnswers();
    }

    /**
     * simple method to read input and set up the game.
     * @param Args - command line arguments specifying the parameters of the game.
     */
    public static void readInput(String [] Args){
        if(Args.length < 6){
            System.err.println("ERROR: Program expects 6 command line arguments, namely:\n"
                                + "[NDice][Nsides][LTarget][UTarget][M][NGames]\n"
                                + "Terminating...");
            System.exit(0);
        }
        int nSides, lTarget, uTarget, nDice, m;
        try{
            nSides = Integer.parseInt(Args[1]);
            lTarget = Integer.parseInt(Args[2]);
            uTarget = Integer.parseInt(Args[3]);
            nDice = Integer.parseInt(Args[0]);
            m = Integer.parseInt(Args[4]);
            nGames = Integer.parseInt(Args[5]);
            game = new Game(nSides, lTarget, uTarget, nDice, m, nGames);
        }
        catch(NumberFormatException e){
            System.out.println("ERROR: The command line arguments must all be integers.\nTerminating...");
            System.exit(0);
        }
    }
}