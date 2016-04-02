CWAC-Pager: Power to the PagerAdapter
=====================================

This project
offers an `ArrayPagerAdapter` that offers another alternative `PagerAdapter`
implementation for use with `ViewPager`.

This Android library project is 
[available as a JAR](https://github.com/commonsguy/cwac-pager/releases)
or as an artifact for use with Gradle. To use that, add the following
blocks to your `build.gradle` file:

```groovy
repositories {
    maven {
        url "https://s3.amazonaws.com/repo.commonsware.com"
    }
}

dependencies {
    compile 'com.commonsware.cwac:pager:0.2.+'
}
```

Or, if you cannot use SSL, use `http://repo.commonsware.com` for the repository
URL.

NOTE: The JAR name, as of v0.2.2, has a `cwac-` prefix, to help distinguish it from other JARs.

The two concrete `PagerAdapter` implementations shipped in the Android
Support package -- `FragmentPagerAdapter` and `FragmentStatePagerAdapter` -- have
their limitations when it comes to things like:

- Using fragments created by those adapters in other fashions, such as in using
`ViewPager` in portrait and columns of "pages" in landscape

- Handling dynamically-changing contents, such as adding pages, removing pages,
or reordering pages

The `ArrayPagerAdapter` is an attempt to provide a more flexible `PagerAdapter`
implementation that still feels a lot like `FragmentPagerAdapter` in terms of its
use of fragments. It also bears some resemblance to the `ArrayAdapter` used for
`AdapterView` implementations like `ListView`, giving rise to its name.

Usage
-----
Once you have added the JAR or AAR to your project, you can start using
`ArrayPagerAdapter`.

### Choosing the Package

There are two implementations of `ArrayPagerAdapter`. One, in the
`com.commonsware.cwac.pager` package, is designed for use with native API Level 11
fragments. The other, in the `com.commonsware.cwac.pager.v4` package, is designed
for use with the Android Support package's backport of fragments. You will need
to choose the right `ArrayPagerAdapter` for the type of fragments that you 
are using.

However, other than choosing suitable versions of classes for `Fragment`, etc.,
there is no real public API difference between the two. Hence, the documentation
that follows is suitable for either implementation of `ArrayPagerAdapter`, so long
as you use the one that matches the source of your fragment implementation.

Note that only `ArrayPagerAdapter` lives in the `com.commonsware.cwac.pager.v4`
package. The classes and interfaces that support `ArrayPagerAdapter`, like
`PageDescriptor`, are implemented in `com.commonsware.cwac.pager` and used by both
implementations of `ArrayPagerAdapter`.

### Creating PageDescriptors

You might think that `ArrayPagerAdapter` would take an array of pages, much like
`ArrayAdapter` takes an array of models.

That's not how it works.

Instead, `ArrayPagerAdapter` wants an `ArrayList` of `PageDescriptor` objects.
`PageDescriptor` is an interface, requiring you to supply implementations of
two methods:

- `getTitle()`, which will be the title used for this page, for things like
`PagerTabStrip` and the ViewPagerIndicator family of indicators

- `getFragmentTag()`, which is a unique tag for this page's fragment

Also, `PageDescriptor` extends the `Parcelable` interface, and so any implementation
of `PageDescriptor` must also implement the methods and `CREATOR` required by
`Parcelable`.

You are welcome to create your own `PageDescriptor` if you wish. However, there
is a built-in implementation, `SimplePageDescriptor`, which probably meets
your needs. You just pass the tag and title into the `SimplePageDescriptor`
constructor, and it handles everything else, including the `Parcelable`
implementation.

Note that `getFragmentTag()` must return unique values compared to any
other outstanding page in the adapter.

### Creating and Populating the Adapter

To work with `ArrayPagerAdapter`, you start by creating an `ArrayList` of
`PageDescriptor` objects, one for each page that is to be in your pager.

Then, create a subclass of `ArrayPagerAdapter`. `ArrayPagerAdapter` uses
Java generics, requiring you to declare the type of fragment the adapter
is serving up to the `ViewPager`. So, for example, if you have a `ViewPager`
that will have each page be an `EditorFragment`, you would declare your
custom `ArrayPagerAdapter` like so:

    static class SamplePagerAdapter extends
       ArrayPagerAdapter<EditorFragment> {

If you will have pages come from a variety of fragments, just use the
`Fragment` base class appropriate for your fragment source
(e.g., `android.app.Fragment`).

Your custom `ArrayPagerAdapter` subclass will need to override
(at minimum) one method: `createFragment()`. This method is responsible for
instantiating fragments, as requested. You are passed the `PageDescriptor`
for the fragment to be created -- you simply create and return that fragment.

Hence, a custom `ArrayPagerAdapter` can be as simple as:

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

Then, you can create an instance of your custom `ArrayPagerAdapter` subclass
as needed, supplying the constructor with a suitable `FragmentManager` and your
`ArrayList` of `PageDescriptor` objects. Once attached to a `ViewPager`,
`ArrayPagerAdapter` behaves much like a `FragmentPagerAdapter` by default.

There is another flavor of the `ArrayPagerAdapter` constructor, one that takes
a `RetentionStrategy` as a parameter. This will eventually allow `ArrayPagerAdapter`
to work either like `FragmentPagerAdapter` (current) or `FragmentStatePagerAdapter`
(future).

### Modifying the Contents

`ArrayPagerAdapter` offers several methods to allow you to change the contents
of the `ViewPager`:

- `add()` takes a `PageDescriptor` and adds a new page at the end of the current
roster of pages

- `insert()` takes a `PageDescriptor` and an insertion point and inserts a new
page before the current page at that insertion point

- `remove()` takes a position and removes the page at that position

- `move()` takes an old and new position and moves the page from the old position
to the new position (effectively combining a `remove()` from the old position and
an `insert()` of the same page into the new position

### Other Useful Methods

- `getExistingFragment()`, given a position, returns the existing fragment for that
position in the `ViewPager`, if that fragment exists. Otherwise, it returns
`null`.

- `getCurrentFragment()` is like `getExistingFragment()`, but returns the fragment
for the currently-viewed page in the `ViewPager`.

- `getPositionForTag()` tells you the position index of the page associated
with a particular tag, where the tag comes from the `PageDescriptor`

- `getPageDescriptor()`, given a position, returns the `PageDescriptor`
associated with that position (e.g., so you can modify data in the descriptor)

### Limitations

The `ViewPager` used by the `ArrayPagerAdapter` must have the same ID in all configurations.
Usually, this is not a problem, particularly if the ID is set to be the same via
`android:id` in layouts. However, if you are dynamically creating your `ViewPager`
instance at runtime, this is something to keep in mind.

Dependencies
------------
This project depends on the Android Support package at compile time, if you are using
the Android library project. It also depends on the Android Support package at runtime
if you are using the `v4` classes. The Gradle files handle this automatically,
pulling from the appropriate repositories.

Version
-------
This is version v0.2.5 of this module, meaning it is still pretty young.

Demo
----
In the `demo/` sub-project you will find a sample project demonstrating the use
of `ArrayPagerAdapter` for the native API Level 11 implementation of fragments. The
`demo-v4/` sub-project has a similar sample for the `v4` backport of fragments from
the Android Support package.

Additional Documentation
------------------------
[The Busy Coder's Guide to Android Development](https://commonsware.com/Android)
contains two chapters covering `ViewPager`. The second chapter, on advanced
`ViewPager` techniques, covers this library in depth.

License
-------
The code in this project is licensed under the Apache
Software License 2.0, per the terms of the included LICENSE
file.

Questions
---------
If you have questions regarding the use of this code, please post a question
on [StackOverflow](http://stackoverflow.com/questions/ask) tagged with `commonsware` and `android`. Be sure to indicate
what CWAC module you are having issues with, and be sure to include source code 
and stack traces if you are encountering crashes.

If you have encountered what is clearly a bug, or if you have a feature request,
please post an [issue](https://github.com/commonsguy/cwac-presentation/issues).
The [contribution guidelines](CONTRIBUTING.md)
provide some suggestions for how to create a bug report that will get
the problem fixed the fastest.

Do not ask for help via Twitter.

Also, if you plan on hacking
on the code with an eye for contributing something back,
please open an issue that we can use for discussing
implementation details. Just lobbing a pull request over
the fence may work, but it may not.
Again, the [contribution guidelines](CONTRIBUTING.md) provide a bit
of guidance here.

Release Notes
-------------
- v0.2.5: added `setTitle()` to `SimplePageDescriptor`, `getPositionForTag()` and `getPageDescriptor()` to `ArrayPagerAdapter`
- v0.2.4: updated to Android Studio 1.0 and new AAR publishing system
- v0.2.3: added some defensive programming around state
- v0.2.2: merged issue #5 to support `List` in `ArrayPagerAdapter` constructor
- v0.2.1: re-fixed `Parcelable` classloader bug, now enforcing tag uniqueness
- v0.2.0: added Gradle build files and published AAR
- v0.1.2: fixed `Parcelable` classloader bug
- v0.1.1: minor bug fixes in backwards-compatibility support
- v0.1.0: initial release

Who Made This?
--------------
<a href="http://commonsware.com">![CommonsWare](http://commonsware.com/images/logo.png)</a>

