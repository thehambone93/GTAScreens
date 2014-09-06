
package weshampson.gtascreens.screenshot;

import java.awt.CardLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import weshampson.commonutils.logging.Logger;

/**
 *
 * @author  Wes Hampson
 * @version 1.0.0 (Sep 6, 2014)
 * @since   1.0.0 (Sep 4, 2014)
 */
public class ScreenshotOperations extends javax.swing.JDialog {
    private static final Map<String, String> iiiURLMap = new HashMap<>();
    private static final Map<String, String> vcURLMap = new HashMap<>();
    private static final Map<String, String> advURLMap = new HashMap<>();
    private static final Map<String, String> saURLMap = new HashMap<>();
    private static final Map<String, String> lcsURLMap = new HashMap<>();
    private static final Map<String, String> vcsURLMap = new HashMap<>();
    private static final Map<String, String> ivURLMap = new HashMap<>();
    private static final Map<String, String> tladURLMap = new HashMap<>();
    private static final Map<String, String> tbogtURLMap = new HashMap<>();
    private static final Map<String, String> cwURLMap = new HashMap<>();
    private static final Map<String, String> vURLMap = new HashMap<>();
    static {
        iiiURLMap.put("PS2", "http://www.rockstargames.com/grandtheftauto3/flash/infoScreens/bigScreens/screen*.jpg");
        iiiURLMap.put("XBox", "http://www.rockstargames.com/grandtheftauto3/xbox/gta3_**.jpg");
        iiiURLMap.put("PC", "http://www.rockstargames.com/grandtheftauto3/pc/ss*.jpg");
        vcURLMap.put("Set 1", "http://www.rockstargames.com/vicecity/images/screens/screen**.jpg");
        vcURLMap.put("Set 2", "http://www.rockstargames.com/vicecity/images/screens/screenC**.jpg");
        vcURLMap.put("Set 3", "http://www.rockstargames.com/vicecity/images/screens/screenD**.jpg");
        advURLMap.put("All", "http://www.rockstargames.com/grandtheftauto/gba/ss/imgs/ss**.jpg");
        saURLMap.put("All", "http://www.rockstargames.com/sanandreas/screens/screen**.jpg");
        lcsURLMap.put("PS2", "http://www.rockstargames.com/libertycitystories/dry/screenshots/ps2/**_ps2.jpg");
        lcsURLMap.put("PSP", "http://www.rockstargames.com/libertycitystories/dry/screenshots/pspgta*.jpg");
        vcsURLMap.put("All", "http://media.rockstargames.com/vicecitystories/dry/screenshots/vcs_*.jpg");
        ivURLMap.put("Consoles", "http://www.rockstargames.com/IV/screens/1280/*.jpg");
        ivURLMap.put("PC", "http://media.rockstargames.com/flies/screens/pc/***.jpg");
        tladURLMap.put("All", "http://media.rockstargames.com/products/rockstar/screenshot%20gallery/thelostanddamned/1/1280x720/*.jpg");
        tbogtURLMap.put("Set 1", "http://media.rockstargames.com/products/rockstar/screenshot%20gallery/theballadofgaytony/1/1280x720/*.jpg");
        tbogtURLMap.put("Set 2", "http://media.rockstargames.com/products/rockstar/screenshot%20gallery/theballadofgaytony/1/1280x720/new/*.jpg");
        tbogtURLMap.put("Set 3", "http://media.rockstargames.com/products/rockstar/screenshot%20gallery/theballadofgaytony/1/1280x720/outnow/*.jpg");
        cwURLMap.put("Set 1", "http://media.rockstargames.com/chinatownwars/US/images/screens/*.jpg");
        cwURLMap.put("Set 2", "http://media.rockstargames.com/chinatownwars/US/images/screens/set2/*.jpg");
        cwURLMap.put("Set 3", "http://media.rockstargames.com/chinatownwars/US/images/screens/set3/*.jpg");
        cwURLMap.put("Set 4", "http://media.rockstargames.com/chinatownwars/US/images/screens/set4/*.jpg");
        cwURLMap.put("Set 5", "http://media.rockstargames.com/chinatownwars/US/images/screens/set5/*.jpg");
        cwURLMap.put("Set 6", "http://media.rockstargames.com/chinatownwars/US/images/screens/set6/*.jpg");
        cwURLMap.put("Set 7", "http://media.rockstargames.com/chinatownwars/US/images/screens/set7/*.jpg");
        cwURLMap.put("Multiplayer", "http://media.rockstargames.com/chinatownwars/US/images/screens/mp/*.jpg");
        cwURLMap.put("Multiplayer 2", "http://media.rockstargames.com/chinatownwars/US/images/screens/mp2/*.jpg");
        vURLMap.put("All", "http://www.rockstargames.com/V/screenshots/screenshot/*-1280.jpg");
    }
    private static final String SEARCH_PANE_IDENTIFIER = "searchPane";
    private static final String DOWNLOAD_PANE_IDENTIFIER = "downloadPane";
    private static final int MAX_ERROR_COUNT = 20;
    private final CardLayout cardLayout = new CardLayout();
    private volatile boolean run = false;
    private volatile boolean cancelled = false;
    private volatile boolean error = false;
    private final Window parentWindow = this;
    public ScreenshotOperations(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLayout(cardLayout);
        cardLayout.addLayoutComponent(searchPane, SEARCH_PANE_IDENTIFIER);
        cardLayout.addLayoutComponent(downloadPane, DOWNLOAD_PANE_IDENTIFIER);
    }
    public List<Screenshot> searchForScreenshots(final ScreenshotOperations.Game game, final String set) {
        Logger.log(Logger.Level.INFO, "Searching for screenshots (" + game + "/" + set + ")...");
        run = true;
        final List<Screenshot> screenshots = new ArrayList<>();
        SwingWorker swingWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                String baseURL = game.getURLMap().get(set);
                int padLength = 0;
                for (char c : baseURL.toCharArray()) {
                    if (c == '*') {
                        padLength++;
                    }
                }
                int index = baseURL.indexOf('*');
                baseURL = baseURL.replaceAll("\\*", "");
                int errorCount = 0;
                int screenshotCount = 0;
                int uRLIndex = 0;
                while (errorCount < MAX_ERROR_COUNT && run) {
                    try {
                        String num = String.format("%0" + padLength + "d", uRLIndex++);
                        StringBuilder sb = new StringBuilder(baseURL);
                        for (int k = 0; k < num.length(); k++) {
                            sb.insert(index + k, num.charAt(k));
                        }
                        URL uRL = new URL(sb.toString());
                        HttpURLConnection httpURLConnection = (HttpURLConnection)uRL.openConnection();
                        httpURLConnection.setRequestMethod("GET");
                        httpURLConnection.connect();
                        if (httpURLConnection.getResponseCode() != 200) {
                            errorCount++;
                        } else {
                            errorCount = 0;
                            screenshotCount++;
                            screenshots.add(new Screenshot(uRL, httpURLConnection.getContentLengthLong(), game, set));
                            Logger.log(Logger.Level.INFO, "Found screenshot: " + uRL);
                            screenshotCountLabel.setText(screenshotCount + " screenshots found.");
                        }
                    } catch (MalformedURLException ex) {
                        Logger.log(Logger.Level.ERROR, ex, null);
                    } catch (ProtocolException ex) {
                        Logger.log(Logger.Level.ERROR, ex, null);
                        int option = JOptionPane.showConfirmDialog(parentWindow,
                                "<html><p style='width: 200px;'>An error occured while searching for screenshots.\n"
                                        + "Would you like to stop the search?\n"
                                        + "\n"
                                        + "Error details:\n"
                                        + ex.toString(),
                                "Error",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.ERROR_MESSAGE);
                        if (option == JOptionPane.YES_OPTION) {
                            stopCurrentOperation();
                        }
                    } catch (IOException ex) {
                        Logger.log(Logger.Level.ERROR, ex, null);
                        int option = JOptionPane.showConfirmDialog(parentWindow,
                                "<html><p style='width: 200px;'>An error occured while searching for screenshots.\n"
                                        + "Would you like to stop the search?\n"
                                        + "\n"
                                        + "Error details:\n"
                                        + ex.toString(),
                                "Error",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.ERROR_MESSAGE);
                        if (option == JOptionPane.YES_OPTION) {
                            stopCurrentOperation();
                            Logger.log(Logger.Level.INFO, "Search stopped.");
                        }
                    }
                }
                if (!run) {
                    Logger.log(Logger.Level.INFO, "Search stopped.");
                }
                Logger.log(Logger.Level.INFO, "Found " + screenshotCount + " screenshots.");
                return(null);
            }
            @Override
            protected void done() {
                dispose();
            }
        };
        swingWorker.execute();
        cardLayout.show(getContentPane(), SEARCH_PANE_IDENTIFIER);
        setTitle("Searching for screenshots...");
        pack();
        setLocationRelativeTo(getParent());
        setVisible(true);
        return(screenshots);
    }
    public void downloadScreenshots(final List<Screenshot> screenshots, final String destinationDirectory) {
        Logger.log(Logger.Level.INFO, "Downloading " + screenshots.size() + " screenshots...");
        Logger.log(Logger.Level.INFO, "Destination directory: " + destinationDirectory);
        run = true;
        cancelled = false;
        error = false;
        SwingWorker swingWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                int bytesRead = 0;
                byte[] buf = new byte[16 * 1024];
                long totalDownloadSize = 0;
                long totalBytesDownloaded = 0;
                int percentComplete = 0;
                int screenshotsDownloaded = 0;
                int totalScreenshots = screenshots.size();
                for (Screenshot s : screenshots) {
                    totalDownloadSize += s.getFileSize();
                }
                Logger.log(Logger.Level.INFO, "Total download size: " + Math.round((float)totalDownloadSize / 1024.0) + " kB");
                BufferedInputStream bufferedInputStream;
                FileOutputStream fileOutputStream;
                File outputFile;
                while (run) {
                    for (Screenshot s : screenshots) {
                        error = false;
                        URL uRL = s.getURL();
                        outputFile = new File(destinationDirectory + "/" + s.getDisplayName());
                        outputFile.getParentFile().mkdirs();
                        bufferedInputStream = new BufferedInputStream(uRL.openStream());
                        fileOutputStream = new FileOutputStream(outputFile);
                        downloadingLabel.setText("Downloading screenshots... (" + screenshotsDownloaded + " / " + totalScreenshots + ")");
                        Logger.log(Logger.Level.INFO, "Downloading " + s.getDisplayName()+ "... ", true, false);
                        try {
                            while ((bytesRead = bufferedInputStream.read(buf)) != -1 && run) {
                                fileOutputStream.write(buf, 0, bytesRead);
                                totalBytesDownloaded += bytesRead;
                                int tempPercentComplete = (int)Math.round(((float)totalBytesDownloaded / (float)totalDownloadSize) * 100.0);
                                if (tempPercentComplete > percentComplete) {
                                    percentComplete = tempPercentComplete;
                                    downloadProgressBar.setValue(percentComplete);
                                }
                                bytesDownloadedLabel.setText(Math.round((float)totalBytesDownloaded / 1024.0) + " kB / " + Math.round((float)totalDownloadSize / 1024.0) + " kB downloaded");
                            }
                        } catch (IOException ex) {
                            error = true;
                            Logger.log(Logger.Level.INFO, " failed!", false, true);
                            Logger.log(Logger.Level.ERROR, ex, null);
                            int option = JOptionPane.showConfirmDialog(parentWindow,
                                    "<html><p style='width: 200px;'>An error occured while downloading " + s.getDisplayName() + ".\n"
                                            + "Would you like to cancel downloading all screenshots?\n"
                                            + "\n"
                                            + "Error details:\n"
                                            + ex.toString(),
                                    "Error",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.ERROR_MESSAGE);
                            if (option == JOptionPane.YES_OPTION) {
                                run = false;
                                cancelled = true;
                            }
                        } finally {
                            fileOutputStream.close();
                            bufferedInputStream.close();
                        }
                        if (!run) {
                            if (!error) {
                                Logger.log(Logger.Level.INFO, " failed!", false, true);
                            }
                            if (cancelled) {
                                Logger.log(Logger.Level.ERROR, "Download cancelled by user.");
                            }
                            outputFile.delete();
                            break;
                        } else {
                            screenshotsDownloaded++;
                            if (!error) {
                                Logger.log(Logger.Level.INFO, " done!", false, true);
                            }
                        }
                    }
                    downloadingLabel.setText("Downloading screenshots... (" + screenshotsDownloaded + " / " + totalScreenshots + ")");
                    run = false;
                }
                return(null);
            }
            @Override
            protected void done() {
                if (!cancelled) {
                    Logger.log(Logger.Level.INFO, "Download complete!");
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(parentWindow, "Download complete!", "Complete", JOptionPane.INFORMATION_MESSAGE);
                }
                dispose();
            }
        };
        swingWorker.execute();
        cardLayout.show(getContentPane(), DOWNLOAD_PANE_IDENTIFIER);
        setTitle("Downloading " + screenshots.size() + " screenshots...");
        pack();
        setLocationRelativeTo(getParent());
        setVisible(true);
    }
    public void stopCurrentOperation() {
        run = false;
        cancelled = true;
    }
    public static enum Game {
        III("III", iiiURLMap),
        VC("Vice City", vcURLMap),
        ADV("Advance", advURLMap),
        SA("San Andreas", saURLMap),
        LCS("Liberty City Stories", lcsURLMap),
        VCS("Vice City Stories", vcsURLMap),
        IV("IV", ivURLMap),
        TLAD("The Lost and Damned", tladURLMap),
        TBOGT("The Ballad of Gay Tony", tbogtURLMap),
        CW("Chinatown Wars", cwURLMap),
        V("V", vURLMap);
        private final String gameName;
        private final Map<String, String> screenShotURLS;
        private Game(String gameName, Map<String, String> screenShotURLS) {
            this.gameName = gameName;
            this.screenShotURLS = screenShotURLS;
        }
        public static Game getGameByName(String name) throws GameNotFoundException {
            for (Game g : values()) {
                if (g.getGameName().equalsIgnoreCase(name)) {
                    return(g);
                }
            }
            throw new GameNotFoundException("game not found for name - " + name);
        }
        public String getGameName() {
            return(gameName);
        }
        public Map<String, String> getURLMap() {
            return(screenShotURLS);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        searchPane = new javax.swing.JPanel();
        searchingLabel = new javax.swing.JLabel();
        screenshotCountLabel = new javax.swing.JLabel();
        infoLabel = new javax.swing.JLabel();
        stopButton1 = new javax.swing.JButton();
        downloadPane = new javax.swing.JPanel();
        downloadingLabel = new javax.swing.JLabel();
        downloadProgressBar = new javax.swing.JProgressBar();
        bytesDownloadedLabel = new javax.swing.JLabel();
        stopButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.CardLayout());

        searchingLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        searchingLabel.setText("Searching for screenshots...");

        screenshotCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        screenshotCountLabel.setText("0 screenshots found.");

        infoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        infoLabel.setText("This dialog will close automatically when the search is complete.");

        stopButton1.setText("Stop");
        stopButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout searchPaneLayout = new javax.swing.GroupLayout(searchPane);
        searchPane.setLayout(searchPaneLayout);
        searchPaneLayout.setHorizontalGroup(
            searchPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(infoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(screenshotCountLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, searchPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(stopButton1))
                    .addComponent(searchingLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        searchPaneLayout.setVerticalGroup(
            searchPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, searchPaneLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(searchingLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(screenshotCountLabel)
                .addGap(18, 18, 18)
                .addComponent(infoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(stopButton1)
                .addContainerGap())
        );

        getContentPane().add(searchPane, "card2");

        downloadingLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        downloadingLabel.setText("Downloading screenshots... (0 / 0)");

        downloadProgressBar.setStringPainted(true);

        bytesDownloadedLabel.setText("0 kB / 0 kB downloaded");

        stopButton2.setText("Stop");
        stopButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout downloadPaneLayout = new javax.swing.GroupLayout(downloadPane);
        downloadPane.setLayout(downloadPaneLayout);
        downloadPaneLayout.setHorizontalGroup(
            downloadPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(downloadPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(downloadPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(downloadProgressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(downloadingLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, downloadPaneLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(stopButton2))
                    .addGroup(downloadPaneLayout.createSequentialGroup()
                        .addComponent(bytesDownloadedLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        downloadPaneLayout.setVerticalGroup(
            downloadPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(downloadPaneLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(downloadingLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(downloadProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bytesDownloadedLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(stopButton2)
                .addContainerGap())
        );

        getContentPane().add(downloadPane, "card3");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void stopButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButton1ActionPerformed
        stopCurrentOperation();
    }//GEN-LAST:event_stopButton1ActionPerformed

    private void stopButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButton2ActionPerformed
        stopCurrentOperation();
    }//GEN-LAST:event_stopButton2ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        stopCurrentOperation();
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bytesDownloadedLabel;
    private javax.swing.JPanel downloadPane;
    private javax.swing.JProgressBar downloadProgressBar;
    private javax.swing.JLabel downloadingLabel;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JLabel screenshotCountLabel;
    private javax.swing.JPanel searchPane;
    private javax.swing.JLabel searchingLabel;
    private javax.swing.JButton stopButton1;
    private javax.swing.JButton stopButton2;
    // End of variables declaration//GEN-END:variables
}