package pro.dbro.glance.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import pro.dbro.glance.fragments.FeedFragment;

public class ReaderSectionAdapter extends FragmentPagerAdapter {

    /**
     * A content feed.
     *
     * If a content feed is managed from a direct Parse query, it doesn't require a url.
     * See {@link pro.dbro.glance.fragments.FeedFragment#createFeedAdapter()} and
     * {@link pro.dbro.glance.adapters.ArticleAdapter}
    */
    public static enum Feed {
        POPULAR     ("Most Popular"),
        RECENT      ("Recent"),
        NEWS        ("News",       "https://api.rss2json.com/v1/api.json?rss_url=http%3A%2F%2Frss.cnn.com%2Frss%2Fcnn_topstories.rss"),
        TECHNOLOGY  ("Technology", "https://api.rss2json.com/v1/api.json?rss_url=http%3A%2F%2Frss.cnn.com%2Frss%2Fcnn_tech.rss"),
        HEALTH      ("Health",     "https://api.rss2json.com/v1/api.json?rss_url=http%3A%2F%2Frss.cnn.com%2Frss%2Fcnn_health.rss"),
        HN          ("HN",         "https://api.rss2json.com/v1/api.json?rss_url=https%3A%2F%2Fnews.ycombinator.com%2Frss"),
        TECH_CRUNCH ("Tech Crunch", "https://api.rss2json.com/v1/api.json?rss_url=http%3A%2F%2Ffeeds.feedburner.com%2FTechCrunch%2F"),
        WIRED       ("Wired",       "https://api.rss2json.com/v1/api.json?rss_url=https%3A%2F%2Fwww.wired.com%2Ffeed%2F");

        private final String mTitle;
        private final String mFeedUrl;

        private Feed(final String title) {
            mTitle = title;
            mFeedUrl = null;
        }

        private Feed(final String title, final String url) {
            mTitle = title;
            mFeedUrl = url;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getFeedUrl() {
            return mFeedUrl;
        }
    }

    public ReaderSectionAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Feed.values()[position].getTitle();
    }

    @Override
    public int getCount() {
        return Feed.values().length;
    }

    @Override
    public Fragment getItem(int position) {
        return FeedFragment.newInstance(Feed.values()[position]);
    }

}
