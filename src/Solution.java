/*
 * Alex Olyunin
 * Стратегия и логика решателя сапера
 * 1) Определение всех безопасных ходов, основанные на соседние ячейки
 * 2) Определение всех безопасных ходов, на основе известных мин
 * 3) Определение всех безопасных ходов, основываясь на информации о соседях
 * 4) Определение наименее опасных ходов
 * 5) Рандом, если вычислить невозможно
 */
public class Solution {
    private static int xSize = 10, ySize = 10, mines = 10;
    private int numOfGames = 0, numOfTurns = 0, wins = 0, safeMoves = 0;
    private Board board;
    private int[] savedBoard;
    private Cell[] cl;

    public Solution() {
        int iterations = 10000; // Количество запусков
        int[] coordArry;

        do {
            numOfGames++;
            board = new Board(xSize, ySize, mines);
            savedBoard = null;
            do  {
                numOfTurns++;
                coordArry = selectNextCell();
            } while (!board.isFinalMove(board.getPositionVal(coordArry[0], coordArry[1]) == -1));

            if (board.win())
                wins++;

            iterations--;
        } while (iterations != 0);

        System.out.println(String.format("%1$20s %2$10s", "Всего игр", numOfGames));
        System.out.println(String.format("%1$20s %2$10s", "Выиграно", wins));
        System.out.println(String.format("%1$20s %2$10s", "Процент побед (%)", (wins / (float) numOfGames * 100)));
    }

    /*
     * По первому действию определяем будем мы действовать рискованно или безопасно
     */
    public int[] selectNextCell() {
        int[] arry = null;
        if (savedBoard == null) {
            safeMoves = 0;
            savedBoard = new int[xSize * ySize];
            cl = new Cell[xSize * ySize];
            for (int i = 0; i < (xSize * ySize); i++)
                cl[i] = new Cell();

            // Нажимая на центральную координату - мы не можем проиграть
            return new int[] { xSize / 2, ySize / 2 };
        }
        if (safeMoves == 0) {
            savedBoard = board.getBoardValues();
            for (int i = 0; i < (xSize * ySize); i++)
                cl[i].setVal(savedBoard[i]);

            checkNeighbors();
            findSafeMoves();
        }

        if (safeMoves == 0) {
            arry = selectRiskyMove();
        } else {
            arry = selectSafeMove();
            safeMoves--;
        }

        return arry;
    }

    /*
     * Для того, чтобы походить безопасно нужно
     * изучить поле и, основываясь на известные мины
     * проанализировать все безопасные ходы
     */
    public void checkNeighbors() {
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                if (cl[(xSize * y) + x].val == 0) continue;
                if (cl[(xSize * y) + x].val == 9) continue;
                cl[(xSize * y) + x].covNeighbors = 0;
                cl[(xSize * y) + x].nearbyMines = 0;

                // Считаем количество открытых соседей
                for (int yy = y - 1; yy <= y + 1; yy++) {
                    if (yy < 0 || yy >= ySize) continue;
                    for (int xx = x - 1; xx <= x + 1; xx++) {
                        if (xx < 0 || xx >= xSize || (yy == y && xx == x)) continue;
                        if (cl[(xSize * yy) + xx].val == 9) {
                            cl[(xSize * y) + x].covNeighbors++;
                            cl[(xSize * yy) + xx].weight += cl[(xSize * y) + x].val;
                        }
                    }
                }

                // Если кол-во соседних мин = значению - все соседние мины
                if (cl[(xSize * y) + x].val == cl[(xSize * y) + x].covNeighbors) {
                    for (int yy = y - 1; yy <= y + 1; yy++) {
                        if (yy < 0 || yy >= ySize) continue;
                        for (int xx = x - 1; xx <= x + 1; xx++) {
                            if (xx < 0 || xx >= xSize || (yy == y && xx == x)) continue;
                            if (cl[(xSize * yy) + xx].val == 9)
                                cl[(xSize * yy) + xx].isMine = 1;
                        }
                    }
                }

                // Посчитаем количество всех найденных мин
                for (int yy = y - 1; yy <= y + 1; yy++) {
                    if (yy < 0 || yy >= ySize) continue;
                    for (int xx = x - 1; xx <= x + 1; xx++) {
                        if (xx < 0 || xx >= xSize || (yy == y && xx == x)) continue;
                        if (cl[(xSize * yy) + xx].isMine == 1)
                            cl[(xSize * y) + x].nearbyMines++;
                    }
                }

                // Проанализируем, можем ли мы обнаружить бехопасные ходы
                if (cl[(xSize * y) + x].val == cl[(xSize * y) + x].nearbyMines) {
                    for (int yy = y - 1; yy <= y + 1; yy++) {
                        if (yy < 0 || yy >= ySize) continue;
                        for (int xx = x - 1; xx <= x + 1; xx++) {
                            if (xx < 0 || xx >= xSize || (yy == y && xx == x)) continue;
                            if (cl[(xSize * yy) + xx].val == 9 &&
                                    cl[(xSize * yy) + xx].isMine == -1) {
                                cl[(xSize * yy) + xx].isMine = 0;
                                safeMoves++;
                            }
                        }
                    }
                }

                // ИСКЛЮЧЕНИЕ! Если количество мин больше, чем значение клетки - ошибка
                if (cl[(xSize * y) + x].val < cl[(xSize * y) + x].nearbyMines) {
                    System.out.println("ERROR - more mines (" + cl[(xSize * y) + x].nearbyMines +
                            ") than value (" + cl[(xSize * y) + x].val + ")! Row " + (y + 1) + " Column " + (x + 1));
                    throw new RuntimeException();
                }
            }
        }
    }

    /*
     * Ищем безопасные ходы
     */
    public void findSafeMoves() {
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
                if (cl[(xSize * y) + x].val == 0) continue;
                if (cl[(xSize * y) + x].val == 9) continue;

                /*
                 *  Если мы знаем значение первой мины - мы можем определить
                 *  где другие мины и найти безопасный ход
                 */
                if (cl[(xSize * y) + x].val - cl[(xSize * y) + x].nearbyMines == 1 &&
                        cl[(xSize * y) + x].covNeighbors - cl[(xSize * y) + x].nearbyMines == 2) {
                    for (int yy = y - 1; yy <= y + 1; yy++) {
                        if (yy < 0 || yy >= ySize) continue;
                        for (int xx = x - 1; xx <= x + 1; xx++) {
                            if (xx < 0 || xx >= xSize || (yy == y && xx == x)) continue;
                            if (cl[(xSize * yy) + xx].val == 9 && cl[(xSize * yy) + xx].isMine == -1) {
                                if (x1 == -1) {
                                    x1 = xx;
                                    y1 = yy;
                                } else {
                                    x2 = xx;
                                    y2 = yy;
                                }
                            }
                        }
                    }

                    if (x1 == -1 || x2 == -1) continue;
                    // Ниже и выше
                    if (x1 == x2) {
                        if (y - 1 >= 0 && y - 1 < ySize)
                            deduceMine(y - 1, x, y1, x1, y2, x2);
                        if (y + 1 >= 0 && y + 1 < ySize)
                            deduceMine(y + 1, x, y1, x1, y2, x2);
                    }
                    // Право и лево
                    if (y1 == y2) {
                        if (x - 1 >= 0 && x - 1 < xSize)
                            deduceMine(y, x - 1, y1, x1, y2, x2);
                        if (x + 1 >= 0 && x + 1 < xSize)
                            deduceMine(y, x + 1, y1, x1, y2, x2);
                    }
                }
            }
        }
    }

    public void deduceMine(int y0, int x0, int y1, int x1, int y2, int x2) {
        for (int yy = y0 - 1; yy <= y0 + 1; yy++) {
            if (yy < 0 || yy >= ySize) continue;
            for (int xx = x0 - 1; xx <= x0 + 1; xx++) {
                // Игнорируем уже просмотренные ходы
                if (xx < 0 || xx >= xSize || !(cl[(xSize * yy) + xx].val == 9 &&
                        cl[(xSize * yy) + xx].isMine == -1) || (yy == y0 && xx == x0) ||
                        (yy == y1 && xx == x1) || (yy == y2 && xx == x2)) continue;
                if (cl[(xSize * y0) + x0].val - cl[(xSize * y0) + x0].nearbyMines == 1) {
                    // Возвращаем, если какая-либо из координат находится в 3х от соответствующих ячеек
                    if (yy == y1 + 3 || yy == y1 - 3 || yy == y2 + 3 || yy == y2 - 3 ||
                            xx == x1 + 3 || xx == x1 - 3 || xx == x2 + 3 || xx == x2 - 3) return;
                    cl[(xSize * yy) + xx].isMine = 0;
                    safeMoves++;
                }
                if (cl[(xSize * y0) + x0].covNeighbors - cl[(xSize * y0) + x0].val == 2 &&
                        cl[(xSize * yy) + xx].isMine == -1) {
                    cl[(xSize * yy) + xx].isMine = 1;

                    // Увеличиваем количество соседних мин, если значение находится в диапозоне от 1 до 8
                    for (int yyy = yy - 1; yyy <= yy + 1; yyy++) {
                        if (yyy < 0 || yyy >= ySize) continue;
                        for (int xxx = xx - 1; xxx <= xx + 1; xxx++) {
                            if (xxx < 0 || xxx >= xSize || (yyy == yy && xxx == xx)) continue;
                            if (cl[(xSize * yyy) + xxx].val > 0 ||  cl[(xSize * yyy) + xxx].val < 9)
                                cl[(xSize * yyy) + xxx].nearbyMines++;
                        }
                    }
                }
            }
        }
    }

    public int[] selectSafeMove() {
        // Все безопасные перемещения идентифицируются с isMine = 0
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                if (cl[(xSize * y) + x].val != 9 || cl[(xSize * y) + x].isMine != 0) continue;
                cl[(xSize * y) + x].isMine = -1;
                return new int[] { x, y };
            }
        }

        throw new RuntimeException("Ошибка - не удалось найти безопасный ход");
    }

    // Взвешиваем вероятность каждого перемещения и выбираем самую низкую вероятность неудачи
    public int[] selectRiskyMove() {
        int prob = 99;
        int[] array = null;
        int[] blindArr = null;
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                if (cl[(xSize * y) + x].val != 9 || cl[(xSize * y) + x].isMine != -1) continue;
                int cWeight = cl[(xSize * y) + x].weight;
                if (cWeight > 0 && prob > cWeight) {
                    prob = cWeight;
                    array = new int[] { x, y };
                } else if (cWeight <= 0 ) {
                    blindArr = new int[] { x, y };
                }
            }
        }

        if (array == null && blindArr == null) {
        } else if (array == null) {
            // Выбираем на рандом, так как больше вариантов нет
            array = blindArr;
        }
        return array;
    }
}
