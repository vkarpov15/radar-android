package com.tabbie.android.radar;

import android.widget.TextView;

/**
 *  RemoteDrawableController.java
 *
 *  Created on: Aug 6, 2012
 *      Author: Valeri Karpov
 *      
 *  Data structure for maintaining a text view with a
 *  length constraint
 */

public class TextViewConstrainLength {
  private final TextView  textView;
  private final int       maxLength;
  
  public TextViewConstrainLength(TextView textView, int maxLength) {
    this.textView   = textView;
    this.maxLength  = maxLength;
  }
  
  public int setText(String text) {
    if (text.length() <= maxLength) {
      textView.setText(text);
      return text.length();
    }
    int space = text.substring(0, maxLength - 3).lastIndexOf(' ');
    if (-1 != space) {
      textView.setText(text.substring(0, space) + "...");
      return space + 3;
    } else {
      textView.setText(text.substring(0, maxLength - 3) + "...");
      return maxLength;
    }
  }
}
