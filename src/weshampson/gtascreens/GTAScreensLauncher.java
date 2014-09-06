
package weshampson.gtascreens;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import weshampson.commonutils.exception.UncaughtExceptionHandler;
import weshampson.commonutils.jar.JarProperties;
import weshampson.commonutils.logging.ANSILogger;
import weshampson.commonutils.logging.Logger;
import weshampson.gtascreens.gui.MainWindow;

/**
 *
 * @author  Wes Hampson
 * @version 1.0.0 (Sep 6, 2014)
 * @since   1.0.0 (Aug 26, 2014)
 */
public class GTAScreensLauncher {
    public static void main(String[] args){
        initJarProperties();
        initUncaughtExceptionHandler();
        initShutdownHook();
        initLogger();
        initLookAndFeel();
        Logger.log(Logger.Level.INFO, "Started " + JarProperties.getApplicationTitle() + " " + JarProperties.getApplicationVersion());
        MainWindow mw = new MainWindow();
        mw.setLocationRelativeTo(null);
        mw.setVisible(true);
    }
    
    public static void initJarProperties() {
        JarProperties.setSourceClass(GTAScreensLauncher.class);
    }
    public static void initUncaughtExceptionHandler() {
        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler();
        uncaughtExceptionHandler.showDialog(true);
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
    }
    public static void initShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.log(Logger.Level.INFO, "Exiting...");
            }
        }));
    }
    public static void initLogger() {
        Logger.setLogger(new ANSILogger());
        Logger.getLogger().setColorEnabled(true);
    }
    public static void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            throw new RuntimeException(ex);
        }
    }
}
