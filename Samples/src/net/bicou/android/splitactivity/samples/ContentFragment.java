package net.bicou.android.splitactivity.samples;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class ContentFragment extends SherlockFragment {
	public static final String KEY_CONTENT_TEXT = "net.bicou.android.splitactivity.samples.ContentText";

	public static ContentFragment newInstance(final Bundle args) {
		final ContentFragment frag = new ContentFragment();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.fragment_content, container, false);
		final TextView tv = (TextView) v.findViewById(R.id.content_text);
		final Bundle args = getArguments();
		if (args != null) {
			tv.setText(args.getString(KEY_CONTENT_TEXT));
		}
		return v;
	}
}
