package com.algonquincollege.doir0008.doorsopenottawa;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.algonquincollege.doir0008.doorsopenottawa.model.Building;
import com.algonquincollege.doir0008.doorsopenottawa.parsers.IdJSONParser;
import java.io.IOException;

/**
 *
 * @author Ryan Doiron (doir0008@algonquinlive.com)
 *
 */

public class NewBuildingActivity extends Activity {

    private ImageView imageView;
    private Building building;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_building);

        // Setup button references
        Button saveButton = (Button) findViewById(R.id.saveBtn);
        Button cancelButton = (Button) findViewById(R.id.cancelBtn);

        imageView = (ImageView) findViewById(R.id.imageView);

        final EditText buildingName = (EditText) findViewById(R.id.newBuildingName);
        final EditText buildingAddress = (EditText) findViewById(R.id.newBuildingAddress);
        final EditText buildingDescription = (EditText) findViewById(R.id.newBuildingDescription);
        final EditText buildingImage = (EditText) findViewById(R.id.newBuildingImage);

        // Event handler for cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( NewBuildingActivity.this, MainActivity.class );
                startActivity( intent );
            }
        });

        // Event handler for save button
        saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                // POST for my building
                Building building = new Building();
                building.setName( buildingName.getText().toString() );
                building.setAddress( buildingAddress.getText().toString() );
                building.setDescription( buildingDescription.getText().toString() );
                building.setImage( buildingImage.getText().toString() );

                RequestPackage pkg = new RequestPackage();
                pkg.setMethod( HttpMethod.POST );
                pkg.setUri( MainActivity.REST_URI );
                pkg.setParam("name", building.getName() );
                pkg.setParam("address", building.getAddress() );
                pkg.setParam("description", building.getDescription() );
                pkg.setParam("image", building.getImage() );

                MyTask task = new MyTask();
                task.execute( pkg );

                Intent intent = new Intent( NewBuildingActivity.this, MainActivity.class );
                startActivity( intent );
            }
        });

        // Event handler for clicking imageView
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class MyTask extends AsyncTask<RequestPackage, String, Building> {
        @Override
        protected Building doInBackground(RequestPackage... params) {
            String content = HttpManager.crud( params[0], "doir0008", "password" );
            building = IdJSONParser.parseFeed(content);

            // We need an Editor object to make preference changes.
            SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.app_name), Context.MODE_PRIVATE );
            SharedPreferences.Editor editor = settings.edit();

            editor.putInt( "myID", building.getBuildingId() );

            // Commit the edits!
            editor.commit();

            return building;
        }
    }
}
