/*
 * @(#)UpdateAllSampleKeywords.java   2010.09.02 at 11:48:09 PDT
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.mbari.samples.integration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *  Description of the Class
 *
 * @author  brian
 * @created  November 3, 2004
 */
public class UpdateAllSampleKeywords extends UpdateSampleKeywords {

    private Collection sampleIds;

    /**
     * Constructor for the UpdateAllSampleKeywords object
     *
     * @exception  Exception Description of the Exception
     */
    public UpdateAllSampleKeywords() throws Exception {
        super();

        sampleIds = new LinkedList();
    }


    /**
     *  Description of the Method
     */
    public void cleanup() {
        sampleIds.clear();
        super.cleanup();
    }

    /**
     *  The main program for the UpdateAllSampleKeywords class
     *
     * @param  args The command line arguments
     */
    public static void main(String[] args) {
        int exitStatus = 0;

        if (args.length != 0) {
            System.err.println("Usage: UpdateSampleKeywords");
            System.exit(1);
        }

        UpdateAllSampleKeywords usk = null;

        try {
            usk = new UpdateAllSampleKeywords();

            int num = usk.populateAllSampleKeywords();

            if (num < 0) {
                System.out.println(num + " Samples had problems");
            }
            else {
                System.out.println("UpdateAllSampleKeywords successful, " + num + " Samples updated!");
            }
        }
        catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            ex.printStackTrace();

            exitStatus = 1;
        }

        if (usk != null) {
            usk.cleanup();
        }

        System.exit(exitStatus);
    }

    /**
     *  Description of the Method
     *
     * @return  Description of the Return Value
     * @exception  Exception Description of the Exception
     */
    public int populateAllSampleKeywords() throws Exception {

        // Make sure list is empty
        sampleIds.clear();

        // 1) Get the SampleIDs from the Samples database
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT SampleID FROM Sample WHERE KeywordLookupStatus <> 0");

            // Loop throught the database output adding to sampleIds list
            while (rs.next()) {
                Integer newInteger = new Integer(rs.getInt(1));

                sampleIds.add(newInteger);
            }
        }
        catch (SQLException ex) {
            System.err.println("KeywordLookup SQLException: " + ex.getMessage());
            ex.printStackTrace();

            throw ex;
        }

        // 2) Update one at a time (this may be very slow, need to rethink!)
        int numSuccess = 0;

        if (!sampleIds.isEmpty()) {
            Iterator idIt = sampleIds.iterator();

            while (idIt.hasNext()) {
                Integer tmpInteger = (Integer) idIt.next();
                int sampleId = tmpInteger.intValue();

                try {
                    System.out.print("SampleID: " + sampleId);

                    int numKeywords = populateSampleKeywords(sampleId);

                    System.out.println("  - Keywords Found: " + numKeywords);

                    numSuccess = numSuccess + 1;
                }
                catch (Exception ex) {
                    System.err.println("SampleID: " + sampleId + ", Exception: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }

        int result = numSuccess - sampleIds.size();

        if (result == 0) {
            result = sampleIds.size();
        }

        return (result);
    }

    // SampleIds from Sample table
}
