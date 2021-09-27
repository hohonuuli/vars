package org.mbari.vars.varspub


/**
 * Copies images from the internal framegrab repository to the external 
 * 'varspub' repository and watermarks them. Run as
 * gsh -e "ImageMigrator.update '/mnt/varspub/framegrabs'"
 */


import groovy.sql.Sql
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.text.SimpleDateFormat
import javax.imageio.ImageIO
import org.slf4j.LoggerFactory
import mbarix4j.awt.image.ImageUtilities

class ImageMigrator {

    static targetRoot
    static externalMap = [:]
    static internalMap = [:]
    static String copyright = new String('\u00a9')
    static dateFormat = new SimpleDateFormat('yyyy')
    private static final log = LoggerFactory.getLogger(ImageMigrator.class)

    static void doDatabaseLookup() {
        def internalDb = Sql.newInstance("jdbc:jtds:sqlserver://equinox.shore.mbari.org:1433/VARS",
                "everyone", "guest", "net.sourceforge.jtds.jdbc.Driver");

        def externalDb = Sql.newInstance("jdbc:jtds:sqlserver://dione.mbari.org:51001/VARS",
                "everyone", "guest", "net.sourceforge.jtds.jdbc.Driver");

        // Fetch all image URL's on the external database
        externalDb.eachRow("""\
SELECT 
    id, 
    StillImageURL 
FROM 
    CameraData 
WHERE 
    (NOT (StillImageURL IS NULL)) ORDER BY id DESC""") { row ->
            externalMap[row[0]] = row[1]
        }
        def imageCount = externalMap.size()
        log.info("Found ${imageCount} image URLs in VARSPUB")

        // Fetch corresponding URLs on the interal database
        int n = 0
        for (id in externalMap.keySet()) {
            n++
            
            // TODO exclude images from annotations recorded in the last 2 years
            def sql = """\
SELECT 
    id, 
    StillImageURL 
FROM 
    CameraData 
WHERE 
    id = ${id}"""
            def result = internalDb.firstRow(sql)
            if (result && result[0] && result[1]) {
                internalMap.put(result[0], result[1])
            }
            
            if (n % 5000 == 0) {
                log.info("Retrieved ${n} of ${imageCount} URL's from Equinox")
            }
        }
    }

    /**
     * Checks to see if an image was found at the given URL
     */
    static boolean imageExists(URL url) {

        log.info("Attempting to read image at ${url}")
        def exists = false
        try {
            // If the image doesn't exist Java will throw an IOException
            def inputStream = url.openStream()
            def buf = new byte[6]
            inputStream.read(buf)
            // This indicates an error webpage from IIS on varspub
/*            if ("<html>".equalsIgnoreCase(new String(buf, "UTF-8"))) {
                exists = false
                log.info("${url} was not found")
            }*/
            inputStream.close()
            exists = true
        }
        catch (Exception e) {
            log.info("\tFailed to read ${url}")
        }
        return exists
    }

    static fetchAndWatermarkImage(def id) {
        //log.info("\n\tINTERNAL: ${internalMap[id]}\n\tEXTERNAL: ${externalMap[id]}")
        
        if (!internalMap[id]) {
            log.info("Image is no longer available on the internal web server")
            return
        }
        
        def internalUrl = new URL(internalMap[id])
        def externalUrl = new URL(externalMap[id])
    
        /*
         * Convert URL to a local File
         */
        def parts = internalUrl.toString().split("/")
        int idx = 0
        for (i in 0..<parts.size()) {
            if ("framegrabs".equals(parts[i].toLowerCase())) {
                idx = i + 1
                break
            }
        }
        // Build file path from parts of url
        File localFile = new File(targetRoot)
        for (i in idx..<parts.size()) {
            if (i == parts.size() - 1) {
                // Some file names contain ":", these need to be replaced with "_"
                parts[i] = parts[i].replaceAll(":", "_") 
            }
            localFile = new File(localFile, parts[i])
        }
        
        // Create the directory if it doesn't already exist
        File parentDir = localFile.getParentFile()
        if (!parentDir?.exists()) {
            log.info("Creating ${parentDir}")
            parentDir.mkdirs()
        }
     
        // Read in the target image
        BufferedImage image = ImageIO.read(internalUrl)
        ImageUtilities.addWatermark(image, "Copyright ${copyright} ${dateFormat.format(new Date())} MBARI", Color.WHITE, new Font("Arial", Font.BOLD, 34), 0.33f)
        ImageUtilities.saveImage(image, localFile)
        log.info("Watermark completed.\n\tCopied ${internalUrl}\n\tto ${localFile}\n\tthe public URL is ${externalUrl}")
    }

    static update(target) {
        println "Starting"
        /*
        * Setup command line interface
        */
        /*def cli = new CliBuilder(usage: "gsh ../scripts/groovy/ImageMigrator -t [targetDirectory]")
                cli.h(longOpt: "help", "usage information")
                cli.t(argName: "target", longOpt: "target", args:1, required: true, "Target directory for images")
        */
        /*
         * Parse the command line options
         */
        /*def options = cli.parse(args)
                if (!options) {
                    return
                }
                if (options.h) {
                    println "Script for copying images to external repository and watermarking them"
                    cli.usage()
                }

                targetRoot = options.t*/
        targetRoot = target
        log.info("Starting database lookup at ${new Date()}")
        doDatabaseLookup()
        log.info("Finished database lookup at ${new Date()}")


        for (id in externalMap.keySet()) {
            URL url = new URL(externalMap[id])
            if (!imageExists(url)) {
                try {
                    fetchAndWatermarkImage(id)
                }
                catch (Exception e) {
                    log.info("Failed to watermark ${url}", e)
                }
            }
            Thread.sleep(50) // REQUIRED!! Otherwise Apache on Eione ends up hanging after a few hundred files
        }
    }
}
