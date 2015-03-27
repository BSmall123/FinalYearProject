
import java.util.ArrayList;
import java.util.List;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * Junior
 */
public class TwitterUtil {

    public static Twitter getTwitterInstance(String consumerKey, String consumerSecret, String tokenKey, String tokenSecret) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey(consumerKey);
        cb.setOAuthConsumerSecret(consumerSecret);
        cb.setOAuthAccessToken(tokenKey);
        cb.setOAuthAccessTokenSecret(tokenSecret);

        Twitter twitter = new TwitterFactory(cb.build()).getInstance();
        return twitter;
    }

    public static List<Status> searchStatuses(String queryString, Twitter twitter) {
        List<Status> statuses = new ArrayList<Status>();
        try {
            Query query = new Query(queryString);
            query.setCount(100);
            while (query != null) {
                QueryResult qr = twitter.search(query);
                List<Status> interimList = qr.getTweets();
                System.out.println("Got statuses interim list with size "+interimList.size());
                if (interimList.size() > 0) {
                    statuses.addAll(interimList);
                    if(statuses.size()>=1000)
                        break;
                }
                query=qr.nextQuery();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statuses;
    }
}
