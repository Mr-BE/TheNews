package c.codeblaq.thenews;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    /*Log Tag*/
    public static final String LOG_TAG = NewsLoader.class.getName();

    //Query Url
    private String mUrl;

    /**
     * Constructs a new {@link NewsLoader}.
     *
     * @param context is the activity context
     * @param mUrl    is url to load data from
     */
    public NewsLoader(Context context, String mUrl) {
        super(context);
        this.mUrl = mUrl;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {//return early
            return null;
        }
        List<News> newsResult = QueryUtils.fetchNewsData(mUrl);
        return newsResult;
    }
}
