package poojab26.popularmovies.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by poojab26 on 30-Jan-18.
 */
public class MoviesContract {
    public static final String AUTHORITY = "poojab26.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    public static final class MoviesEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_ID = "movie_id";
        public static final String COLUMN_TITLE = "movie_title";
    }
}
