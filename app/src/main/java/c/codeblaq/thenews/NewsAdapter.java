package c.codeblaq.thenews;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsAdapter extends ArrayAdapter<News> {

    private static final String TIME_SEPARATOR = "T";

    /*Locate views to be bound*/
    //Locate view for bullet
    @BindView(R.id.bullet_TV)
    TextView bulletView;

    //Locate headline view
    @BindView(R.id.headline_TV)
    TextView headlineTextView;

    //Locate news type view
    @BindView(R.id.content_type_TV)
    TextView newsType;

    //Locate publication date view
    @BindView(R.id.date_TV)
    TextView newsDate;

    //Locate publication time view
    @BindView(R.id.time_TV)
    TextView newsTime;

    //Locate contributor view
    @BindView(R.id.contributor_TV)
    TextView contributor;

    /*Adapter constructor*/
    public NewsAdapter(@NonNull Context context, @NonNull List<News> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Check if the existing view is being used, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }
        //Get News object at a given position
        News currentNews = getItem(position);

        //Attach ButterKnife to view {convert view}
        ButterKnife.bind(this, convertView);

        //Get and store original time and date gotten from JSON as a single variable
        String originalTime = currentNews.getmPublicationTime();

        //Make adjustment for date and time zones
        if (originalTime.contains(TIME_SEPARATOR)) {
            //Split time from date
            String[] parts = originalTime.split(TIME_SEPARATOR);
            String part1 = parts[0];
            //Format date string
            String formattedate = formatDate(part1);
            //Set as date string
            newsDate.setText(formattedate);

            String part2 = parts[1];
            //Format time
            String formattedTime = formatTime(part2);
            //Set new text as publication time
            newsTime.setText(formattedTime);
        }
        //Set headline to TextView
        headlineTextView.setText(currentNews.getmNewsHeadline());

        //Get News type
        String currentNewsType = currentNews.getmNewsType();
        //Set news type to TextView
        newsType.setText(currentNewsType);

        //Format headline to get first character
        String formattedHeadline = formatHeadline(currentNews.getmNewsHeadline());
        //Set ID char to bullet view
        bulletView.setText(formattedHeadline);

        //Attach contributor name
        Log.v("NewsAdapter", "contributor name is "+ currentNews.getmContributor());
        contributor.setText(currentNews.getmContributor());

        //Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable newsTypeBullet = (GradientDrawable) bulletView.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int newsTypeColor = getTypeColor(currentNews.getmNewsType());

        newsTypeBullet.setColor(newsTypeColor);

        return convertView;
    }

    //Get bullet color based on news type
    private int getTypeColor(String newsType) {
        //instantiate color value as 0
        int typeColorValue = 0;
        switch (newsType) {
            case "Sport":
                typeColorValue = ContextCompat.getColor(getContext(), R.color.sport);
                break;
            case "News":
                typeColorValue = ContextCompat.getColor(getContext(), R.color.news);
                break;
            case "Lifestyle":
                typeColorValue = ContextCompat.getColor(getContext(), R.color.lifestyle);
                break;
            case "Art":
                typeColorValue = ContextCompat.getColor(getContext(), R.color.art);
                break;
            default:
                typeColorValue = ContextCompat.getColor(getContext(), R.color.colorPrimaryLight);
        }
        return typeColorValue;
    }

    /**
     * Helper method to get the first letter of word in headline
     *
     * @param title is the headline
     * @return headline first character
     */
    private String formatHeadline(String title) {
        String idChar = "";
        //Check for special characters
        if (title.contains("\'") || title.contains("\"")) {
            idChar = String.valueOf(title.charAt(1));
        } else {
            //Get first character
            idChar = String.valueOf(title.charAt(0));
        }
        return idChar;
    }

    /**
     * Format received string date
     */
    @TargetApi(Build.VERSION_CODES.O)
    private String formatDate(String date) {
        DateFormat dt = new SimpleDateFormat("dd-MM");
        String date1 = dt.format(new Date());
        return date1;
    }

    /**
     * Format received string time
     */
    @TargetApi(Build.VERSION_CODES.O)
    private String formatTime(String time) {
        DateFormat dt = new SimpleDateFormat("HH:mm a");
        String time1 = dt.format(new Date());
        return time1;
    }
}
