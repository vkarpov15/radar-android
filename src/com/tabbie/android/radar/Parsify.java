package com.tabbie.android.radar;

import java.util.Date;
public class Parsify {
  protected static String makeYourTime(final Date d) {
    final String minutes = d.getMinutes() > 9 ? ":" + d.getMinutes() : ":0"
        + d.getMinutes();
    final int hours = d.getHours();
    if (hours == 0)
      return "12" + minutes + "am";
    else if (hours > 0 && hours < 12)
      return Integer.toString(hours) + minutes + "am";
    else
      return Integer.toString(hours - 12) + minutes + "pm";
  }
}