
public class Matrix {

	private double[] matrix;
	private int num_rows;
	private int num_cols;

	public static Matrix createEmptyMatrix(int rows, int cols) {
		return new Matrix(rows, cols);
	}

	public static Matrix copyMatrix(Matrix mat) {
		int num_rows = mat.getNumRows();
		int num_cols = mat.getNumCols();
		Matrix copy = new Matrix(num_rows, num_cols);

		for (int i = 1; i <= num_rows; i++) {
			for (int j = 1; j <= num_cols; j++) {
				copy.setElement(i, j, mat.getElement(i, j));
			}
		}

		return copy;
	}

	private Matrix(int rows, int cols) throws RuntimeException {
		if (rows < 1) {
			throw new RuntimeException("Number of rows must be >= 1.");
		}

		if (cols < 1) {
			throw new RuntimeException("Number of columns must be >= 1.");
		}

		num_rows = rows;
		num_cols = cols;

		matrix = new double[num_rows * num_cols];
	}

	private int convertRowCol(int row, int col) {
		// row = -1;
		assert row > 0 : "Invalid row.";
		assert row <= getNumRows() : "Invalid row.";
		assert col > 0 : "Invalid column.";
		assert col <= getNumCols() : "Invalid column.";

		int num_cols = getNumCols();
		int index = (row - 1) * num_cols + col;
		return index;
	}

	public int getNumRows() {
		return num_rows;
	}

	public int getNumCols() {
		return num_cols;
	}

	public void setElement(int row, int col, double value) throws RuntimeException {
		if (row > getNumRows() || row < 1 || col > getNumCols() || col < 1) {
			throw new RuntimeException("Invalid row and/or column.");
		}

		int index = convertRowCol(row, col);
		matrix[index - 1] = value;
	}

	public double getElement(int row, int col) throws RuntimeException {
		if (row > getNumRows() || row < 1 || col > getNumCols() || col < 1) {
			throw new RuntimeException("Invalid row and/or column.");
		}

		int index = convertRowCol(row, col);
		return matrix[index - 1];
	}

	public String toString() {
		int num_rows = getNumRows();
		int num_cols = getNumCols();

		String to_string = "";

		for (int i = 1; i <= num_rows; i++) {
			for (int j = 1; j <= num_cols; j++) {
				to_string += getElement(i, j) + "  ";
			}
			to_string += "\r\n";
		}

		return to_string;
	}

	public static int[] convertLocToRowCol(Matrix board, int loc) {
		int num_rows = board.getNumRows();
		int row = (loc - 1) / num_rows + 1;
		int col = (loc - 1) % num_rows + 1;
		int[] result = new int[2];
		result[0] = row;
		result[1] = col;
		return result;
	}
}
