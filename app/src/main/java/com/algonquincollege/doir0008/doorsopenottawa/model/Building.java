package com.algonquincollege.doir0008.doorsopenottawa.model;

import android.graphics.Bitmap;
import java.util.List;
import java.util.ArrayList;

/**
 *  Model a building from Doors Open Ottawa
 *
 *  A building has the following properties:
 *      buildingId
 *      name
 *      address
 *      image
 *      openHours
 *
 *  @author Ryan Doiron (doir0008@algonquinlive.com)
 */

public class Building {

    private String address;
    private Bitmap bitmap;
    private int buildingId;
    private String description;
    private String image;
    private String name;
    private List<String> openHours;

    public Building() {
        super();
        openHours = new ArrayList<>();
    }

    public void addDate(String date){
        openHours.add(date);
    }

    public String getAddress() { return address; }
    public Bitmap getBitmap() { return bitmap; }
    public int getBuildingId() { return buildingId; }
    public String getDescription() { return description; }
    public String getImage() { return image; }
    public String getName() { return name; }
    public List<String> getOpenHours() { return openHours; }

    public void setAddress(String address) { this.address = address + " Ottawa, Ontario"; }
    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }
    public void setBuildingId(int buildingId) { this.buildingId = buildingId; }
    public void setDescription(String description) { this.description = description; }
    public void setName(String name) { this.name = name; }
    public void setImage(String image) { this.image = image; }
}
