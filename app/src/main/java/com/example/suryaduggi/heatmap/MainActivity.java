package com.example.suryaduggi.heatmap;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
//import android.location.LocationListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.maps.android.heatmaps.Gradient;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {


    private GoogleMap map;
    private List<LatLng> list;
    private Context con;
    Button refresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



            refresh = (Button) findViewById(R.id.refreshButton);
            Parse.initialize(this, "leH3EigZZKFsc3rcM7EWGLS9H3NUHMu7HWBuYkRp", "9RSzw2F6Yb1Qg9aBjUaIPSBwQhHvWPpqV10cR0Ir");
            ParseQuery<ParseObject> query = ParseQuery.getQuery("BroncoData");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        ParseData(objects);
                    } else {
                        Toast.makeText(MainActivity.this, "parse data is done", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseQuery<ParseObject> query = ParseQuery.getQuery("BroncoData");
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            ParseData(objects);
                        } else {
                            Toast.makeText(MainActivity.this, "parse data is done", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void ParseData(List<ParseObject> p){
        int[] colors4 = {
                                         // red
                Color.rgb(255, 0,0),
                Color.rgb(255,0,0)    // blue
        };

        int[] colors3 = {
                Color.rgb(0,255,0), // blue
                Color.rgb(0, 255, 0)    // red
        };

        float[] startPoints = {.2f,1f};

        map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.suryamap)).getMap();
        List<LatLng>  list1=new ArrayList<LatLng>();
        List<LatLng>   list2=new ArrayList<LatLng>();
        int logcount =0;
         for(int i=0;i<p.size();i++){
             logcount++;
           // locations+=p.get(i).getInt("strength")+"\n";

              if((int)p.get(i).getNumber("strength")<=2){
                  list1.add(new LatLng((double)p.get(i).getNumber("latitude"),(double)p.get(i).getNumber("longitude")));

              }else if((int)p.get(i).getNumber("strength")>=3){
                 list2.add(new LatLng((double)p.get(i).getNumber("latitude"),(double)p.get(i).getNumber("longitude")));

              }

             Log.d("LOCD",p.get(i).getInt("strength")+"----"+(i+1));
         }
        Log.d("LOCD",logcount+"---- LOG COUNT");

        HeatmapTileProvider mProvider;
        if(!list1.isEmpty()) {
            mProvider = new HeatmapTileProvider.Builder()
                    .data(list1)
                    .gradient(new Gradient(colors4, startPoints))
                    .build();

            // Add a tile overlay to the map, using the heat map tile provider.
            map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }

        if(!list2.isEmpty()) {
            mProvider = new HeatmapTileProvider.Builder()
                    .data(list2)
                    .gradient(new Gradient(colors3, startPoints))
                    .build();

            map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }

        LatLng coordinate = new LatLng(37.3487354, -121.938334);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 17);
        map.animateCamera(yourLocation);
    }


}
