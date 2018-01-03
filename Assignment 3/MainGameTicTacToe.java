/***
*	To compile this program:
*   		javac MainGameTicTacToe.java
*	To run serial version of the program:
*		java MainGameTicTacToe serial
*	To run parallel version of the program:
*		java MainGameTicTacToe parallel 
*
***/



import java.util.Scanner;

public class MainGameTicTacToe {
	private static Matrix board;
	private static boolean game_over;
	private static int turn;

	private static boolean parallel;
	private static Scanner scan = new Scanner(System.in);
	private static boolean isHuman = true;

	public static void main(String[] args){
		
		 if (args.length != 1) {
	            System.err.println("Number of command line arguments must be 2");
	            System.err.println("You have given only " + args.length + "command line arguments");
	            System.err.println("Incorrect usage. Program terminated");
	            System.err.println("Correct usage: java Extract <input-file-name> <output-file-name> ");
	            System.exit(1);
	     }
		 
		 String programchoice = args[0];
		 if(programchoice.equals("parallel"))
			 parallel = true;
		 else
			 parallel = false;
		
		System.out.println("Enter your choice\n1 - To play first with notation \'X\' \n2 - To play after computer with notation \'O\' ");
		int choice = scan.nextInt();
		if (choice == 2)
			isHuman = false;
		
		//parallel = true;

		board = Matrix.createEmptyMatrix(3, 3);
		displayGame();
		play(isHuman);

	}
	
	// method to start game
	private static void play(boolean human_player_side) {
		if (human_player_side)
		{
			getHumanPlayerMove();
		}
		while (!isGameOver()) {
			getComputerPlayerMove();
			if (!isGameOver()) {
				getHumanPlayerMove();
			}
		}
		System.out.println("Game Over !!!");
	}
	
	// method to take input from the human player
	private static void getHumanPlayerMove() {
		System.out.println("Enter desired move: ");
		int humanPlayerMove = scan.nextInt();
		while (!isValidMove(humanPlayerMove)) {
			System.out.println("Enter desired move: ");
			humanPlayerMove = scan.nextInt();
		}
		move(humanPlayerMove);
	}

	// method to get computer's move
	// this method calls search method in MinMaxSerial class if it is serial version
	// else search method of MinMaxParallel if it is parallel version 
	private static void getComputerPlayerMove() {
		if (parallel) {
			 int minimax_move = MinMaxParallel.search(board);
			 move(minimax_move);
		} else {
			int minMaxMove = MinMaxSerial.search(board);
			move(minMaxMove);
		}
	}
	
	// method to check whether user input is valid or not
	private static boolean isValidMove(int loc) {
		if (loc < 1 || loc > 9)
			return false;

		int[] result = Matrix.convertLocToRowCol(board, loc);
		int row = result[0];
		int col = result[1];
		return isValidMove(row, col);
	}

	// this is overloaded method of isValidMove(int loct) and gets called from isValidMove(int loc)
	// to make sure the move is valid while checking in matrix (board)
	private static boolean isValidMove(int row, int col) {
		if (board.getElement(row, col) == 0) {
			return true;
		}

		return false;
	}
	
	// method to display the move in game board and decides whether game is over or not
	private static void move(int loc) {
		int player_id = 1;
		if (turn % 2 == 0)
			player_id = 2;

		int[] result = Matrix.convertLocToRowCol(board, loc);
		int row = result[0];
		int col = result[1];

		if (!isValidMove(row, col)) {
			System.out.println("Invalid Move.");
			return;
		}

		board.setElement(row, col, player_id);

		int winner = checkForWinner(player_id);
		if ((winner > 0) || (turn >= 9)) {
			game_over = true;
		} else {
			turn++;
		}

		displayGame();
	}

	public static boolean isGameOver() {
		return game_over;
	}

	// method to display game board
	public static void displayGame() {
		System.out.println("\nGame Board: ");
		int num_rows = board.getNumRows();
		int num_cols = board.getNumCols();

		int count = 1;
		for (int i = 1; i <= num_rows; i++) {
			for (int j = 1; j <= num_cols; j++) {
				if (board.getElement(i, j) == 1) {
					System.out.print("O ");
				} else if (board.getElement(i, j) == 2) {
					System.out.print("X ");
				} else {
					System.out.print(count + " ");
				}
				count++;
			}

			System.out.println();
		}
	}
	
	// method which checks whether game is over or not by checking logic of game (value at different location of game board)
	public static int checkForWinner(int player_id) {
		int winner = 0;

		// check for winner
		if (board.getElement(1, 1) == player_id && board.getElement(2, 2) == player_id
				&& board.getElement(3, 3) == player_id) {
			winner = player_id;
		}

		else if (board.getElement(1, 3) == player_id && board.getElement(2, 2) == player_id
				&& board.getElement(3, 1) == player_id) {
			winner = player_id;
		}

		else if (board.getElement(1, 1) == player_id && board.getElement(2, 1) == player_id
				&& board.getElement(3, 1) == player_id) {
			winner = player_id;
		}

		else if (board.getElement(1, 2) == player_id && board.getElement(2, 2) == player_id
				&& board.getElement(3, 2) == player_id) {
			winner = player_id;
		}

		else if (board.getElement(1, 3) == player_id && board.getElement(2, 3) == player_id
				&& board.getElement(3, 3) == player_id) {
			winner = player_id;
		}

		else if (board.getElement(1, 1) == player_id && board.getElement(1, 2) == player_id
				&& board.getElement(1, 3) == player_id) {
			winner = player_id;
		}

		else if (board.getElement(2, 1) == player_id && board.getElement(2, 2) == player_id
				&& board.getElement(2, 3) == player_id) {
			winner = player_id;
		}

		else if (board.getElement(3, 1) == player_id && board.getElement(3, 2) == player_id
				&& board.getElement(3, 3) == player_id) {
			winner = player_id;
		}

		return winner;
	}

}
