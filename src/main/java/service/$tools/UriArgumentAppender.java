package service.$tools;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Tool used to append arguments to a URL.
 */
public class UriArgumentAppender {

    /**
     * It appends a new argument to the specified URL.
     * @param uri to append the argument to.
     * @param parameterKey of the new argument.
     * @param parameterValue of the new argument.
     * @return a new URI including the appended argument.
     */
    public URI appendArgument(URI uri, String parameterKey, String parameterValue) {
        String newQuery = uri.getQuery();
        String appendQuery = parameterKey + "=" + parameterValue;
        if (newQuery == null) {
            newQuery = appendQuery;
        } else {
            newQuery += "&" + appendQuery;
        }
        try {
            return new URI(uri.getScheme(), uri.getAuthority(),
              uri.getPath(), newQuery, uri.getFragment());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
