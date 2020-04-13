We have used an implementation of 2048 from Michael Zherdev which can be found here:
 https://github.com/michaelzherdev/2048
 Our code is in the ai package and we have only made minor changes to the 2048 implementation located in the 
 com.mzherdev package.
 
 The game can be run trough the Main.java class, it is currently set up to use one of our ai as the player.
 By default the minimax algorithm is used with evalOFSMM heuristic.
 If you want to change the algorithm used it can be done in the getNextMove function on line 177 in AILauncher.java.
 There are two lines of code assigning the optimal value on line 181 and 182, these can be commented/outcommented 
 to change between minimax and expectimax. The heuristic used can be changed in the first if statement in the 
 minimax and expectimax functions.
