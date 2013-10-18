package net.bicou.splitactivity.samples;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v4.app.Fragment;

public class ContentFragment extends Fragment {
	public static final String KEY_CONTENT_TEXT = "net.bicou.splitactivity.samples.ContentText";

	public static ContentFragment newInstance(final Bundle args) {
		final ContentFragment frag = new ContentFragment();
		frag.setArguments(args);
		return frag;
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		Log.d("SplitActivitySamples", "ContentFragment#onCreateView(" + savedInstanceState + ")");
		final View v = inflater.inflate(R.layout.fragment_content, container, false);
		final TextView tv = (TextView) v.findViewById(R.id.sa__content_text);
		v.findViewById(R.id.sa__open_subactivity_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				startActivity(new Intent(getActivity(), SubActivity.class));
			}
		});
		final Bundle args = getArguments();
		if (args != null) {
			tv.setText(args.getString(KEY_CONTENT_TEXT));
		}
		return v;
	}
}
