package de.hochschule_bochum.matrixtable.utils;

/**
 * Created by nikla on 05.07.2017.
 */
public class ArrayUtils {
    public static int[][] rotateArray(int[][] inputArray) {
        final int M = inputArray.length;
        final int N = inputArray[0].length;
        int[][] ret = new int[N][M];
        for (int r = 0; r < M; r++) {
            for (int c = 0; c < N; c++) {
                ret[c][M - 1 - r] = inputArray[r][c];
            }
        }
        return ret;
    }

}
