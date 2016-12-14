package com.algonquincollege.doir0008.doorsopenottawa;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.algonquincollege.doir0008.doorsopenottawa.model.Building;
import com.algonquincollege.doir0008.doorsopenottawa.parsers.BuildingJSONParser;
import com.algonquincollege.doir0008.doorsopenottawa.parsers.IdJSONParser;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Ryan Doiron (doir0008@algonquinlive.com)
 *
 */

public class NewBuildingActivity extends Activity {

    private final int CAMERA_REQUEST_CODE = 100;
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



                // Send picture
//                RequestPackage pkg1 = new RequestPackage();
//                pkg.setMethod( HttpMethod.POST );
//                pkg.setUri( MainActivity.REST_URI );
//
//                imageView.buildDrawingCache();
//                Bitmap bitmap = imageView.getDrawingCache();
//                ByteArrayOutputStream stream=new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
//                byte[] image=stream.toByteArray();
//                String img_str = Base64.encodeToString(image, 0);
//

//                SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.app_name), Context.MODE_PRIVATE );


//                pkg.setParam("image", img_str );
//                pkg.setParam("id", settings.getInt( "myID", 0 ););
//
//                MyTask task = new MyTask();
//                task.execute( pkg1 );






                Intent intent = new Intent( NewBuildingActivity.this, MainActivity.class );
                startActivity( intent );
            }
        });

        // Event handler for clicking imageView
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
//                startActivityForResult( intent, CAMERA_REQUEST_CODE );



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
                // Log.d(TAG, String.valueOf(bitmap));

                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }


//            Uri uri = data.getData();
//            String[] projection = { MediaStore.Images.Media.DATA };
//
//            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//            cursor.moveToFirst();
//
//            Log.d(TAG, DatabaseUtils.dumpCursorToString(cursor));
//
//            int columnIndex = cursor.getColumnIndex(projection[0]);
//            String picturePath = cursor.getString(columnIndex); // returns null
//            cursor.close();
//





        }
    }






//    /**
//     * This method is called after the user finishes using the Camera.
//     *
//     * @param requestCode The integer request code originally supplied to startActivityForResults(),
//     *                    allowing you to identity who this result came from.
//     * @param resultCode The integer result code returned by the child activity through its
//     *                   setResult();
//     * @param resultIntent An Intent, which can return result data to caller (various data can
//     *                     be attached to Intent "extras".
//     */
//    @Override
//    protected void onActivityResult( int requestCode, int resultCode, Intent resultIntent ) {
//        Bundle extras;
//        Bitmap imageBitmap;
//
//        // Did the user cancel the Camera?
//        // Abort
//        if (resultCode == RESULT_CANCELED) {
//            Toast.makeText( getApplicationContext(), "Cancelled!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // We need a switch statement because this method could be called if our app called other
//        // activities. For example, we could extend the app to take a Video. Then we would have a
//        // case like: case VIDEO_REQUEST_CODE: // code to handle the video capture.
//        switch ( requestCode ) {
//            case CAMERA_REQUEST_CODE:
//                // the photo is returned as an extra to the Intent
//                extras = resultIntent.getExtras();
//                // the photo is referred to by "data", and is returned as a Bitmap.
//                imageBitmap = (Bitmap) extras.get( "data" );
//
//                // display the photo
//                // set the <ImageView> to the photo
//                if ( imageBitmap != null ) {
//                    imageView.setImageBitmap( imageBitmap );
//                }
//                break;
//        }
//    }


    private class MyTask extends AsyncTask<RequestPackage, String, Building> {



        @Override
        protected Building doInBackground(RequestPackage... params) {

            // String content = HttpManager.getData(params[0]);
//            String content = HttpManager.getData( params[0], "doir0008", "password" );
            String content = HttpManager.crud( params[0], "doir0008", "password" );
            Log.i("POST results", content);



            building = IdJSONParser.parseFeed(content);

//            building.getBuildingId()

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
