/*
 * @(#)Lookup.java   2009.09.30 at 04:56:10 PDT
 *
 * Copyright 2009 MBARI
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package vars.knowledgebase.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.awt.Frame;
import org.mbari.swing.ProgressDialog;
import org.mbari.util.Dispatcher;
import vars.knowledgebase.KnowledgebaseModule;
import vars.shared.ui.FatalErrorSubscriber;
import vars.shared.ui.NonFatalErrorSubscriber;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Sep 29, 2009
 * Time: 11:39:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class Lookup {

    protected static final Object KEY_DISPATCHER_APPLICATION_FRAME = KnowledgebaseFrame.class;
    protected static final Object KEY_DISPATCHER_APPLICATION = KnowledgebaseApp.class;
    public static final String RESOURCE_BUNDLE = "knowlegebase-app";
    public static final Object KEY_DISPATCHER_GUICE_INJECTOR = Injector.class;
    public static final String TOPIC_DELETE_CONCEPT = "Delete Concepts";
    public static final String TOPIC_DELETE_CONCEPT_NAME = "Delete ConceptNames";
    public static final String TOPIC_DELETE_HISTORY = "Delete Histories";
    public static final String TOPIC_DELETE_LINK_REALIZATION = "Delete LinkRealizations";
    public static final String TOPIC_DELETE_LINK_TEMPLATE = "Delete LinkTemplates";
    public static final String TOPIC_DELETE_MEDIA = "Delete Medias";

    /**
     * Subscribers to this topic will get a {@link String} as the data
     */
    public static final String TOPIC_NONFATAL_ERROR = NonFatalErrorSubscriber.TOPIC_NONFATAL_ERROR;

    /**
     * Subscribers to this topic will get and {@link Exception} as the data
     */
    public static final String TOPIC_FATAL_ERROR = FatalErrorSubscriber.TOPIC_FATAL_ERROR;
    private static ProgressDialog progressDialog;

    protected static Dispatcher getApplicationDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_APPLICATION);
    }

    public static Dispatcher getApplicationFrameDispatcher() {
        return Dispatcher.getDispatcher(KEY_DISPATCHER_APPLICATION_FRAME);
    }

    public static Dispatcher getGuiceInjectorDispatcher() {
        final Dispatcher dispatcher = Dispatcher.getDispatcher(KEY_DISPATCHER_GUICE_INJECTOR);
        Injector injector = (Injector) dispatcher.getValueObject();
        if (injector == null) {
            injector = Guice.createInjector(new KnowledgebaseModule());
            dispatcher.setValueObject(injector);
        }

        return Dispatcher.getDispatcher(KEY_DISPATCHER_GUICE_INJECTOR);
    }

    public static ProgressDialog getProgressDialog() {
        if (progressDialog == null) {
            Frame frame = (Frame) getApplicationFrameDispatcher().getValueObject();
            progressDialog = new ProgressDialog(frame);
            progressDialog.setLocationRelativeTo(frame);
        }

        return progressDialog;
    }
}
