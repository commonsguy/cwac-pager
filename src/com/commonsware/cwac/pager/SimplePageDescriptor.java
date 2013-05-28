package com.commonsware.cwac.pager;

import android.os.Parcel;
import android.os.Parcelable;

public class SimplePageDescriptor implements PageDescriptor {
  private String tag=null;
  private String title=null;

  public static final Parcelable.Creator<SimplePageDescriptor> CREATOR=
      new Parcelable.Creator<SimplePageDescriptor>() {
        public SimplePageDescriptor createFromParcel(Parcel in) {
          return new SimplePageDescriptor(in);
        }

        public SimplePageDescriptor[] newArray(int size) {
          return new SimplePageDescriptor[size];
        }
      };

  public SimplePageDescriptor(String tag, String title) {
    this.tag=tag;
    this.title=title;
  }

  private SimplePageDescriptor(Parcel in) {
    tag=in.readString();
    title=in.readString();
  }

  @Override
  public int describeContents() {
    return(0);
  }

  @Override
  public void writeToParcel(Parcel out, int flags) {
    out.writeString(tag);
    out.writeString(title);
  }

  public String getTitle() {
    return(title);
  }

  public String getFragmentTag() {
    return(tag);
  }
}