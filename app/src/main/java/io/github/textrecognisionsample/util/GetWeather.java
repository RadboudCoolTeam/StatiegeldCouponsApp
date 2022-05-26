package io.github.textrecognisionsample.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;



public class GetWeather {

    private final String key;
    private final String latitude;
    private final String longitude;

    public GetWeather(String key, String latitude, String longitude) {
        this.key = key;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getWeather() throws IOException {

        String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon="+ longitude +"&appid=" + key;


            StringBuilder result = new StringBuilder();
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;

            while((line = rd.readLine()) != null) {
                result.append(line);
            }

            rd.close();

            Map<String, Object> respMap = jsonToMap(result.toString());
            Map<String, Object> mainMap = jsonToMap(Objects.requireNonNull(respMap.get("main")).toString());
            Map<String, Object> windMap = jsonToMap(Objects.requireNonNull(respMap.get("wind")).toString());

            conn.disconnect();


        return mainMap.get("temp").toString();
    }

    public static Map<String, Object> jsonToMap(String str) {
        Map<String, Object> map = new Gson().fromJson(
                str, new TypeToken<HashMap<String, Object>>() {}.getType()
        );

        return map;
    }

}
