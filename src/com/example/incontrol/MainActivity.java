package com.example.incontrol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.layout;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;




public class MainActivity extends Activity {
	private ToggleButton btnOne;
	private ToggleButton btnTwo;
	private SeekBar seekbar;
	private boolean btnoneSrv;
	private boolean btntwoSrv;
	private int seekbarSrv;//this im not sure yet
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	setContentView(R.layout.activity_main);
	    if (networkInfo != null && networkInfo.isConnected()) {
	    	Log.d("myapp","is connected");
	       	connect_to_server("http://192.168.1.112/localhost/android/httpd/www/get.php");
	       	set_buttons();
	    } else {
	    	Log.d("myapp","else part");
	    	AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

	        dlgAlert.setMessage("No internet connection!");
	        dlgAlert.setTitle("Error Message...");
	        dlgAlert.setPositiveButton("OK", null);
	        dlgAlert.setCancelable(true);
	        Log.d("myapp","before settings the click listener");
	        dlgAlert.setPositiveButton("Ok",
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                    	exit();
	                    }
	                });
	        dlgAlert.create().show();
	    }
    }
        
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void set_buttons() {
    	Log.d("myapp","set button");
    	String value = Boolean.toString(btnoneSrv) + "\n" + Boolean.toString(btntwoSrv) + "\n" + Integer.toString(seekbarSrv);
    	Log.d("myapp",value); 
		btnOne = (ToggleButton)findViewById(R.id.btnOne);
		Log.d("myapp","button found");
		btnOne.setChecked(btnoneSrv);
		Log.d("myapp","button set as true");
		btnTwo = (ToggleButton) findViewById(R.id.btnTwo);
		btnTwo.setChecked(btntwoSrv);
		seekbar = (SeekBar) findViewById(R.id.slider);
		seekbar.setProgress(seekbarSrv);
	}
    
    private void connect_to_server(String link) {
    	Toast.makeText(this, "connect to server", Toast.LENGTH_SHORT).show();
		// TODO Auto-generated method stub
		//Log.d("myapp","connect to server");
		// connect to srv and get data.JSON
		String data = "";
    	InputStream inputStream = null;
    	//Log.d(logTag,"init of local variables");
    	try {
    		 // create HttpClient
            Log.d("myapp","create httpclient");
            // make GET request to the given URL
    		URL url = new URL(link);
    		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    		conn.setReadTimeout(10000);
    		conn.setConnectTimeout(15000);
    		conn.setRequestMethod("GET");
    		conn.setDoInput(true);
    		conn.connect();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            Log.d("myapp","before while");
            //Log.d("myapp",reader.toString());
            while((line = reader.readLine())!= null){
            	Log.d("myapp",line);
            	Toast.makeText(this,line, Toast.LENGTH_SHORT).show();
            	data += line;
            }
            //inputStream = entity.getContent();
            //Log.d(logTag,"receive response as inputstream");
            // convert inputstream to string
            
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			Log.d("myapp",e.getMessage().toString());
			data = e.getMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d("myapp",e.getMessage().toString());
			data = e.getMessage();
		}
		
		try {
			JSONObject serverData = new JSONObject(data.toString());
			//String values = "json from website: " + serverData.toString();
			//Log.d("myapp",values);
			Log.d("myapp","1st button");
			btnoneSrv = serverData.getBoolean("buttonOne");
			Log.d("myapp","2nd button");
			btntwoSrv = serverData.getBoolean("buttonTwo");
			Log.d("myapp","slider");
			seekbarSrv = serverData.getInt("slider");
			Log.d("myapp","finish geting json from server");
			//Toast.makeText(this, Boolean.toString(btnoneSrv), Toast.LENGTH_SHORT).show();
			//String value1 = "btnOne: " + Boolean.toString(btnoneSrv)+"\n";
			//String value2 = "btnTwo: " + Boolean.toString(btntwoSrv)+"\n";
			//String value3 = "slider: " + Integer.toString(seekbarSrv)+"\n";
			//String value = value1 + value2 + value3;
			//Log.d("myapp",value);
		} catch (JSONException e) {
			String text = "error: " + e.getMessage().toString();// + e.getCause().toString();
			Log.d("myapp",text);
			//Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
		}
		//Toast.makeText(this, "finish conecting to server", Toast.LENGTH_SHORT).show();
		Log.d("myapp","finish conecting to server");
		set_values();
	}
    
    public void exit(){
    	Log.d("myapp","call the exit");
    	this.finish();
    	android.os.Process.killProcess(android.os.Process.myPid());
    	super.onDestroy();
    }
    
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    }   
    
    public void saving(View v){
		try {
			JSONObject send_data = new JSONObject();
			Log.d("myapp","init json " + send_data.toString());
			InputStream inputStream = null;
			String result = "";
			send_data.put("buttonOne",btnOne.isChecked());
			send_data.put("buttonTwo",btnTwo.isChecked());
			send_data.put("slider",seekbar.getProgress());
			Log.d("myapp","json with data: " + send_data.toString());
			URI url = new URI("http://192.168.1.112/localhost/android/httpd/www/testpost.php");
			int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
			HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
			HttpClient client = new DefaultHttpClient(httpParams);
			Log.d("myapp",send_data.toString());
			HttpPost request = new HttpPost(url);
			request.setEntity(new StringEntity(
			    send_data.toString()));
			HttpResponse response = client.execute(request);
			String temp = EntityUtils.toString(response.getEntity());
	        Log.d("myapp", temp);
			/*
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			
			conn.setRequestProperty("User-Agent", "Mozilla/30.0");
			conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			
			String parameters = send_data.toString();
			
			//conn.setDoOutput(true);
			//#conn.connect();
			
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(parameters);
			wr.flush();
			wr.close();
			
			int responseCode = conn.getResponseCode();
			//System.out.println("\nSending 'POST' request to URL : " + url);
			//System.out.println("Post parameters : " + parameters);
			//System.out.println("Response Code : " + responseCode);

			BufferedReader inR = new BufferedReader(
			        new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = inR.readLine()) != null) {
				response.append(inputLine);
			}
			inR.close();
			Log.d("myapp",response.toString());
			*/
		} catch (JSONException e) {
			Log.d("myapp",e.getMessage().toString()+ "json");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			Log.d("myapp",e.getMessage().toString()+"bad url");
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			Log.d("myapp",e.getMessage().toString()+"protocol");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d("myapp",e.getMessage().toString()+"io");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	
    }
    
    public void set_values(){
    	try{
	    	btnOne.setChecked(btnoneSrv);
	    	btnTwo.setChecked(btntwoSrv);
	    	seekbar.setProgress(seekbarSrv);
    	}
    	catch (Exception e){
    		TextView test = (TextView) findViewById(R.id.text);
    		test.setText(e.getMessage());
    	}
    }
}
