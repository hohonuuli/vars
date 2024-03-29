/*
 * Copyright 2005 MBARI
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1 
 * (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package vars.query.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JFileChooser;
import javax.swing.ProgressMonitor;

import mbarix4j.awt.event.ActionAdapter;
import mbarix4j.awt.event.ActionRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vars.query.ui.actions.SaveFramegrabsAction;



/**
 * Class for saving all the images in a search to the desktop. The class
 * SaveFramegrabsAction does all the work however we wrap it in this action to
 * a) get the images to save from the queryresults and b) Pop up a dialog to
 * select the save location.
 *
 * @author Brian Schlining
 * @version $Id: SaveImagesFromQueryResultsAction.java 332 2006-08-01 18:38:46Z hohonuuli $
 */
public class SaveImagesFromQueryResultsAction extends ActionAdapter {

    private static final Logger log = LoggerFactory.getLogger(SaveImagesFromQueryResultsAction.class);


    private final SaveFramegrabsAction action = new SaveFramegrabsAction();
    private JFileChooser chooser;
    private QueryResultsFrame queryResultsFrame;


    /**
     *     @see mbarix4j.awt.event.IAction#doAction()
     */
    public void doAction() {
        /*
         * Show dialog for selecting a directory
         */
        int option = getChooser().showOpenDialog(queryResultsFrame);
        if (option == JFileChooser.APPROVE_OPTION) {
            action.setSaveLocation(getChooser().getSelectedFile());
            URL[] urls = getImageURLs();
            action.setUrls(urls);
            action.setProgressMonitor(
                    new ProgressMonitor(queryResultsFrame,
                    "Downloading images", "", 0, urls.length));
            ActionRunnable ar = new ActionRunnable(action);
            ar.start();
        }
    }

    private JFileChooser getChooser() {
        if (chooser == null) {
            chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }

        return chooser;
    }

    /**
     * This method walks a QueryResults object data set searching for all
     * data items that start with "http" (case insensitive) and returns them
     * in an array of URLs
     *
     * @return An array containing all URLs found in a QueryResults object.
     */
    @SuppressWarnings("unchecked")
    private URL[] getImageURLs() {
        Object[][] data = queryResultsFrame.getQueryResults().toRowOrientedArray();
        Collection urlList = new ArrayList();
        for (int row = 0; row < data.length; row++) {
            Object[] dataRow = data[row];
            for (int col = 0; col < dataRow.length; col++) {
                Object obj = data[row][col];
                if (obj != null) {
                    String s = obj.toString();
                    String sUpper = s.toUpperCase();
                    if (sUpper.startsWith("HTTP") || sUpper.startsWith("FILE")) {
                        if (sUpper.endsWith(".PNG") ||
                                sUpper.endsWith(".JPG") ||
                                sUpper.endsWith(".JPEG") ||
                                sUpper.endsWith(".TIF") ||
                                sUpper.endsWith(".TIFF") ||
                                sUpper.endsWith(".GIF")) {
                            try {
                                URL url = new URL(s);
                                urlList.add(url);
                            } catch (MalformedURLException e) {
                                log.info(
                                        "The value, " + s +
                                        ", is not a valid URL");
                            }
                        }
                    }
                }
            }
        }

        return (URL[]) urlList.toArray(new URL[urlList.size()]);
    }

    public void setQueryResultsFrame(QueryResultsFrame queryResultsFrame) {
        this.queryResultsFrame = queryResultsFrame;
    }
}
