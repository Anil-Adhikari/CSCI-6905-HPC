
public class MinMaxSerial {

	// method gets called from MainGameTicTacToe class to get computer's move
	public static int search(Matrix board) {
		int move;
		int turn = computeTurn(board);

		long start_time = System.nanoTime();

		if (turn % 2 == 1)
			move = minimizeScore(board, turn);
		else
			move = maximizeScore(board, turn);

		long end_time = System.nanoTime();
		long delta_time = end_time - start_time;
		System.out.print("Time taken by Computer to find the next position: " + delta_time / 1.E6 + " ms");

		return move;
	}

	private static int searchMax(Matrix board, int turn) {
		// check for a board that has a winner (base case)
		int terminal = evaluateTerminalState(board, turn);

		if (terminal != 1000) {
			return terminal; // found a terminal state, so return the terminal state value
		}

		turn = turn + 1;
		int best_so_far = -1000;

		int minimax;
		for (int loc = 1; loc <= 9; loc++) {
			int[] result = Matrix.convertLocToRowCol(board, loc);
			int row = result[0];
			int col = result[1];

			// if a spot is already taken, skip it
			if (board.getElement(row, col) != 0)
				continue;

			board.setElement(row, col, 1); // make this an X move
			minimax = searchMin(board, turn);
			board.setElement(row, col, 0); // back out the previous move

			if (minimax > best_so_far) {
				best_so_far = minimax; // Xs turn, assume the best option
			}
		}

		return best_so_far;
	}

	private static int searchMin(Matrix board, int turn) {
		// check for a board that has a winner (base case)
		int terminal = evaluateTerminalState(board, turn);

		if (terminal != -1000) {
			return terminal; // found a terminal state, so return the terminal
								// state value
		}

		turn = turn + 1;
		int best_so_far = 1000;

		int minimax;
		for (int loc = 1; loc <= 9; loc++) {
			int[] result = Matrix.convertLocToRowCol(board, loc);
			int row = result[0];
			int col = result[1];

			// if a spot is already taken, skip it
			if (board.getElement(row, col) != 0)
				continue;

			board.setElement(row, col, 2); // make this an X move
			minimax = searchMax(board, turn);
			board.setElement(row, col, 0); // back out the previous move

			if (minimax < best_so_far) {
				best_so_far = minimax; // Xs turn, assume the best option
			}

		}

		return best_so_far;
	}

	// in this method, turn % 2 == 1
	// need to know the current turn
	private static int minimizeScore(Matrix board, int turn) {
		// initial branching factor is 9
		// array to store thread results
		int[] thread_minimax = new int[9];

		for (int i = 1; i <= 9; i++) {
			thread_minimax[i - 1] = -1000;
		}

		for (int loc = 1; loc <= 9; loc++) {
			int[] result = Matrix.convertLocToRowCol(board, loc);
			int row = result[0];
			int col = result[1];
			if (board.getElement(row, col) != 0)
				continue;

			Matrix copy_board = copyBoard(board);
			copy_board.setElement(row, col, 1); // make this an X move
			thread_minimax[loc - 1] = searchMin(copy_board, turn);

			// System.out.println(loc);
		}

		int best_so_far;
		best_so_far = -1000;

		int move = 0;
		// find the best move after all of the threads have finished
		for (int loc = 1; loc <= 9; loc++) {
			int minimax = thread_minimax[loc - 1];
			if (minimax > best_so_far) {
				best_so_far = minimax;
				move = loc;
			}
		}

		return move;
	}

	// in this method, turn % 2 == 0
	private static int maximizeScore(Matrix board, int turn) {
		// initial branching factor is 9
		int[] thread_minimax = new int[9];

		for (int i = 1; i <= 9; i++) {
			thread_minimax[i - 1] = 1000;
		}
		
		for (int loc = 1; loc <= 9; loc++) {
			int[] result = Matrix.convertLocToRowCol(board, loc);
			int row = result[0];
			int col = result[1];
			if (board.getElement(row, col) != 0)
				continue;

			Matrix copy_board = copyBoard(board);

			copy_board.setElement(row, col, 2); // make this an O move
			thread_minimax[loc - 1] = searchMax(copy_board, turn);

			// System.out.println(loc);
		}

		int best_so_far;
		best_so_far = 1000;

		int move = 0;
		// find the best move after all of the threads have finished
		for (int loc = 1; loc <= 9; loc++) {
			int minimax = thread_minimax[loc - 1];

			if (minimax < best_so_far) {
				best_so_far = minimax;
				move = loc;
			}
		}

		return move;
	}

	// method used to compute the turn to determine how many places are remained in the game board
	private static int computeTurn(Matrix board) {
		int empty = 0;
		int num_rows = board.getNumRows();
		int num_cols = board.getNumCols();

		for (int i = 1; i <= num_rows; i++) {
			for (int j = 1; j <= num_cols; j++) {
				int val = (int) board.getElement(i, j);
				if (val == 0)
					empty++;
			}
		}

		return 10 - empty;
	}

	private static Matrix copyBoard(Matrix board) {
		int num_rows = board.getNumRows();
		int num_cols = board.getNumCols();

		Matrix copy_board = (Matrix) Matrix.createEmptyMatrix(num_rows, num_cols);

		for (int i = 1; i <= num_rows; i++) {
			for (int j = 1; j <= num_cols; j++) {
				copy_board.setElement(i, j, board.getElement(i, j));
			}
		}

		return copy_board;
	}

	// if turn is an odd number, it is Xs turn (MAX)
	// min or max get an extra bonus for defeating the opponent quickly
	private static int evaluateTerminalState(Matrix board, int turn) {
		int sign;
		int player_id;
		int terminal_value; // special return value to indicate terminal state
							// not yet reached

		if (turn % 2 == 1) // MAX
		{
			terminal_value = -1000;
			player_id = 1;
			sign = 1;
		} else // MIN
		{
			terminal_value = 1000;
			player_id = 2;
			sign = -1;
		}

		int win_value = 9 - (turn - 1); // number of empty spaces

		// check for winner
		if (board.getElement(1, 1) == player_id && board.getElement(2, 2) == player_id
				&& board.getElement(3, 3) == player_id) {
			terminal_value = win_value * sign;
		}

		else if (board.getElement(1, 3) == player_id && board.getElement(2, 2) == player_id
				&& board.getElement(3, 1) == player_id) {
			terminal_value = win_value * sign;
		}

		else if (board.getElement(1, 1) == player_id && board.getElement(2, 1) == player_id
				&& board.getElement(3, 1) == player_id) {
			terminal_value = win_value * sign;
		}

		else if (board.getElement(1, 2) == player_id && board.getElement(2, 2) == player_id
				&& board.getElement(3, 2) == player_id) {
			terminal_value = win_value * sign;
		}

		else if (board.getElement(1, 3) == player_id && board.getElement(2, 3) == player_id
				&& board.getElement(3, 3) == player_id) {
			terminal_value = win_value * sign;
		}

		else if (board.getElement(1, 1) == player_id && board.getElement(1, 2) == player_id
				&& board.getElement(1, 3) == player_id) {
			terminal_value = win_value * sign;
		}

		else if (board.getElement(2, 1) == player_id && board.getElement(2, 2) == player_id
				&& board.getElement(2, 3) == player_id) {
			terminal_value = win_value * sign;
		}

		else if (board.getElement(3, 1) == player_id && board.getElement(3, 2) == player_id
				&& board.getElement(3, 3) == player_id) {
			terminal_value = win_value * sign;
		}

		else if (turn == 9) // 9th turn did not produce a winner
		{
			terminal_value = 0; // TIE
		}

		return terminal_value;
	}
}
