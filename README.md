SplitActivity
=============

Activity master+content pattern library.

   Image: IO13 example
![IO13 Example](https://raw.github.com/BenoitDuffez/SplitActivity/master/io13_example.png)


Behavior
--------

The activies that extend `SplitActivity` from this lib will display their content:

* On phones: only one pane (either master or content)
* On 7", portrait: same as phones
* On 7", landscape: same as tablets
* On tablets: two panes (master on the left, content on the right)

Also, when there is a screen rotation, the activity behaves as expected. This can be tricky, especially on 7" tablets where we switch from single to dual pane display.

It is then really easy to have a behavior like in the IO13 app (see screenshot above).


Java code
---------

* make your activity extend `SplitActivity`.
* implement `createMainFragment`: this method is called when the main fragment has to be created
* implement `createContentFragment`: this method is called when the content fragment has to be created
* you can override `createEmptyFragment` if you want to use a custom fragment when there is no content selected, but the UI has two panes.
* set up your themes

Here's an example of a minimal `Activity`:


```java
public class MainActivity extends SplitActivity<MainListFragment, ContentFragment> {
	@Override
	protected MainListFragment createMainFragment(Bundle args) {
		return MainListFragment.newInstance(args);
	}

	@Override
	protected ContentFragment createContentFragment(final Bundle args) {
		return ContentFragment.newInstance(args);
	}

	@Override
	protected Fragment createEmptyFragment(Bundle args) {
		return new EmptyFragment();
	}

	private static class EmptyFragment extends SherlockFragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View v = new LinearLayout(inflater.getContext());
			v.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			v.setBackgroundResource(R.drawable.projects_empty_fragment); // a custom empty image displayed in the center of the content pane
			return v;
		}
	}
}
```


Here's what your main fragment should do to load something (here we have a `ListView`, but it could be anything):


```java
public class MainFragment extends SherlockFragment {
	// ...

	@Override
	public void onListItemClick(final ListView l, final View v, final int position, final long id) {
		final Bundle args = new Bundle();
		// ...
		// build your Bundle with args for your contents fragment
		((SplitActivity) getActivity()).selectContent(args);
		// this call will replace the current active fragment with the content one on phones,
		// and will just replace the content fragment on tablets
	}
}
```


XML code
--------

Just make your theme point to `Theme.SplitActivity` (or use this one as the parent of your theme). There is also `Theme.SplitActivity.Light` and all the family!

All the themes have `Theme.Sherlock.*` as their parent, so no worries about your ActionBarSherlock setup.

Themes:
Your main theme can implement these:

```xml
    <resources>
        <style name="Example" parent="Theme.SplitActivity.Light">
            <item name="mainPaneStyle">@style/MainPaneStyle</item>
            <item name="contentPaneStyle">@style/ContentPaneStyle</item>
            <item name="containerStyle">@style/ContainerStyle</item>
            <item name="separatorStyle">@style/SeparatorStyle</item>
        </style>
    </resources>
```

Here's an example of customization:

```xml
    <!-- The container exists only when there are two panes. It's the big container. -->
    <style name="ContainerStyle" parent="Widget.SplitActivity.Container">
        <item name="android:background">@drawable/hashed_background</item>
    </style>

    <!-- This is the main pane style. -->
    <style name="MainPaneStyle" parent="Widget.SplitActivity.MainPane">
        <item name="android:layout_margin">12dp</item>
        <item name="android:layout_weight">2</item>
        <item name="android:background">#FFF</item>
    </style>

    <!-- This is the content pane style. It is used both when in single pane (phone) or dual pane (tablet) presentation. -->
    <style name="ContentPaneStyle" parent="Widget.SplitActivity.ContentPane">
        <item name="android:layout_margin">24dp</item>
        <item name="android:layout_weight">3</item>
        <item name="android:background">#FFF</item>
    </style>

    <!-- This is the separator view between the master and the content. Of course, only used in tablet presentation! -->
    <style name="SeparatorStyle" parent="Widget.SplitActivity.Separator">
        <item name="android:background">#F00</item>
        <item name="android:layout_width">2dp</item>
    </style>
```

Don't forget to tune your styles with `-sw600dp-land` and `-sw720dp`!  
For example, you could specify a different `layout_weight` ratio for 7" landscape and 10" tablets.

Go check the Samples app that is bundled, it should compile with a few tweaks and show pretty well how to use and style this.



LICENSE
=======

```
Copyright 2013 Benoit Duffez

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```


