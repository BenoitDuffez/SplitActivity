package net.bicou.android.splitactivity.samples;

import net.bicou.android.splitscreen.SplitActivity;
import android.os.Bundle;

public class MainActivity extends SplitActivity<MainListFragment, ContentFragment> {
	@Override
	protected MainListFragment createMainFragment() {
		return MainListFragment.newInstance();
	}

	@Override
	protected ContentFragment createContentFragment(final Bundle args) {
		return ContentFragment.newInstance(args);
	}
}
