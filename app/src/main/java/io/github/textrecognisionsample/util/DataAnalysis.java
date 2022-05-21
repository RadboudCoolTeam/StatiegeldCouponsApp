package io.github.textrecognisionsample.util;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.github.textrecognisionsample.model.SupermarketChain;


public class DataAnalysis {

    private ArrayList<String> analysis = new ArrayList<>();

    private final List<Pair<SupermarketChain, String[]>> shopData = new ArrayList<>();

    private final String[] albert = {"albert", "heijn"};
    private final String[] jumbo = {"jumbo"};
    private final String[] lidl = {"lidl", "ldl", "lgdl"};
    private final String[] aldi = {"aldi", "aldl"};

    private final String data;

    public DataAnalysis(String data) {
        this.data = data;
        shopData.add(Pair.of(SupermarketChain.AH, new String[]{"albert", "heijn"}));
        shopData.add(Pair.of(SupermarketChain.JUMBO, new String[]{"jumbo"}));
        shopData.add(Pair.of(SupermarketChain.LIDL, new String[]{"lidl", "ldl", "lgdl"}));
        shopData.add(Pair.of(SupermarketChain.ALDI, new String[]{"aldi", "aldl"}));
        shopData.add(Pair.of(SupermarketChain.COOP, new String[]{"coop"}));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filterData(Intent data) {

        HashMap<SupermarketChain, Integer> shops = new HashMap<>();

        pre_analysis(analysis);

        for (SupermarketChain value : SupermarketChain.values()) {
            shops.put(value, 0);
        }

        for (Pair<SupermarketChain, String[]> pair : shopData) {
            shops.put(pair.getFirst(), Compability(pair.getSecond(), analysis));
        }

        data.putExtra("shop", shops.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .limit(1)
                .findAny()
                .get().getKey().getFriendlyName()
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public int Compability(String[] array, ArrayList<String> analysis) {

        ArrayList<Integer> results = new ArrayList<>();
        int result;

        for (int i = 0; i < array.length; i++) {
            for (int k = 0; k < analysis.size(); k++) {
                result = LevenshteinDistance(array[i], analysis.get(k));
                results.add(result);
            }
        }

        Collections.sort(results);

        return Collections.min(results);
    }

    public void pre_analysis(ArrayList<String> analysis) {
        String[] pre_ana = data.toLowerCase(Locale.ROOT)
                .split("[ \n]");

        analysis.addAll(Arrays.asList(pre_ana));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    static int LevenshteinDistance(String a, String b) {

        int[][] result = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    result[i][j] = j;
                } else if (j == 0) {
                    result[i][j] = i;
                } else {
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
