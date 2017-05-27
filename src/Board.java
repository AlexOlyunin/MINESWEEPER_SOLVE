/*
 * Alex Olyunin
 */
public class Board {
    private int colLen;                 // размер поля
    private int rowLen;                 // размер поля
    private int mCount;                 // количество мин
    private int Row, Column;            // координаты для ячеек
    private int[] boardgame;
    private Field minefield;

    public Board (int xSize, int ySize, int mineCount) {
        colLen = xSize;
        rowLen = ySize;
        mCount = mineCount;
        startBoard();
    }

    private void startBoard() {
        boardgame = new int[colLen * rowLen];
        for (int i = 0; i < boardgame.length; i++)
            boardgame[i] = 9;
    }

    public int[] getBoardValues() {
        return boardgame;
    }

    public boolean isCellCovered(int column, int row) {
        return boardgame[(colLen * row) + column] == 9;
    }


    public int getPositionVal(int column, int row) {
        // Задание минного поля
        Column = column;
        Row = row;
        if (minefield == null)
            minefield = new Field(colLen, rowLen, mCount, row, column);

        return minefield.getCellVal((colLen * row) + column);
    }

    public boolean isFinalMove(boolean isMine) {
        // Если область пустая - открыть область
        if (!isMine) {
            openNeighbors();
            isMine = win();
        }
        return isMine;
    }

    /*
     * При открывании поля учитываем размеры поля
     * Если данная ячейка пустая, то с помощью рекурсивного метода
     * продолжаем открывать поле, пока не откроем все ячейки
     */
    private void openNeighbors() {
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++) {
                if ((Row + i) < 0 || (Row + i) >= rowLen || (Column + j) < 0 || (Column + j) >= colLen)
                    continue;
                if (minefield.getCellVal((colLen * (Row + i)) + (Column + j)) == minefield.getMineVal())
                    continue;
                int val = minefield.getCellVal((colLen * (Row + i)) + (Column + j));
                if (!isCellCovered(Column + j, Row + i))
                    continue;

                boardgame[(colLen * (Row + i)) + (Column + j)] = val;
                if (val == 0 && !((Row + i) == Row && (Column + j) == Column)) {
                    Row += i;
                    Column += j;
                    openNeighbors();
                    Column -= j;
                    Row -= i;
                }
            }
    }

    /*
     * Как только число помеченных ячеек равняется количеству мин
     * то игра считается выигранной
     */
    public boolean win() {
        int count = 0;
        for (int row = 0; row < rowLen; row++)
            for (int column = 0; column < colLen; column++)
                if (isCellCovered(column, row))
                    count++;

        return count == mCount;
    }

}
