package com.ttkw.Utils;

import android.os.RemoteException;

import com.ttkw.service.TtkwService;

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
}
