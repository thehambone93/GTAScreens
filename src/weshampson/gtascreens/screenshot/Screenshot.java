
package weshampson.gtascreens.screenshot;

import java.net.URL;

/**
 *
 * @author  Wes Hampson
 * @version 1.0.0 (Sep 6, 2014)
 * @since   1.0.0 (Aug 28, 2014)
 */
public class Screenshot {
    private final URL uRL;
    private final long fileSize;
    private final ScreenshotOperations.Game game;
    private final String set;
    public Screenshot(URL uRL, long fileSize, ScreenshotOperations.Game game, String set) {
        this.uRL = uRL;
        this.fileSize = fileSize;
        this.game = game;
        this.set = set;
    }
    public String getDisplayName() {
        return(game + "/" + set + "/" + getFileName());
    }
    public String getFileName() {
        return(uRL.getPath().substring(uRL.getPath().lastIndexOf("/") + 1, uRL.getPath().length()));
    }
    public long getFileSize() {
        return(fileSize);
    }
    public ScreenshotOperations.Game getGame() {
        return(game);
    }
    public String getSet() {
        return(set);
    }
    public URL getURL() {
        return(uRL);
    }
}
