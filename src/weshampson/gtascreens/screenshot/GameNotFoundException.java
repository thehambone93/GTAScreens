
package weshampson.gtascreens.screenshot;

/**
 *
 * @author  Wes Hampson
 * @version 1.0.0 (Sep 6, 2014)
 * @since   1.0.0 (Aug 28, 2014)
 */
public class GameNotFoundException extends Exception {
    public GameNotFoundException() {
        super();
    }
    public GameNotFoundException(String message) {
        super(message);
    }
    public GameNotFoundException(Throwable cause) {
        super(cause);
    }
    public GameNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
