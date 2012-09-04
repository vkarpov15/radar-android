package com.tabbie.android.radar;

import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.Facebook;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tabbie.android.radar.MultiSpinner.MultiSpinnerListener;
import com.tabbie.android.radar.http.ServerGetRequest;
import com.tabbie.android.radar.http.ServerResponse;

public class RadarActivity extends Activity implements
    OnTabChangeListener,
    OnItemClickListener,
    OnItemLongClickListener,
    Handler.Callback {
	
  public static final String TAG = "RadarActivity";
  private static final Handler upstreamHandler;
  private static final HandlerThread serverThread;

  static {
  	serverThread = new HandlerThread(TAG + "Thread");
  	serverThread.start();
  	upstreamHandler = new ServerThreadHandler(serverThread.getLooper());
  }
  
  // Intent constants
  private static final String[] FOUNDERS_EMAIL = {"founders@tonight-life.com"};
  private static final String APP_FEEDBACK_SUBJECT = "TonightLife Application Feedback";
  private static final int REQUEST_LOGIN = 42;

  // Often-used views
  private TabHost tabHost;
  private ListView[] listViews = new ListView[3];
  private TextView myNameView;
  private ProgressDialog loadingDialog;
  
  

  // Internal state for views
  private String tabbieAccessToken = null;
  private int currentViewPosition = 0;
  private short currentTabIndex = 0;

  // Controllers and adapters
  private RadarCommonController commonController;

  // FB junk
  private final Facebook facebook = new Facebook("217386331697217");
  private SharedPreferences preferences;
  
  // Google analytics
  private GoogleAnalyticsTracker googleAnalyticsTracker;
  
  
  @Override
  public void onCreate(final Bundle savedInstanceState) {
	  
	// Set initial conditions
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    commonController = new RadarCommonController();

    // Start Google Analytics
    googleAnalyticsTracker = GoogleAnalyticsTracker.getInstance();
    googleAnalyticsTracker.startNewSession("UA-34193317-1", this);
    googleAnalyticsTracker.trackPageView(this.getClass().getName());
    
    // Instantiate list views
    listViews[RadarCommonController.FEATURED] = (ListView) findViewById(R.id.featured_event_list);
    listViews[RadarCommonController.ALL] = (ListView) findViewById(R.id.all_event_list);
    listViews[RadarCommonController.LINEUP] = (ListView) findViewById(R.id.lineup_event_list);
    
    myNameView = (TextView) findViewById(R.id.user_name);
    tabHost = (FlingableTabHost) findViewById(android.R.id.tabhost);
    
    findViewById(R.id.map_button).setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(RadarActivity.this, RadarMapActivity.class);
            intent.putExtra("controller", commonController);
            intent.putExtra("token", tabbieAccessToken);
            startActivity(intent);
        }
      });

    // Set up the Tab Host
    tabHost.setup();
    tabHost.setOnTabChangedListener(this);
    for(final ListView v : listViews) {
    	v.setAdapter(new EventListAdapter(this,
    			commonController.findListById(v.getId())));
    	v.setFastScrollEnabled(true);
    	v.setOnItemClickListener(this);
    	v.setOnItemLongClickListener(this);
    	ListViewTabFactory.createTabView(tabHost, v); 
    }
    tabHost.setCurrentTab(currentTabIndex);
    
    // Launch Authentication Activity
    preferences = getPreferences(MODE_PRIVATE);
    final Intent authenticate = new Intent(this, AuthenticateActivity.class);
    authenticate.putExtra("token", preferences.getString("access_token", null));
    authenticate.putExtra("expires", preferences.getLong("access_expires", 0));
    startActivityForResult(authenticate, REQUEST_LOGIN);
  }

  public void onTabChanged(final String tabName) {
	  for(short i = 0; i < listViews.length; i++) {
		  if(tabName.equals(listViews[i].getTag())) {
			  currentTabIndex = i;
		  }
	  }
	  if(commonController.hasNoEvents(currentTabIndex)) {
		  findViewById(R.id.radar_list_empty_text).setVisibility(View.VISIBLE);
		  return;
	  } else {
		  findViewById(R.id.radar_list_empty_text).setVisibility(View.GONE);
	  }
	  commonController.sort(currentTabIndex);
	  final ListView tabView = listViews[currentTabIndex];
	  ((BaseAdapter) tabView.getAdapter()).notifyDataSetChanged();
      PlayAnim(tabView, getBaseContext(), android.R.anim.fade_in, 100);
  }

  public Animation PlayAnim(View v, Context con, int animationId,
      int StartOffset) {
    if (null != v) {
      Animation animation = AnimationUtils.loadAnimation(con, animationId);
      animation.setStartOffset(StartOffset);
      v.startAnimation(animation);

      return animation;
    }
    return null;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    
    switch (requestCode) {
      case REQUEST_LOGIN:
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString("access_token", data.getStringExtra("fbAccessToken"));
        editor.putLong("access_expires", data.getLongExtra("expires", 0));
        editor.commit();
          
      	tabbieAccessToken = data.getStringExtra("tabbieAccessToken");
      	myNameView.setText(data.getStringExtra("facebookName"));
      	
      	final ServerGetRequest req = new ServerGetRequest(
      			getString(R.string.tabbie_server) + "/mobile/all.json?auth_token="
      			+ tabbieAccessToken, MessageType.LOAD_EVENTS);
      	loadingDialog = ProgressDialog.show(this, null, "Loading... Please wait");
      	req.setResponseHandler(new Handler(this));
      	final Message message = Message.obtain();
      	message.obj = req;
      	upstreamHandler.sendMessage(message);
      	
      	break;
    	
      case RadarCommonController.REQUEST_RETRIEVE_INSTANCE:
        final Bundle controller = data.getExtras();
        
        commonController = controller.getParcelable("controller");
        
        tabHost.setCurrentTab(currentTabIndex);

        for(final ListView v : listViews) {
        	v.setAdapter(new EventListAdapter(this,
        			commonController.findListById(v.getId())));
        }
        listViews[currentTabIndex].setSelection(currentViewPosition);
        
        // TODO Use this paradigm (in more robust form) for other instances of this view
        if(((BaseAdapter) listViews[currentTabIndex].getAdapter()).isEmpty()) {
        	findViewById(R.id.radar_list_empty_text).setVisibility(View.VISIBLE);
        } else {
        	findViewById(R.id.radar_list_empty_text).setVisibility(View.GONE);
        }
        break;
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    facebook.extendAccessTokenIfNeeded(this, null);
  }
  
  @Override
	protected void onRestart() {
		super.onRestart();
        ServerGetRequest req = new ServerGetRequest(
            getString(R.string.tabbie_server) + "/mobile/all.json?auth_token="
                + tabbieAccessToken, MessageType.LOAD_EVENTS);
        req.setResponseHandler(new Handler(this));
        final Message message = Message.obtain();
        message.obj = req;
        upstreamHandler.sendMessage(message);
	}

  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    final MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {

    switch(item.getItemId()) {
      case R.id.refresh_me:
    	  ServerGetRequest req = new ServerGetRequest(
    			  getString(R.string.tabbie_server) + "/mobile/all.json?auth_token="
    					  + tabbieAccessToken, MessageType.LOAD_EVENTS);
    	  req.setResponseHandler(new Handler(this));
    	  final Message message = Message.obtain();
    	  message.obj = req;
    	  upstreamHandler.sendMessage(message);
    	  break;
      case R.id.report_me:
    		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    		emailIntent.setType("plain/text");
    		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, FOUNDERS_EMAIL);
    		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, APP_FEEDBACK_SUBJECT);
    		startActivity(Intent.createChooser(emailIntent, "Send feedback..."));
    		break;
		
      case R.id.preference_me:
    		final PreferencesDialog preferencesDialog = new PreferencesDialog(this, R.layout.preferences);
    		preferencesDialog.setTitle("Preferences");
    		preferencesDialog.setOnAgeItemSelectedListener(new OnItemSelectedListener() {
    
    			@Override
    			public void onItemSelected(AdapterView<?> parent, View v,
    					int position, long id) {
    				// TODO Auto-generated method stub
    				
    			}
    
    			@Override
    			public void onNothingSelected(AdapterView<?> container) {
    				// TODO Auto-generated method stub
    				
    			}
    		});
    		preferencesDialog.setOnCostItemsSelectedListener(new MultiSpinnerListener() {
    			
    			@Override
    			public void onItemsSelected(boolean[] selected) {
    				// TODO Auto-generated method stub
    				
    			}
    		});
    		preferencesDialog.setOnEnergyItemsSelectedListener(new MultiSpinnerListener() {
    			
    			@Override
    			public void onItemsSelected(boolean[] selected) {
    				// TODO Auto-generated method stub
    				
    			}
    		});
    		preferencesDialog.show();
    		break;
		
      default:
        return super.onOptionsItemSelected(item);
    }
    return true;
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    googleAnalyticsTracker.stopSession();
  }
  
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
		
		final Event e = (Event) parent.getItemAtPosition(position);
		
		Log.d("OnItemClick", "Event is " + e.getName());
		
	    if (null != e) {
	        currentViewPosition = position;
	        ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE))
	            .vibrate(30);

	        new AsyncTask<Void, Void, Intent>() {

	          private ProgressDialog dialog;

	          @Override
	          protected void onPreExecute() {
	            dialog = ProgressDialog.show(RadarActivity.this, "",
	                "Loading, please wait...");
	            super.onPreExecute();
	          }

	          @Override
	          protected Intent doInBackground(Void... params) {
	            Intent intent = new Intent(RadarActivity.this,
	                EventDetailsActivity.class);
	            intent.putExtra("eventId", e.getTag());
	            intent.putExtra("controller", commonController);
	            intent.putExtra("token", tabbieAccessToken);
	            return intent;
	          }

	          @Override
	          protected void onPostExecute(Intent result) {
	            startActivityForResult(result,
	                RadarCommonController.REQUEST_RETRIEVE_INSTANCE);
	            dialog.dismiss();

	          };
	        }.execute();
	      }
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v, int position,
			long rowId) {
		// TODO Pop up a dialog here
		Toast.makeText(this, "Long click!", Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public boolean handleMessage(final Message msg) {
		Log.d(TAG, "Received message response");
		if(!(msg.obj instanceof ServerResponse)) {
			Log.e(TAG, "Message is not a Server Response");
			return false;
		}
		final ServerResponse resp = (ServerResponse) msg.obj;
		final JSONArray list = resp.parseJsonArray();
		final Set<String> serverRadarIds = new LinkedHashSet<String>();
		try {
			final JSONObject radarObj = list.getJSONObject(list.length() - 1);
			JSONArray tmpRadarList = radarObj.getJSONArray("radar");
			for(int i = 0; i < tmpRadarList.length(); ++i) {
				serverRadarIds.add(tmpRadarList.getString(i));
			}
			commonController.clear();
			for(int i = 0; i < list.length() - 1; ++i) {
				final JSONObject obj = list.getJSONObject(i);
				final String radarCountStr = obj.getString("user_count");
				int radarCount = 0;
				if (null != radarCountStr && 0 != radarCountStr.compareTo("null"))
					radarCount = Integer.parseInt(radarCountStr);
				final Event e = new Event(  obj.getString("id"),
	                                    obj.getString("name"),
	                                    obj.getString("description"),
	                                    obj.getString("location"),
	                                    obj.getString("street_address"),
	                                    new URL("http://tonight-life.com" + obj.getString("image_url")),
	                                    obj.getDouble("latitude"),
	                                    obj.getDouble("longitude"),
	                                    radarCount,
	                                    obj.getBoolean("featured"),
	                                    obj.getString("start_time"),
	                                    serverRadarIds.contains(obj.getString("id")));
				commonController.addEvent(e);
			}
        } catch (final JSONException e) {
        	Toast.makeText(this, "Fatal Error: Failed to Parse JSON",
            Toast.LENGTH_SHORT).show();
        	e.printStackTrace();
        	return false;
        } catch (final Exception e) {
        	Log.e("RadarActivity",
            "Fatal Error: Non JSON-Exception during event creation");
        	throw new RuntimeException();
        }
	    this.runOnUiThread(new Runnable() {
		  public void run() {

		  for(final ListView v : listViews) {
      		final BaseAdapter adapter = (BaseAdapter) v.getAdapter();
      		if(adapter!=null) {
      			adapter.notifyDataSetChanged();
      		}
      	  }
        
      	  if(commonController.hasNoEvents(currentTabIndex)) {
      		findViewById(R.id.radar_list_empty_text).setVisibility(View.VISIBLE);
      	  } else {
      		findViewById(R.id.radar_list_empty_text).setVisibility(View.GONE);
       	  }
      	
      	  tabHost.setCurrentTab(currentTabIndex);
        }
      });
    
	  if(loadingDialog!=null && loadingDialog.isShowing()) {
		loadingDialog.dismiss();
		loadingDialog = null;
	  }
	  return true;
	}
}