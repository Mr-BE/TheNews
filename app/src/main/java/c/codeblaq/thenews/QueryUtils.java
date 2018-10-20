package c.codeblaq.thenews;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import butterknife.internal.Utils;

/**
 * Helper methods to aid in requesting and receiving news data
 */
public final class QueryUtils {

    /**
     * Log Tag
     */
    public static final String LOG_TAG = Utils.class.getSimpleName();
    //HTTP  success code
    private static final int SUCCESS_CODE = 200;
    //Read Timeout
    private static final int READ_TIMEOUT = 10000;
    //Connection Timeout
    private static final int CONNECTION_TIMEOUT = 1500;

    //Constructor
    public QueryUtils() {
    }

    //Query the Guardian dataset and return a list News objects
    public static List<News> fetchNewsData(String requestUrl) {
        //Create URL object
        URL url = createUrl(requestUrl);

        /*Perform HTTP request to specified URL and receive a JSON response*/
        //Initialize JSON response as null
        String jsonResponse = null;
        try {//make HTTP request
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making HTTP request");
        }

        //Extract relevant fields from the JSON response and create a list of News object
        List<News> newsList = extractFeaturesFromJson(jsonResponse);
        //Return {@Link News (news)}
        return newsList;
    }

    /**
     * Return a list of {@link News} objects that has been built after parsing JSON
     */
    private static URL createUrl(String stringUrl) {
        //Instantiate url as a null object
        URL url = null;
        try { //Attempt to make Url object from String url
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating URL", e);
        }
        return url;
    }

    /**
     * Make a HTTP request to the specified URL and return a String as the response
     */
    private static String makeHttpRequest(URL url) throws IOException {
        //Instantiate json response string
        String jsonResponse = "";

        //Check if URL is null. If null, exit early
        if (url == null) {
            return jsonResponse;
        }
        //If URL is not null
        /*Instantiate HttpUrlConnection and InputStream*/
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {//make connection to url
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);/*milliseconds*/
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT); /*milliseconds*/
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //If the request was successful (i.e response code =200)
            //then read the input stream and parse the response
            int urlResponseCode = urlConnection.getResponseCode();

            if (urlResponseCode == SUCCESS_CODE) {//successful connection
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlResponseCode);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the News JSON results");
        } finally {
            //Clean up resources and shut down connections
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert entire InputStream into a String containing JSON response
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        //Build string from input stream
        StringBuilder builderOutput = new StringBuilder();
        if (inputStream != null) {
            //Instantiate an InputStreamReader to parse InputStream
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            //Instantiate BufferedReader to read input stream en masse
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();//Use BufferedReader to read inputstream to string
            //Ensure String produced is not empty at anytime
            while (line != null) {
                //Use {@Link StringBuilder} builderOutput to create concrete String object
                // out of Char sequence and employ BufferedReader
                builderOutput.append(line);
                line = bufferedReader.readLine();
            }
        }
        //Return created string from StringBuilder
        return builderOutput.toString();
    }

    /**
     * Extract News features from Json
     */
    private static List<News> extractFeaturesFromJson(String newsJSON) {
        //If JSON is null, then return early
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        //Create an empty ArrayList that News object can be added to
        List<News> newsList = new ArrayList<>();

        /*Try to extract features from received JSON*/
        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject responseObject = baseJsonResponse.optJSONObject("response");
            JSONArray resultsArray = responseObject.getJSONArray("results");
            //Iterate the jsonArray and get info for all news objects
            for (int i = 0; i < resultsArray.length(); i++) {
                String authorName =" ";

                //Get jsonObject
                JSONObject results = resultsArray.getJSONObject(i);

                //extract news headline from JSON
                String headline = results.optString("webTitle");
                //extract news type
                String newsType = results.optString("pillarName");
                //extract publication date and time
                String newsTime = results.optString("webPublicationDate");
                //extract news URL
                String newsUrl = results.optString("webUrl");

               /*Access tags array with results array*/
                JSONArray tagsArray = results.optJSONArray("tags");
                for (int j = 0; j<tagsArray.length(); j++) {
                    JSONObject tags = tagsArray.getJSONObject(j);
                    //extract author name
                    authorName = tags.optString("webTitle");
                }

                //print out data
                News news = new News(headline, newsType, newsTime, newsUrl,authorName);
                newsList.add(news);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the news JSON results");
        }
        return newsList;
    }
}
