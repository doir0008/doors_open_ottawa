package com.algonquincollege.doir0008.doorsopenottawa.parsers;

import com.algonquincollege.doir0008.doorsopenottawa.model.Building;

import org.json.JSONException;
import org.json.JSONObject;


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
                Building building = new Building();
                building.setBuildingId( jsonResponse.getInt("buildingId") );
            return building;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
