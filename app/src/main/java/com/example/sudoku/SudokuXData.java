package com.example.sudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SudokuXData {
    public static final String x1 = "123456789789123456456789123637218945918534267542697831364972518871345692295861374";
    public static final String x2 = "123456798798123456456798123637219845819534267542687931364872519971345682285961374";
    public static final String x3 = "123456879879123456456879123638217945917534268542698731364982517781345692295761384";
    public static final String x4 = "123456897897123456456897123638219745719534268542678931364782519981345672275961384";
    public static final String x5 = "123456978978123456456978123639217845817534269542689731364892517791345682285761394";
    public static final String x6 = "123456987987123456456987123639218745718534269542679831364792518891345672275861394";
    public static final String x7 = "123456789789123456456879123637218945918534267542697831394762518871345692265981374";
    public static final String x8 = "123456798798123456456978123637219845819534267542687931384762519971345682265891374";
    public static final String x9 = "123456879879123456456789123638217945917534268542698731394862517781345692265971384";
    public static final String x10= "123456897897123456456987123638219745719534268542678931374862519981345672265791384";

    public static final String[] xArr = {x1, x2, x3, x4, x5, x6, x7, x8, x9, x10};

    public static int indexUsed;

    public static List<Integer> xList = new ArrayList<>();

    public static List<Integer> getXList() {
        List<Integer> temp = new ArrayList<>();
        Random random = new Random();
        int index = random.nextInt(xArr.length);
        indexUsed = index;
        char[] chars = xArr[index].toCharArray();
        for(char c : chars) {
            int i = Character.getNumericValue(c);
            temp.add(i);
        }
        return temp;
    }

    public static int[][] getXArr() {
        int[][] arr = new int[9][9];
        int[] temp = new int[9];
        Random random = new Random();
        int index = random.nextInt(xArr.length);
        int position = 0;
        int count = 0;

        for(char c: xArr[index].toCharArray()) {
            temp[count] = Character.getNumericValue(c);
            if(count == 8) {
                arr[position] = temp;
                temp = new int[9];
                position++;
                count = -1;
            }
            count++;
        }
        return arr;
    }

    private static int[][] rotateClockWise(int[][] matrix) {
        int size = matrix.length;
        int[][] ret = new int[size][size];

        for (int i = 0; i < size; ++i)
            for (int j = 0; j < size; ++j)
                ret[i][j] = matrix[size - j - 1][i];
        return ret;
    }

    public static List<Integer> generateX() {
        int num = new Random().nextInt(4); //0,1,2,3
        int[][] arr = getXArr();
        for(int i = 0; i < num; i++) {
            arr = rotateClockWise(arr);
        }
        xList = twoDArrayToList(arr);
        return xList;
    }

    public static List<Integer> twoDArrayToList(int[][] twoDArray) {
        List<Integer> list = new ArrayList<>();
        for (int[] array : twoDArray) {
            for(int ele : array) {
                list.add(ele);
            }
        }
        Random rand = new Random();
        if(rand.nextBoolean())         //Reverse the order of the list
            Collections.reverse(list);
        return list;
    }
}
