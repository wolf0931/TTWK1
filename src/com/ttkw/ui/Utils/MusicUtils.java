package com.ttkw.ui.Utils;

import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.Playlists;
import android.provider.MediaStore.Audio.PlaylistsColumns;

import com.ttkw.ui.config.Constants;
import com.ttkw.ui.service.ServiceBinder;
import com.ttkw.ui.service.ServiceToken;
import com.ttkw.ui.service.TService;
import com.ttkw.ui.service.TtkwService;

public class MusicUtils {
	private final static StringBuilder sFormatBuilder = new StringBuilder();

	private final static Formatter sFormatter = new Formatter(sFormatBuilder,Locale.getDefault());

	public static TtkwService mService = null;
	private static HashMap<Context, ServiceBinder> sConnectionMap = new HashMap<Context, ServiceBinder>();
	private final static long[] sEmptyList = new long[0];

	private static final Object[] sTimeArgs = new Object[5];

	private static ContentValues[] sContentValuesCache = null;

	/**
	 * @return current track ID
	 */
	public static long getCurrentAudioId() {

		if (MusicUtils.mService != null) {
			try {
				return mService.getAudioId();
			} catch (RemoteException ex) {
			}
		}
		return -1;
	}

	/**
	 * @return current album ID
	 */
	public static long getCurrentAlbumId() {

		if (mService != null) {
			try {
				return mService.getAlbumId();
			} catch (RemoteException ex) {
			}
		}
		return -1;
	}

	/**
	 * @return current artist name
	 */
	public static String getArtistName() {

		if (mService != null) {
			try {
				return mService.getArtistName();
			} catch (RemoteException ex) {
			}
		}
		return null;
	}

	/**
	 * @return current album name
	 */
	public static String getAlbumName() {

		if (mService != null) {
			try {
				return mService.getAlbumName();
			} catch (RemoteException ex) {
			}
		}
		return null;
	}

	/**
	 * @param context
	 * @param uri
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	public static Cursor query(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		return query(context, uri, projection, selection, selectionArgs,
				sortOrder, 0);
	}

	/**
	 * @param context
	 * @param uri
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @param limit
	 * @return
	 */
	public static Cursor query(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder,
			int limit) {
		try {
			ContentResolver resolver = context.getContentResolver();
			if (resolver == null) {
				return null;
			}
			if (limit > 0) {
				uri = uri.buildUpon().appendQueryParameter("limit", "" + limit)
						.build();
			}
			return resolver.query(uri, projection, selection, selectionArgs,
					sortOrder);
		} catch (UnsupportedOperationException ex) {
			return null;
		}
	}

	/**
	 * @param context
	 * @param id
	 */
	public static void addToFavorites(Context context, long id) {

		long favorites_id;

		if (id < 0) {

		} else {
			ContentResolver resolver = context.getContentResolver();

			String favorites_where = PlaylistsColumns.NAME + "='"
					+ Constants.PLAYLIST_NAME_FAVORITES + "'";
			String[] favorites_cols = new String[] { BaseColumns._ID };
			Uri favorites_uri = Audio.Playlists.EXTERNAL_CONTENT_URI;
			Cursor cursor = resolver.query(favorites_uri, favorites_cols,
					favorites_where, null, null);
			if (cursor.getCount() <= 0) {
				favorites_id = createPlaylist(context,
						Constants.PLAYLIST_NAME_FAVORITES);
			} else {
				cursor.moveToFirst();
				favorites_id = cursor.getLong(0);
				cursor.close();
			}

			String[] cols = new String[] { Playlists.Members.AUDIO_ID };
			Uri uri = Playlists.Members.getContentUri(Constants.EXTERNAL,
					favorites_id);
			Cursor cur = resolver.query(uri, cols, null, null, null);

			int base = cur.getCount();
			cur.moveToFirst();
			while (!cur.isAfterLast()) {
				if (cur.getLong(0) == id)
					return;
				cur.moveToNext();
			}
			cur.close();

			ContentValues values = new ContentValues();
			values.put(Playlists.Members.AUDIO_ID, id);
			values.put(Playlists.Members.PLAY_ORDER, base + 1);
			resolver.insert(uri, values);
		}
	}

	/**
	 * @param context
	 * @param name
	 * @return
	 */
	public static long createPlaylist(Context context, String name) {

		if (name != null && name.length() > 0) {
			ContentResolver resolver = context.getContentResolver();
			String[] cols = new String[] { PlaylistsColumns.NAME };
			String whereclause = PlaylistsColumns.NAME + " = '" + name + "'";
			Cursor cur = resolver.query(Audio.Playlists.EXTERNAL_CONTENT_URI,
					cols, whereclause, null, null);
			if (cur.getCount() <= 0) {
				ContentValues values = new ContentValues(1);
				values.put(PlaylistsColumns.NAME, name);
				Uri uri = resolver.insert(Audio.Playlists.EXTERNAL_CONTENT_URI,
						values);
				return Long.parseLong(uri.getLastPathSegment());
			}
			return -1;
		}
		return -1;
	}

	/**
	 * @param context
	 * @param id
	 */
	public static void removeFromFavorites(Context context, long id) {
		long favorites_id;
		if (id < 0) {
		} else {
			ContentResolver resolver = context.getContentResolver();
			String favorites_where = PlaylistsColumns.NAME + "='"
					+ Constants.PLAYLIST_NAME_FAVORITES + "'";
			String[] favorites_cols = new String[] { BaseColumns._ID };
			Uri favorites_uri = Audio.Playlists.EXTERNAL_CONTENT_URI;
			Cursor cursor = resolver.query(favorites_uri, favorites_cols,
					favorites_where, null, null);
			if (cursor.getCount() <= 0) {
				favorites_id = createPlaylist(context,
						Constants.PLAYLIST_NAME_FAVORITES);
			} else {
				cursor.moveToFirst();
				favorites_id = cursor.getLong(0);
				cursor.close();
			}
			Uri uri = Playlists.Members.getContentUri(Constants.EXTERNAL,
					favorites_id);
			resolver.delete(uri, Playlists.Members.AUDIO_ID + "=" + id, null);
		}
	}

	/**
	 * @param mContext
	 * @return
	 */
	public static int getCardId(Context mContext) {
		ContentResolver res = mContext.getContentResolver();
		Cursor c = res.query(Uri.parse("content://media/external/fs_id"), null,
				null, null, null);
		int id = -1;
		if (c != null) {
			c.moveToFirst();
			id = c.getInt(0);
			c.close();
		}
		return id;
	}

	/**
	 * @param context
	 * @param id
	 * @return
	 */
	public static boolean isFavorite(Context context, long id) {

		long favorites_id;

		if (id < 0) {

		} else {
			ContentResolver resolver = context.getContentResolver();

			String favorites_where = PlaylistsColumns.NAME + "='"
					+ Constants.PLAYLIST_NAME_FAVORITES + "'";
			String[] favorites_cols = new String[] { BaseColumns._ID };
			Uri favorites_uri = Audio.Playlists.EXTERNAL_CONTENT_URI;
			Cursor cursor = resolver.query(favorites_uri, favorites_cols,
					favorites_where, null, null);
			if (cursor.getCount() <= 0) {
				favorites_id = createPlaylist(context,
						Constants.PLAYLIST_NAME_FAVORITES);
			} else {
				cursor.moveToFirst();
				favorites_id = cursor.getLong(0);
				cursor.close();
			}

			String[] cols = new String[] { Playlists.Members.AUDIO_ID };
			Uri uri = Playlists.Members.getContentUri(Constants.EXTERNAL,
					favorites_id);
			Cursor cur = resolver.query(uri, cols, null, null, null);

			cur.moveToFirst();
			while (!cur.isAfterLast()) {
				if (cur.getLong(0) == id) {
					cur.close();
					return true;
				}
				cur.moveToNext();
			}
			cur.close();
			return false;
		}
		return false;
	}

	/**
	 * @return if music is playing
	 */
	public static boolean isPlaying() {
		if (mService == null)
			return false;

		try {
			return mService.isPlaying();
		} catch (RemoteException e) {
		}
		return false;
	}

	public static ServiceToken bindToService(Activity context) {
		return bindToService(context, null);
	}

	/**
	 * @param context
	 * @param callback
	 * @return
	 */
	public static ServiceToken bindToService(Context context,
			ServiceConnection callback) {
		Activity realActivity = ((Activity) context).getParent();
		if (realActivity == null) {
			realActivity = (Activity) context;
		}
		ContextWrapper cw = new ContextWrapper(realActivity);
		cw.startService(new Intent(cw, TService.class));
		ServiceBinder sb = new ServiceBinder(callback);
		if (cw.bindService((new Intent()).setClass(cw, TService.class), sb, 0)) {
			sConnectionMap.put(cw, sb);
			return new ServiceToken(cw);
		}
		return null;
	}
	
    /**
     * @param token
     */
    public static void unbindFromService(ServiceToken token) {
        if (token == null) {
            return;
        }
        ContextWrapper cw = token.mWrappedContext;
        ServiceBinder sb = sConnectionMap.remove(cw);
        if (sb == null) {
            return;
        }
        cw.unbindService(sb);
        if (sConnectionMap.isEmpty()) {
            mService = null;
        }
    }

    /**
     * @param context
     * @param cursor
     */
    public static void shuffleAll(Context context, Cursor cursor) {
        playAll(context, cursor, 0, true);
    }
    
    /**
     * @param context
     * @param cursor
     * @param position
     * @param force_shuffle
     */
    private static void playAll(Context context, Cursor cursor, int position, boolean force_shuffle) {

        long[] list = getSongListForCursor(cursor);
        playAll(context, list, position, force_shuffle);
    }
    /**
     * @param context
     * @param list
     * @param position
     * @param force_shuffle
     */
    private static void playAll(Context context, long[] list, int position, boolean force_shuffle) {
        if (list.length == 0 || mService == null) {
            return;
        }
        try {
            if (force_shuffle) {
                mService.setShuffleMode(TService.SHUFFLE_NORMAL);
            }
            long curid = mService.getAudioId();
            int curpos = mService.getQueuePosition();
            if (position != -1 && curpos == position && curid == list[position]) {
                // The selected file is the file that's currently playing;
                // figure out if we need to restart with a new playlist,
                // or just launch the playback activity.
                long[] playlist = mService.getQueue();
                if (Arrays.equals(list, playlist)) {
                    // we don't need to set a new list, but we should resume
                    // playback if needed
                    mService.play();
                    return;
                }
            }
            if (position < 0) {
                position = 0;
            }
            mService.open(list, force_shuffle ? -1 : position);
            mService.play();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * @param cursor
     * @return
     */
    public static long[] getSongListForCursor(Cursor cursor) {
        if (cursor == null) {
            return sEmptyList;
        }
        int len = cursor.getCount();
        long[] list = new long[len];
        cursor.moveToFirst();
        int colidx = -1;
        try {
            colidx = cursor.getColumnIndexOrThrow(Audio.Playlists.Members.AUDIO_ID);
        } catch (IllegalArgumentException ex) {
            colidx = cursor.getColumnIndexOrThrow(BaseColumns._ID);
        }
        for (int i = 0; i < len; i++) {
            list[i] = cursor.getLong(colidx);
            cursor.moveToNext();
        }
        return list;
    }
    
    public static void notifyWidgets(String what){ 
        try {
        	mService.notifyChange(what);
        } catch (Exception e) {
        }
    }
}
