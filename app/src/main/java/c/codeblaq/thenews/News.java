package c.codeblaq.thenews;

/*Parameters for each news object*/
public class News {
    //News type
    private String mNewsType;
    //News headline
    private String mNewsHeadline;
    //News publication time
    private String mPublicationTime;
    //News URL
    private String mNewsUrl;

    /**
     * Values for various news
     *
     * @param mNewsType        is type of news (eg. Sports)
     * @param mNewsHeadline    is news text visible in list view
     * @param mPublicationTime is date and time of news publication
     * @param mNewsUrl         is url to fetch news
     */
    public News(String mNewsHeadline, String mNewsType,
                String mPublicationTime, String mNewsUrl) {

        this.mNewsHeadline = mNewsHeadline;
        this.mNewsType = mNewsType;
        this.mPublicationTime = mPublicationTime;
        this.mNewsUrl = mNewsUrl;
    }

    //Get news headline
    public String getmNewsHeadline() {
        return mNewsHeadline;
    }

    //Get type of news
    public String getmNewsType() {
        return mNewsType;
    }

    //Get date of publication
    public String getmPublicationTime() {
        return mPublicationTime;
    }

    //Get news url
    public String getmNewsUrl() {
        return mNewsUrl;
    }
}
