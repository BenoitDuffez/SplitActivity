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

	private static final String TAG_MAIN = "net.bicou.android.splitactivity.MainFragmentTag";
	private static final String TAG_CONTENT = "net.bicou.android.splitactivity.ContentFragmentTag";

	private static final String KEY_CONTENT_ARGS = "net.bicou.android.splitactivity.ContentFragmentArgs";

	protected abstract MainFragment createMainFragment();

	protected abstract ContentFragment createContentFragment(Bundle args);

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sa__activity_split_screen);

		mIsSplitScreen = findViewById(R.id.sa__pane_content) != null;

		final FragmentManager fm = getSupportFragmentManager();
		if (savedInstanceState == null) {
			fm.beginTransaction().replace(R.id.sa__pane_main, createMainFragment(), TAG_MAIN).commit();

			if (mIsSplitScreen) {
				fm.beginTransaction().replace(R.id.sa__pane_content, createContentFragment(null), TAG_CONTENT).commit();
			}
		} else {
			@SuppressWarnings("unchecked")
			final MainFragment mf = (MainFragment) fm.findFragmentByTag(TAG_MAIN);
			@SuppressWarnings("unchecked")
			final ContentFragment cf = (ContentFragment) fm.findFragmentByTag(TAG_CONTENT);
			final boolean wasScreenSplit = mf != null && cf != null;
			mContentArgs = savedInstanceState.getBundle(KEY_CONTENT_ARGS);

			Log.d("SplitActivity", "IS=" + mIsSplitScreen + " WAS=" + wasScreenSplit //
					+ " mf=" + (mf == null ? "null" : mf.getClass().getSimpleName()) //
					+ " cf=" + (cf == null ? "null" : cf.getClass().getSimpleName()) //
					+ " args=" + (mContentArgs == null ? "null" : mContentArgs.size() + " items"));

			if (mIsSplitScreen) {
				if (wasScreenSplit) {
					fm.beginTransaction() //
							.remove(mf) //
							.remove(cf) //
							.add(R.id.sa__pane_main, createMainFragment(), TAG_MAIN) //
							.add(R.id.sa__pane_content, createContentFragment(mContentArgs), TAG_CONTENT) //
							.commit();
				} else {
					// Activity is now split: restore main/content panes
					if (mf == null && cf != null && mContentArgs != null) {
						// Screen was showing the content pane
						fm.beginTransaction() //
								.remove(cf) //
								.add(R.id.sa__pane_main, createMainFragment(), TAG_MAIN) //
								.add(R.id.sa__pane_content, createContentFragment(mContentArgs), TAG_CONTENT) //
								.commit();
						// fm.beginTransaction() //
						// .replace(R.id.sa__pane_main, createMainFragment(),
						// TAG_MAIN) //
						// .commit();
					} else {
						// Screen was showing the main pane
						fm.beginTransaction() //
								.replace(R.id.sa__pane_content, createContentFragment(null), TAG_CONTENT) //
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
								.add(R.id.sa__pane_main, createContentFragment(mContentArgs), TAG_CONTENT) //
								.commit();
					} else {
						// Content was not selected: show main fragment
						fm.beginTransaction() //
								.replace(R.id.sa__pane_main, createMainFragment(), TAG_MAIN) //
								.commit();
					}
				} else {
					// Screen wasn't split and still isn't, nothing to do
				}
			}
		}
	}

	public boolean isSplitScreen() {
		return mIsSplitScreen;
	}

	public void selectContent(final Bundle args) {
		mContentArgs = args;
		final FragmentManager fm = getSupportFragmentManager();
		final ContentFragment frag = createContentFragment(args);
		if (mIsSplitScreen) {
			fm.beginTransaction().replace(R.id.sa__pane_content, frag, TAG_CONTENT).addToBackStack("BackStack").commit();
		} else {
			fm.beginTransaction().replace(R.id.sa__pane_main, frag, TAG_CONTENT).addToBackStack("BackStack").commit();
		}
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("SplitActivity", "save: args=" + mContentArgs);
		outState.putBundle(KEY_CONTENT_ARGS, mContentArgs);
	}

	@Override
	public void onBackPressed() {
		if (mIsSplitScreen == false) {
			if (mContentArgs != null) {
				getSupportFragmentManager().beginTransaction() //
						.replace(R.id.sa__pane_main, createMainFragment(), TAG_MAIN) //
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
