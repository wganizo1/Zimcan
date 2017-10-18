package app.security.zimcan.zimcan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Dashboard extends AppCompatActivity {
    public TextView mTextMessage;
    public ImageView panicPic;
    public Button sendPanic;
    public Uri file;
    public String base64StringForImage;
    public SQLiteAdapter mySQLiteAdapter;
    private EditText panicDescription;
    private String checkInData[];
    public String url ="http://192.168.43.100/zimcan/mbl.php";
    private ProgressDialog loading;
    private LocationUpdateService loc = new LocationUpdateService();
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    home();
                    return true;
                case R.id.navigation_dashboard:
                    dash();
                    return true;
                case R.id.navigation_notifications:
                    mySQLiteAdapter = new SQLiteAdapter(Dashboard.this);
                    mySQLiteAdapter.openToWrite();
                    mySQLiteAdapter.deleteAllloginDetails();
                    mySQLiteAdapter.deleteAllCheckInData();
                    mySQLiteAdapter.close();
                    stopService(new Intent(Dashboard.this, LocationUpdateService.class));
                    finish();
                    Intent i = new Intent(Dashboard.this,Launcher.class);
                    startActivity(i);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String timeStamp = new SimpleDateFormat("HH:mm").format(new Date());
        showToast(timeStamp);
        home();
    }

    public void home() {
        setContentView(R.layout.home);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mySQLiteAdapter = new SQLiteAdapter(Dashboard.this);
        mySQLiteAdapter.openToRead();
        checkInData = mySQLiteAdapter.getCheckInData().split("#");
        if (checkInData.length > 1) {
            mTextMessage.setText("RECORD TIME:\n" + checkInData[2] +
                    "\n\nLAT-LON:\n" + checkInData[3] +
                    "\n\nNAME:\n" + checkInData[0] +
                    "\n\nSITE NAME:\n" + checkInData[4]+
                    "\n\nGEO-FENCE RADIUS :"+checkInData[5]+"m"+
                    "\n\nEXPECTED CHECK IN TIME :"+checkInData[6]+
                    "\n\nEXPECTED CHECKOUT TIME :"+checkInData[7]
            );
        }
        mySQLiteAdapter.close();
    }

    public void dash() {
        setContentView(R.layout.panic_report);
        panicDescription = (EditText) findViewById(R.id.panicDescription);
        panicPic = (ImageView) findViewById(R.id.panicImage);
        sendPanic = (Button) findViewById(R.id.sendPanic);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        panicPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
        sendPanic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendPanic(panicDescription.getText().toString(), base64StringForImage, checkInData[4], checkInData[1]);
                }catch (Exception e)
                {
                    showToast("Take Photo of Scene, Please");
                }
            }
        });
    }

    public void sendPanic(final String description, final String image, final String site, final String guardId){
        try {
            show_dialog("Logging in...");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            loading.dismiss();
                            showToast(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showToast("Communication error\n"+error);
                            loading.dismiss();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("description", description.toUpperCase());
                    params.put("image", image);
                    params.put("site", site);
                    params.put("guardId", guardId);
                    params.put("category", "panic");
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }catch (Exception e){
            loading.dismiss();
        }
    }
    public void showToast(String message){
        Toast.makeText(Dashboard.this, message, Toast.LENGTH_SHORT).show();
    }
    public void show_dialog(String ltxt){
        loading = ProgressDialog.show(this, ltxt, "Please wait...", false, false);
    }
    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        startActivityForResult(intent, 2);
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Prosper");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {

        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        try {
            panicPic.setImageURI(file);
            Bitmap bitmapOrg = ((BitmapDrawable) panicPic.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 10, baos);

            Bitmap photo = ((BitmapDrawable) panicPic.getDrawable()).getBitmap();
            photo = Bitmap.createScaledBitmap(photo, 250, 250, false);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            byte[] by = bytes.toByteArray();
            String strBase64 = Base64.encodeToString(by, Base64.DEFAULT);
            String stringForBase64 = strBase64;
            base64StringForImage = stringForBase64;
        } catch (Exception e) {
        }

    }
}
