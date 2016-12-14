package com.algonquincollege.doir0008.doorsopenottawa;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.ListView;

import com.algonquincollege.doir0008.doorsopenottawa.model.Building;
import com.algonquincollege.doir0008.doorsopenottawa.parsers.BuildingJSONParser;
//import com.algonquincollege.doir0008.doorsopenottawa.HttpManager;
//import com.algonquincollege.doir0008.doorsopenottawa.HttpMethod;
//import com.algonquincollege.doir0008.doorsopenottawa.RequestPackage;


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
//    private ArrayList favBuildings = new ArrayList();

    private Context myContext = this;

//    public int myID = 123;

    // Swipe refresh
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

        // Swipe refresh
        swipeRefreshList = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_list);
        swipeRefreshList.setOnRefreshListener(this);

        // TODO: SharedPrefs
        final SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.app_name), Context.MODE_PRIVATE );

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

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Building theSelectedBuilding = buildingList.get( position );

                if ( theSelectedBuilding.getBuildingId() == settings.getInt("myID", 0)) {
                    // Setup an intent to pass data to next activity
                    Intent intent = new Intent(MainActivity.this, EditBuildingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(intent);
                } else {
                    // not my building
                    Toast.makeText(myContext, "You can only edit your own building.", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

////        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
//        // load up fav buildings
//        int arraySize = settings.getInt("array_size", 0);
//        for ( int i = 0; i < arraySize; i++ ) {
//            favBuildings.add( settings.getString("favItem" + i, null) );
//        }

        if (isOnline()) {
            requestData( REST_URI );
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }

        // Swipe refresh
        this.onRefresh();

        // TODO: Search
        handleIntent(getIntent());
    }

    // TODO: Search
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    // TODO: Search
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        // TODO: Adding search capabilities
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


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
            // start an intent and switch to NewBuildingActivity
            Intent intent = new Intent( MainActivity.this, NewBuildingActivity.class );
            startActivity( intent );
        }

        if (item.getItemId() == R.id.action_put_data) {
            if (isOnline()) {
//                updatePlanet( REST_URI );

                Intent intent = new Intent( MainActivity.this, EditBuildingActivity.class );
                startActivity( intent );

            } else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
        }

        if (item.getItemId() == R.id.action_delete_data) {
            if (isOnline()) {
//                deletePlanet( REST_URI );

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                // set title
                alertDialogBuilder.setTitle("Confirm Delete");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Do you really want to delete my building?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
//                                MainActivity.this.finish();

                                // need to get ID of my building
                                SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.app_name), Context.MODE_PRIVATE );

                                // send request to delete
                                RequestPackage pkg = new RequestPackage();
                                pkg.setMethod( HttpMethod.DELETE );
                                pkg.setUri( REST_URI + "/" + settings.getInt("myID", 0) );

                                MyTask task = new MyTask();
                                task.execute( pkg );
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();











//                    // need to get ID of my building
//                SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.app_name), Context.MODE_PRIVATE );
////                settings.getInt("myID", 0);
//
//                    // send request to delete
//                    RequestPackage pkg = new RequestPackage();
//                    pkg.setMethod( HttpMethod.DELETE );
//                    pkg.setUri( REST_URI + "/" + settings.getInt("myID", 0) );
//
//                    MyTask task = new MyTask();
//                    task.execute( pkg );



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

        // new http methods
        RequestPackage pkg = new RequestPackage();
        pkg.setMethod( HttpMethod.GET );
        pkg.setUri( uri );
        MyTask task = new MyTask();
        task.execute( pkg );

//        MyTask task = new MyTask();
//        task.execute(uri);
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

    // Swipe refresh
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


//    // TODO: SharedPrefs - remember saved settings
//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        // We need an Editor object to make preference changes.
//        SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.app_name), Context.MODE_PRIVATE );
//        SharedPreferences.Editor editor = settings.edit();
//
//        editor.putInt( "myID",   mModel.getRed() );
//
//        // Commit the edits!
//        editor.commit();
//    }

//    private class MyTask extends AsyncTask<String, String, List<Building>> {
    private class MyTask extends AsyncTask<RequestPackage, String, List<Building>> {


        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Building> doInBackground(RequestPackage... params) {

            // String content = HttpManager.getData(params[0]);
//            String content = HttpManager.getData( params[0], "doir0008", "password" );
            String content = HttpManager.crud( params[0], "doir0008", "password" );

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
