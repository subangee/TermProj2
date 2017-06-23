package com.example.jeonghyeon.termproj;

/**
 * Created by JeongHyeon on 2017-04-04.
 * 배열을 다루는 함수를 모은 클래스
 * 단순 편의를 위해 작성
 */

public class ArrayUsing {

    public static int biFind(int find, int[] array){//정렬된 int형 배열에서 어떤 값을 이진탐색으로 찾는다. 시작부분
        int i = array.length/2;
        if(find == array[i]){
            return i;
        }
        else if(find > array[i]){
            return biFind(find, array, i+1, array.length-1);
        }
        else{
            return biFind(find, array, 0, i-1);
        }
    }

    private static int biFind(int find, int[] array, int start, int end){//이진탐색 동작부분
        if(start > end)
            return -1;
        int i = (start + end)/2;
        if(find == array[i]){
            return i;
        }
        else if(find > array[i]){
            return biFind(find, array, i + 1, end);
        }
        else{
            return biFind(find, array, start, i - 1);
        }
    }

    public static int findFirstIndex(int find, int[][] array){//int형 2차원배열에서 찾는 값을 갖는 행 번호를 리턴한다
        for(int i = 0; array[i][0] != -1; i++){
            if(biFind(find, array[i]) != -1){
                return i;
            }
        }
        return -1;
    }
}
