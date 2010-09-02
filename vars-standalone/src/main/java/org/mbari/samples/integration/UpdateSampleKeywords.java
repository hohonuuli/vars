/*
 * @(#)UpdateSampleKeywords.java   2010.09.02 at 11:46:25 PDT
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


import java.sql.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *  Description of the Class
 *
 * @author  brian
 * @created  November 3, 2004
 */
public class UpdateSampleKeywords {

    private DatabaseUtility databaseUtility = new DatabaseUtility();


    /**
     * Samples Database connection
     */
    protected Connection con;

    // Seeds from table
    private Collection keywords;

    // Vims Keyword interface
    private Collection seeds;

    // Prepared Statement variable for Select
    private PreparedStatement stmtDel;

    // Prepared Statement variable for Update
    private PreparedStatement stmtIns;

    // Keywords from Vims
    // Prepared Statement variable for Delete
    private PreparedStatement stmtUpd;

    // Constructor

    /**
     * Constructor for the UpdateSampleKeywords object
     *
     * @exception  Exception Description of the Exception
     */
    public UpdateSampleKeywords() throws Exception {
        seeds = new LinkedList();
        keywords = new LinkedList();

        try {
            con = databaseUtility.newConnection();
            stmtIns = con.prepareStatement("INSERT INTO VIMSkeywords (SampleID, KeywordString) VALUES (?,?)");
            stmtDel = con.prepareStatement("DELETE FROM VIMSkeywords WHERE SampleID = ?");
            stmtUpd = con.prepareStatement("UPDATE Sample SET KeywordLookupStatus = ? WHERE SampleID = ?");
        }
        catch (SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
            ex.printStackTrace();

            throw ex;
        }
    }

    // All PUBLIC Methods in Alpha Order

    /**
     *  Description of the Method
     */
    public void cleanup() {
        seeds.clear();
        keywords.clear();

        try {
            con.close();
        }
        catch (SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Main

    /**
     *  The main program for the UpdateSampleKeywords class
     *
     * @param  args The command line arguments
     */
    public static void main(String[] args) {
        int exitStatus = 0;

        if (args.length != 1) {
            System.err.println("Usage: UpdateSampleKeywords Sample_ID");
            System.err.println(" SampleID = SQL database ID of the Sample");
            System.exit(1);
        }

        UpdateSampleKeywords usk = null;

        try {
            usk = new UpdateSampleKeywords();

            int sampleID = Integer.parseInt(args[0]);
            int num = usk.populateSampleKeywords(sampleID);

            System.out.println(num + " keywords entered for SampleID: " + sampleID);
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
     * @param  SampleId Description of the Parameter
     * @return  Description of the Return Value
     * @exception  Exception Description of the Exception
     */
    public int populateSampleKeywords(int SampleId) throws Exception {
        int result = 0;

        // Make sure lists are empty
        seeds.clear();
        keywords.clear();

        // 1) Get the SeedConcepts from the Samples database
        try {

            // Execute the "DELETE FROM VIMSkeywords WHERE SampleID = ?"
            stmtDel.setInt(1, SampleId);
            stmtDel.executeUpdate();

            // System.out.println("Made it past stmtDel with id:" + SampleId);

            // Execute the "SELECT SeedConcept FROM SeedVIMSConcepts WHERE SampleID = ?"
            // *** Can not figure out wy this doesn't work ***
            // stmtSel.setInt(1, SampleId);
            // ResultSet rs = stmtSel.executeQuery ();
            // ***********************************************
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT SeedConcept FROM SeedVIMSConcepts WHERE SampleID = " + SampleId);

            // Loop throught the database output adding to seeds
            while (rs.next()) {
                seeds.add(rs.getString(1));
            }
        }
        catch (SQLException ex) {
            System.err.println("Select SQLException: " + ex.getMessage());

            result = -100;

            throw ex;
        }

        int keywordStatus = 0;

        // 2) Get the Keyswords from VIMS
        if (seeds.isEmpty()) {

            // No Seeds found
            keywordStatus = 2;
        }
        else {

            // Make a temporary list
            Collection tmpKeywords = new LinkedList();

            // Make sure keywords is empty
            keywords.clear();

            // Loop through all the seeds and add the not already found Vims keywords to list
            Iterator seedsIt = seeds.iterator();

            while (seedsIt.hasNext()) {
                tmpKeywords.clear();

                try {
                    String kwString = (String) seedsIt.next();

                    tmpKeywords.addAll(databaseUtility.getKeywords(kwString));

                    if (tmpKeywords.isEmpty()) {
                        keywordStatus = 2;
                    }
                    else {
                        Iterator tmpIt = tmpKeywords.iterator();

                        while (tmpIt.hasNext()) {
                            String strKeyword = (String) tmpIt.next();

                            if (!keywords.contains(strKeyword)) {
                                keywords.add(strKeyword);
                            }
                        }

                        // Get rid of "Object" and "Physical object"

                        String strObject = "Object";
                        String strPhysicalObject = "Physical object";
                        LinkedList lkeywords = (LinkedList) keywords;
                        int i = lkeywords.indexOf(strObject);

                        if (i >= 0) {
                            lkeywords.remove(i);
                        }

                        i = lkeywords.indexOf(strPhysicalObject);

                        if (i >= 0) {
                            lkeywords.remove(i);
                        }
                    }
                }
                catch (Exception ex) {
                    System.err.println("VimsException: " + ex.getMessage());

                    result = -100;

                    throw ex;
                }
            }
        }

        // 3) Add the VIMS keywords to the Samples database
        if (keywords.isEmpty()) {

            // No Keywords found
        }
        else {
            try {

                // Set the SampleID part of the insert
                stmtIns.setInt(1, SampleId);

                // Loop throut the list performing the insert;
                // "INSERT INTO VIMSkeywords (SampleID, KeywordString) VALUES (?,?)"
                Iterator keyIt = keywords.iterator();

                while (keyIt.hasNext()) {
                    stmtIns.setString(2, (String) keyIt.next());
                    stmtIns.executeUpdate();

                    result++;
                }
            }
            catch (SQLException ex) {
                System.err.println("SQLException: " + ex.getMessage());

                throw ex;
            }
        }

        // 4) If everything succeeded then update the Sample table
        try {

            // Execute the "UPDATE Sample SET KeywordLookupStatus = ? WHERE SampleID = ?"
            stmtUpd.setInt(1, keywordStatus);
            stmtUpd.setInt(2, SampleId);
            stmtUpd.executeUpdate();
        }
        catch (SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());

            throw ex;
        }

        return result;
    }

    // Prepared Statement variable for Insert
}
