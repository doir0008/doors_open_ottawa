package com.algonquincollege.doir0008.doorsopenottawa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by ryan on 2016-12-12.
 */

public class EditBuildingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_building);

        // Setup button references
        Button saveButton = (Button) findViewById(R.id.editSaveBtn);
        Button cancelButton = (Button) findViewById(R.id.editCancelBtn);

        final EditText buildingAddress = (EditText) findViewById(R.id.editBuildingAddress);
        final EditText buildingDescription = (EditText) findViewById(R.id.editBuildingDescription);


        // Event handler for cancel button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditBuildingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Event handler for save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // We need an Editor object to make preference changes.
                SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.app_name), Context.MODE_PRIVATE );

                // PUT for my building
                RequestPackage pkg = new RequestPackage();
                pkg.setMethod( HttpMethod.PUT );
                pkg.setUri( MainActivity.REST_URI + "/" + settings.getInt("myID", 0) );
                pkg.setParam("address", buildingAddress.getText().toString() );
                pkg.setParam("description", buildingDescription.getText().toString() );

                MyTask task = new MyTask();
                task.execute( pkg );

                Intent intent = new Intent( EditBuildingActivity.this, MainActivity.class );
                startActivity( intent );
            }
        });
    }

    private class MyTask extends AsyncTask<RequestPackage, String, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            String content = HttpManager.crud( params[0], "doir0008", "password" );
            return content;
        }
    }
}
