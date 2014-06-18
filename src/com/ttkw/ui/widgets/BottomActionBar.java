package com.ttkw.ui.widgets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ttkw.R;
import com.ttkw.Utils.MusicUtils;
import com.ttkw.cache.ImageInfo;
import com.ttkw.config.Constants;

public class BottomActionBar extends LinearLayout {
	public BottomActionBar(Context context) {
		super(context);
	}

	public BottomActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@SuppressLint("NewApi")
	public BottomActionBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public void updateBottomActionBar(Activity activity){
		View bottomActionBar = activity.findViewById(R.id.bottom_action_bar);
        if (bottomActionBar == null) {
            return;
        }
        if (MusicUtils.mService != null && MusicUtils.getCurrentAudioId() != -1){
            // Track name
            TextView mTrackName = (TextView)bottomActionBar.findViewById(R.id.bottom_action_bar_track_name);
            // Artist name
            TextView mArtistName = (TextView)bottomActionBar.findViewById(R.id.bottom_action_bar_artist_name);
            // Album art
            ImageView mAlbumArt = (ImageView)bottomActionBar.findViewById(R.id.bottom_action_bar_album_art);
            	
            ImageInfo mInfo = new ImageInfo();
            mInfo.type = Constants.TYPE_ALBUM;
            mInfo.size = Constants.SIZE_THUMB;
            mInfo.source = Constants.SRC_FIRST_AVAILABLE;
            
            mInfo.data = new String[]{ String.valueOf(MusicUtils.getCurrentAlbumId()) , MusicUtils.getArtistName(), MusicUtils.getAlbumName() };
            
            
            
        }
	
	}
}
