package com.tabbie.android.radar.auth;

import android.os.Parcel;
import android.os.Parcelable;

import com.tabbie.android.radar.core.facebook.FacebookAuthenticator;

/**
 *  AuthenticationState.java
 * 
 *  Created on: October 21, 2012
 *      Author: Valeri Karpov
 * 
 *  High level interface for handling our authentication state
 * 
 */

public class AuthenticationState implements Parcelable {
  private FacebookAuthenticator facebookAuthenticator;
  
  @Override
  public int describeContents() {
    return 0;
  }
  @Override
  public void writeToParcel(Parcel arg0, int arg1) {
    // TODO Auto-generated method stub
    
  }
  
}
