package net.bicou.android.splitactivity.samples;

import net.bicou.android.splitscreen.SplitActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

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
