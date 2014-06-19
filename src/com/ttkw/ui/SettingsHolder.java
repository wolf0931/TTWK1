package com.ttkw.ui;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
import android.view.View;

import com.ttkw.R;
import com.ttkw.ui.Utils.MusicUtils;
import com.ttkw.ui.Utils.ThemeUtils;
import com.ttkw.ui.cache.ImageProvider;
import com.ttkw.ui.config.Constants;
import com.ttkw.ui.debuglog.DebugLog;
import com.ttkw.ui.preferences.ThemePreview;
import com.ttkw.ui.service.ServiceToken;
import com.ttkw.ui.service.TService;
import com.ttkw.ui.service.TtkwService;

public class SettingsHolder extends PreferenceActivity implements ServiceConnection {
	private static final String TAG = "SettingsHolder";
	Context mContext;
	private ServiceToken mToken;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DebugLog.i(TAG, "onCreate");
		mContext = this;
		int preferencesResId = R.xml.settings;
		addPreferencesFromResource(preferencesResId);

		// Init widget style change option
		initChangeWidgetTheme();
		// Init delete cache option
		initDeleteCache();
		// Load the theme chooser
		initThemeChooser();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * @param v
	 */
	public void applyTheme(View v) {
		ThemePreview themePreview = (ThemePreview) findPreference(Constants.THEME_PREVIEW);
		String packageName = themePreview.getValue().toString();
		ThemeUtils.setThemePackageName(this, packageName);
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}

	/**
	 * @param v
	 */
	public void getThemes(View v) {
		Uri marketUri = Uri.parse("https://market.android.com/search?q=ApolloThemes&c=apps&featured=APP_STORE_SEARCH");
		Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(marketUri);
		startActivity(marketIntent);
		finish();
	}

	private void initThemeChooser() {
		SharedPreferences sp = getPreferenceManager().getSharedPreferences();
		String themePackage = sp.getString(Constants.THEME_PACKAGE_NAME,Constants.APOLLO);
		ListPreference themeLp = (ListPreference) findPreference(Constants.THEME_PACKAGE_NAME);
		themeLp.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				ThemePreview themePreview = (ThemePreview) findPreference(Constants.THEME_PREVIEW);
				themePreview.setTheme(newValue.toString());
				return false;
			}
		});

		Intent intent = new Intent("com.andrew.apollo.THEMES");
		intent.addCategory("android.intent.category.DEFAULT");
		PackageManager pm = getPackageManager();
		List<ResolveInfo> themes = pm.queryIntentActivities(intent, 0);
		String[] entries = new String[themes.size() + 1];
		String[] values = new String[themes.size() + 1];
		entries[0] = Constants.APOLLO;
		values[0] = Constants.APOLLO;
		for (int i = 0; i < themes.size(); i++) {
			String appPackageName = (themes.get(i)).activityInfo.packageName
					.toString();
			String themeName = (themes.get(i)).loadLabel(pm).toString();
			entries[i + 1] = themeName;
			values[i + 1] = appPackageName;
		}
		themeLp.setEntries(entries);
		themeLp.setEntryValues(values);
		ThemePreview themePreview = (ThemePreview) findPreference(Constants.THEME_PREVIEW);
		themePreview.setTheme(themePackage);
	}

	private void initDeleteCache() {
		final Preference deleteCache = findPreference("delete_cache");
		deleteCache
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(final Preference preference) {
						new AlertDialog.Builder(SettingsHolder.this)
								.setMessage(R.string.delete_warning)
								.setPositiveButton(android.R.string.ok,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													final DialogInterface dialog,
													final int which) {
												ImageProvider.getInstance(
														(Activity) mContext)
														.clearAllCaches();
											}
										})
								.setNegativeButton(android.R.string.cancel,
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													final DialogInterface dialog,
													final int which) {
												dialog.dismiss();
											}
										}).create().show();
						return true;
					}
				});
	}

	private void initChangeWidgetTheme() {
		ListPreference listPreference = (ListPreference) findPreference(Constants.WIDGET_STYLE);
		listPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,Object newValue) {
						MusicUtils.notifyWidgets(TService.META_CHANGED);
						return true;
					}
				});
	}

	@Override
	public void onServiceConnected(ComponentName arg0, IBinder obj) {
		// TODO Auto-generated method stub
		MusicUtils.mService = TtkwService.Stub.asInterface(obj);
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		// TODO Auto-generated method stub
		MusicUtils.mService = null;
	}

	@Override
	protected void onStart() {
		DebugLog.i(TAG, "onStart");
		// Bind to Service
		mToken = MusicUtils.bindToService(this, this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(TService.META_CHANGED);
		super.onStart();
	}

	@Override
	protected void onStop() {
		// Unbind
		if (MusicUtils.mService != null)
			MusicUtils.unbindFromService(mToken);

		// TODO: clear image cache

		super.onStop();
	}
}
