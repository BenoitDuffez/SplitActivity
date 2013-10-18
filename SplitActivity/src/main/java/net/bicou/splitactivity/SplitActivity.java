package net.bicou.splitactivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import net.bicou.splitactivity.R;

public abstract class SplitActivity<MainFragment extends Fragment, ContentFragment extends Fragment> extends ActionBarActivity {
	private boolean mIsSplitScreen;
	private Bundle mMainState, mContentArgs;
	private static final boolean DEBUG = true;

	public enum ActiveContent {
		BOTH,
		MAIN,
		CONTENT,
	}

	private static final String TAG = "SplitActivity";

	private static final String TAG_MAIN = "net.bicou.android.splitactivity.MainFragmentTag";
	private static final String TAG_CONTENT = "net.bicou.android.splitactivity.ContentFragmentTag";

	private static final String KEY_CONTENT_ARGS = "net.bicou.android.splitactivity.ContentFragmentArgs";
	private static final String KEY_IS_SPLIT_SCREEN = "net.bicou.android.splitactivity.IsSplitScreen";

	protected abstract MainFragment createMainFragment(Bundle args);

	protected abstract ContentFragment createContentFragment(Bundle args);

	private void L(String msg) {
		if (DEBUG) {
			Log.d(TAG, msg);
		}
	}

	/**
	 * Used to create the default empty fragment.<br /> Override this to customize the content fragment when nothing is selected from the main fragment.
	 *
	 * @param args Arguments to the empty fragment
	 *
	 * @return By default, the empty fragment is the one returned by {@link #createContentFragment(Bundle)} with an empty {@link android.os.Bundle Bundle}.
	 */
	protected Fragment createEmptyFragment(Bundle args) {
		L("createEmptyFragment: " + args);
		return createContentFragment(args);
	}

	/**
	 * Optional arguments to pass to main fragment upon its creation.<br /> Override this to customize the main fragment arguments.
	 *
	 * @param savedInstanceState The Bundle usually passed as parameter when calling onCreate
	 *
	 * @return By default, an empty {@link android.os.Bundle Bundle} is returned.
	 */
	protected Bundle getMainFragmentArgs(Bundle savedInstanceState) {
		L("getMainFragmentArgs");
		return new Bundle();
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sa__activity_split_screen);
		L("onCreate: " + savedInstanceState);

		mIsSplitScreen = findViewById(R.id.sa__right_pane) != null;
		final FragmentManager fm = getSupportFragmentManager();
		final MainFragment mf = getMainFragment();
		final ContentFragment cf = getContentFragment();
		Fragment lp = fm.findFragmentById(R.id.sa__left_pane);
		Fragment rp = fm.findFragmentById(R.id.sa__right_pane);
		final boolean wasScreenSplit = savedInstanceState != null && savedInstanceState.getBoolean(KEY_IS_SPLIT_SCREEN, mf != null && cf != null);
		mContentArgs = savedInstanceState == null ? null : savedInstanceState.getBundle(KEY_CONTENT_ARGS);

		dumpState("onCreate, savedInstanceState: " + savedInstanceState);
		L("IS=" + mIsSplitScreen + " WAS=" + wasScreenSplit);
		L("saved content args=" + mContentArgs);
		L("---------------------------------------------------------------------");

		if (savedInstanceState == null) {
			fm.beginTransaction().replace(R.id.sa__left_pane, createMainFragment(getMainFragmentArgs(savedInstanceState)), TAG_MAIN).commit();

			if (mIsSplitScreen) {
				fm.beginTransaction().replace(R.id.sa__right_pane, createEmptyFragment(new Bundle()), TAG_CONTENT).commit();
			}
		} else {
			if (mIsSplitScreen != wasScreenSplit) {
				// Layout change, we need to tell the framework which fragment to display in which pane
				if (mIsSplitScreen) {
					if (mContentArgs != null) {
						FragmentTransaction ft = fm.beginTransaction();
						ft.replace(R.id.sa__left_pane, createMainFragment(getMainFragmentArgs(savedInstanceState)));
						ft.replace(R.id.sa__right_pane, createContentFragment(mContentArgs));
						ft.commit();
					} else {
						FragmentTransaction ft = fm.beginTransaction();
						if (rp != null) {
							ft.remove(rp);
						}
						if (lp != null) {
							ft.remove(lp);
						}
						ft.add(R.id.sa__left_pane, createMainFragment(getMainFragmentPreviousState()));
						ft.add(R.id.sa__right_pane, createEmptyFragment(new Bundle()));
						ft.commit();
					}
				} else {
					if (mContentArgs != null) {
						FragmentTransaction ft = fm.beginTransaction();
						//						ft.remove(lp);
						ft.replace(R.id.sa__left_pane, createContentFragment(mContentArgs));
						ft.remove(rp);
						ft.commit();
					} else {
						FragmentTransaction ft = fm.beginTransaction();
						ft.replace(R.id.sa__left_pane, createMainFragment(getMainFragmentArgs(savedInstanceState)));
						ft.remove(lp);
						ft.commit();
					}
				}
			}
		}
	}

	/**
	 * Checks whether the screen is split in two panes or not
	 *
	 * @return true if the screen is split in two panes, false otherwise
	 */

	public boolean isSplitScreen() {
		L("isSplitScreen: " + mIsSplitScreen);
		return mIsSplitScreen;
	}

	/**
	 * Use this method to retrieve the current layout
	 *
	 * @return {@link ActiveContent#BOTH} if the screen is split (10" tablets and 7" landscape tablets)<br /> {@link ActiveContent#MAIN} if the screen is not
	 * split, and
	 * currently displaying the main pane (phones and 7" portrait tablets)<br /> {@link ActiveContent#CONTENT} if the screen is not split,
	 * and currently displaying the
	 * content pane (phones and 7" portrait tablets).
	 */
	public ActiveContent getActiveContent() {
		L("getActiveContent");
		if (isSplitScreen()) {
			return ActiveContent.BOTH;
		}
		return getSupportFragmentManager().findFragmentByTag(TAG_CONTENT) == null ? ActiveContent.MAIN : ActiveContent.CONTENT;
	}

	/**
	 * Retrieves the current main fragment.
	 *
	 * @return The main fragment if it is displayed on screen, null otherwise.
	 */
	@SuppressWarnings("unchecked")
	public MainFragment getMainFragment() {
		L("getMainFragment");
		return (MainFragment) getSupportFragmentManager().findFragmentByTag(TAG_MAIN);
	}

	/**
	 * Retrieves the current content fragment.
	 *
	 * @return The content fragment if it is displayed on screen, null otherwise.
	 */
	@SuppressWarnings("unchecked")
	public ContentFragment getContentFragment() {
		L("getContentFragment");
		return (ContentFragment) getSupportFragmentManager().findFragmentByTag(TAG_CONTENT);
	}

	/**
	 * Force display the main fragment.<br /> This can only be used in single pane layout, with the content fragment currently active.
	 *
	 * @param args Arguments required for the new main fragment
	 *
	 * @return true if a fragment transaction was made, false otherwise.
	 */
	public boolean showMainFragment(Bundle args) {
		L("showMainFragment: " + args);
		switch (getActiveContent()) {
		case CONTENT:
			getSupportFragmentManager() //
					.beginTransaction() //
					.replace(R.id.sa__left_pane, createMainFragment(args), TAG_MAIN) //
					.commit();
			return true;

		default:
			return false;
		}
	}

	/**
	 * Callback used from the main fragment to trigger a choice for the content fragment.<br /> If the screen is currently split,
	 * the content fragment is replaced with
	 * a new content fragment with the arguments passed as parameter.<br /> If the screen is not split, the whole screen is replaced with a new content fragment
	 * with
	 * the arguments passed as a parameter.
	 *
	 * @param args The arguments required to create the new content fragment.
	 */
	public void selectContent(final Bundle args) {
		dumpState("selectContent, args: " + args);

		final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		final ContentFragment frag = createContentFragment(args);
		int destination = mIsSplitScreen ? R.id.sa__right_pane : R.id.sa__left_pane;

		ft.replace(destination, frag, TAG_CONTENT);
		ft.addToBackStack("BackStack");
		ft.commit();

		mContentArgs = args;
	}

	/**
	 * Hide the current fragment and display an empty fragment instead
	 *
	 * @param args The arguments required to create the empty fragment. Will be passed to {@link #createEmptyFragment(android.os.Bundle)}
	 */
	public void selectEmptyFragment(Bundle args) {
		dumpState("selectEmptyFragment, args: " + args);

		final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		final Fragment frag = createEmptyFragment(args);
		int destination = mIsSplitScreen ? R.id.sa__right_pane : R.id.sa__left_pane;

		ft.replace(destination, frag, TAG_CONTENT);
		ft.addToBackStack("BackStack");
		ft.commit();

		mContentArgs = args;
	}

	/**
	 * Call this when you need to save the main fragment state. This is used on single pane layouts, where the main fragment will be recreated when the user hits
	 * the
	 * back key from the content fragment.<br /> Typical flow:
	 * <p/>
	 * <pre>
	 * onCreateView() {
	 *     final Bundle args = ((SplitActivity) getActivity()) getMainFragmentPreviousState());
	 *
	 *     if (args == null) {
	 *     		args = getArguments();
	 *     }
	 * }
	 *
	 * onDestroyView() {
	 *      Bundle args = new Bundle();
	 *      // ... save state
	 *      ((SplitActivity) getActivity()).saveMainFragmentState(args);
	 * }
	 * </pre>
	 */
	public void saveMainFragmentState(Bundle args) {
		L("saveMainFragmentState");
		mMainState = args;
	}

	public Bundle getMainFragmentPreviousState() {
		L("getMainFragmentPreviousState");
		return mMainState;
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		super.onSaveInstanceState(outState);
		L("save: args=" + mContentArgs);
		outState.putBundle(KEY_CONTENT_ARGS, mContentArgs);
		outState.putBoolean(KEY_IS_SPLIT_SCREEN, mIsSplitScreen);
	}

	private void dumpState(final String s) {
		if (!DEBUG) {
			return;
		}

		mIsSplitScreen = findViewById(R.id.sa__right_pane) != null;
		final FragmentManager fm = getSupportFragmentManager();
		final MainFragment mf = getMainFragment();
		final ContentFragment cf = getContentFragment();
		Fragment lp = fm.findFragmentById(R.id.sa__left_pane);
		Fragment rp = fm.findFragmentById(R.id.sa__right_pane);

		L("--------------------------------------- " + s);
		L("IS=" + mIsSplitScreen);
		if (mf == null) {
			L("main frag    = null");
		} else {
			L("main frag    = " + mf.getClass().getSimpleName() + ":" + mf.hashCode() + " attached @ " + (mf.getId() == R.id.sa__left_pane ? "left pane" : (mf
					.getId() == R.id.sa__right_pane ? "right pane" : "unknown")));
		}
		if (cf == null) {
			L("content frag = null");
		} else {
			L("content frag = " + cf.getClass().getSimpleName() + ":" + cf.hashCode() + " attached @ " + (cf.getId() == R.id.sa__left_pane ? "left pane" : (cf
					.getId() == R.id.sa__right_pane ? "right pane" : "unknown pane")));
		}
		if (lp == null) {
			L("left pane    = null");
		} else {
			L("left pane    = " + lp.getClass().getSimpleName() + ":" + lp.hashCode() + " Tag:" + lp.getTag() + " args:" + lp.getArguments());
		}
		if (rp == null) {
			L("right pane   = null");
		} else {
			L("right pane   = " + rp.getClass().getSimpleName() + ":" + rp.hashCode() + " Tag:" + rp.getTag() + " args:" + rp.getArguments());
		}
		L("saved content args=" + mContentArgs);
		L("---------------------------------------------------------------------");
	}

	@Override
	public void onBackPressed() {
		dumpState("onBackPressed");
		mContentArgs = null;

		// Don't know why some rogue fragments won't disappear
		if (mIsSplitScreen) {
			FragmentManager fm = getSupportFragmentManager();
			// Happens with 7" and the following behavior: 2 content fragments stay and overlap in the right pane
			//-----------------------------------------------------------------------------
			// step		   action taken       	 layout mode		content of right pane
			//-----------------------------------------------------------------------------
			//  1.		  normal start        		dual				  empty
			//  2. 		  selectContent       		dual				 content
			//  3. 		 screen rotation     	   single				 content
			//  4. 		 screen rotation     		dual				 content
			//  5. 			back press          	dual			  double content  :(
			//-----------------------------------------------------------------------------
			if (getMainFragment() == null) {
				Fragment f = fm.findFragmentById(R.id.sa__right_pane);
				fm.beginTransaction().remove(f).commit();
			}


			// Happens with 7" and the following behavior: back button has no effect and doesn't do content -> empty
			//-----------------------------------------------------------------------------
			// step		   action taken       	 layout mode		content of right pane
			//-----------------------------------------------------------------------------
			//  1.		  normal start        	   single				  N/A
			//  2. 		  selectContent       	   single				 content
			//  3. 	   launch sub-activity     	   single			  sub-activity
			//  4. 		 screen rotation     	   single				 content
			//  5. 			back press          	dual			  	 content
			//  6. 			back press          	dual			  	 content :(
			//-----------------------------------------------------------------------------
			ContentFragment cf = getContentFragment();
			if (cf != null) {
				fm.beginTransaction().remove(cf).commit();
			}
			Fragment f = fm.findFragmentById(R.id.sa__right_pane);
			if (f != null) {
				fm.beginTransaction().remove(f).commit();
			}
			fm.beginTransaction().add(R.id.sa__right_pane, createEmptyFragment(new Bundle()), TAG_CONTENT).commit();
		}

		super.onBackPressed();
	}
}
