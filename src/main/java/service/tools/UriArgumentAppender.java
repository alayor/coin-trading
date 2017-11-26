package service.tools;

import java.net.URI;
import java.net.URISyntaxException;

class UriArgumentAppender {

    URI appendArgument(URI uri, String parameterKey, String parameterValue) {
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
