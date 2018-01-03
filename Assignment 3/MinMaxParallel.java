
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MinMaxParallel extends RecursiveTask<Integer>{
	private static final long serialVersionUID = 1L;
	
		private Matrix board;
		private int turn;

		private int thread_id;
	    private static final int NUM_THREADS = 10;
		
	    public MinMaxParallel(Matrix board, int turn, int thread_id)
		{
			this.board = board;
			this.turn = turn;
			this.thread_id = thread_id;
		}
		
		protected Integer compute()  //dispatch method
		{
			int result=0;
			
			//top level thread uses this conditional
			if (thread_id <= 1)
			{
				if (turn % 2 == 1)
				{
					//result = minForkJoin(board, turn);	
					result = maxForkJoin(board, turn);

				}
				else 
				{
					result = minForkJoin(board, turn);
					//result = maxForkJoin(board, turn);

				}

				return result;
			}
			
			//the other threads use the following conditional
			//no new threads will be started
			if (turn % 2 == 1)
			{
				//looks like should be searchMax but turn is incremented in the method
				//after checking for a terminal state
				result = searchMin(board, turn);
			}
			else
			{
				result = searchMax(board, turn);
			}
		
			return result;
		}
		
		//this method is called from TicTacToe to get the move
		//create a top level thread and use ForkJoinPool
		public static int search(Matrix board)
		{
			int turn = computeTurn(board);
			
			long start_time = System.nanoTime();
			
	        ForkJoinPool pool = new ForkJoinPool(NUM_THREADS);
	        int move = pool.invoke(new MinMaxParallel(board, turn,0));
			
			long end_time = System.nanoTime();
			
			long delta_time = end_time - start_time;
			double milli_time = delta_time/1.E6;
			System.out.print("Time taken by Computer to find the next position: " + milli_time + " ms");
			
			//return move;
			return move;
			
		}
		
		// in this method, turn % 2 == 1 (Xs turn)
		private static int maxForkJoin(Matrix board, int turn)
		{
			//maximum number of threads is 9 for tic-tac-toe
			int[] thread_minimax = new int[9];
			Matrix copy_board;
			for (int loc = 1; loc <= 9; loc++)
			{
				int[] row_col = Matrix.convertLocToRowCol(board, loc);
				int row = row_col[0];
				int col = row_col[1];
				if (board.getElement(row, col) != 0)  continue;  //spot already occupied
				copy_board = copyBoard(board);  
				copy_board.setElement(row, col, 1); //make this an X move
				thread_minimax[loc - 1] = (int)copy_board.getElement(row, col);
				
				// new thread created and forked
				MinMaxParallel para = new MinMaxParallel(copy_board, turn, loc);
				para.fork();
				para.join();
			}
			
			int best_so_far = -1000;  //used to identify the optimal move
			int move = 0;  //will contain the optimal move (1-9)

			for (int loc = 1; loc <= 9; loc++)
			{		

				int minimax = thread_minimax[loc-1];
				if (minimax > best_so_far) {
					best_so_far = minimax;
					move = loc;
				}
				
			}
			
			return move;
		}

		//in this method, turn % 2 == 0 (Os turn)
		private static int minForkJoin(Matrix board, int turn)
		{
			int[] thread_minimax = new int[9];

			for (int loc = 1; loc <= 9; loc++)
			{
				int[] row_col = Matrix.convertLocToRowCol(board, loc);
				int row = row_col[0];
				int col = row_col[1];
				if (board.getElement(row, col) != 0)  continue;
				Matrix copy_board = copyBoard(board);
				copy_board.setElement(row, col, 2); //make this an O move
				thread_minimax[loc-1] = (int)copy_board.getElement(row, col);

				// new thread created and forked
				MinMaxParallel para = new MinMaxParallel(copy_board, turn, loc);
				para.fork();
				para.join();
				
			}
			
			int best_so_far = 1000;
			int move = 0;
			
			for (int loc = 1; loc <= 9; loc++)
			{				
				int minimax = thread_minimax[loc-1];
				if (minimax > best_so_far) {
					best_so_far = minimax;
					move = loc;
				}
			}
			
			return move;
		}

		private static int searchMax(Matrix board, int turn)
		{
			//check for a board that has a winner (base case)
			int terminal = evaluateTerminalState(board, turn);

			if (terminal != 1000)
			{
				return terminal;  //found a terminal state, so return the terminal state value
			}

			turn = turn + 1;
			int best_so_far = -1000;

			int minimax;
			for (int loc = 1; loc <= 9; loc++)
			{
				int[] result = Matrix.convertLocToRowCol(board, loc);
				int row = result[0];
				int col = result[1];
					 
				//if a spot is already taken, skip it
				if (board.getElement(row, col) != 0) continue;
					
				board.setElement(row, col, 1); //make this an X move
				minimax = searchMin(board, turn);
				board.setElement(row, col, 0);  //back out the previous move

				if (minimax > best_so_far)
				{
					best_so_far = minimax; //Xs turn, assume the best option
				}
			}
			
			return best_so_far;
		}

		private static int searchMin(Matrix board, int turn)
		{
			int terminal = evaluateTerminalState(board, turn);

			if (terminal != -1000)
			{
				return terminal; 
			}

			turn = turn + 1;
			int best_so_far = 1000;

			int minimax;
			for (int loc = 1; loc <= 9; loc++)
			{
				int[] result = Matrix.convertLocToRowCol(board, loc);
				int row = result[0];
				int col = result[1];
					 
				if (board.getElement(row, col) != 0) continue;
					
				board.setElement(row, col, 2); 
				minimax = searchMax(board, turn);
				board.setElement(row, col, 0); 

				if (minimax < best_so_far)
				{
					best_so_far = minimax; 
				}

			}
			
			return best_so_far;
		}

		private static int computeTurn(Matrix board)
		{
			int empty = 0;
			int num_rows = board.getNumRows();
			int num_cols = board.getNumCols();

			for (int i = 1; i <= num_rows; i++)
			{
				for (int j = 1; j <= num_cols; j++)
				{
					int val = (int) board.getElement(i, j);
					if (val == 0) empty++;
				}
			}
			
			return 10 - empty;
		}

		private static Matrix copyBoard(Matrix board)
		{
			int num_rows = board.getNumRows();
			int num_cols = board.getNumCols();
			
			Matrix copy_board = (Matrix) Matrix.createEmptyMatrix(num_rows, num_cols);

			for (int i = 1; i <= num_rows; i++)
			{
				for (int j = 1; j <= num_cols; j++)
				{
					copy_board.setElement(i, j, board.getElement(i, j));
				}
			}
			
			return copy_board;
		}
	   
		//if turn is an odd number, it is Xs turn (MAX)
		//min or max get an extra bonus for defeating the opponent quickly
		private static int evaluateTerminalState(Matrix board, int turn)
		{
			int sign;
			int player_id;
			int terminal_value;   //special return value to indicate terminal state not yet reached
			
			if (turn % 2 == 1)  //MAX
			{
				terminal_value = -1000;
				player_id = 1;
				sign = 1;
			}
			else  //MIN
			{
				terminal_value = 1000;
				player_id = 2;
				sign = -1;
			}
			
			int win_value = 9 - (turn - 1); //number of empty spaces

			  //check for winner
			  if (board.getElement(1,1) == player_id && board.getElement(2,2) == player_id && board.getElement(3,3) == player_id)
			  {
				 terminal_value = win_value * sign;
			  }
			  
			  else if (board.getElement(1,3) == player_id && board.getElement(2,2) == player_id && board.getElement(3,1) == player_id)
			  {
				 terminal_value = win_value * sign;
			  }
			  
			  else if (board.getElement(1,1) == player_id && board.getElement(2,1) == player_id && board.getElement(3,1) == player_id)
			  {
				 terminal_value = win_value * sign;
			  }

			  else if (board.getElement(1,2) == player_id && board.getElement(2,2) == player_id && board.getElement(3,2) == player_id)
			  {
				 terminal_value = win_value * sign;
			  }
			  
			  else if (board.getElement(1,3) == player_id && board.getElement(2,3) == player_id && board.getElement(3,3) == player_id)
			  {
				 terminal_value = win_value * sign;
			  }
			  
			  else if (board.getElement(1,1) == player_id && board.getElement(1,2) == player_id && board.getElement(1,3) == player_id)
			  {
				 terminal_value = win_value * sign;
			  }

				else if (board.getElement(2,1) == player_id && board.getElement(2,2) == player_id && board.getElement(2,3) == player_id)
			  {
				 terminal_value = win_value * sign;
			  }

				else if (board.getElement(3,1) == player_id && board.getElement(3,2) == player_id && board.getElement(3,3) == player_id)
			  {
				 terminal_value = win_value * sign;
			  }
			  
			  else if (turn == 9)  //9th turn did not produce a winner
			  {
				  terminal_value = 0;  //TIE
			  }

			  return terminal_value;
		}
}
