package com.yourcompany.rentalmanagement.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressData {
    private static Map<String, List<String>> provinceCities = new HashMap<>();
    public static Map<String, List<String>> cityWards = new HashMap<>();

    public static Map<String, List<String>> fetchProvinceData() {
        List<String> provinces = new ArrayList<>();
            try {
                String apiUrl = "https://provinces.open-api.vn/api/p/";
                String jsonResponse = getJsonFromApi(apiUrl);

                JSONArray provincesJson = new JSONArray(jsonResponse);

                List<String> cities = new ArrayList<>();
                for (int i = 0; i < provincesJson.length(); i++) {
                    JSONObject provinceJson = provincesJson.getJSONObject(i);
                    String provinceName = provinceJson.getString("name");
                    String provinceCode = String.valueOf(provinceJson.getInt("code"));

                    provinces.add(provinceName);
                    fetchCityData(provinceCode, provinceName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        return provinceCities;
    }

    private static void fetchCityData(String provinceCode, String provinceName) throws IOException {
        String apiUrl = "https://provinces.open-api.vn/api/p/" + provinceCode + "?depth=2";
        String jsonResponse = getJsonFromApi(apiUrl);

        JSONObject provinceJson = new JSONObject(jsonResponse);
        JSONArray districtsJson = provinceJson.getJSONArray("districts");

        List<String> cities = new ArrayList<>();

        for (int i = 0; i < districtsJson.length(); i++) {
            JSONObject cityJson = districtsJson.getJSONObject(i);
            String cityName = cityJson.getString("name");
            String cityCode = String.valueOf(cityJson.getInt("code"));
            cities.add(cityName);

            fetchWardData(cityCode, cityName);
        }

        provinceCities.put(provinceName, cities);
    }

    private static void fetchWardData(String cityCode, String cityName) throws IOException {
        String apiUrl = "https://provinces.open-api.vn/api/d/" + cityCode + "?depth=2";
        String jsonResponse = getJsonFromApi(apiUrl);

        JSONObject wardJson = new JSONObject(jsonResponse);
        JSONArray wardsJson = wardJson.getJSONArray("wards");

        List<String> wards = new ArrayList<>();

        for (int i = 0; i < wardsJson.length(); i++) {
            JSONObject wardJsonObject = wardsJson.getJSONObject(i);
            String wardName = wardJsonObject.getString("name");
            wards.add(wardName);
        }
        cityWards.put(cityName, wards);
    }

    private static String getJsonFromApi(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        }
    }
}
