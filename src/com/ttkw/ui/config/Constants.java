package com.ttkw.ui.config;

public final class Constants {
	public final static String LASTFM_API_KEY = "0bec3f7ec1f914d7c960c12a916c8fb3";
	
	  // SharedPreferences
    public final static String APOLLO = "Apollo", APOLLO_PREFERENCES = "apollopreferences",
            ARTIST_KEY = "artist", ALBUM_KEY = "album", ALBUM_ID_KEY = "albumid", NUMALBUMS = "num_albums",
            GENRE_KEY = "genres", ARTIST_ID = "artistid", NUMWEEKS = "numweeks",
            PLAYLIST_NAME_FAVORITES = "Favorites", PLAYLIST_NAME = "playlist", WIDGET_STYLE="widget_type",
            THEME_PACKAGE_NAME = "themePackageName", THEME_DESCRIPTION = "themeDescription",
            THEME_PREVIEW = "themepreview", THEME_TITLE = "themeTitle", VISUALIZATION_TYPE="visualization_type", 
            UP_STARTS_ALBUM_ACTIVITY = "upStartsAlbumActivity", TABS_ENABLED = "tabs_enabled";
    
	
	// Image Loading Constants
	public final static String TYPE_ARTIST = "artist", TYPE_ALBUM = "album",
			TYPE_GENRE = "genre", TYPE_PLAYLIST = "playlist",
			ALBUM_SUFFIX = "albartimg", ARTIST_SUFFIX = "artstimg",
			PLAYLIST_SUFFIX = "plylstimg", GENRE_SUFFIX = "gnreimg",
			SRC_FIRST_AVAILABLE = "first_avail", SRC_LASTFM = "last_fm",
			SRC_FILE = "from_file", SRC_GALLERY = "from_gallery",
			SIZE_NORMAL = "normal", SIZE_THUMB = "thumb";
	 // Bundle & Intent type
    public final static String MIME_TYPE = "mimetype", INTENT_ACTION = "action", DATA_SCHEME = "file";
	
	// Theme item type
    public final static int THEME_ITEM_BACKGROUND = 0, THEME_ITEM_FOREGROUND = 1;
    
    // Storage Volume
    public final static String EXTERNAL = "external";
}
