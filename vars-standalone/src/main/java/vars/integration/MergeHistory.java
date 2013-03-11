/*
 * @(#)MergeHistory.java   2013.03.11 at 11:04:08 PDT
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



package vars.integration;

import com.google.common.base.Preconditions;
import java.util.Date;

/**
 * This record represents a successful merge of a VideoArchiveSet with the corresponding data in
 * from an expedition database (position, ctd, etc.)
 * <pre>
 *     CREATE TABLE [dbo].[EXPDMergeHistory]  (
 *	[VideoArchiveSetID_FK]	bigint NOT NULL,
 	[MergeDate]           	datetime NOT NULL,
 	[IsNavigationEdited]  	smallint NOT NULL,
 	[StatusMessage]       	varchar(512) NULL,
 	[VideoFrameCount]     	int NOT NULL,
 	[DateSource]          	varchar(4) NULL,
 	[id]                  	bigint IDENTITY(1,1) NOT NULL,
 	[MergeType]           	varchar(25) NOT NULL,
 	[IsHD]                	smallint NULL,
 	CONSTRAINT [EXPDMergeHistory_PK] PRIMARY KEY NONCLUSTERED([id])
 )
 ON [PRIMARY]
 GO
 *
 * </pre>
 * @author Brian Schlining
 * @since 2013-03-06
 */
public class MergeHistory {

    private String dateSource;
    private Boolean hd;
    private final Long id;
    private Date mergeDate;
    private String mergeType;
    private Boolean navigationEdited;
    private String statusMessage;
    private Long videoArchiveSetID;
    private Integer videoFrameCount;

    /**
     * Constructs ...
     */
    public MergeHistory() {
        id = -1L;
    }

    /**
     * Constructs ...
     *
     * @param videoArchiveSetID
     * @param mergeDate
     * @param mergeType
     * @param navigationEdited
     * @param statusMessage
     * @param videoFrameCount
     * @param dateSource
     * @param hd
     */
    public MergeHistory(Long videoArchiveSetID, Date mergeDate, String mergeType, Boolean navigationEdited,
                        String statusMessage, Integer videoFrameCount, String dateSource, Boolean hd) {
        this(-1L, videoArchiveSetID, mergeDate, mergeType, navigationEdited, statusMessage, videoFrameCount,
             dateSource, hd);
    }

    /**
     * Constructs ...
     *
     * @param id
     * @param videoArchiveSetID
     * @param mergeDate
     * @param mergeType
     * @param navigationEdited
     * @param statusMessage
     * @param videoFrameCount
     * @param dateSource
     * @param hd
     */
    public MergeHistory(Long id, Long videoArchiveSetID, Date mergeDate, String mergeType, Boolean navigationEdited,
                        String statusMessage, Integer videoFrameCount, String dateSource, Boolean hd) {
        this.id = id;
        this.videoArchiveSetID = videoArchiveSetID;
        this.mergeDate = mergeDate;
        this.mergeType = mergeType;
        this.navigationEdited = navigationEdited;
        this.statusMessage = statusMessage;
        this.videoFrameCount = videoFrameCount;
        this.dateSource = dateSource;
        this.hd = hd;
    }

    /**
     * @return
     */
    public String getDateSource() {
        return dateSource;
    }

    /**
     * @return
     */
    public Boolean isHd() {
        return hd;
    }

    /**
     *
     * @return The primary key. -1 is returned if it has not been set yet.
     */
    public Long getId() {
        return id;
    }

    /**
     * @return
     */
    public Date getMergeDate() {
        return mergeDate;
    }

    /**
     * @return
     */
    public String getMergeType() {
        return mergeType;
    }

    /**
     * @return
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * @return
     */
    public Long getVideoArchiveSetID() {
        return videoArchiveSetID;
    }

    /**
     * @return
     */
    public Integer getVideoFrameCount() {
        return videoFrameCount;
    }

    /**
     * @return
     */
    public Boolean isNavigationEdited() {
        return navigationEdited;
    }

    /**
     *
     * @param dateSource
     */
    public void setDateSource(String dateSource) {
        this.dateSource = dateSource;
    }

    /**
     *
     * @param hd
     */
    public void setHd(Boolean hd) {
        this.hd = hd;
    }

    /**
     *
     * @param mergeDate
     */
    public void setMergeDate(Date mergeDate) {
        this.mergeDate = mergeDate;
    }

    /**
     *
     * @param mergeType
     */
    public void setMergeType(String mergeType) {
        this.mergeType = mergeType;
    }

    /**
     *
     * @param navigationEdited
     */
    public void setNavigationEdited(Boolean navigationEdited) {
        this.navigationEdited = navigationEdited;
    }

    /**
     *
     * @param statusMessage
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     *
     * @param videoArchiveSetID
     */
    public void setVideoArchiveSetID(Long videoArchiveSetID) {
        this.videoArchiveSetID = videoArchiveSetID;
    }

    /**
     *
     * @param videoFrameCount
     */
    public void setVideoFrameCount(Integer videoFrameCount) {
        this.videoFrameCount = videoFrameCount;
    }
}
