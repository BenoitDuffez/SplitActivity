SplitActivity
=============

Activity master+content pattern library.
* On phones: only one pane (either master or content)
* On 7", portrait: same as phones
* On 7", landscape: same as tablets
* On tablets: two panes (master on the left, content on the right)

Java:
* make your activity extend `SplitActivity`.
* implement `createMainFragment`: this method is called when the main fragment has to be created
* implement `createContentFragment`: this method is called when the content fragment has to be created
* you can override `createEmptyFragment` if you want to use a custom fragment when there is no content selected, but the UI has two panes.

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
	}

XML:
just make your theme point to `Theme.SplitActivity` (or use this one as the parent of your theme). There is also `Theme.SplitActivity.Light` and all the family!
All the themes have `Theme.Sherlock.*` as their parent, so no worries about your ActionBarSherlock setup.

Themes:
Your main theme can implement these:

    <resources>
        <style name="Example" parent="Theme.SplitActivity.Light">
            <item name="mainPaneStyle">@style/MainPaneStyle</item>
            <item name="contentPaneStyle">@style/ContentPaneStyle</item>
            <item name="containerStyle">@style/ContainerStyle</item>
            <item name="separatorStyle">@style/SeparatorStyle</item>
        </style>
    </resources>

Here's an example of customization:

    <!-- Master/content related styles -->
    <style name="ContainerStyle" parent="Widget.SplitActivity.Container">
        <!--<item name="android:background">#00FF00</item>-->
    </style>

    <style name="MainPaneStyle" parent="Widget.SplitActivity.MainPane">
        <item name="android:layout_margin">12dp</item>
        <item name="android:layout_weight">2</item>
        <item name="android:background">#FFF</item>
    </style>

    <style name="ContentPaneStyle" parent="Widget.SplitActivity.ContentPane">
        <item name="android:layout_margin">24dp</item>
        <item name="android:layout_weight">3</item>
        <item name="android:background">#FFF</item>
    </style>

    <style name="SeparatorStyle" parent="Widget.SplitActivity.Separator">
        <item name="android:background">@null</item>
    </style>
