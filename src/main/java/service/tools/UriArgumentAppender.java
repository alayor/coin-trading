package service.tools;

import java.net.URI;
import java.net.URISyntaxException;

class UriArgumentAppender {

    URI append(URI uri, String parameterKey, String parameterValue) throws URISyntaxException {
        String newQuery = uri.getQuery();
        String appendQuery = parameterKey + "=" + parameterValue;
        if (newQuery == null) {
            newQuery = appendQuery;
        } else {
            newQuery += "&" + appendQuery;
        }
        return new URI(uri.getScheme(), uri.getAuthority(),
          uri.getPath(), newQuery, uri.getFragment());
    }
}
