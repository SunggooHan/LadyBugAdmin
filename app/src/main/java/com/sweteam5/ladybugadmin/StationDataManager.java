package com.sweteam5.ladybugadmin;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class StationDataManager {

    // Station data array read from json file
    public Station[] stations;

    // Read station data from json file and initialize it
    public boolean init(Context context) {
        String json = getJsonString(context);
        boolean result = parseJson(json);
        return result;
    }

    // Read json data from json tile
    private String getJsonString(Context context) {
        String json = "";

        try {
            InputStream is = context.getAssets().open("stations.json");

            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }

    // Parse string typed json data to Station object array and returns true if successful.
    private boolean parseJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);

            JSONArray stationArray = jsonObject.getJSONArray("Stations");
            stations = new Station[stationArray.length()];

            for(int i = 0; i < stationArray.length(); i++)
            {
                JSONObject stationObj = stationArray.getJSONObject(i);

                stations[i] = new Station();
                stations[i].setName(stationObj.getString("name"));
                stations[i].setLatitude(stationObj.getDouble("latitude"));
                stations[i].setLongitude(stationObj.getDouble("longitude"));
                stations[i].setIndex(i * 2);
            }
            return true;

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get Station name array from station data array
    public String[] getStationNameArray() {
        String[] list = new String[stations.length];
        for(int i = 0; i < list.length; i++)
            list[i] = stations[i].getName();
        return list;
    }
}
