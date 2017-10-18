package app.security.zimcan.zimcan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View.OnClickListener;

public class SQLiteAdapter {

	public static final String MyDatabase = "zimcan_security";
	public static final String CheckInData = "CheckInData";
	public static final String loginDetails = "LoginDetails";
	public static final int DatabaseVersion = 1;


	public int indexFullName;
	public int indexUsername;
	public int indexPassword;

	public String userFullName;
	public String username;
	public String password;


	public int indexName;
	public int indexId;
	public int indexTime;
	public int indexLatitude;
	public int indexLongitude;
	public int indexSite;
	public int indexCheckin;
	public int indexCheckout;
	public int indexFence_radius;


	public String userName;
	public String userId;
	public String recordTime;
	public String latitude;
	public String longitude;
	public String site;
	public String checkin;
	public String checkout;
	public String fence_radius;

	public static final String FULLNAME = "fullname";
	public static final String USERNAME = "usernme";
	public static final String PASSWORD = "password";

	public static final String NME = "name";
	public static final String ID = "uid";
	public static final String TYM = "tym";
	public static final String LAT = "lat";
	public static final String LON = "lon";
	public static final String SITE = "site";
	public static final String CHECKIN = "checkin";
	public static final String CHECKOUT = "checkout";
	public static final String FENCE = "fence_radius";
	
	//create table MY_DATABASE (ID integer primary key, Content text not null);
	private static final String SCRIPT_CREATE_TBLEUSER =
		"create table " + CheckInData + " ("+"'id' INTEGER PRIMARY KEY AUTOINCREMENT" +","+NME+","+ID+","+TYM+","+LAT+","+LON+","+SITE+","+FENCE+","+CHECKIN+","+CHECKOUT+")";

	//create table MY_DATABASE (ID integer primary key, Content text not null);
	private static final String SCRIPT_CREATE_TBLELOGIN =
			"create table " + loginDetails + " ("+"'id' INTEGER PRIMARY KEY AUTOINCREMENT" +","+FULLNAME+","+USERNAME+","+PASSWORD+")";

	private SQLiteHelper sqLiteHelper;
	private SQLiteDatabase sqLiteDatabase;

	
	private Context context;
	
	public SQLiteAdapter(Context c){
		context = c;
	}
	
	public SQLiteAdapter(OnClickListener onClickListener) {
		// TODO Auto-generated constructor stub
	}

	public SQLiteAdapter openToRead() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MyDatabase, null, DatabaseVersion);
		sqLiteDatabase = sqLiteHelper.getReadableDatabase();
		return this;	
	}
	
	public SQLiteAdapter openToWrite() throws android.database.SQLException {
		
	sqLiteHelper = new SQLiteHelper(context, MyDatabase, null, DatabaseVersion);
	sqLiteDatabase = sqLiteHelper.getWritableDatabase();
	return this;	
	
	}
	  
	private void startManagingCursor(Cursor cursor) {
		// TODO Auto-generated method stub
		
	}

	public void close(){
		sqLiteHelper.close();
	}
	
	public long addCheckInData( String nme,String id,String tym,String lat, String site, String fence_radius, String checkin, String checkout){
		ContentValues contentValues = new ContentValues();
		contentValues.put(NME, nme);
		contentValues.put(ID, id);
		contentValues.put(TYM, tym);
		contentValues.put(LAT, lat);
		contentValues.put(SITE, site);
		contentValues.put(CHECKIN, checkin);
		contentValues.put(CHECKOUT, checkout);
		contentValues.put(FENCE, fence_radius);
		return sqLiteDatabase.insert(CheckInData, null, contentValues);
	}
    public long addLoginDetails(String id, String fullnme,String username,String password){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(FULLNAME, fullnme);
        contentValues.put(USERNAME, username);
        contentValues.put(PASSWORD, password);
        return sqLiteDatabase.insert(loginDetails, null, contentValues);
    }


	public int deleteAllCheckInData(){
		return sqLiteDatabase.delete(CheckInData, null, null);
	}

    public int deleteAllloginDetails(){
        return sqLiteDatabase.delete(loginDetails, null, null);
    }

	public String getData(){
		String[] columns = new String[]{NME,ID,TYM,LAT,LON,SITE,FENCE,CHECKIN,CHECKOUT};
		Cursor cursor = sqLiteDatabase.query(CheckInData, columns,
				null, null, null, null, null);
		String result = "";

		indexName = cursor.getColumnIndex(NME);
		indexId = cursor.getColumnIndex(ID);
		indexTime = cursor.getColumnIndex(TYM);
		indexLatitude = cursor.getColumnIndex(LAT);
		indexLongitude = cursor.getColumnIndex(LON);
		indexSite = cursor.getColumnIndex(SITE);
		indexCheckin = cursor.getColumnIndex(CHECKIN);
		indexCheckout = cursor.getColumnIndex(CHECKOUT);
		indexFence_radius = cursor.getColumnIndex(FENCE);
		for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){

			userName = cursor.getString(indexName);
			userId = cursor.getString(indexId);
			recordTime = cursor.getString(indexTime);
			latitude = cursor.getString(indexLatitude);
            longitude = cursor.getString(indexLongitude);
			site = cursor.getString(indexSite);
			checkin = cursor.getString(indexCheckin);
			checkout = cursor.getString(indexCheckout);
			fence_radius = cursor.getString(indexFence_radius);
			result = result + cursor.getString(indexTime);
		}

		return result;
	}

	public String getCheckInData(){
		String[] columns = new String[]{NME,ID,TYM,LAT,SITE,FENCE,CHECKIN,CHECKOUT};
		Cursor cursor = sqLiteDatabase.query(CheckInData, columns,
				null, null, null, null, null);
		String result = "";

		indexName = cursor.getColumnIndex(NME);
		indexId = cursor.getColumnIndex(ID);
		indexTime = cursor.getColumnIndex(TYM);
		indexLatitude = cursor.getColumnIndex(LAT);
		indexSite = cursor.getColumnIndex(SITE);
		indexCheckin = cursor.getColumnIndex(CHECKIN);
		indexCheckout = cursor.getColumnIndex(CHECKOUT);
		indexFence_radius = cursor.getColumnIndex(FENCE);
		for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){

			userName = cursor.getString(indexName);
			userId = cursor.getString(indexId);
			recordTime = cursor.getString(indexTime);
			latitude = cursor.getString(indexLatitude);
			site = cursor.getString(indexSite);
			checkin = cursor.getString(indexCheckin);
			checkout = cursor.getString(indexCheckout);
			fence_radius = cursor.getString(indexFence_radius);
			result = result + cursor.getString(indexName)+"#"+cursor.getString(indexId)+"#"+cursor.getString(indexTime)+"#"+cursor.getString(indexLatitude)+"#"+cursor.getString(indexSite)+"#"+cursor.getString(indexFence_radius)+"#"+cursor.getString(indexCheckin)+"#"+cursor.getString(indexCheckout);
		}
		return result;
	}

    public String getLoginDetails(){
        String[] columns = new String[]{FULLNAME,USERNAME,PASSWORD};
        Cursor cursor = sqLiteDatabase.query(loginDetails, columns,
                null, null, null, null, null);
        String result = "";

        indexFullName = cursor.getColumnIndex(FULLNAME);
        indexUsername = cursor.getColumnIndex(USERNAME);
        indexPassword = cursor.getColumnIndex(PASSWORD);
        for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){

            userFullName = cursor.getString(indexFullName);
            username = cursor.getString(indexUsername);
            password = cursor.getString(indexPassword);
            result = result + cursor.getString(indexUsername)+":"+cursor.getString(indexPassword);
        }

        return result;
    }

	public class SQLiteHelper extends SQLiteOpenHelper {

		public SQLiteHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(SCRIPT_CREATE_TBLEUSER);
            db.execSQL(SCRIPT_CREATE_TBLELOGIN);
		}



		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}
	
}
