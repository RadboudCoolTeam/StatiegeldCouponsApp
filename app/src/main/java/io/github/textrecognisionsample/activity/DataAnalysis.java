package io.github.textrecognisionsample.activity;

import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;


public class DataAnalysis {

    ArrayList<String> analysis = new ArrayList<>();

    final String[] albert = {"albert", "heijn"};
    final String[] jumbo = {"jumbo"};
    final String[] lidl = {"lidl", "ldl"};
    final String[] aldi = {"aldi", "aldl"};

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filterData(Intent data){

        HashMap<String, Integer> shops = new HashMap<>();

        pre_analysis(data, analysis);

        shops.put("Albert Heijn",0);
        shops.put("Jumbo",0);
        shops.put("Aldi",0);

        if(Compability(albert, analysis)){
            data.putExtra("shop", "Albert Heijn");
        } else if(Compability(jumbo, analysis)) {
            data.putExtra("shop", "Jumbo");
        } else if(Compability(lidl,analysis)) {
            data.putExtra("shop", "Lidl");
        } else if(Compability(aldi, analysis)) {
            data.putExtra("shop", "Aldi");
        } else {
            data.putExtra("shop", "No shop detected");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean Compability(String[] array, ArrayList<String> analysis) {

        ArrayList<Integer> results = new ArrayList<>();
        int result;

        for(int i = 0; i < array.length; i++) {

            for(int k = 0; k < analysis.size(); k++){

                result = LevenshteinDistance(albert[i], analysis.get(k));
                results.add(result);
            }

        }
        Collections.sort(results);

        return Collections.min(results) < 3;
    }

    public void pre_analysis(Intent data,  ArrayList<String> analysis){

        String[] pre_ana = {};
        data.getStringExtra("text").toLowerCase();

        pre_ana = data.getStringExtra("text").split(" ");
        analysis.addAll(Arrays.asList(pre_ana));
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
