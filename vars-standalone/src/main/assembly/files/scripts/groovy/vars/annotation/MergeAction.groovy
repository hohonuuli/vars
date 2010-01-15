package vars.annotation


/**
 * Merges VARS annotation data with SHip EXPD data based on the 
 * VideoFrame.recordedDate value. There are 4 types of merges possible
 * <ol>
 *   <li>PRAGMATIC: Bad recordedDates are merged by timecode. All others by recordedDate</li>
 *   <li>OPTIMISTIC: Assumes VARS VideoFrame.recordedDates are correct</li>
 *   <li>PESSIMISTIC: Assumes VARS VideoFrame.recordedDates are incorrect</li>
 *   <li>CONSERVATIVE: Merges first by recordedDate then by timecode.
*        No recrdedDates are changed</li>
 * </ol>
 */
class MergeAction {
    
    String platform
    def seqNumber
    List annotations
    List expdData
    /** Map<Annotation, EXPDDatum> */
    Map data = [:] 
    EXPDMergeStatus expdMergeStatus
    def statusMessages = []
    boolean useHD
    
    private final blankDatum = new EXPDDatum()
    
    /** 
     * Flag to indicate merge type. Pragmatic means merge by time, any unmatched
     * annotations will then be merged by timecode. Bogus annotation dates will
     * be corrected. Unmerged annotations will not be changed
     */
    static final FLAG_PRAGMATIC = 0
    
    /**
     * Assumes all annotation dates are correct. Merges by time, any unmatched
     * annotations will not be merged. No annotation dates will be 
     * changed
     */
    static final FLAG_OPTIMISTIC = 1
    
    /**
     * Merges by timecode. Assumes all annotation dates will need to be 
     * overwritten. Unmerged annotations will have the recordedDates set to 
     * null
     */
    static final FLAG_PESSIMISTIC = 2
    
    /**
     * Same as PRAGMATIC but no annotation dates are changed
     */
    static final FLAG_CONSERVATIVE = 3
    
    static final FLAGS = ["PRAGMATIC", "OPTIMISTIC", "PESSIMISTIC", "CONSERVATIVE"]
    
    
    /**
     * Function for coallate annotations with expd data by date using
     * the annotation.recordedDate an expdData.navigationDate as the 
     * coallation keys. Inputs are 2 lists. Returns a Map<Annotation, EXPDDatum>
     */
    static final Closure coallateByDate = { annotations, expdData ->
        
        Logger.log(MergeAction.class, "Merging ${annotations.size()} annotations by date")
        // Filter out any objects with null coallation keys
        annotations = annotations.findAll { it.recordedDate != null }
        expdData = expdData.findAll { it.navigationDate != null }
        
        // Sort by the coallation keys
        annotations = annotations.sort { it.recordedDate }
        expdData = expdData.sort { it.navigationDate }
        
        def dataMap = [:]
        def offset = Constants.OFFSET_SECONDS * 1000
        int i = 0;
        annotations.each { a ->
            def t0 = a.recordedDate.time
            def dtBest = offset
            def goodData = null
            for (row in i..<expdData.size()) {
                def e = expdData[row]
                if (e.navigationDate) {
                    def t1 = e.navigationDate.time
                    def dt = Math.abs(t0 - t1)
                    if (dt <= dtBest) {
                        dtBest = dt
                        i = row
                        goodData = e
                    }
                    else if (dt > dtBest && t1 > t0) {
                        break   
                    }
                }
            }
            
            if (goodData) {
                //println "Matching VARS@${a.recordedDate} with EXPD@${goodData.navigationDate}"
                dataMap[a] = goodData
            }
            else {
                Logger.log(MergeAction.class, "No CTD data was found for the annotation at ${a.recordedDate}")
            }
        }
        return dataMap
    }
    
    /**
     * Function for coallate annotations with expd data by timecode using
     * the annotation.timecode and expdData.timecode as the 
     * coallation keys. Inputs are 2 lists. Returns a Map<Annotation, EXPDDatum>
     */
    static final Closure coallateByTimecode = { annotations, expdData ->
        
        Logger.log(MergeAction.class, "Merging ${annotations.size()} annotations by timecode")
        // Filter out any objects with null coallation keys
        annotations = annotations.findAll { it.timecode != null }
        expdData = expdData.findAll { it.timecode != null }
        
        // Sort by the coallation keys
        annotations = annotations.sort { it.timecode }
        expdData = expdData.sort { it.timecode }
        
        def dataMap = [:]
        def offset = (new org.mbari.movie.Timecode("00:00:${String.format('%02d',org.mbari.expd.Constants.OFFSET_SECONDS)}:00")).frames
        int i = 0
        annotations.each { a ->
            //println "Examining annotation at ${a.timecode}"
            def t0 = a.timecode.frames
            def dtBest = offset
            def goodData = null
            for (row in i..<expdData.size()) {
                def e = expdData[row]   
                if (e.timecode) {
                    def t1 = e.timecode.frames
                    def dt = Math.abs(t0 - t1)
                    
                    /*if (dt < 10000) {
                        println "\t EXPD of ${e.timecode}"
                    }*/
                    
                    if (dt <= dtBest) {
                        //println "dt = ${dt} frames for ${a.timecode} and ${e.timecode}"
                        dtBest = dt
                        i = row
                        goodData = e
                    }
                    else if (dt > dtBest && t1 > t0) {
                        break
                    }
                }
            }
            
            if (goodData) {
                //println "Matching VARS@${a.recordedDate} with EXPD@${goodData.navigationDate}"
                dataMap[a] = goodData
            }
            else {
                Logger.log(MergeAction.class, "No CTD data was found for the annotation at ${a.timecode}")
            }
        }
        return dataMap  
    }
    
    MergeAction(String platform, seqNumber, boolean useHD = false) {
        this.platform = platform
        this.seqNumber = seqNumber
        this.useHD = useHD
        
        expdMergeStatus = new EXPDMergeStatus(mergeDate: new Date(),
                statusMessage: "Success", merged: 0)
    }
    
    def doAction(flag) {
        fetchData()
        merge(flag)
        boolean overwrite = (flag == FLAG_PESSIMISTIC || flag == FLAG_PRAGMATIC)
        update(overwrite)
    }
    
    def fetchData() {
        
        // reset data map
        data.clear()
        
        // Fetch EXPD data
        Logger.log(this.class, "----- Fetching ship data from EXPD " +
                "for ${platform} #${seqNumber} -----")
        expdData = AnnotationDAO.fetchExpdData(platform, seqNumber, useHD)
        
        /*
         * HACK!! If using HD change all the EXPDData timecodes to be the
         * hdTimecode
         */
        if (useHD && expdData) {
            expdData.each {
                it.timecode = it.hdTimecode
            }
        }
        
        if (!expdData?.size()) {
            statusMessages << "No expedition data found in EXPD"   
        }
        
        // Fetch the annotations
        Logger.log(this.class, "----- Fetching annotations from VARS " +
                "for ${platform} #${seqNumber} -----")
        annotations = AnnotationDAO.fetchByDive(platform, seqNumber, useHD)
        if (!annotations?.size()) {
            statusMessages << "No annotations found in VARS"  
        }
        else {
            // Set the primary key for the expdMergeStatus
            expdMergeStatus.videoArchiveSetID_FK = AnnotationDAO.findVideoArchiveSetId(annotations[0].videoFrameID)   
        }
    }
    
    def merge(flag = FLAG_CONSERVATIVE) {
        
        statusMessages << "Using ${FLAGS[flag]} merge"
        
        if (expdData == null) {
            fetchData()   
        }
        
        data.clear()
        if (expdData.size()) {
            Logger.log(this.class, "----- Merging VARS annotations with EXPD information " +
                    "for ${platform} #${seqNumber} using a ${FLAGS[flag]} merge -----")
            switch (flag) {
                case (FLAG_OPTIMISTIC):
                    mergeOptimistic()
                    break
                case (FLAG_PESSIMISTIC):
                    mergePessimistic()
                    break
                case (FLAG_PRAGMATIC):
                    mergePragmatic()
                    break
                default:
                    mergeConservative()
            }
            expdMergeStatus.merged = 1
        }
        else {
            Logger.log(this.class, "No CTD data is available for ${platform} #${seqNumber}. " +
                    "Unable to merge with Expedition data!")
            expdMergeStatus.merged = 0   
        }

    }
    
    /**
     * Merge annotations by Date, then any that weren't merged are merged
     * by timecode.
     */
    private mergeConservative() {
        
        def annotationBuffer = new ArrayList()
        annotationBuffer.addAll(annotations)
        
        // Merge annotations by Date
        def dataMap = coallateByDate(annotationBuffer, expdData)
        
        // Merge unmatched annotations by timecode
        def keys = dataMap.keySet()
        if (keys && keys.size() > 0) {
            annotationBuffer.removeAll(keys)
        }
        
        dataMap.putAll(coallateByTimecode(annotationBuffer, expdData))
        data.putAll(dataMap)   
        expdMergeStatus.dateSource = "VARS"
    }
    
    private mergePragmatic() {
        
        /*
         * Merge annotations with bogus dates by timecode
         */
        def dateBounds = EXPDDAO.findDateBounds(platform, seqNumber)
        def annotationBuffer = annotations.findAll { annotation ->
            def d = annotation.recordedDate
            return !d || d.before(dateBounds[0]) || d.after(dateBounds[1])
        }
        def dataMap = coallateByTimecode(annotationBuffer, expdData)
        // Change bogus dates to ones found in EXPD
        dataMap.each {annotation, expdDatum ->
             annotation.recordedDate = expdDatum?.cameraDate
        }
        if (dataMap.size()) {
            statusMessages << "Fixed ${dataMap.size()} annotation dates"
            expdMergeStatus.dateSource = "Both"
        }
        else {
            expdMergeStatus.dateSource = "VARS"   
        }
        
        // Merge remaing annotations by Date
        annotationBuffer = new ArrayList()
        annotationBuffer.addAll(annotations)
        def keys = dataMap.keySet()
        if (keys && keys.size() > 0) {
            annotationBuffer.removeAll(keys)
        }
        dataMap.putAll(coallateByDate(annotationBuffer, expdData))
        
        data.putAll(dataMap)
    }
    
    private mergeOptimistic() {
        data.putAll(coallateByDate(annotations, expdData))
        expdMergeStatus.dateSource = "VARS"
    }
    
    private mergePessimistic() {
        // Merge by timecode. Update all dates in the annotations
        def dataMap = coallateByTimecode(annotations, expdData)
        // Change dates to ones found in EXPD
        dataMap.each {annotation, expdDatum ->
             annotation.recordedDate = expdDatum?.cameraDate
        }
        
        // Set all unmerged annotations to have a null date
        def annotationBuffer = new ArrayList()
        annotationBuffer.addAll(annotations)
        def keys = dataMap.keySet()
        if (keys && keys.size() > 0) {
            annotationBuffer.removeAll(keys)
        }
        annotationBuffer.each {
            it.recordedDate = null   
            dataMap.put(it, blankDatum)
        }
        
        data.putAll(dataMap)
        expdMergeStatus.dateSource = "EXPD"
    }
    
    def update(boolean overwrite) {
        data.each { annotation, expdDatum ->
            AnnotationDAO.updateAnnotation(annotation, expdDatum, overwrite)
        }
        updateMergeStatus(overwrite)
    }
    
    
    private updateMergeStatus(boolean overwrite) {
        if (statusMessages) {
            expdMergeStatus.statusMessage = statusMessages.join("; ")
        }
        else {
            expdMergeStatus.statusMessage = "Status is Unknown" 
        }
        expdMergeStatus.videoFrameCount = AnnotationDAO.findVideoFrameCount(expdMergeStatus.videoArchiveSetID_FK)
        // Check to see if the nav is edited.
        expdMergeStatus.navigationEdited = 0
        for (expdDatum in data.values()) {
            if (expdDatum.edited) {
                expdMergeStatus.navigationEdited = 1
                break
            }
        }
        if (expdMergeStatus.videoArchiveSetID_FK) {
            EXPDMergeStatusDAO.update(expdMergeStatus)
        }
        else {
            Logger.log(this.class, "Unable to update EXPDMergeStatus")   
        }
    }
    
    /**
     * Dump the merged data 
     */
    def dumpData(File target) {
        if (!expdData) {
            target << "NO DATA. You should run a merge first!"
        } else {
            use (PropertyCategory) {
                target << "${data.keySet().iterator().next().toKeyString('\t')}${data.values().iterator().next().toKeyString('\t')}\n"  
                data.each {key, value->
                    target << "${key.toValueString('\t')}${value.toValueString('\t')}\n"
                }
            }
        }
    }

}