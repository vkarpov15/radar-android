package com.tabbie.android.radar;

import java.net.URL;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventListAdapter extends BaseAdapter {
	
	private final Context context;
	
	private List<Event> events;
	
	
	public ImageLoader imageLoader; // I don't know why this is public, but it is in the source

    public EventListAdapter(Context context, List<Event> events) {
    	this.events = events;
    	this.context = context;
    	imageLoader = new ImageLoader(context);
    }

    @Override
    public View getView(final int position,
    		View convertView, final ViewGroup parent) {
    	
      if (null == convertView) {
    	Log.i("EventListAdapter", "Instantiating a new list view element");
        convertView = LayoutInflater.from(context).inflate(R.layout.event_list_element, null);
      }
      
      final Event e = (Event) getItem(position);
      
      
      // Populate TextViews with data
      ((TextView) convertView
    		  .findViewById(R.id.event_text))
    		  .setText(e.name);

      ((TextView) convertView
    		  .findViewById(R.id.event_list_time))
    		  .setText(e.time.makeYourTime());

      ((TextView) convertView
          .findViewById(R.id.event_location))
          .setText(e.venueName);

      ((TextView) convertView
    		  .findViewById(R.id.upvotes))
    		  .setText(Integer.toString(e.radarCount));

      /*
       * Check and see if there is an image that has been loaded If there is an
       * image that has been loaded and it has been drawn, then do nothing If
       * there is an image that has been loaded, but it hasn't been drawn, draw
       * it If there is no image that has been loaded, display the loader and
       * LOAD THAT SH*T
       */
      /*
      final ImageView loader = (ImageView) convertView
          .findViewById(R.id.element_loader);*/
      final ImageView img = (ImageView) convertView
          .findViewById(R.id.event_image);
      
      imageLoader.displayImage(e.image.toString(), img);

      /*
      convertView.findViewById(R.id.list_list_element_layout)
          .setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
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
                    intent.putExtra("eventId", e.id);
                    intent.putExtra("controller", commonController);
                    intent.putExtra("image", remoteDrawableController.getAsParcelable(e.image));
                    intent.putExtra("token", token);
                    if (tabbieVirgin) {
                      intent.putExtra("virgin", true); // Make sure this
                                                       // activity knows it's in
                                                       // tutorial mode
                      tabbieVirgin = false; // User is no longer a prepubescent
                                            // pussy
                      getPreferences(MODE_PRIVATE).edit()
                          .putBoolean("virgin", false).commit();
                    }
                    return intent;
                  }

                  @Override
                  protected void onPostExecute(Intent result) {
                    startActivityForResult(result,
                        RadarCommonController.RETRIEVE_INSTANCE);
                    dialog.dismiss();

                  };
                }.execute();

              }
            }
          }); */

      /*
      convertView.findViewById(R.id.location_image_layout).setOnClickListener(
          new OnClickListener() {
            public void onClick(View v) {
              Intent intent = new Intent(RadarActivity.this,
                  RadarMapActivity.class);
              intent.putExtra("controller", commonController);
              intent.putExtra("event", e);
              startActivity(intent);
            }
          });*/
      return convertView;
    }

	@Override
	public int getCount() {
		return events.size();
	}

	@Override
	public Object getItem(int position) {
		return events.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
  }