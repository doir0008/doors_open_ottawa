package com.algonquincollege.doir0008.doorsopenottawa.parsers;

import com.algonquincollege.doir0008.doorsopenottawa.model.Building;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *  Parse a JSON object for a BuildingId.
 *
 *  @author Ryan Doiron (doir0008@algonquinlive.com)
 *
 */

public class IdJSONParser {

    public static Building parseFeed(String content) {

        try {
            JSONObject jsonResponse = new JSONObject(content);
//            JSONArray buildingArray = jsonResponse.getJSONArray("buildings");
//            List<Building> buildingList = new ArrayList<>();

//            for (int i = 0; i < buildingArray.length(); i++) {


//                JSONObject obj = buildingArray.getJSONObject(i);
                Building building = new Building();

                building.setBuildingId( jsonResponse.getInt("buildingId") );
//                building.setName(obj.getString("name"));
//                building.setAddress(obj.getString("address"));
//                building.setImage(obj.getString("image"));

//                JSONArray open_hours = obj.getJSONArray("open_hours");
//                for (int j=0; j < open_hours.length(); j++) {
//                    building.addDate( open_hours.getJSONObject(j).getString("date") );
//                }
//
//                building.setDescription( obj.getString("description") );
            return building;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
