package com.ttkw.ui.fragments;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.ttkw.R;
import com.ttkw.ui.widgets.BottomActionBar;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BottomActionBarFragment extends Fragment {
	private static final String TAG = "BottomActionBarFragment";
	private ImageButton mPrev, mPlay, mNext;
	private BottomActionBar mBottomActionBar;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View root = inflater.inflate(R.layout.bottom_action_bar, container);
		mBottomActionBar = new BottomActionBar(getActivity());
		
		
		return root;
	}
}
