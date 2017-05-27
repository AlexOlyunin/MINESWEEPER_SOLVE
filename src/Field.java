import java.util.Random;
/*
 * Alex Olyunin
 */
public class Field {
    private static int MINE_VAL = -1; // значение для мины
    private int[] minefield;

    public Field(int colLength, int rowLength, int mineCount, int Row, int Column) {
        minefield = new int[colLength * rowLength];
        for (int i = 0; i < minefield.length; i++)
            minefield[i] = 0;

        placeMines(colLength, rowLength, mineCount, Row, Column);
        fillHints(colLength, rowLength);
    }

    public int getMineVal() {
        return MINE_VAL;
    }

    public int getCellVal(int pos) {
        return minefield[pos];
    }
    // Расставляем мины
    private void placeMines(int colLength, int rowLength, int mineCount, int Row, int Column) {
        if (colLength * rowLength <= mineCount) throw new IllegalArgumentException("Количество мин больше или равно размера поля");
        int row, column;
        Random random = new Random();
        for (int i = 0; i < mineCount; i++) {
            do {
                row = random.nextInt(rowLength);
                column = random.nextInt(colLength);
            } while (minefield[(colLength * row) + column] == MINE_VAL || (row == Row && column == Column));

            minefield[(colLength * row) + column] = MINE_VAL;
        }
    }
    /*
     * Чтобы открыть все поле необходимо просмотреть все
     * ячейки и посчитать количество мин вокруг
     */
    private void fillHints(int colLength, int rowLength) {
        for (int row = 0; row < rowLength; row++)
            for (int column = 0; column < colLength; column++) {
                for (int i = -1; i <= 1; i++)
                    for (int j = -1; j <= 1; j++)
                        if ((row + i) >= 0 && (row + i) < rowLength && (column + j) >= 0 && (column + j) < colLength)
                            if (minefield[(colLength * row) + column] != MINE_VAL)
                                if (minefield[(colLength * (row + i)) + (column + j)] == MINE_VAL)
                                    minefield[(colLength * row) + column]++;
            }
    }
}
