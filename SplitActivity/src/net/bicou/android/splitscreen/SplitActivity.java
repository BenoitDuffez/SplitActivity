package net.bicou.android.splitscreen;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public abstract class SplitActivity<MainFragment extends Fragment, ContentFragment extends Fragment> extends SherlockFragmentActivity {
	private boolean mIsSplitScreen;
	private Bundle mContentArgs;
	private Configuration mPreviousConfiguration;
	
	private static final String TAG = "SplitActivity";

	private static final String TAG_MAIN = "net.bicou.android.splitactivity.MainFragmentTag";
	private static final String TAG_CONTENT = "net.bicou.android.splitactivity.ContentFragmentTag";

	private static final String KEY_CONTENT_ARGS = "net.bicou.android.splitactivity.ContentFragmentArgs";

	protected abstract MainFragment createMainFragment(Bundle args);
	
	protected abstract ContentFragment createContentFragment(Bundle args);

	/**
	 * Used to create the default empty fragment.<br />
	 * Override this to customize the content fragment when nothing is selected from the main fragment.
	 * @param args Arguments to the empty fragment
	 * @return By default, the empty fragment is the one returned by {@link #createContentFragment(Bundle)} with an empty {@link android.os.Bundle Bundle}.
	 */
	protected Fragment createEmptyFragment(Bundle args) {
		Log.d(TAG, "createEmptyFragment: "+args);
		return createContentFragment(args);
	}
	
	/**
	 * Optional arguments to pass to main fragment upon its creation.<br />
	 * Override this to customize the main fragment arguments.
	 * @return By default, an empty {@link android.os.Bundle Bundle} is returned.
	 */
	protected Bundle getMainFragmentArgs() {
		Log.d(TAG, "getMainFragmentArgs");
		return new Bundle();
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sa__activity_split_screen);
		Log.d(TAG, "onCreate: "+savedInstanceState);

		mIsSplitScreen = findViewById(R.id.sa__content_pane) != null;

		final FragmentManager fm = getSupportFragmentManager();
		if (savedInstanceState == null) {
			fm.beginTransaction().replace(R.id.sa__main_pane, createMainFragment(getMainFragmentArgs()), TAG_MAIN).commit();

			if (mIsSplitScreen) {
				fm.beginTransaction().replace(R.id.sa__content_pane, createEmptyFragment(new Bundle()), TAG_CONTENT).commit();
			}
		} else {
			@SuppressWarnings("unchecked")
			final MainFragment mf = (MainFragment) fm.findFragmentByTag(TAG_MAIN);
			@SuppressWarnings("unchecked")
			final ContentFragment cf = (ContentFragment) fm.findFragmentByTag(TAG_CONTENT);
			final boolean wasScreenSplit = mf != null && cf != null;
			mContentArgs = savedInstanceState.getBundle(KEY_CONTENT_ARGS);

			Log.d(TAG, "IS=" + mIsSplitScreen + " WAS=" + wasScreenSplit //
					+ " mf=" + (mf == null ? "null" : mf.getClass().getSimpleName()) //
					+ " cf=" + (cf == null ? "null" : cf.getClass().getSimpleName()) //
					+ " args=" + (mContentArgs == null ? "null" : mContentArgs.size() + " items"));

			if (mIsSplitScreen) {
				if (wasScreenSplit) {
					// TODO: is it useful? I mean, the framework should recreate the fragments, right?
					fm.beginTransaction() //
							.remove(mf) //
							.remove(cf) //
							.add(R.id.sa__main_pane, createMainFragment(getMainFragmentArgs()), TAG_MAIN) //
							.add(R.id.sa__content_pane, createContentFragment(mContentArgs), TAG_CONTENT) //
							.commit();
				} else {
					// Activity is now split: restore main/content panes
					if (mf == null && cf != null && mContentArgs != null) {
						// Screen was showing the content pane
						fm.beginTransaction() //
								.remove(cf) //
								.add(R.id.sa__main_pane, createMainFragment(getMainFragmentArgs()), TAG_MAIN) //
								.add(R.id.sa__content_pane, createContentFragment(mContentArgs), TAG_CONTENT) //
								.commit();
						// fm.beginTransaction() //
						// .replace(R.id.sa__pane_main, createMainFragment(), TAG_MAIN) //
						// .commit();
					} else {
						// Screen was showing the main pane
						fm.beginTransaction() //
								.replace(R.id.sa__content_pane, createEmptyFragment(new Bundle()), TAG_CONTENT) //
								.commit();
					}
				}
			} else {
				if (wasScreenSplit) {
					if (mContentArgs != null) {
						// Some content was selected: show content fragment
						fm.beginTransaction() //
								.remove(mf) //
								.remove(cf) //
								.commit();
						fm.beginTransaction()
								.add(R.id.sa__main_pane, createContentFragment(mContentArgs), TAG_CONTENT) //
								.commit();
					} else {
						// Content was not selected: show main fragment
						fm.beginTransaction() //
								.replace(R.id.sa__main_pane, createMainFragment(getMainFragmentArgs()), TAG_MAIN) //
								.commit();
					}
				} else {
					// Screen wasn't split and still isn't, nothing to do
				}
			}
		}
	}

	public boolean isSplitScreen() {
		Log.d(TAG, "isSplitScreen: "+mIsSplitScreen);
		return mIsSplitScreen;
	}

	public void selectContent(final Bundle args) {
		Log.d(TAG, "selectContent: "+args);
		
		mContentArgs = args;
		final FragmentManager fm = getSupportFragmentManager();
		final ContentFragment frag = createContentFragment(args);
		if (mIsSplitScreen) {
			fm.beginTransaction().replace(R.id.sa__content_pane, frag, TAG_CONTENT).addToBackStack("BackStack").commit();
		} else {
			fm.beginTransaction().replace(R.id.sa__main_pane, frag, TAG_CONTENT).addToBackStack("BackStack").commit();
		}
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(TAG, "save: args=" + mContentArgs);
		outState.putBundle(KEY_CONTENT_ARGS, mContentArgs);
	}

	@Override
	public void onBackPressed() {
		if (mIsSplitScreen == false) {
			if (mContentArgs != null) {
				getSupportFragmentManager().beginTransaction() //
						.replace(R.id.sa__main_pane, createMainFragment(getMainFragmentArgs()), TAG_MAIN) //
						.commit();
			} else {
				finish();
			}
		} else {
			super.onBackPressed();
		}
		mContentArgs = null;
	}
}
