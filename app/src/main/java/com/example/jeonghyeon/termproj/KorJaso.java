package com.example.jeonghyeon.termproj;

/**
 * Created by JeongHyeon on 2017-03-28.
 * 입력한 단어를 기준으로 찾을 정규표현식을 생성하는 클래스
 */

public class KorJaso {
    //각 자소의 인덱스를 확인하기 위한 참고
    /*private final static char[] firstSound =
            {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};
    private final static char[] middleSound =
            {'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ','ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'};
    private final static char[] lastSound =
            {' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ',
                    'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};
    */


    public static int[] seperateKor(String str){//한글 문자열을 각 자소값으로 변환
        if(str == null){
            return null;
        }
        if(str.length() == 0){
            return null;
        }
        int[] jasoIndex = new int[str.length() * 3];//받침이 없는 것도 하나의 종성으로 취급하여, 글자수*3 크기의 int형 배열 생성
        char[] inputArray = str.toCharArray();//String을 char형 배열로
        for (int i = 0; i < inputArray.length; i++)
        {
            if (inputArray[i] >= '가' && inputArray[i] <= '힣')//한글일 때
            {
                int c = inputArray[i] - '가';//한글 유니코드는 가 + ((초성*21)+중성)*28+종성
                jasoIndex[i*3] = c / 588;//초성
                c = c % 588;//중성*28 + 종성
                jasoIndex[i*3 +1] = c / 28;//중성
                c = c % 28;//종성
                jasoIndex[i*3 +2] = c;
            }
        }
        return jasoIndex;
    }
}
