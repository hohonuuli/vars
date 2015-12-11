package vars.queryfx.ui;


import com.guigarage.sdk.Application;
import com.guigarage.sdk.action.Action;
import com.guigarage.sdk.container.WorkbenchView;
import com.guigarage.sdk.footer.ActionFooter;
import com.guigarage.sdk.form.EditorType;
import com.guigarage.sdk.form.FormLayout;
import com.guigarage.sdk.list.MediaList;
import com.guigarage.sdk.util.DefaultMedia;
import com.guigarage.sdk.util.Media;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.TimeZone;


public class QueryAppMockup {

    private static Logger log;

    public static void main(String[] args) {
        System.setProperty("user.timezone", "UTC");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        /*
          Log uncaught Exceptions
         */
        Thread.setDefaultUncaughtExceptionHandler((thread, e) -> {
            Logger log = LoggerFactory.getLogger(thread.getClass());
            log.error("Exception in thread [" + thread.getName() + "]", e);
        });

        final Logger mainLog = getLog();

        if (mainLog.isInfoEnabled()) {
            final Date date = new Date();
            mainLog.info("This application was launched at " + date.toString());
        }

        Application app = new Application();
        app.setTitle("VARS Query");
        app.setToolbarBackgroundImage("http://www.mbari.org/art/global/header/rotator.php");
        app.setStopCallback(() -> System.exit(0));
        app.addToolbarItem(new Action(AppIcons.PLAY, "Run Search"));

        app.setBaseColor(new Color(0x1B / 255D, 0x4D / 255D, 0x93 / 255D, 1));
        app.addMenuEntry(new Action(AppIcons.SEARCH, "Basic Search", () -> showSimpleSearch(app)));

        app.addMenuEntry(new Action(AppIcons.SEARCH_PLUS, "Advanced Search", () -> showAdvancedSearch(app)));

        app.addMenuEntry(new Action(AppIcons.GEARS, "Customize Results", () -> showCustomizeResults(app)));

        showSimpleSearch(app);
        app.show();
    }

    /**
     * Do NOT initialize a log until the 'user.timezone' property has been
     * set or you will not be able to store dates in the UTC timezone! This
     */
    private static Logger getLog() {
        if (log == null) {
            log = LoggerFactory.getLogger(App.class);
        }
        return log;
    }

    private static void showSimpleSearch(Application app) {
        WorkbenchView view = new WorkbenchView();
        MediaList<Media> list = new MediaList<>();
        list.getItems().add(new DefaultMedia("Chiroteuthis", "and descendants",
                "http://dsg.mbari.org/images/dsg/external/Mollusca/Cephalopoda/Chiroteuthis_calyx_01.png"));


        ActionFooter footer = new ActionFooter();
        footer.addAction(new Action(AppIcons.TRASH, "Remove all"));
        view.setFooterNode(footer);

        view.setCenterNode(list);

        app.setWorkbench(view);
        app.clearGlobalActions();
        app.addGlobalAction(new Action(AppIcons.PLUS, () -> {}));
    }

    private static void showAdvancedSearch(Application app) {
        WorkbenchView view = new WorkbenchView();
        FormLayout formLayout = new FormLayout();

        formLayout.addHeader("Advanced Search");
        formLayout.addField("Name");

        formLayout.addActions(new Action("Save"), new Action("Cancel"));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(formLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        view.setCenterNode(scrollPane);

        app.setWorkbench(view);
        app.clearGlobalActions();
    }

    private static void showCustomizeResults(Application app) {
        WorkbenchView view = new WorkbenchView();

        FormLayout formLayout = new FormLayout();
        formLayout.addHeader("Customize Results");

        formLayout.addField("Return related associations", EditorType.CHECKBOX);
        formLayout.addField("Return concurrent observations", EditorType.CHECKBOX);
        formLayout.addField("Return concept hierarchy", EditorType.CHECKBOX);
        formLayout.addField("Return basic organism phylogeny", EditorType.CHECKBOX);
        formLayout.addField("Return detailed organism phylogeny", EditorType.CHECKBOX);
        formLayout.addField("Categorize associations into columns", EditorType.CHECKBOX);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(formLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        view.setCenterNode(scrollPane);

        app.setWorkbench(view);
        app.clearGlobalActions();
    }

}