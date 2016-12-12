package com.algonquincollege.doir0008.doorsopenottawa;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ListView;

import com.algonquincollege.doir0008.doorsopenottawa.model.Building;
import com.algonquincollege.doir0008.doorsopenottawa.parsers.BuildingJSONParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Displaying web service data in a ListActivity.
 *
 * @see {BuildingAdapter}
 * @see {res.layout.item_building.xml}
 *
 * @author Ryan Doiron (doir0008@algonquinlive.com)
 *
 * Reference: based on DisplayList in "Connecting Android Apps to RESTful Web Services" with David Gassner
 */

 public class MainActivity extends ListActivity implements SwipeRefreshLayout.OnRefreshListener {
//public class MainActivity extends ListActivity {

    // URL to RESTful API Service hosted on Bluemix account.
    public static final String IMAGES_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";

    private static final String ABOUT_DIALOG_TAG;
    private static final String LOG_TAG;

    private ProgressBar pb;
    private List<MyTask> tasks;

    private List<Building> buildingList;

    // TODO: Swipe refresh
    SwipeRefreshLayout swipeRefreshList;

    static {
        ABOUT_DIALOG_TAG = "About Dialog";
        LOG_TAG = "***";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();

        // TODO: Swipe refresh
        swipeRefreshList = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_list);
        swipeRefreshList.setOnRefreshListener(this);

        // single selection && register this ListActivity as the event handler
        getListView().setChoiceMode( ListView.CHOICE_MODE_SINGLE );
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Building theSelectedBuilding = buildingList.get( position );

                // Setup an intent to pass data to next activity
                Intent intent = new Intent( MainActivity.this, DetailActivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                intent.putExtra( "buildingAddress", theSelectedBuilding.getAddress() );
                intent.putExtra( "buildingDescription", theSelectedBuilding.getDescription() );
                intent.putExtra( "buildingName", theSelectedBuilding.getName() );
                intent.putExtra( "buildingOpenHours", theSelectedBuilding.getOpenHours().toString() );

                startActivity( intent );
            }
        });

        if (isOnline()) {
            requestData( REST_URI );
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }

        // TODO: Swipe refresh
        this.onRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//        int id = item.getItemId();
//
//        if (id == R.id.action_about) {
//            DialogFragment newFragment = new AboutDialogFragment();
//            newFragment.show(getFragmentManager(), ABOUT_DIALOG_TAG);
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.action_about) {
            DialogFragment newFragment = new AboutDialogFragment();
            newFragment.show(getFragmentManager(), ABOUT_DIALOG_TAG);
            return true;
        }

        if (item.getItemId() == R.id.action_post_data) {
            if (isOnline()) {
//                createPlanet( REST_URI );
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }

        if (item.getItemId() == R.id.action_put_data) {
            if (isOnline()) {
//                updatePlanet( REST_URI );
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }

        if (item.getItemId() == R.id.action_delete_data) {
            if (isOnline()) {
//                deletePlanet( REST_URI );
            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }

        if ( item.isCheckable() ) {
            // leave if the list is null
            if ( buildingList == null ) {
                return true;
            }

            // which sort menu item did the user pick?
            switch( item.getItemId() ) {
                case R.id.action_sort_name_asc:
                    Collections.sort( buildingList, new Comparator<Building>() {
                        @Override
                        public int compare( Building lhs, Building rhs ) {
                            Log.i( "BUILDINGS", "Sorting buildings by name (a-z)" );
                            return lhs.getName().compareTo( rhs.getName() );
                        }
                    });
                    break;

                case R.id.action_sort_name_dsc:
                    Collections.sort( buildingList, Collections.reverseOrder(new Comparator<Building>() {
                        @Override
                        public int compare( Building lhs, Building rhs ) {
                            Log.i( "BUILDINGS", "Sorting buildings by name (z-a)" );
                            return lhs.getName().compareTo( rhs.getName() );
                        }
                    }));
                    break;
            }
            // remember which sort option the user picked
            item.setChecked( true );
            // re-fresh the list to show the sort order
            ((ArrayAdapter)getListAdapter()).notifyDataSetChanged();
        }
        return true;
    }

    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }

    protected void updateDisplay() {
        // Use BuildingAdapter to display data
        BuildingAdapter adapter = new BuildingAdapter(this, R.layout.item_building, buildingList);
        setListAdapter(adapter);
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("TAG","onDestroy");
        if (isOnline()) {
            requestData( IMAGES_BASE_URL + "users/logout" );
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }
    }

    // TODO: Swipe refresh
    @Override
    public void onRefresh() {
        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

        // This method performs the actual data-refresh operation.
        // The method calls setRefreshing(false) when it's finished.
        // myUpdateOperation();
        if (isOnline()) {
            requestData( REST_URI );
            swipeRefreshList.setRefreshing(false);
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }
    }

    private class MyTask extends AsyncTask<String, String, List<Building>> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Building> doInBackground(String... params) {

            // String content = HttpManager.getData(params[0]);
            String content = HttpManager.getData( params[0], "doir0008", "password" );
            buildingList = BuildingJSONParser.parseFeed(content);

            return buildingList;
        }

        @Override
        protected void onPostExecute(List<Building> result) {

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }

            buildingList = result;
            updateDisplay();
        }
    }
}
