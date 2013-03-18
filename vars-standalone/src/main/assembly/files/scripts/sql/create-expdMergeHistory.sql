-- Tables used for tracking merges between VARS and EXPD
-- Also used by VARSQC

CREATE TABLE [dbo].[EXPDMergeHistory]  (
	[VideoArchiveSetID_FK]	bigint NOT NULL,
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


CREATE VIEW [dbo].[EXPDMergeHistorySummary]
AS
SELECT
    vas.id AS VideoArchiveSetID_FK,
    PlatformName,
    SeqNumber,
    ChiefScientist,
    StartDTG AS StartDate,
    EndDTG AS EndDate,
    MergeDate,
    DateSource,
    IsNavigationEdited,
    VideoFrameCount,
    StatusMessage,
    IsHD,
    MergeType
FROM
    EXPDMergeHistory AS ms LEFT OUTER JOIN
    VideoArchiveSet AS vas ON vas.id = ms.VideoArchiveSetID_FK LEFT OUTER JOIN
    CameraPlatformDeployment AS cpd ON cpd.VideoArchiveSetID_FK = vas.id
GO
