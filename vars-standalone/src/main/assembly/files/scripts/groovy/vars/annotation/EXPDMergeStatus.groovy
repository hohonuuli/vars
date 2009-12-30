package vars.annotation

class EXPDMergeStatus {
    
    Long videoArchiveSetID_FK
    Date mergeDate = new Date()
    Integer navigationEdited = 0
    String statusMessage
    Long videoFrameCount
    Integer merged = 0
    def dateSource
    
}