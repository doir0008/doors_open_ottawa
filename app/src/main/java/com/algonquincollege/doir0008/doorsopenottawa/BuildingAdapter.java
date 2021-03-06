package com.algonquincollege.doir0008.doorsopenottawa;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.algonquincollege.doir0008.doorsopenottawa.model.Building;

/**
 * Purpose: customize the Building cell for each building displayed in the ListActivity (i.e. MainActivity).
 * Usage:
 *   1) extend from class ArrayAdapter<YourModelClass>
 *   2) @override getView( ) :: decorate the list cell
 *
 * Based on the Adapter OO Design Pattern.
 *
 * @author Ryan Doiron (doir0008@algonquinlive.com)
 *
 * Reference: based on DisplayList in "Connecting Android Apps to RESTful Web Services" with David Gassner
 */


public class BuildingAdapter extends ArrayAdapter<Building> {

    private Context context;
    private List<Building> buildingList;
    private ArrayList favs = new ArrayList();

    // cache the binary image for each building
    private LruCache<Integer, Bitmap> imageCache;

    public BuildingAdapter(Context context, int resource, List<Building> objects) {
        super(context, resource, objects);
        this.context = context;
        this.buildingList = objects;

        // instantiate the imageCache
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() /1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        // load up fav buildings
        int arraySize = settings.getInt("array_size", 0);
        for ( int i = 0; i < arraySize; i++ ) {
            favs.add( settings.getString("favItem" + i, null) );
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_building, parent, false);

        //Display building name in the TextView widget
        final Building building = buildingList.get(position);
        TextView tv = (TextView) view.findViewById(R.id.textView1);
        TextView tv1 = (TextView) view.findViewById(R.id.textView3);
        tv.setText(building.getName());
        tv1.setText(building.getAddress());
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);

        // favourites logic
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        checkBox.setChecked( favs.contains( building.getBuildingId() + "" ) );
        checkBox.setTag( building.getBuildingId() + "" );

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myBuildingId = (String) v.getTag();
                if ( favs.contains( myBuildingId ) ) {
                    favs.remove( myBuildingId );
                } else {
                    favs.add( myBuildingId );
                }
                SharedPreferences.Editor mEdit1 = settings.edit();
                mEdit1.putInt( "array_size", favs.size() );
                for ( int i = 0; i < favs.size(); i++ ) {
                    mEdit1.remove( "favItem" + i );
                    mEdit1.putString( "favItem" + i, favs.get( i ).toString() );
                    mEdit1.commit();
                }
            }
        });

        // Display building photo in ImageView widget
        Bitmap bitmap = imageCache.get(building.getBuildingId());
        if (bitmap != null) {
            ImageView image = (ImageView) view.findViewById(R.id.buildingImage);
            image.setImageBitmap(building.getBitmap());
        }
        else {
            BuildingAndView container = new BuildingAndView();
            container.building = building;
            container.view = view;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }

        return view;
    }

    // container for AsyncTask params
    private class BuildingAndView {
        public Building building;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<BuildingAndView, Void, BuildingAndView> {

        @Override
        protected BuildingAndView doInBackground(BuildingAndView... params) {

            BuildingAndView container = params[0];
            Building building = container.building;

            try {
                String imageUrl = MainActivity.IMAGES_BASE_URL + building.getImage();
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                building.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (Exception e) {
                System.err.println("IMAGE: " + building.getName() );
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(BuildingAndView result) {
            // Guard for null results to prevent crashing
            if( result != null ) {
                ImageView image = (ImageView) result.view.findViewById(R.id.buildingImage);
                image.setImageBitmap(result.bitmap);
                imageCache.put(result.building.getBuildingId(), result.bitmap);
            }
        }
    }
}