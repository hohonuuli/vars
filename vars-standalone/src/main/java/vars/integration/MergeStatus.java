package vars.integration;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Dec 26, 2009
 * Time: 2:53:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class MergeStatus {

    private Long videoArchiveSetID;
    private Date mergeDate;
    private Integer navigationEdited;
    private String statusMessage;
    private Long videoFrameCount;
    private Integer merged;
    private String dateSource;

    public Long getVideoArchiveSetID() {
        return videoArchiveSetID;
    }

    public void setVideoArchiveSetID(Long videoArchiveSetID) {
        this.videoArchiveSetID = videoArchiveSetID;
    }

    public Date getMergeDate() {
        return mergeDate;
    }

    public void setMergeDate(Date mergeDate) {
        this.mergeDate = mergeDate;
    }

    public Integer getNavigationEdited() {
        return navigationEdited;
    }

    public void setNavigationEdited(Integer navigationEdited) {
        this.navigationEdited = navigationEdited;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Long getVideoFrameCount() {
        return videoFrameCount;
    }

    public void setVideoFrameCount(Long videoFrameCount) {
        this.videoFrameCount = videoFrameCount;
    }

    public Integer getMerged() {
        return merged;
    }

    public void setMerged(Integer merged) {
        this.merged = merged;
    }

    public String getDateSource() {
        return dateSource;
    }

    public void setDateSource(String dateSource) {
        this.dateSource = dateSource;
    }
}
