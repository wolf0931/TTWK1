package com.ttkw.ui.Utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.Playlists;
import android.provider.MediaStore.Audio.PlaylistsColumns;

import com.ttkw.ui.config.Constants;
import com.ttkw.ui.service.TtkwService;

public class MusicUtils {
	public static TtkwService mService = null;

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
    public static Cursor query(Context context, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        return query(context, uri, projection, selection, selectionArgs, sortOrder, 0);
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
    public static Cursor query(Context context, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder, int limit) {
        try {
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return null;
            }
            if (limit > 0) {
                uri = uri.buildUpon().appendQueryParameter("limit", "" + limit).build();
            }
            return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
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

            String favorites_where = PlaylistsColumns.NAME + "='" + Constants.PLAYLIST_NAME_FAVORITES + "'";
            String[] favorites_cols = new String[] {
                BaseColumns._ID
            };
            Uri favorites_uri = Audio.Playlists.EXTERNAL_CONTENT_URI;
            Cursor cursor = resolver.query(favorites_uri, favorites_cols, favorites_where, null,
                    null);
            if (cursor.getCount() <= 0) {
                favorites_id = createPlaylist(context, Constants.PLAYLIST_NAME_FAVORITES);
            } else {
                cursor.moveToFirst();
                favorites_id = cursor.getLong(0);
                cursor.close();
            }

            String[] cols = new String[] {
                Playlists.Members.AUDIO_ID
            };
            Uri uri = Playlists.Members.getContentUri(Constants.EXTERNAL, favorites_id);
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
            String[] cols = new String[] {
                PlaylistsColumns.NAME
            };
            String whereclause = PlaylistsColumns.NAME + " = '" + name + "'";
            Cursor cur = resolver.query(Audio.Playlists.EXTERNAL_CONTENT_URI, cols, whereclause,
                    null, null);
            if (cur.getCount() <= 0) {
                ContentValues values = new ContentValues(1);
                values.put(PlaylistsColumns.NAME, name);
                Uri uri = resolver.insert(Audio.Playlists.EXTERNAL_CONTENT_URI, values);
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
            String favorites_where = PlaylistsColumns.NAME + "='" + Constants.PLAYLIST_NAME_FAVORITES + "'";
            String[] favorites_cols = new String[] {
                BaseColumns._ID
            };
            Uri favorites_uri = Audio.Playlists.EXTERNAL_CONTENT_URI;
            Cursor cursor = resolver.query(favorites_uri, favorites_cols, favorites_where, null,
                    null);
            if (cursor.getCount() <= 0) {
                favorites_id = createPlaylist(context, Constants.PLAYLIST_NAME_FAVORITES);
            } else {
                cursor.moveToFirst();
                favorites_id = cursor.getLong(0);
                cursor.close();
            }
            Uri uri = Playlists.Members.getContentUri(Constants.EXTERNAL, favorites_id);
            resolver.delete(uri, Playlists.Members.AUDIO_ID + "=" + id, null);
        }
    }
    
    /**
     * @param mContext
     * @return
     */
    public static int getCardId(Context mContext) {
        ContentResolver res = mContext.getContentResolver();
        Cursor c = res.query(Uri.parse("content://media/external/fs_id"), null, null, null, null);
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

            String favorites_where = PlaylistsColumns.NAME + "='" + Constants.PLAYLIST_NAME_FAVORITES + "'";
            String[] favorites_cols = new String[] {
                BaseColumns._ID
            };
            Uri favorites_uri = Audio.Playlists.EXTERNAL_CONTENT_URI;
            Cursor cursor = resolver.query(favorites_uri, favorites_cols, favorites_where, null,
                    null);
            if (cursor.getCount() <= 0) {
                favorites_id = createPlaylist(context, Constants.PLAYLIST_NAME_FAVORITES);
            } else {
                cursor.moveToFirst();
                favorites_id = cursor.getLong(0);
                cursor.close();
            }

            String[] cols = new String[] {
                Playlists.Members.AUDIO_ID
            };
            Uri uri = Playlists.Members.getContentUri(Constants.EXTERNAL, favorites_id);
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

}
