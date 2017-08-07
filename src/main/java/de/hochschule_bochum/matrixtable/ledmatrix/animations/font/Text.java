package de.hochschule_bochum.matrixtable.ledmatrix.animations.font;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by nikla on 07.08.2017.
 */
public class Text {

    private String text;
    private int textLength;
    private char[] charArray;
    private int[][] hexData;
    private int[][] val;

    public Text(String text) {
        textLength = text.length() * Font.xL;
        text = text + "  ";
        this.text = text;
        charArray = text.toCharArray();

        hexData = new int[charArray.length][Font.yL];
        for (int i = 0; i < charArray.length; i++) {
            // https://www.cs.cmu.edu/~pattis/15-1XX/common/handouts/ascii.html
            int asciiVal = String.valueOf(charArray[i]).codePointAt(0);
            if (asciiVal > 126) asciiVal = 63; // If is out of range use "?"
            int counter = 0;
            for (int j = (asciiVal - 32) * Font.yL; j < ((asciiVal - 32) * Font.yL) + Font.yL; j++) {
                hexData[i][counter] = Font.font[j];
                counter++;
            }
        }

        val = new int[Font.yL][Font.xL * hexData.length];
        for (int i = 0; i < hexData.length; i++) {
            int[] hexVals = hexData[i];
            for (int j = 0; j < hexVals.length; j++) {
                String binVals = StringUtils.leftPad(Integer.toBinaryString(hexVals[j]), Font.xL, '0');
                char[] binValsArr = binVals.toCharArray();
                for (int k = 0; k < binValsArr.length; k++) {
                    int value = binValsArr[k] == '1' ? 1 : 0;
                    val[j][k + (i * (Font.xL - 1))] = value;
                }
            }
        }
    }

    public Text(Text cpText) {
        this(cpText.getText());
    }

    public int[][] scroll(int maxx, int maxy, int skip) {
        int skipReal = skip;
        int[][] returnArray = new int[maxy][maxx];
        int counterY = 0;
        for (int y = 0; y < val.length; y++) {
            if (counterY == maxy) continue;
            skip = skipReal;
            int[] rowData = val[y];
            int counterOffset = -1;
            int counterX = 0;
            while (skip < 0) {
                counterOffset++;
                if (counterX == maxx) continue;
                returnArray[y][counterX] = 0;
                skip++;
                counterX++;
            }
            for (int x = 0; x < rowData.length; x++) {
                counterOffset++;
                if (counterOffset < skip) continue;
                if (counterX == maxx) continue;
                returnArray[y][(skip > 0 ? x : counterX) - skip] = rowData[x];

                if (x == rowData.length - 1 && returnArray[y].length <= x) x = 0;
                counterX++;
            }
            counterY++;
        }
        return returnArray;
    }

    public int getTextLength() {
        return textLength;
    }

    public String getText() {
        return text;
    }

    public void print(int[][] data) {
        for (int i = 0; i < data.length; i++) {
            StringBuilder valS = new StringBuilder();
            int[] val = data[i];
            for (int j = 0; j < val.length; j++) {
                valS.append(val[j]);
            }
            System.out.println(valS.toString());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Text t = new Text("LoL! 123 :)");
        for (int length = -5; length < t.getTextLength(); length++) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            int[][] data = t.scroll(10, 20, length);
            for (int i = 0; i < data.length; i++) {
                StringBuilder valS = new StringBuilder();
                int[] val = data[i];
                for (int j = 0; j < val.length; j++) {
                    valS.append(val[j]);
                }
                System.out.println(valS.toString());
            }

            if (length == t.getTextLength() - 1) length = -10;
            Thread.sleep(250);
        }
    }
}
