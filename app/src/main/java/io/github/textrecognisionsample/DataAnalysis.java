package io.github.textrecognisionsample;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import java.util.ArrayList;
import java.util.Arrays;


public class DataAnalysis {

    String[] pre_ana = {};
    ArrayList<String> analysis = new ArrayList<>();

    String[] albert = {"albert", "heijn"};

    public void pre_analysis(Intent data){

        data.getStringExtra("text").toLowerCase();

        pre_ana = data.getStringExtra("text").split(" ");
        analysis.addAll(Arrays.asList(pre_ana));
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String filterData(Intent data){

        pre_analysis(data);

        for(int i = 0; i < albert.length; i++) {

            for(int k = 0; k < analysis.size(); k++){

                int result_albert = LevenshteinDistance(albert[i], analysis.get(k));
            }

        }




        return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    static int LevenshteinDistance(String a, String b) {

        int[][] result = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    result[i][j] = j;
                }
                else if (j == 0) {
                    result[i][j] = i;
                }
                else {
                    result[i][j] = min(result[i - 1][j - 1]
                                    + cost(a.charAt(i - 1), b.charAt(j - 1)),
                            result[i - 1][j] + 1,
                            result[i][j - 1] + 1);
                }
            }
        }

        return result[a.length()][b.length()];
    }
    public static int cost(char a, char b) {
        return a == b ? 0 : 1;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }

}
