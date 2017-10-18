    package app.security.zimcan.zimcan;

    import android.app.Activity;
    import android.app.Dialog;
    import android.app.ProgressDialog;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.location.LocationManager;
    import android.os.Bundle;
    import android.os.Handler;
    import android.provider.Settings;
    import android.support.v7.app.AlertDialog;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.Toast;
    import com.android.volley.Request;
    import com.android.volley.RequestQueue;
    import com.android.volley.Response;
    import com.android.volley.VolleyError;
    import com.android.volley.toolbox.StringRequest;
    import com.android.volley.toolbox.Volley;
    import org.json.JSONArray;
    import org.json.JSONObject;
    import java.text.SimpleDateFormat;
    import java.util.Date;
    import java.util.HashMap;
    import java.util.Map;
    
    public class Launcher extends Activity {
    public LocationManager locationManager;
    public EditText user,pass,cpass;
    private ProgressDialog loading;
    public String url ="http://192.168.43.100/zimcan/mbl.php";
    public static final String JSON_ARRAY = "result";
    public SQLiteAdapter mySQLiteAdapter;
    public JSONArray response = null;
    public static String[] securityName;
    public static String[] siteID;
    public static String[] siteName;
    public static String[] latLon;
    public static String[] fence_radius;
    public static String[] check_in;
    public static String[] check_out;
    public static String[] securityId;
    public String username,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        int SPLASH_TIME_OUT = 1000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                check_gps_status();
            }
        }, SPLASH_TIME_OUT);
    }

     public void check_gps_status() {
        try {

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.gpsDisabled));
                builder.setMessage(R.string.enableGPS);
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        finish();
                    }
                });

                builder.setNegativeButton(getString(R.string.quit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });

                Dialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
            else{
                checkLoginData();
            }

        } catch (Exception e) {
            Toast.makeText(Launcher.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void checkLoginData(){
        mySQLiteAdapter = new SQLiteAdapter(Launcher.this);
        mySQLiteAdapter.openToRead();
        if(mySQLiteAdapter.getCheckInData().length()<1){
            loginInterface();
        }
        else{
            startService(new Intent(Launcher.this, LocationUpdateService.class));
            Intent i = new Intent(Launcher.this,Dashboard.class);
            startActivity(i);
            finish();
        }
        }

    public void loginInterface(){
        try{
            LayoutInflater li = LayoutInflater.from(Launcher.this);
            View promptsView = li.inflate(R.layout.logininterface, null);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    Launcher.this);
            alertDialogBuilder.setView(promptsView);
            user = (EditText) promptsView.findViewById(R.id.edtuser);
            pass = (EditText) promptsView.findViewById(R.id.edtpass);
            Button login = (Button) promptsView.findViewById(R.id.lgin);
            Button reg = (Button) promptsView.findViewById(R.id.reg);
            reg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    registrationInterface();
                }
                });
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (user.getText().length() < 6) {
                        showToast(getString(R.string.enter_full_username));
                        loginInterface();
                    }else  if (!user.getText().toString().contains("@")) {
                        showToast(user.getText().toString() + getString(R.string.invalid_email));
                    } else if (!user.getText().toString().contains(".")) {
                        showToast(user.getText().toString() + getString(R.string.invalid_email));
                    } else if (pass.getText().length() < 6) {
                        showToast(getString(R.string.enter_full_password));
                        loginInterface();
                    } else {
                        username=user.getText().toString();
                        password=pass.getText().toString();
                        showDashboard();
                    }
                }
            });

            alertDialogBuilder
                    .setCancelable(false);
            alertDialogBuilder.setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        finish();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            }catch (Exception e){
            showToast(e+"");
            loginInterface();
            }
    }


        public void registrationInterface(){
            try{
                LayoutInflater li = LayoutInflater.from(Launcher.this);
                View promptsView = li.inflate(R.layout.registrationinterface, null);
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        Launcher.this);
                alertDialogBuilder.setView(promptsView);
                user = (EditText) promptsView.findViewById(R.id.edtuser);
                pass = (EditText) promptsView.findViewById(R.id.edtpass);
                cpass = (EditText) promptsView.findViewById(R.id.edtcpass);
                Button reg = (Button) promptsView.findViewById(R.id.reg);
                Button login = (Button) promptsView.findViewById(R.id.login);
                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginInterface();
                    }
                    });
                reg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (user.getText().length() < 6) {
                        showToast(getString(R.string.enter_full_username));
                        registrationInterface();
                        }else  if (!user.getText().toString().contains("@")) {
                            showToast(user.getText().toString() + getString(R.string.invalid_email));
                        } else if (!user.getText().toString().contains(".")) {
                            showToast(user.getText().toString() + getString(R.string.invalid_email));
                        } else if (pass.getText().length() < 6) {
                            showToast(getString(R.string.enter_full_password));
                            registrationInterface();
                        }
                        else {
                            username=user.getText().toString();
                            password=pass.getText().toString();
                            loginRegister(username,password,"register");
                        }
                    }
                });

                alertDialogBuilder
                        .setCancelable(false);
                alertDialogBuilder.setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }catch (Exception e){
                showToast("Error!\nVerify Registration Details");
                loginInterface();
            }
        }

    public void showToast(String message){
        Toast.makeText(Launcher.this, message, Toast.LENGTH_SHORT).show();
    }
    public void show_dialog(String ltxt){
        loading = ProgressDialog.show(this, ltxt, "Please wait...", false, false);
    }
    public void showDashboard(){
        loginRegister(username, password,"login");
    }

        private void loginRegister(final String u, final String p, final String action){
            try {
                show_dialog("Logging in...");
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                loading.dismiss();
                                try{
                                    JSONObject jsonObject ;
                                    jsonObject = new JSONObject(response);
                                    Launcher.this.response = jsonObject.getJSONArray(JSON_ARRAY);
                                    securityName = new String[Launcher.this.response.length()];
                                    siteID = new String[Launcher.this.response.length()];
                                    siteName = new String[Launcher.this.response.length()];
                                    fence_radius = new String[Launcher.this.response.length()];
                                    latLon = new String[Launcher.this.response.length()];
                                    securityId = new String[Launcher.this.response.length()];
                                    check_in = new String[Launcher.this.response.length()];
                                    check_out = new String[Launcher.this.response.length()];
                                    for (int i = 0; i < Launcher.this.response.length(); i++) {
                                        JSONObject jo = Launcher.this.response.getJSONObject(i);
                                        securityName[i] = jo.getString("full_name");
                                        siteID[i] = jo.getString("site_id");
                                        siteName[i] = jo.getString("site_name");
                                        fence_radius[i] = jo.getString("fence_radius");
                                        latLon[i] = jo.getString("lat_lon");
                                        securityId[i] = jo.getString("id");
                                        check_in[i] = jo.getString("check_in");
                                        check_out[i] = jo.getString("check_out");
                                    }
                                    if(securityName.length>0){
                                        showToast("Welcome "+ securityName[0]);
                                        startService(new Intent(Launcher.this, LocationUpdateService.class));
                                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                        mySQLiteAdapter = new SQLiteAdapter(Launcher.this);
                                        mySQLiteAdapter.openToWrite();
                                        mySQLiteAdapter.deleteAllCheckInData();
                                        mySQLiteAdapter.addLoginDetails(securityId[0],securityName[0], u,p);
                                        mySQLiteAdapter.addCheckInData(securityName[0],securityId[0],timeStamp,latLon[0],siteName[0],fence_radius[0],check_in[0],check_out[0]);
                                        mySQLiteAdapter.close();

                                        Intent i = new Intent(Launcher.this,Dashboard.class);
                                        startActivity(i);
                                        finish();

                                    }
                                    else{
                                        showToast("Login Failed\nInvalid Username or Password");
                                        loginInterface();}
                                }catch (Exception e){
                                    showToast(e+"");
                                    loginInterface();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                showToast("Communication error\n"+error);
                                loginInterface();
                                loading.dismiss();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("user", u);
                        params.put("pass", p);
                        params.put("category", action);
                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
            }catch (Exception e){
                loading.dismiss();
            }
        }



}


