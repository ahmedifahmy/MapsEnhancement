package com.test.flatmarkers;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.maps.android.ui.IconGenerator;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MapPaneActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    private static int BEST_PATH_COLOR = Color.rgb(20, 203, 235);
    private static int LIGHT_GRAY = Color.rgb(160, 160, 160);
    private final double DEST_LAT =  23.031924; //24.7443003;
    private final double DEST_LNG =  72.590717; //46.6807419;
    private static final int[] colors = {LIGHT_GRAY, Color.GRAY};
    private LatLng currentLocation;
    GMapV2Direction md;
    MarkerOptions myLocationMarkerOptions;
    MarkerOptions accidentMarkerOptions;
    GoogleMap mMap;

//    List<DirectionsRoute> theRoutes = new ArrayList<DirectionsRoute>();
    Marker myLocationMarker;

    private DirectionsRoute selectedRout;
    private GoogleApiClient mGoogleApiClient;
    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map_pane);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {
        double myLat = location.getLatitude();
        double myLng = location.getLongitude();
        currentLocation = new LatLng(myLat,myLng);
        myLocationMarker.setPosition(currentLocation);
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                REQUEST,
                this);  // LocationListener

        Location  myLocation = null;
        try{
            myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        setUpMapIfNeeded(myLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }




    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap(Location myLocation)} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded(Location myLocation) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap(myLocation);
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap(Location myLocation) {

        double myLat = myLocation.getLatitude();
        double myLng = myLocation.getLongitude();
        LatLng accident = new LatLng(DEST_LAT,DEST_LNG);
        currentLocation = new LatLng(myLat,myLng);
        md = new GMapV2Direction();
        myLocationMarkerOptions = new MarkerOptions().position(currentLocation).title("Investigator").icon(BitmapDescriptorFactory.fromResource(R.drawable.overlay_user));
        accidentMarkerOptions = new MarkerOptions().position(accident).title("Accident").icon(BitmapDescriptorFactory.fromResource(R.drawable.overlay_car));
        myLocationMarker = mMap.addMarker(myLocationMarkerOptions);
        mMap.addMarker(accidentMarkerOptions);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
//                for (DirectionsRoute route : theRoutes) {   //selecting the route
//                    if (route.getMarker().getPosition().equals(marker.getPosition())) {
//                        selectedRout = route;
//                    }
//                }
                return true;  //to cancel the default behavior which is camera moves and google marker menu appears

            }
        });
        new AsynchT().execute(currentLocation, accident);
    }

    private class AsynchT extends AsyncTask<LatLng, Void, Document> {
        @Override
        protected Document doInBackground(LatLng... params) {
            Document doc = md.getDocument(params[0], params[1],
            GMapV2Direction.MODE_DRIVING);
            return doc;
        }

       @Override
       protected void onPostExecute(Document result) {
           drawPolylinesAndZoom(result);
           final Button navigateButton = (Button) findViewById(R.id.navigateButton2);
           navigateButton.setVisibility(View.VISIBLE);
           navigateButton.setOnClickListener(new View.OnClickListener() {
               public void onClick(View v) {

                   double curLat = currentLocation.latitude;
                   double curLng = currentLocation.longitude;
                   LatLng theWaypoint = selectedRout.getWaypoint();

                   startNavigationApp(curLat, curLng, theWaypoint, false);
               }
           });

           final Button startButton = (Button) findViewById(R.id.startButton);
           startButton.setVisibility(View.VISIBLE);
           startButton.setOnClickListener(new View.OnClickListener() {
               public void onClick(View v) {
                   Uri gmmIntentUri = Uri.parse("google.navigation:&q=" + DEST_LAT + "," + DEST_LNG + "&mode=d");
                   Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                   mapIntent.setPackage("com.google.android.apps.maps");
                   startActivity(mapIntent);
               }
           });

//            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {  //select routes
//                @Override
//                public void onMapClick(LatLng clickCoords) {
//                    float zoom =  mMap.getCameraPosition().zoom;
//                    int intZoom = (int)zoom;
//                    int[] zoomLevels = {9, 10, 11, 12, 13, 14, 15};
//                    int[] distances = {2000, 1500, 1000, 500, 300, 200, 100};
//                    boolean zoomInRange = false;
//                    int zoomIndex = -1;
//                    for (int zi=0; zi<zoomLevels.length; zi++){
//                        int iZoom = zoomLevels[zi];
//                        if (intZoom == iZoom){
//                            zoomInRange = true;
//                            zoomIndex = zi;
//                            break;
//                        }
//                    }
//                    Log.e("7amada", ">>>>>>>>>>>>>Zoom"+ zoom);
//                    boolean routeFound = false;
//
//                    for (int ii = 0; ii<theRoutes.size();ii++) {
//                        DirectionsRoute dRoute = theRoutes.get(ii);
//                        boolean isBestRoute = false;
//                        if (dRoute.equals(selectedRout)){
//                           isBestRoute = true;
//                        }
//                        PolylineOptions polyline1 = dRoute.getPolyline();
//                        for (LatLng polyCoords : polyline1.getPoints()) {
//                            float[] results = new float[1];
//                            Location.distanceBetween(clickCoords.latitude, clickCoords.longitude,
//                                    polyCoords.latitude, polyCoords.longitude, results);
//                            if (! isBestRoute) {
//                                int distanceRange = zoomInRange ? distances[zoomIndex] : intZoom > 15 ? 100 : 250;
//                                if ( results[0] < distanceRange) {
//                                    // If distance is less than 100 meters, this is your polyline
//                                    Log.e("7amada", "Found @ " + clickCoords.latitude + " " + clickCoords.longitude);
//                                    polyline1.color(BEST_PATH_COLOR);
//                                    selectedRout = dRoute;
//                                    routeFound = true;
//                                }
//
//                            }
//                        }
//                    }
//                    if (routeFound) {
//                        mMap.clear();
//                        int ri = 0;
//                        for (DirectionsRoute route : theRoutes) {
//                            if (route.equals(selectedRout)) {
//                                selectedRout.getPolyline().color(BEST_PATH_COLOR);
//                            } else {
//                                route.getPolyline().color(colors[ri++]);
//                                mMap.addPolyline(route.getPolyline());
//
//                            }
//                            addIcon(route.getDurationText(),route.getMarker());
//                        }
//
//                        myLocationMarker = mMap.addMarker(myLocationMarkerOptions);
//                        mMap.addMarker(accidentMarkerOptions);
//                        for (int i=0;i<2;i++) {
//                            mMap.addPolyline(selectedRout.getPolyline()); //two times to make sure it overwrites any other color -- Don't know why!
//                        }
//
//
//                    }
//                }
//            });
//
//
//
//
//           final Button button = (Button) findViewById(R.id.navigateButton);
//           button.setVisibility(View.VISIBLE);
//           button.setOnClickListener(new View.OnClickListener() {
//               public void onClick(View v) {
//
//                   double curLat = currentLocation.latitude;
//                   double curLng = currentLocation.longitude;
//                   LatLng theWaypoint = selectedRout.getWaypoint();
//
//                   startNavigationApp(curLat, curLng, theWaypoint, true);
//               }
//           });
//
       }
    }



    private void drawPolylinesAndZoom(Document result) {

        List<Double> latList = new ArrayList<Double>();
        List<Double> longList = new ArrayList<Double>();
        final ArrayList<DirectionsRoute> directionRoutes = md.getDirection(result);
        LatLng boundsSouthWest = null;
        LatLng boundsNorthEast = null;
        if (directionRoutes != null) {
            Collections.sort(directionRoutes);
            selectedRout = directionRoutes.get(0);
            if (directionRoutes.size() > 3) {
                for (int i = directionRoutes.size() - 1; i > 2; i--) {
                    directionRoutes.remove(i);
                }
            }
            for (int j = 0; j < directionRoutes.size(); j++) {
                DirectionsRoute directionResult = directionRoutes.get(j);
                LatLng southWest = directionResult.getSouthWest();
                LatLng northEast = directionResult.getNorthEast();
                latList.add(southWest.latitude);
                latList.add(northEast.latitude);
                longList.add(southWest.longitude);
                longList.add(northEast.longitude);
                List<LatLng> directionPoints = directionResult.getGeopoints();
                int color = BEST_PATH_COLOR;
                PolylineOptions rectLine = new PolylineOptions().width(15).color(directionResult.equals(selectedRout) ? color :
                        colors[j - 1]);

                for (int i = 0; i < directionPoints.size(); i++) {
                    rectLine.add(directionPoints.get(i));
                }
                directionResult.setPolyline(rectLine);

//                theRoutes.add(directionResult);
            }
            drawPolylines(directionRoutes);

        }else {
            latList.add(currentLocation.latitude);
            latList.add(DEST_LAT);
            longList.add(currentLocation.longitude);
            longList.add(DEST_LNG);

        }
        Collections.sort(latList);
        Collections.sort(longList);
        boundsSouthWest = new LatLng(latList.get(0),longList.get(0));
        boundsNorthEast = new LatLng(latList.get(latList.size()-1),longList.get(longList.size()-1));
        LatLngBounds bounds = new LatLngBounds(boundsSouthWest, boundsNorthEast);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 175)); //padding of 175
    }

    private void startNavigationApp(double curLat, double curLng, LatLng theWaypoint, boolean useWayPoint) {
        String waypointString = "";
        if (theWaypoint != null && useWayPoint) {
            double waypointLat = theWaypoint.latitude;
            double waypointLng = theWaypoint.longitude;
            waypointString = "'" + waypointLat + "," + waypointLng + "'/";
        }
        String uri = "https://www.google.com/maps/dir/'" + curLat + "," + curLng + "'/" + waypointString + "'" + DEST_LAT + "," + DEST_LNG + "'";
        Log.e("Navigation uri", uri);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    private void drawPolylines(ArrayList<DirectionsRoute> directionRoutes) {
        for (int routeIndex= directionRoutes.size()-1; routeIndex>=0; routeIndex--){
            DirectionsRoute directionResult = directionRoutes.get(routeIndex);
            Polyline polyline = mMap.addPolyline(directionResult.getPolyline());
            addIcon(directionResult.getDurationText(),directionResult.getMarker());
        }
    }

    private void addIcon(String text, MarkerOptions markerOptions) {
        IconGenerator iconFactory = new IconGenerator(this);
        iconFactory.setRotation(90);
        iconFactory.setContentRotation(-90);
        iconFactory.setStyle(IconGenerator.STYLE_WHITE);
        iconFactory.setTextAppearance(R.style.Icon_TextAppearance);
        markerOptions.
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text,175,120))).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        mMap.addMarker(markerOptions);

    }


}