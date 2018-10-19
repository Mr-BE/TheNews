package c.codeblaq.thenews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<News>> {

    /**
     * URL to query
     */
    private static final String QUERY_URL =
            "https://content.guardianapis.com/search";
    private static final String API_KEY =
            "57bf0cb9-3a78-4f55-8953-f3fc65960b92";
    /**
     * Constant value for the news loader ID
     */
    private static final int NEWS_LOADER_ID = 1;
    /**
     * Bind View
     */
    //List view
    @BindView(R.id.list)
    ListView newsListView;
    //Empty view
    @BindView(R.id.empty_view)
    TextView mEmptyStateTextView;
    //Spinning progress indicator
    @BindView(R.id.loading_spinner)
    ProgressBar mProgressSpinner;
    /**
     * Instantiate News Adapter object
     */
    private NewsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind ButterKnife to view
        ButterKnife.bind(this);

        //Set Empty state view
        newsListView.setEmptyView(mEmptyStateTextView);

        //Create new adapter that accepts News list as an input
        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        //Set adapter to list view
        newsListView.setAdapter(mAdapter);

        //Init ConnectivityManager
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //Get network info
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        //Create a boolean value for good connection
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {//Internet connection available

            //Get reference to LoaderManager
            LoaderManager loaderManger = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManger.initLoader(NEWS_LOADER_ID, null, this);
        } else { //No internet connection
            //Stop loader
            mProgressSpinner.setVisibility(View.GONE);
            //Update empty text view
            mEmptyStateTextView.setText(R.string.no_internet);
        }

        //Set onItemClickListener to listview
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Locate current news that was clicked on
                News currentNews = mAdapter.getItem(position);

                //Get News URL as a String and convert to URI object
                String stringUrl = currentNews.getmNewsUrl();
                Uri newsUri = Uri.parse(stringUrl);
                /*Setup intent with Uri inline*/
                Intent urlIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                //Begin intent
                startActivity(urlIntent);
            }
        });
    }

    /**
     * Loader data
     */
    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {

        //Break apart Uri that is passed into its parameter
        Uri baseUri = Uri.parse(QUERY_URL);

        //Prepares the baseUri just parsed for query parameters
        Uri.Builder uriBuilder = baseUri.buildUpon();

        //Append query parameter and its value
        uriBuilder.appendQueryParameter("api-key", API_KEY);
        //Return new appended url
        //Create new loader for specified URL
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        //Clear all previous data from the adapter
        mAdapter.clear();


        //If a valid list of {@Link EarthQuake}s exists, add to adapter
        if (data != null && !data.isEmpty()) {
            //Hide progress spinner
            mProgressSpinner.setVisibility(View.GONE);
            //Add data
            mAdapter.addAll(data);
        } else {
            mEmptyStateTextView.setText(R.string.empty_view);
            //Hide progress spinner
            mProgressSpinner.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        //Reset setup with loader
        mAdapter.clear();
    }
}

