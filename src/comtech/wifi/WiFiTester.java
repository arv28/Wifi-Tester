package comtech.wifi;

import java.util.List;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import org.json.simple.JSONObject;

public class WiFiTester extends Activity {
   	WebView mwebview;
   	WebSettings mwebsettings;
   	int numOfPossibleConnections = 50;
   	JSONObject obj = null;
   	StringWriter out;
   	String jsontext;

    /** Called when the activity is first created. */
	
	public void onCreate(Bundle savedInstanceState) {
        //Button TestButton;
       	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
       	mwebview = (WebView) findViewById(R.id.webview);
        mwebsettings = mwebview.getSettings();
       	mwebsettings.setJavaScriptEnabled(true);
        mwebsettings.setBuiltInZoomControls(true);
        mwebview.setWebViewClient(new mWebViewClient());
        mwebview.addJavascriptInterface(new JavaScriptInterface(this), "Android");
        mwebview.loadUrl("file:///android_asset/index.html");
        
    	try{
    		//Toast.makeText(getBaseContext(), "1xxx1", Toast.LENGTH_LONG).show();
            WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> scanList = null;
            ScanResult scanResult = null;
            if(!wifi.isWifiEnabled()){
            	long start = System.currentTimeMillis();
            	long current = 0;
            	while (!wifi.setWifiEnabled(true)){
            		current = System.currentTimeMillis();
            		if (current-start >= 5000){
            			Toast.makeText(getBaseContext(), "Unable to enable WiFi\n Exiting Application...", Toast.LENGTH_LONG).show();
            			System.exit(1);
            		}
            	}
            }
            if(wifi.isWifiEnabled()){
            	initiateAndScan(wifi, scanList, scanResult);
            }
        }
        catch(java.lang.SecurityException e){

        }
    }

    public void initiateAndScan(WifiManager w, List<ScanResult> sl, ScanResult s){
    	Toast.makeText(getBaseContext(), R.string.wifi_name_true, Toast.LENGTH_LONG).show();
    	if(w.startScan()){
        	Toast.makeText(getBaseContext(), "... Scan was initiated.\n", Toast.LENGTH_LONG).show();
    		sl = w.getScanResults();
    		obj = new JSONObject();
    		for (int i = 0; i < sl.size(); i++){
    			obj.put(sl.get(i).SSID, sl.get(i).level);
    		}
    		out = new StringWriter();
    		try {
				obj.writeJSONString(out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jsontext = out.toString();
			System.out.println(jsontext);
    	}else
        	Toast.makeText(getBaseContext(), R.string.wifi_scan_failed, Toast.LENGTH_LONG).show();
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mwebview.canGoBack()) {
            mwebview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private class mWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
    
    public class JavaScriptInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
        
        /** Show a toast from the web page */
        public void showToastAndRedirect(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
            //FUTURE: TRY TO SAVE A COOKIE
            Uri uri = Uri.parse("http://itracs-web.comtech.ncsu.edu/das/index.php");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            //mwebview.loadUrl("http://itracs-web.comtech.ncsu.edu/das/index.php");
        }
        
        public String showWiFiConnections(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
            //FUTURE: TRY TO SAVE A COOKIE
            //mwebview.loadData(summary, "text/html", "utf-8");
            //mwebview.loadUrl("http://itracs-web.comtech.ncsu.edu/das/index.php");
            return jsontext;
        }
        
    }
}