package com.tabbie.android.radar.remote;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

import com.tabbie.android.radar.core.BasicCallback;
import com.tabbie.android.radar.enums.MessageType;
import com.tabbie.android.radar.http.ServerRequest;
import com.tabbie.android.radar.http.ServerResponse;

/**
 *  TonightLifeAuthenticator.java
 * 
 *  Created on: October 21, 2012
 *      Author: Valeri Karpov
 * 
 *  Manage TonightLife authentication state, similar to FacebookAuthenticator
 * 
 */

public class TonightLifeAuthenticator implements Handler.Callback {
  public static final String TAG = "TonightlifeAuthenticator";
  private static final String TONIGHTLIFE_TOKEN_PREFERENCES_NAME = "tonightlife_token";
  private static final String TONIGHTLIFE_TOKEN_EXPIRES_PREFERENCES_NAME = "tonightlife_token_expires";
  private static final String TONIGHTLIFE_GCM_KEY_NAME = "tonightlife_gcm_key";
  
  private final SharedPreferences preferences;
  
  private String tonightlifeToken;
  private String gcmKey;
  private long expires = 0;
  
  private BasicCallback<Pair<String, String> > tonightlifeTokenAndGCMCallback = null;

  public TonightLifeAuthenticator(SharedPreferences preferences) {
    this.preferences = preferences;
  }
  
  public boolean isValidSession() {
    return System.currentTimeMillis() < expires;
  }
  
  public String getTonightlifeToken() {
    return tonightlifeToken;
  }
  
  public String getGCMKey() {
    return gcmKey;
  }
  
  public void loadStoredState() {
    tonightlifeToken = preferences.getString(TONIGHTLIFE_TOKEN_PREFERENCES_NAME, "");
    expires = preferences.getLong(TONIGHTLIFE_TOKEN_EXPIRES_PREFERENCES_NAME, 0);
    gcmKey = preferences.getString(TONIGHTLIFE_GCM_KEY_NAME, "");
  }
  
  public void authenticate(Handler serverCallHandler,
      String fbAccessToken, final BasicCallback<Pair<String, String> > tonightlifeTokenAndGCMCallback) {
    this.tonightlifeTokenAndGCMCallback = tonightlifeTokenAndGCMCallback;
    
    loadStoredState();
    
    if (isValidSession()) {
      tonightlifeTokenAndGCMCallback.onDone(new Pair<String, String>(tonightlifeToken, gcmKey));
    } else {
      ServerRequest req = new ServerRequest(MessageType.TABBIE_LOGIN, new Handler(this));
      req.mParams.put("fb_token", fbAccessToken);
      final Message message = Message.obtain();
      message.obj = req;
      serverCallHandler.sendMessage(message);
    }
  }

  @Override
  public boolean handleMessage(Message msg) {
    final ServerResponse resp = (ServerResponse) msg.obj;
    if (tonightlifeTokenAndGCMCallback == null) {
      return false;
    }
    if (resp.responseTo != MessageType.TABBIE_LOGIN) {
      tonightlifeTokenAndGCMCallback.onFail("Invalid message response!");
      return false;
    }
    
    final JSONObject json = resp.parseJsonContent();
    Editor prefs = preferences.edit();
    if (json.has("token")) {
      try {
        tonightlifeToken = json.getString("token");
        prefs.putString(TONIGHTLIFE_TOKEN_PREFERENCES_NAME, tonightlifeToken);
        prefs.putLong(TONIGHTLIFE_TOKEN_EXPIRES_PREFERENCES_NAME,
            System.currentTimeMillis() + 24 * 60 * 60 * 1000 /* 24 hours */);
      } catch (final JSONException e) {
        e.printStackTrace();
        tonightlifeTokenAndGCMCallback.onFail(e.toString());
        return false;
      }
    } else {
      Log.e(TAG, "Tabbie Log-in JSON does not have a token!");
      tonightlifeTokenAndGCMCallback.onFail("Login didn't return a token!");
      return false;
    }
    
    if (json.has("gcm_key")) {
      try {
        gcmKey = json.getString("gcm_key");
        prefs.putString(TONIGHTLIFE_GCM_KEY_NAME, gcmKey);
        prefs.commit();
        tonightlifeTokenAndGCMCallback.onDone(new Pair<String, String>(tonightlifeToken, gcmKey));
      } catch (JSONException e) {
        e.printStackTrace();
        return false;
      }
    } else {
      tonightlifeTokenAndGCMCallback.onFail("No gcm key!");
      return false;
    }
    return true;
  }
}
