package com.demo.gcmnotification;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	//EXTRA_MASSAGE is used for debugging.
	public static final String EXTRA_MESSAGE = "message"; 
	//registration_id will be storing the id with which android device gets register with GCM.
	public static final String PROPERTY_REG_ID = "registration_id"; 
	//SENDER_ID is the project number, which you get from google code website.
	String SENDER_ID = "341663712821";
	
	//Define the request code sent to Google Play services
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	static final String TAG = CommonUtilities.TAG;
	//gcm is an instance of GoogleCloudMessaging, which provides access to google cloud.
    GoogleCloudMessaging gcm;
    //TextView will display register id of device in user interface 
    //and “regid” is a string holding device registation id.
    TextView mDisplay;
    String regid;
    //Context tells about the current state of the application. 
    //We call it to get information about our application, 
    //for example when we are working with background thread we use context.
    Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mDisplay = (TextView) findViewById(R.id.display);
		context = getApplicationContext();
		//Now we will get instance from GoogleCloudMessaging and assign it to gcm.
		gcm = GoogleCloudMessaging.getInstance(this);
		//RegisterBackground, which is our AsyncTask,
		//helps to connect to google cloud using background thread.
		if(isPlayServicesAvaliable()){
			//new RegisterBackground().execute();
			
			String msg = "";
			try {
				Log.e(TAG, "doInBackground");
                if (gcm == null) {
                	Log.e(TAG, "gcm == null");
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                Log.e(TAG, "gcm.register(SENDER_ID)");
                regid = gcm.register(SENDER_ID);
                Log.e(TAG, regid);
                msg = "Dvice registered, registration ID=" + regid;
                Log.d(TAG, msg);
                //sendRegistrationIdToBackend(regid);

            } catch (Exception ex) {
                msg = "Error :" + ex.getMessage();
                Log.e(TAG, msg);
            }
		}
	}
    
	private boolean isPlayServicesAvaliable() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
		if (ConnectionResult.SUCCESS == resultCode) {
			Log.d(TAG,"Google Play services is available.");
			return true;
		}else{
			if (ConnectionResult.SERVICE_MISSING == resultCode ||
				ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED == resultCode ||
				ConnectionResult.SERVICE_DISABLED == resultCode||
				ConnectionResult.SERVICE_INVALID == resultCode) {
				Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
						resultCode ,
	                    this,
	                    CONNECTION_FAILURE_RESOLUTION_REQUEST);
				errorDialog.show();
			}

			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume(){
		super.onResume();
	}
	
	class RegisterBackground extends AsyncTask<String,String,String>{

		@Override
		protected String doInBackground(String... arg0) {
			//“doInBackground” is the method which will be having the main code.
			String msg = "";
			try {
				Log.e(TAG, "doInBackground");
                if (gcm == null) {
                	Log.e(TAG, "gcm == null");
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                Log.e(TAG, "gcm.register(SENDER_ID)");
                regid = gcm.register(SENDER_ID);
                Log.e(TAG, regid);
                msg = "Dvice registered, registration ID=" + regid;
                Log.d(TAG, msg);
                sendRegistrationIdToBackend(regid);

            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
                Log.e(TAG, msg);
            }
            return msg;
        }

		@Override
        protected void onPostExecute(String msg) {
			//“onPostExecute” will contain stuff to update the data on TextView, 
			//which we have received from internet.
            mDisplay.append(msg + "\n");

        }
		private void sendRegistrationIdToBackend(String regid) {
            // this code will send registration id of a device to our own server.
			String url = "http://www.yourwebsitename.com/getregistrationid.php";
			List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("regid", regid));
           DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            try {
				httpPost.setEntity(new UrlEncodedFormEntity(params));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

            try {
				HttpResponse httpResponse = httpClient.execute(httpPost);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		}
	}
}
