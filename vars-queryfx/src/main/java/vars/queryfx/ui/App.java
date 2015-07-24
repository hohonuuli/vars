package vars.queryfx.ui;

import com.google.common.base.Preconditions;
import com.google.inject.Injector;
import com.guigarage.sdk.Application;
import com.guigarage.sdk.action.Action;
import com.guigarage.sdk.container.WorkbenchView;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vars.queryfx.Lookup;
import vars.queryfx.RXEventBus;
import vars.queryfx.ToolBelt;
import vars.queryfx.ui.sdkfx.BasicSearchWorkbench;
import vars.queryfx.ui.sdkfx.ConceptConstraintsWorkbench;
import vars.shared.ui.GlobalLookup;

import java.util.Date;
import java.util.TimeZone;

/**
 *
 */
public class App {

    /**
     * 378720000 = 1982-01-01
     */
    public static final Date MIN_RECORDED_DATE = new Date(378720000L * 1000L);
    private RXEventBus eventBus = new RXEventBus();
    private static Logger log;
    private final ToolBelt toolBelt;
    private Application application;
    private BasicSearchWorkbench basicSearchWorkbench;
    private ConceptConstraintsWorkbench conceptConstraintsWorkbench;

    public App(ToolBelt toolBelt) {
        Preconditions.checkArgument(toolBelt != null);
        this.toolBelt = toolBelt;
    }

    private Application getApplication() {
        if (application == null) {
            application = new Application();
            application.setTitle("VARS Query");
            application.setStopCallback(() -> System.exit(0));
            application.addToolbarItem(new Action(AppIcons.PLAY, "Run Search"));

            application.setBaseColor(new Color(0x1B / 255D, 0x4D / 255D, 0x93 / 255D, 1));
            application.addMenuEntry(new Action(AppIcons.SEARCH, "Basic Search", () -> showBasicSearch(application)));

            //application.addMenuEntry(new Action(AppIcons.SEARCH_PLUS, "Advanced Search", () -> showAdvancedSearch(app)));

            //application.addMenuEntry(new Action(AppIcons.GEARS, "Customize Results", () -> showCustomizeResults(app)));

            showBasicSearch(application);
        }
        return application;
    }

    protected void showBasicSearch(Application app) {
        WorkbenchView view = getBasicSearchWorkbench();
        app.setWorkbench(view);
        app.clearGlobalActions();
        app.addGlobalAction(new Action(AppIcons.PLUS, () -> showConceptConstraintsWorkBench(app)));
    }

    protected void showConceptConstraintsWorkBench(Application app) {
        app.clearGlobalActions();
        WorkbenchView view = getConceptConstraintsWorkbench(app);
        app.setWorkbench(view);
    }

    protected BasicSearchWorkbench getBasicSearchWorkbench()  {
        if (basicSearchWorkbench == null) {
            basicSearchWorkbench = new BasicSearchWorkbench();
        }
        return basicSearchWorkbench;
    }

    protected ConceptConstraintsWorkbench getConceptConstraintsWorkbench(Application app) {
        if (conceptConstraintsWorkbench == null) {
            conceptConstraintsWorkbench = new ConceptConstraintsWorkbench(
                    toolBelt.getQueryService(), toolBelt.getExecutor());
            conceptConstraintsWorkbench.getFormLayout().addActions(new Action(AppIcons.TRASH, "Cancel", () -> showBasicSearch(app)),
                    new Action("Apply"));
        }
        return conceptConstraintsWorkbench;
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

    public static void main( String[] args ) {
        System.setProperty("user.timezone", "UTC");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        GlobalLookup.getSettingsDirectory(); // Not used

        /*
          Log uncaught Exceptions
         */
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread thread, Throwable e) {
                Logger log = LoggerFactory.getLogger(thread.getClass());
                log.error("Exception in thread [" + thread.getName() + "]", e);
            }
        });

        final Logger mainLog = getLog();

        if (mainLog.isInfoEnabled()) {
            final Date date = new Date();
            mainLog.info("This application was launched at " + date.toString());
        }

        Injector injector = Lookup.getInjector();
        ToolBelt toolBelt = injector.getInstance(ToolBelt.class);


        App app = new App(toolBelt);
        app.getApplication().setPrefSize(400, 800);
        app.getApplication().show();

    }

}
