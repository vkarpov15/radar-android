package com.tabbie.android.radar;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.Facebook;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.gcm.GCMRegistrar;
import com.tabbie.android.radar.MultiSpinner.MultiSpinnerListener;
import com.tabbie.android.radar.adapters.AbstractEventListAdapter;
import com.tabbie.android.radar.adapters.EventListAdapter;
import com.tabbie.android.radar.core.BasicCallback;
import com.tabbie.android.radar.http.ServerGetRequest;
import com.tabbie.android.radar.http.ServerPostRequest;
import com.tabbie.android.radar.http.ServerResponse;
import com.tabbie.android.radar.maps.TLMapActivity;
import com.tabbie.android.radar.model.AbstractViewInflater;
import com.tabbie.android.radar.model.Event;
import com.tabbie.android.radar.model.ListManager;
import com.tabbie.android.radar.model.ShareMessage;

public class MainActivity extends Activity implements
    OnTabChangeListener,
    OnItemClickListener,
    OnItemLongClickListener,
    Handler.Callback {
	
  public static final String TAG = "MainActivity";
  public static final int REQUEST_EVENT_DETAILS = 40;
  public static final int REQUEST_FACEBOOK = 41;
  
  // Important Server Call and Receive Handlers/Threads
  private final Handler upstreamHandler;
  
  // Tab View Constants
  private final short FEATURED = 0;
  private final short ALL = 1;
  private final short LINEUP = 2;
  
  // Inflater objects for adapters
  private final AbstractViewInflater<Event> eventInflater;
  private final AbstractViewInflater<ShareMessage> messageInflater;
  
  // Adapter lists
  private ArrayList<Event> events = new ArrayList<Event>();
  private ListManager manager = new ListManager();

  // Often-used views
  private TabHost tabHost;
  private ListView[] listViews = new ListView[3];

  // Internal state for views
  private int currentViewPosition = 0;
  private short currentTabIndex = 0;

  // FB junk
  private final Facebook facebook = new Facebook("217386331697217");
  private FacebookAuthenticator facebookAuthenticator;
  private FacebookUserRemoteResource facebookUserRemoteResource;
  
  private String tabbieAccessToken = null;
  
  // Google analytics
  private GoogleAnalyticsTracker googleAnalyticsTracker;
  
  public MainActivity() {
	  super();
    
	  /*
	   * Builder object for displaying views
	   * in the user's Event feed
	   */
	  eventInflater = new AbstractViewInflater<Event>(this, R.layout.event_list_element) {
	  	private final ImageLoader mLoader = new ImageLoader(mContext);

			@Override
			protected View bindView(Event data, View v) {
		    ((TextView) v.findViewById(R.id.event_text))
		  		  .setText(data.name);
		
		    ((TextView) v
		  		  .findViewById(R.id.event_list_time))
		  		  .setText(data.time.makeYourTime());
		
		    ((TextView) v
		        .findViewById(R.id.event_location))
		        .setText(data.venue);
		    
		    final View viewHolder = v.findViewById(R.id.image_holder);
		    viewHolder.findViewById(R.id.element_loader).startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.rotate));
		    mLoader.displayImage(data.imageUrl.toString(),
		  		  (ImageView) viewHolder.findViewById(R.id.event_image));
				return v;
			}
		};
		
		/*
		 * Builder object for displaying views
		 * in the user's message feed
		 */
	  messageInflater = new AbstractViewInflater<ShareMessage>(this, R.layout.event_list_element) {
		  // TODO This layout is currently a placeholder for future xml ---------------^
			@Override
			protected View bindView(ShareMessage data, View v) {
				// TODO Auto-generated method stub
				return null;
			}
	  	
	  };
	  
	  final HandlerThread serverThread = new HandlerThread(TAG + "Thread");
	  serverThread.start();
	  upstreamHandler = new ServerThreadHandler(serverThread.getLooper());
  }
  
  @Override
  public void onCreate(final Bundle savedInstanceState) {
	  
    // Set initial conditions
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    
    // Google told me to do this so I did
	  GCMRegistrar.checkDevice(this);
    GCMRegistrar.checkManifest(this);
    final String regId = GCMRegistrar.getRegistrationId(this);
    if (regId.equals("")) {
      GCMRegistrar.register(this, getString(R.string.sender_id));
    } else {
      Log.d(TAG, "Already registered");
      Log.d(TAG, "RegistrationID is: " + regId);
    }

    // Start Google Analytics
    googleAnalyticsTracker = GoogleAnalyticsTracker.getInstance();
    
    // Throw some d's on that bitch
    ((ImageView) findViewById(R.id.loading_spin)).startAnimation(AnimationUtils
        .loadAnimation(this, R.anim.rotate));
  	
  	// Grab a hold of some views
    tabHost = (FlingableTabHost) findViewById(android.R.id.tabhost);
    findViewById(R.id.map_button).setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, TLMapActivity.class);
            intent.putParcelableArrayListExtra("events", events);
            intent.putExtra("token", tabbieAccessToken);
            startActivity(intent);
        }
      });

    // Set up the Tab Host
    tabHost.setup();
    tabHost.setOnTabChangedListener(this);
    tabHost.setCurrentTab(currentTabIndex);
    
    // Instantiate list views
    listViews[FEATURED] = (ListView) findViewById(R.id.featured_event_list);
    listViews[ALL] = (ListView) findViewById(R.id.all_event_list);
    listViews[LINEUP] = (ListView) findViewById(R.id.lineup_event_list);

    // Set Initial Adapters
  	listViews[FEATURED].setAdapter(
  			new EventListAdapter(MainActivity.this,
  					manager.featuredEventsList,
  					new ListManager.DefaultComparator()));
  	
  	
  	// TODO #####################################################
  	final ImageLoader imageLoader = new ImageLoader(this);
  	final AbstractViewInflater<Event> eventInflater = new AbstractViewInflater<Event>(this, R.layout.event_list_element) {
  		
			@Override
			protected View bindView(Event data, View v) {
		    ((TextView) v
		  		  .findViewById(R.id.event_text))
		  		  .setText(data.name);

		    ((TextView) v
		  		  .findViewById(R.id.event_list_time))
		  		  .setText(data.time.makeYourTime());

		    ((TextView) v
		        .findViewById(R.id.event_location))
		        .setText(data.venue);
		    
		    final View viewHolder = v.findViewById(R.id.image_holder);
		    viewHolder.findViewById(R.id.element_loader).startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.rotate));
		    imageLoader.displayImage(data.imageUrl.toString(),
		  		  (ImageView) viewHolder.findViewById(R.id.event_image));
		    
				return null;
			}
		};
		
		final AbstractViewInflater<ShareMessage> smsInflater = new AbstractViewInflater<ShareMessage>(this, R.layout.event_list_element) {

			@Override
			protected View bindView(ShareMessage data, View v) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		final AbstractEventListAdapter myAdapter = new AbstractEventListAdapter<Event>(this, manager.featuredEventsList, R.layout.event_list_element) {

			@Override
			public void buildView(Event source, View v) {
				v = eventInflater.getView(source, v);
			}
		};
		
		// TODO ###################################################
  	
  	listViews[ALL].setAdapter(
  			new EventListAdapter(MainActivity.this,
  					manager.allEventsList,
  					new ListManager.DefaultComparator()));
  	
  	listViews[LINEUP].setAdapter(
  			new EventListAdapter(MainActivity.this,
  					manager.lineupEventsList,
  					new ListManager.ChronologicalComparator()));
    
  	// Set ListView properties
    for(final ListView v : listViews) {
    	v.setFastScrollEnabled(true);
    	v.setOnItemClickListener(this);
    	v.setOnItemLongClickListener(this);
    	createTabView(tabHost, v); 
    }
    
    // Start our authentication chain. First authenticate against facebook
    facebookAuthenticator = new FacebookAuthenticator(facebook, getPreferences(MODE_PRIVATE));
    facebookUserRemoteResource = new FacebookUserRemoteResource(getPreferences(MODE_PRIVATE));
    
    ((TextView) findViewById(R.id.loading_text)).setText("Checking Facebook credentials...");
    facebookAuthenticator.authenticate(this, new BasicCallback<String>() {
      @Override
      public void onFail(String reason) {
        Log.e(this.getClass().getName(), "Facebook auth failed because '" + reason + "'");
        ((TextView) findViewById(R.id.loading_text)).setText("Checking facebook credentials FAILED!");
        throw new RuntimeException(reason);
      }
      
      @Override
      public void onDone(String response) {
        ((TextView) findViewById(R.id.loading_text)).setText("Loading Facebook user information...");
        
        facebookUserRemoteResource.load(upstreamHandler, response, new BasicCallback<String>() {
          @Override
          public void onFail(String reason) {
            Log.e(this.getClass().getName(), "Facebook user info load failed because '" + reason + "'");
            ((TextView) findViewById(R.id.loading_text)).setText("Loading Facebook user information FAILED!");
            throw new RuntimeException(reason);
          }
          
          @Override
          public void onDone(String response) {
            ((TextView) findViewById(R.id.user_name)).setText(response);
            
            ((TextView) findViewById(R.id.loading_text)).setText("Retrieving events...");
            final ServerPostRequest req = new ServerPostRequest(
                getString(R.string.tabbie_server) + "/mobile/auth.json",
                MessageType.TABBIE_LOGIN);

            req.params.put("fb_token", facebook.getAccessToken());
            req.responseHandler = new Handler(MainActivity.this);
            final Message message = Message.obtain();
            message.obj = req;
            upstreamHandler.sendMessage(message);
          }
        });
      }
    });
  }
  
  @Override
  protected void onStart() {
  	if(googleAnalyticsTracker!=null) {
	  	googleAnalyticsTracker.startNewSession("UA-34193317-1", 20, this);
	  	googleAnalyticsTracker.trackPageView(TAG);
  	}
  	super.onStart();
  }
  
  @Override
  protected void onStop() {
  	if(googleAnalyticsTracker!=null) {
  		googleAnalyticsTracker.stopSession();
  	}
  	super.onStop();
  }

  public void onTabChanged(final String tabName) {
	  for(short i = 0; i < listViews.length; i++) {
		  if(tabName.equals(listViews[i].getTag())) {
			  currentTabIndex = i;
		  }
	  }
	  
	  if(listViews[currentTabIndex].getAdapter().isEmpty()) {
		  findViewById(R.id.radar_list_empty_text).setVisibility(View.VISIBLE);
		  return;
	  } else {
		  findViewById(R.id.radar_list_empty_text).setVisibility(View.GONE);
	  }
	  final ListView tabView = listViews[currentTabIndex];
	  ((BaseAdapter) tabView.getAdapter()).notifyDataSetChanged();
      playAnimation(tabView, getBaseContext(), android.R.anim.fade_in, 100);
  }

  public Animation playAnimation(View v, Context con, int animationId,
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
    case REQUEST_FACEBOOK:
      facebook.authorizeCallback(requestCode, resultCode, data);
      break;
    case REQUEST_EVENT_DETAILS:
      final Bundle parcelables = data.getExtras();
      events = parcelables.getParcelableArrayList("events");
      manager.clear();
      manager.addAll(events);
        
      tabHost.setCurrentTab(currentTabIndex);

      for (final ListView v : listViews) {
        final BaseAdapter adapter = (BaseAdapter) v.getAdapter();
        if (adapter != null) {
          adapter.notifyDataSetChanged();
        }
      }

      listViews[currentTabIndex].setSelection(currentViewPosition);
        
      // TODO Use this paradigm (in more robust form) for other instances of this view
      if(listViews[currentTabIndex].getAdapter().isEmpty()) {
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
	  req.responseHandler = new Handler(this);
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
      	req.responseHandler = new Handler(this);
    	  final Message message = Message.obtain();
    	  message.obj = req;
    	  upstreamHandler.sendMessage(message);
    	  break;
      case R.id.report_me:
    		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    		emailIntent.setType("plain/text");
    		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, getString(R.array.founders_email));
    		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
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
    GCMRegistrar.onDestroy(this);
  }
  
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
		
		final Event e = (Event) parent.getItemAtPosition(position);
		
		googleAnalyticsTracker.trackEvent("Event", "Click", e.name, 1);
		googleAnalyticsTracker.dispatch();
		
		Log.d("OnItemClick", "Event is " + e.name);
		
	  if (null != e) {
	    currentViewPosition = position;
	    ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(30);

	    new AsyncTask<Void, Void, Intent>() {
	      private ProgressDialog dialog;

	      @Override
	      protected void onPreExecute() {
	        dialog = ProgressDialog.show(MainActivity.this, "",
	            "Loading, please wait...");
	        super.onPreExecute();
	      }

	      @Override
	      protected Intent doInBackground(Void... params) {
	        Intent intent = new Intent(MainActivity.this,
	            EventDetailsActivity.class);
	        intent.putExtra("eventIndex", events.indexOf(e));
	        intent.putParcelableArrayListExtra("events", events);
	        intent.putExtra("token", tabbieAccessToken);
	        return intent;
	      }

	      @Override
	      protected void onPostExecute(Intent result) {
	        startActivityForResult(result,
	            REQUEST_EVENT_DETAILS);
	        dialog.dismiss();
	      }
	    }.execute();
	  }
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v, int position,
			long rowId) {
		// TODO Pop up a dialog here
		
		GCMRegistrar.unregister(this);
		Log.d(TAG, "Registration ID: " + GCMRegistrar.getRegistrationId(this));
		
		return true;
	}

	@Override
	public synchronized boolean handleMessage(final Message msg) {
		Log.d(TAG, "Received message response");
		if(!(msg.obj instanceof ServerResponse)) {
			Log.e(TAG, "Message is not a Server Response");
			return false;
		}
		final ServerResponse resp = (ServerResponse) msg.obj;
		Log.v(TAG, resp.content);
		
		switch (resp.responseTo) {
    case TABBIE_LOGIN: {
      final JSONObject json = resp.parseJsonContent();
      if (json.has("token")) {
        try {
          tabbieAccessToken = json.getString("token");
        } catch (final JSONException e) {
          e.printStackTrace();
          return false;
        } finally {
          
          final ServerGetRequest req = new ServerGetRequest(
              getString(R.string.tabbie_server) + "/mobile/all.json?auth_token="
              + tabbieAccessToken, MessageType.LOAD_EVENTS);
          req.responseHandler = new Handler(this);
          final Message message = Message.obtain();
          message.obj = req;
          upstreamHandler.sendMessage(message);
        }
      } else {
      Log.e(TAG, "Tabbie Log-in JSON does not have a token!");
        throw new RuntimeException();
      }
      break;
    }
		case LOAD_EVENTS:
  		final JSONArray list = resp.parseJsonArray();
  		final Set<String> serverRadarIds = new LinkedHashSet<String>();
  		try {
  			final JSONObject radarObj = list.getJSONObject(list.length() - 1);
  			JSONArray tmpRadarList = radarObj.getJSONArray("radar");
  			for(int i = 0; i < tmpRadarList.length(); ++i) {
  				serverRadarIds.add(tmpRadarList.getString(i));
  			}
  			events.clear();
  			final String domain = getString(R.string.tabbie_server);
  			for(int i = 0; i < list.length() - 1; ++i) {
  				final JSONObject obj = list.getJSONObject(i);
  				final String radarCountStr = obj.getString("user_count");
  				int radarCount = 0;
  				if (null != radarCountStr && 0 != radarCountStr.compareTo("null"))
  					radarCount = Integer.parseInt(radarCountStr);
  				final JSONObject rsvpObj = obj.getJSONObject("rsvp");
  				
  				Pair<String, String> rsvp = null;
  				if (rsvpObj.has("url")) {
  					rsvp = new Pair<String, String>("url", rsvpObj.getString("url"));
  				} else if (rsvpObj.has("email")) {
  					rsvp = new Pair<String, String>("email", rsvpObj.getString("email"));
  				} else {
  					rsvp = new Pair<String, String>("", "");
  				}
  				
  				// TODO ################################################
  				final int energy = 0;
  				final Event.Energy energyLevel;
  				switch(energy) {
  				case 0:
  					energyLevel = Event.Energy.LOW;
  					break;
  				case 1:
  					energyLevel = Event.Energy.MODERATE;
  					break;
  				case 2:
  					energyLevel = Event.Energy.HIGH;
  					break;
  					default:
  						energyLevel = Event.Energy.MODERATE;
  				}
  				
  				final int price = 0;
  				final Event.Price priceLevel;
  				switch(price) {
  				case 0:
  					priceLevel = Event.Price.FREE;
  					break;
  				case 1:
  					priceLevel = Event.Price.CHEAP;
  					break;
  				case 2:
  					priceLevel = Event.Price.EXPENSIVE;
  					break;
  					default:
  						priceLevel = Event.Price.CHEAP;
  				}
  				// TODO ################################
  				final Event e = new Event(  obj.getString("id"),
  	                                    obj.getString("name"),
  	                                    obj.getString("description"),
  	                                    obj.getString("location"),
  	                                    obj.getString("street_address"),
  	                                    new URL(domain + obj.getString("image_url")),
  	                                    (int) (obj.getDouble("latitude")*1E6),
  	                                    (int) (obj.getDouble("longitude")*1E6),
  	                                    radarCount,
  	                                    obj.getBoolean("featured"),
  	                                    obj.getString("start_time"),
  	                                    serverRadarIds.contains(obj.getString("id")),
  	                                    rsvp);
  				events.add(e);
  			}
  			manager.clear();
  			manager.addAll(events);
      } catch (final JSONException e) {
      	Toast.makeText(this, "Fatal Error: Failed to Parse JSON",
          Toast.LENGTH_SHORT).show();
      	e.printStackTrace();
      	return false;
      } catch (final MalformedURLException e) {
      	Log.e(TAG, "Malformed URL during Event creation");
      	Toast.makeText(this, "Error occurred during boot", Toast.LENGTH_LONG).show();
      	return false;
      }
  	  this.runOnUiThread(new Runnable() {
  		  public void run() {
  			  for(final ListView v : listViews) {
  	      		final BaseAdapter adapter = (BaseAdapter) v.getAdapter();
  	      		if(adapter!=null) {
  	      			adapter.notifyDataSetChanged();
  	      		}
  	      }
  	    	if(listViews[currentTabIndex].getAdapter().isEmpty()) {
  	    		findViewById(R.id.radar_list_empty_text).setVisibility(View.VISIBLE);
  	    	} else {
  	    		findViewById(R.id.radar_list_empty_text).setVisibility(View.GONE);
  	     	}
  	    	tabHost.setCurrentTab(currentTabIndex);
        }
      });
  	  
  	  ((ImageView) findViewById(R.id.loading_spin)).clearAnimation();
  	  findViewById(R.id.loading_screen).setVisibility(View.GONE);
  	  findViewById(R.id.tonightlife_layout).setVisibility(View.VISIBLE);
  	  tabHost.setVisibility(View.VISIBLE);
  	  
  	  break;
  	default:
  	  break;  
		}
	  return true;
	}
	
	private static final void createTabView(final TabHost host, final ListView view) {
		final String tag = view.getTag().toString();
		final View tabIndicatorView = LayoutInflater.from(host.getContext())
				.inflate(R.layout.tabs_bg, null); 
		((TextView) tabIndicatorView.findViewById(R.id.tabs_text)).setText(tag);
		
	  final TabSpec content = host.newTabSpec(tag)
	      .setIndicator(tabIndicatorView)
	      .setContent(new TabHost.TabContentFactory() {
	        public View createTabContent(final String tag) {
	          return view;
	        }
	      });
	  host.addTab(content);
	}
}