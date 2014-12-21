package io.github.mthli.Tweetin.Tweet;

import android.content.Context;
import io.github.mthli.Tweetin.Database.DataRecord;
import io.github.mthli.Tweetin.R;
import io.github.mthli.Tweetin.Twitter.TwitterUnit;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.Status;
import twitter4j.URLEntity;

public class TweetUnit {

    private Context context;

    private String useScreenName;

    public TweetUnit(Context context) {
        this.context = context;

        this.useScreenName = TwitterUnit.getUseScreenNameFromSharedPreferences(context);
    }

    public String getPictureURLFromStatus(Status status) {
        String[] suffixes = context.getResources().getStringArray(R.array.tweet_picture_suffix);

        URLEntity[] urlEntities;
        MediaEntity[] mediaEntities;

        if (status.isRetweet()) {
            urlEntities = status.getRetweetedStatus().getURLEntities();
            mediaEntities = status.getRetweetedStatus().getMediaEntities();
        } else {
            urlEntities = status.getURLEntities();
            mediaEntities = status.getMediaEntities();
        }

        for (MediaEntity mediaEntity : mediaEntities) {
            if (mediaEntity.getType().equals(context.getString(R.string.tweet_media_type))) {
                return mediaEntity.getMediaURL();
            }
        }

        for (URLEntity urlEntity : urlEntities) {
            String expandedURL = urlEntity.getExpandedURL();
            for (String suffix : suffixes) {
                if (expandedURL.endsWith(suffix)) {
                    return expandedURL;
                }
            }
        }

        return null;
    }

    public String getDetailTextFromStatus(Status status) {
        URLEntity[] urlEntities;
        MediaEntity[] mediaEntities;

        String text;

        if (status.isRetweet()) {
            urlEntities = status.getRetweetedStatus().getURLEntities();
            mediaEntities = status.getRetweetedStatus().getMediaEntities();
            text = status.getRetweetedStatus().getText();
        } else {
            urlEntities = status.getURLEntities();
            mediaEntities = status.getMediaEntities();
            text = status.getText();
        }

        for (URLEntity urlEntity : urlEntities) {
            text = text.replace(
                    urlEntity.getURL(),
                    urlEntity.getExpandedURL()
            );
        }

        for (MediaEntity mediaEntity : mediaEntities) {
            text = text.replace(
                    mediaEntity.getURL(),
                    mediaEntity.getMediaURL()
            );
        }

        return text;
    }

    public Tweet getTweetFromDataRecord(DataRecord record) {
        Tweet tweet = new Tweet();

        tweet.setAvatarURL(record.getAvatarURL());
        tweet.setName(record.getName());
        tweet.setScreenName(record.getScreenName());
        tweet.setCreatedAt(record.getCreatedAt());
        tweet.setCheckIn(record.getCheckIn());
        tweet.setProtect(record.isProtect());
        tweet.setPictureURL(record.getPictureURL());
        tweet.setText(record.getText());
        tweet.setRetweetedByName(record.getRetweetedByName());
        tweet.setFavorite(record.isFavorite());

        tweet.setStatusId(record.getStatusId());
        tweet.setInReplyToStatusId(record.getInReplyToStatusId());
        tweet.setRetweetedByScreenName(record.getRetweetedByScreenName());

        tweet.setBitmap(null);

        return tweet;
    }

    public DataRecord getDataRecordFromStatus(Status status) {
        DataRecord record = new DataRecord();

        if (status.isRetweet()) {
            record.setAvatarURL(status.getRetweetedStatus().getUser().getOriginalProfileImageURL());
            record.setName(status.getRetweetedStatus().getUser().getName());
            record.setScreenName(status.getRetweetedStatus().getUser().getScreenName());
            record.setCreatedAt(status.getRetweetedStatus().getCreatedAt().getTime());
            Place place = status.getRetweetedStatus().getPlace();
            if (place != null) {
                record.setCheckIn(place.getFullName());
            } else {
                record.setCheckIn(null);
            }
            record.setProtect(status.getRetweetedStatus().getUser().isProtected());
            record.setPictureURL(getPictureURLFromStatus(status.getRetweetedStatus()));
            record.setText(getDetailTextFromStatus(status.getRetweetedStatus()));
            record.setRetweetedByName(status.getUser().getName());
            record.setFavorite(status.getRetweetedStatus().isFavorited());

            record.setStatusId(status.getRetweetedStatus().getId());
            record.setInReplyToStatusId(status.getRetweetedStatus().getInReplyToStatusId());
            record.setRetweetedByScreenName(status.getUser().getScreenName());
        } else {
            record.setAvatarURL(status.getUser().getOriginalProfileImageURL());
            record.setName(status.getUser().getName());
            record.setScreenName(status.getUser().getScreenName());
            record.setCreatedAt(status.getCreatedAt().getTime());
            Place place = status.getPlace();
            if (place != null) {
                record.setCheckIn(place.getFullName());
            } else {
                record.setCheckIn(null);
            }
            record.setProtect(status.getUser().isProtected());
            record.setPictureURL(getPictureURLFromStatus(status));
            record.setText(getDetailTextFromStatus(status));
            record.setRetweetedByName(null);
            record.setFavorite(status.isFavorited());

            record.setStatusId(status.getId());
            record.setInReplyToStatusId(status.getInReplyToStatusId());
            record.setRetweetedByScreenName(null);
        }
        /* Do something, check isRetweetedByMe() || isRetweeted() */

        return record;
    }
}