package net.bicou.splitactivity.samples;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockActivity;

/**
 * Created by bicou on 12/08/13.
 */
public class SubActivity extends SherlockActivity {
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subactivity);
	}
}
