import java.util.*;

/**
 * Class that represents a game.  A game stores necessary information for playing it,
 * and can play against itself multiple times while storing the results.  it stores two
 * 3d arrays that denote the amount of times the program has won or lost from any given
 * game state by making any given move.
 * 
 * @author Reed N Schick
 */
public class Game{
    double nSides, m, nGames;
    int lTarget, uTarget, nDice;
    int [][][] winCount, loseCount;
    boolean p1Win; //set to true if player 1 wins

    public Game(int nSides, int lTarget, int uTarget, int nDice, int m, int nGames){
        this.nSides = nSides;
        this.lTarget = lTarget;
        this.uTarget = uTarget;
        this.nDice = nDice;
        this.m = m;
        this.nGames = nGames;
        winCount = new int [lTarget][lTarget][nDice + 1];
        loseCount = new int [lTarget][lTarget][nDice + 1];
    }

    /**
     * Method to play one game.  It calls the recursive play method
     * with both players starting at position zero
     */
    public void play(){
        playRec(0, 0);
    }

    /**
     * main logic for playing one instance of the game.  This method first runs player one's turn,
     * updating the matrexes is player one wins or loses.  if p1 does not win, the method then runs player
     * two's turn, using player one's new position, updating the matrexes if player two wins or loses.
     * if neither player has won after this, the method calls itself recursively to start another round.
     * Once a player has won or lost, the p1Win variable is updated to denote who won.  Then, the method
     * begins to return back to all of the recursive calls made, and the method updates the matrexes for
     * each round of the game as it returns out, based on the p1Win boolean.
     * @param p1Pos - the position of player 1
     * @param p2Pos - the position of player 2
     */
    private void playRec(int p1Pos, int p2Pos){
        int dice1; //how many dice p1 will roll
        int p1PosNew; //p1 position after rolling
        dice1 = chooseDice(p1Pos, p2Pos); //get dice for p1
        p1PosNew =  p1Pos + rollDice(dice1); //roll dice and update p1 position
        if(p1PosNew >= lTarget){ //game has ended
            if(p1PosNew > uTarget){ //p1 lost if over uTarget
                p1Win = false;
                loseCount[p1Pos][p2Pos][dice1] ++;
                return;
            }
            p1Win = true; //p1 wins
            winCount[p1Pos][p2Pos][dice1] ++;
            return;
        }
        int dice2; //how many dice p2 will roll
        int p2PosNew; //p2 position after rolling
        dice2 = chooseDice(p2Pos, p1PosNew); //get dice for p2
        p2PosNew =  p2Pos + rollDice(dice2); //roll dice and update p2 position
        if(p2PosNew >= lTarget){ //game has ended
            if(p2PosNew > uTarget){ //p2 lost if over uTarget
                p1Win = true;
                winCount[p1Pos][p2Pos][dice1] ++;
                loseCount[p2Pos][p1PosNew][dice2] ++;
                return;
            }
            p1Win = false; //p2 wins
            winCount[p2Pos][p1PosNew][dice2] ++;
            loseCount[p1Pos][p2Pos][dice1] ++;
            return;
        }
        playRec(p1PosNew, p2PosNew); //start next round of gameplay
    
        if(p1Win){ //after game ends, update matrexes based on who won.
            winCount[p1Pos][p2Pos][dice1] ++;
            loseCount[p2Pos][p1PosNew][dice2] ++;
        }
        else{
            winCount[p2Pos][p1PosNew][dice2] ++;
            loseCount[p1Pos][p2Pos][dice1] ++;
        }
    }

    /**
     * this method chooses the number of dice to roll based off of prior experience that has been collected in the matrexes.
     * each number of dice that a player can roll has a success rate calculated from the games past runs.  This success rate
     * if used to decide the probability distribution over the number of dice the player can roll.  The option with the
     * highest success rate is weighted more heavily towards being chosen
     * @param player - the position of the player who is rolling
     * @param opponent - the position of the opponent
     * @return - number of dice to roll
     */
    private int chooseDice(int player, int opponent){
        double winCountJ, loseCountJ; //temp storage for wincount and lost count for various dice amounts
        int B = 0; //number of dice to roll for the highest probability of resulting in a win, based on experience
        double highest = -1; //highest probability for winning available when choosing dice to roll, based on experience
        double temp = 0;
        double[] winPercent = new double[nDice]; //used to store Fj (individual success rate for each dice amount)
        for(int i = 0; i < nDice; i ++){ //get probability of success for each dice amount
            winCountJ = winCount[player][opponent][i+1];
            loseCountJ = loseCount[player][opponent][i+1];
            if(winCountJ + loseCountJ == 0) { //no games have been played; don't know success rate
                winPercent[i] = 0.5;
                if(0.5 > highest){
                    highest = 0.5;
                    B = i + 1;
                }
            } 
            else { 
                temp = winCountJ / (winCountJ + loseCountJ); //wins / total games == success rate
                winPercent[i] = temp;
                if(temp > highest){ //check if the calculated success rate is now the highest
                    highest = temp;
                    B = i + 1;
                }
            }          
        }

        double g = 0; // sum over all Fj that are not fB in winPercent array
        for(int i = 0; i < nDice; i ++){
            if((B - 1) == i){continue;}
            g += winPercent[i];
        }

        double T = 0; //Total number of games that have gone through this state
        for(int i = 0; i <= nDice; i ++){
            T += winCount[player][opponent][i] + loseCount[player][opponent][i];
        }

        double[] distribution = new double[nDice]; //get probabilit distribution for each possible number of dice, weighted towards the best option
        distribution[B-1] = (T * winPercent[B-1] + m)/(T * winPercent[B-1] + nDice * m);
        double pB = distribution[B-1];
        for(int i = 0; i < nDice; i ++){
            if((B - 1) == i) {continue;}
            distribution[i] = (1 - pB) * ((T * winPercent[i] + m)/(g * T + (nDice - 1) * m));
        }
        int choice = chooseFromDist(distribution) + 1; //get dice choice and add 1 due to 0 indexing arrays
        return choice;
    }

    /**
     * simple method for rolling dice.  This method rolls a number of dice
     * specified in the parameter, and sums the total of all rolls to return
     * to the player
     * @param dice - the number of dice to roll
     * @return - the value of the dice roll
     */
    private int rollDice(int dice){
        int result = 0;
        for(int i = 0; i < dice; i ++){
            result += (int) (Math.random() * nSides + 1);
        }
        return result;
    }

    /**
     * given a probability distribution, this method randomly selects from this distribution and
     * returns the index that is chosen.
     * @param p - the probability distribution to choose from
     * @return - index chosen from the probability distribution
     */
    private int chooseFromDist(double[] p){
        double choice = Math.random();
        double total = 0;
        for(int i = 0; i < nDice - 1; i ++){
            total = total + p[i];
            if(choice < total) {return i;}
        }
        return nDice - 1;
    }

    /**
     * this method generates the results of playing the game nGame times.  It prints out the
     * best choice at any state in a matrex format, and then prints the probability of winning with the
     * best move.
     */
    public void generateAnswers(){
        System.out.println("PLAY =");
        int best = 0;
        double[][] probability = new double[lTarget][lTarget];
        double chance = -1;
        double bestChance = -1;
        for(int i = 0; i < lTarget; i ++){
            for(int j = 0; j < lTarget; j ++){
                for(int k = 0; k <= nDice; k ++){
                    if(winCount[i][j][k] + loseCount[i][j][k] == 0) {continue;} //no games were played in this state; ignore it
                    chance = (double) winCount[i][j][k] / ((double) winCount[i][j][k] + (double) loseCount[i][j][k]); //calculate success rate
                    if(chance > bestChance){ //if this success rate is the new best, record it
                        bestChance = chance;
                        best = k;
                    }
                }
                if(chance == -1){
                    chance = 0;
                }
                System.out.print(String.format("%6d", best)); //print out best option
                if(best == 0){
                    probability[i][j] = 0;
                }
                else{
                    probability[i][j] = bestChance; //store the best option success rate
                }
                best = 0;
                bestChance = -1;
                chance = -1;
            }
            System.out.println();
        }

        System.out.println("\nPROB =");
        for(int i = 0; i < lTarget; i ++){ //print the success rates for each best option
            for(int j = 0; j < lTarget; j ++){
                System.out.print(String.format("%10.4f", probability[i][j]));
            }
            System.out.println();
        }
    }
}