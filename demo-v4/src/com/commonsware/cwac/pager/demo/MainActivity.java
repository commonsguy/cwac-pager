/***
  Copyright (c) 2013 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
  
  From _The Busy Coder's Guide to Android Development_
    http://commonsware.com/Android
 */

package com.commonsware.cwac.pager.demo;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import java.util.ArrayList;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.commonsware.cwac.pager.PageDescriptor;
import com.commonsware.cwac.pager.SimplePageDescriptor;
import com.commonsware.cwac.pager.v4.ArrayPagerAdapter;

public class MainActivity extends SherlockFragmentActivity {
  private ArrayPagerAdapter<EditorFragment> adapter=null;
  private ViewPager pager=null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    pager=(ViewPager)findViewById(R.id.pager);
    adapter=buildAdapter();
    pager.setAdapter(adapter);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getSupportMenuInflater().inflate(R.menu.actions, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.add:
        add(true);
        break;

      case R.id.split:
        add(false);
        break;

      case R.id.remove:
        remove();
        break;

      case R.id.swap:
        swap();
        break;
    }

    return(super.onOptionsItemSelected(item));
  }

  private String buildTag(int position) {
    return("editor" + String.valueOf(position));
  }

  private String buildTitle(int position) {
    return(String.format(getString(R.string.hint), position + 1));
  }

  private ArrayPagerAdapter<EditorFragment> buildAdapter() {
    ArrayList<PageDescriptor> pages=new ArrayList<PageDescriptor>();

    for (int i=0; i < 10; i++) {
      pages.add(new SimplePageDescriptor(buildTag(i), buildTitle(i)));
    }

    return(new SamplePagerAdapter(getSupportFragmentManager(), pages));
  }

  private void add(boolean before) {
    int current=pager.getCurrentItem();
    SimplePageDescriptor desc=
        new SimplePageDescriptor(buildTag(adapter.getCount()),
                                 buildTitle(adapter.getCount()));

    if (before) {
      adapter.insert(desc, current);
    }
    else {
      if (current < adapter.getCount() - 1) {
        adapter.insert(desc, current + 1);
      }
      else {
        adapter.add(desc);
      }
    }
  }

  private void remove() {
    if (adapter.getCount() > 1) {
      adapter.remove(pager.getCurrentItem());
    }
  }

  private void swap() {
    int current=pager.getCurrentItem();

    if (current < adapter.getCount() - 1) {
      adapter.move(current, current + 1);
    }
    else {
      adapter.move(current, current - 1);
    }
  }

  static class SamplePagerAdapter extends
      ArrayPagerAdapter<EditorFragment> {
    public SamplePagerAdapter(FragmentManager fragmentManager,
                              ArrayList<PageDescriptor> descriptors) {
      super(fragmentManager, descriptors);
    }

    @Override
    protected EditorFragment createFragment(PageDescriptor desc) {
      return(EditorFragment.newInstance(desc.getTitle()));
    }
  }
}