package com.commonsware.cwac.pager;

import android.os.Parcelable;

public interface PageDescriptor extends Parcelable {
  String getFragmentTag();

  String getTitle();
}