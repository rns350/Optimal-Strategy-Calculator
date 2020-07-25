# Simple Game Learning Algorithm

This algorithm is designed to learn and play a simple game of chance using a similar method that AlphaZero used to learn chess.  The game is a variant of Black Jack, where two players alternately roll dice and keep track of their total across turns.  The goal is to reach a target range.  If a player rolls a dice and their total score enters the range, they win.  If it overshoots the range, they immediately lose.  

The program can take arguments to define the game:

NDice: number of dice rolled on each turn.

Nsides: how many sides each dice has.

LTarget: The low target in the target range.

UTarget: The upper target in the target range.

M: A hyper parameter.  Increasing M will make the program more likely to try moves with a lower chance of success, which can be very useful in the learning stage to help make sure that the program tries multiple options that may be better even if it is having success with one option.

NGames: How many games should the program play?

The ouput Will provide a detailed look at what the program learned.  The first matrix tells you how many dice to roll from a given state for the best chance of winning.  For example, Matrix[1,2] will hold the number of dice to roll if you the player are at 1 point and the opponent is at 2.  The second matrix is indexed similarly, but instead provides the actual probability of winning from that point based on the game's experience while learning.

TO COMPILE
----------

javac Game.java Play.java

TO RUN
------

java Play [NDice] [NSides] [LTarget] [UTarget] [M] [NGames]
