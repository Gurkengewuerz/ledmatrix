package de.hochschule_bochum.tetris.utils;

/**
 * Created by nikla on 05.07.2017.
 */
public class ArrayUtils {
    public static int[][] cloneArray(int[][] src) {
        int length = src.length;
        int[][] target = new int[length][src[0].length];
        for (int i = 0; i < length; i++) {
            System.arraycopy(src[i], 0, target[i], 0, src[i].length);
        }
        return target;
    }

    public static int[][] rotateArray(int[][] inputArray) {
        int side = inputArray.length;

        int[][] newArray = ArrayUtils.cloneArray(inputArray);

        for (int i = 0; i < side; i++) {
            for (int j = 0; j < side; j++) {
                newArray[j][side - i - 1] = inputArray[i][j];
            }
        }

        return newArray;
    }

}
