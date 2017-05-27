/*
 * Alex Olyunin
 * Используется решателем для хранения информации
 * является ли данная ячейка миной или свободной
 *
 */
public class Cell {
    public int val;
    public int covNeighbors = 0;
    public int nearbyMines = 0;
    public int weight = 0;
    public int isMine = -1; // -1 = неопределенность, 0 = нет мины, 1 = мина

    public void setVal(int value) {
        val = value;
        weight = 0;
        isMine = -1;
    }
}
