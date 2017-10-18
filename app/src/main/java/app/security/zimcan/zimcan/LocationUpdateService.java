package app.security.zimcan.zimcan;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LocationUpdateService extends Service {
    private static final String BROADCAST_ACTION = "Hello World";
    private LocationManager locationManager;
    private MyLocationListener listener;
    public double userCurrentLatitude;
    public double userCurrentLongitude;
    private Context context;
    private Intent intent;
    public  Boolean checkedIn;
    public float userDistanceToSite;
    public SQLiteAdapter mySQLiteAdapter;
    public String currentTime;
    public String checkInData[];
    public String url ="http://192.168.43.100/zimcan/mbl.php";
    private ProgressDialog loading;
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            intent = new Intent(BROADCAST_ACTION);
            context = this;
        }catch (Exception x){
            Toast.makeText(context, R.string.zimcan, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {

        try {
            mySQLiteAdapter = new SQLiteAdapter(getApplication());
            mySQLiteAdapter.openToRead();
            checkInData = mySQLiteAdapter.getCheckInData().split("#");
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            listener = new MyLocationListener();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
             return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
        } catch (Exception e) {
            Toast.makeText(context, R.string.zimcan, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        try{
            super.onDestroy();
            Log.v("STOP_SERVICE", "DONE");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
             return;
            }
            locationManager.removeUpdates(listener); }catch (Exception e){
            Toast.makeText(context, R.string.zimcan, Toast.LENGTH_SHORT).show();
        }
    }

        private class MyLocationListener implements LocationListener{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");

        public void onLocationChanged(final Location userLocation)
        {
            try{
                userLocation.getLatitude();
                userLocation.getLongitude();
                currentTime  = dateFormat.format(new Date());
                intent.putExtra("Latitude", userLocation.getLatitude());
                intent.putExtra("Longitude", userLocation.getLongitude());
                intent.putExtra("Provider", userLocation.getProvider());
                sendBroadcast(intent);
                userCurrentLatitude = userLocation.getLatitude();
                userCurrentLongitude= userLocation.getLongitude();

                DecimalFormat f = new DecimalFormat("##.0000000");
                String currentLat = f.format(userLocation.getLatitude());
                String currentLon = f.format(userLocation.getLongitude());
                Location userCurrentLocation = new Location("");
                userCurrentLocation.setLatitude(Double.parseDouble(currentLat));
                userCurrentLocation.setLongitude(Double.parseDouble(currentLon));

                Location siteLocation = new Location("");
                String getSiteGPS[] = checkInData[3].split(",");
                siteLocation.setLatitude(Double.parseDouble(getSiteGPS[0]));
                siteLocation.setLongitude(Double.parseDouble(getSiteGPS[1]));
                userDistanceToSite = userCurrentLocation.distanceTo(siteLocation);

                if(userDistanceToSite < Double.parseDouble(checkInData[5])) {
                checkedIn = true;
                checkInCheckOut(checkInData[1],userDistanceToSite+"","update");

                mySQLiteAdapter = new SQLiteAdapter(context);
                mySQLiteAdapter.openToWrite();
                mySQLiteAdapter.deleteAllCheckInData();
                mySQLiteAdapter.close();
                mySQLiteAdapter.openToWrite();
                mySQLiteAdapter.addCheckInData(checkInData[0],checkInData[1], currentTime,checkInData[3],checkInData[4],checkInData[5],checkInData[6],checkInData[7]);
                mySQLiteAdapter.close();
               }
            }catch (Exception e){
            Toast.makeText(context, e+"", Toast.LENGTH_SHORT).show();
        }

    }
            public void sendNotice(final String guardId,  final String act){
                try{
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("msg", checkInData[0].toUpperCase()+" "+act+" GPS SERVICE");
                            params.put("guardId", guardId);
                            params.put("category", "notice");
                            return params;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);
                }catch (Exception e){
                    loading.dismiss();
                }
            }
            public void checkInCheckOut(final String guardId, final String distance, final String action){
                try{
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("distance", distance);
                            params.put("guardId", guardId);
                            params.put("status", String.valueOf(checkedIn));
                            params.put("category", action);
                            return params;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);
                }catch (Exception e){
                    loading.dismiss();
                }
            }

        public void onProviderDisabled(String provider)
        {
            Toast.makeText( getApplicationContext(), R.string.gpsDisabled, Toast.LENGTH_SHORT ).show();
            sendNotice(checkInData[1],"Deactivated");
        }


        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(), R.string.gpsEnabled, Toast.LENGTH_SHORT).show();
            sendNotice(checkInData[1],"Activated");
        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }

    }


}