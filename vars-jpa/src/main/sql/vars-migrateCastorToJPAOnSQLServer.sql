-- Artifact -----------------------------------------------------------
CREATE TABLE [dbo].[Artifact]  ( 
    [ConceptDelegateID_FK]	bigint NOT NULL,
    [GroupId]             	varchar(64) NOT NULL,
    [ArtifactId]          	varchar(256) NOT NULL,
    [Version]             	varchar(64) NOT NULL,
    [Classifier]          	varchar(64) NULL,
    [Description]         	varchar(2048) NULL,
    [MimeType]            	varchar(32) NULL,
    [Caption]             	varchar(1024) NULL,
    [Reference]           	varchar(1024) NOT NULL,
    [Credit]              	varchar(1024) NULL,
    [id]                  	bigint NOT NULL,
    [LAST_UPDATED_TIME]   	datetime NOT NULL,
    [CreationDate]        	datetime NULL,
    CONSTRAINT [Artifact_PK] PRIMARY KEY([id])
)
ON [PRIMARY]
GO
ALTER TABLE [dbo].[Artifact]
    ADD CONSTRAINT [Artifact_FK1]
	FOREIGN KEY([ConceptDelegateID_FK])
	REFERENCES [dbo].[ConceptDelegate]([id])
	ON DELETE NO ACTION 
	ON UPDATE NO ACTION 
GO
CREATE INDEX [idx_Artifact_LUT]
    ON [dbo].[Artifact]([LAST_UPDATED_TIME])
GO
CREATE UNIQUE INDEX [idx_Artifact_CK]
	ON [dbo].[Artifact]([ConceptDelegateID_FK], [GroupId], [ArtifactId], [Version], [Classifier])
GO
CREATE INDEX [idx_Artifact_FK1]
    ON [dbo].[Artifact]([ConceptDelegateID_FK])
GO


-- Association --------------------------------------------------------
ALTER TABLE Association
	ADD LAST_UPDATED_TIME DATETIME NULL
GO

-- CameraData ---------------------------------------------------------
ALTER TABLE CameraData
    ADD LogDTG DATETIME NULL
GO

ALTER TABLE CameraData
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

ALTER TABLE CameraData
    ADD X FLOAT
GO

ALTER TABLE CameraData
    ADD Y FLOAT
GO

ALTER TABLE CameraData
    ADD Z FLOAT
GO

ALTER TABLE CameraData
    ADD PITCH FLOAT
GO

ALTER TABLE CameraData
    ADD ROLL FLOAT
GO

ALTER TABLE CameraData
    ADD XYUNITS VARCHAR(50)
GO

ALTER TABLE CameraData
    ADD ZUNITS VARCHAR(50)
GO

ALTER TABLE CameraData
    ADD HEADING FLOAT
GO

ALTER TABLE CameraData
    ADD VIEWHEIGHT FLOAT
GO

ALTER TABLE CameraData
    ADD VIEWWIDTH FLOAT
GO

ALTER TABLE CameraData
    ADD VIEWUNITS VARCHAR(50)
GO

-- Enforce the 1:1 relationship between VideoFrame and CameraData tables: TODO Duplicate keys already exist
ALTER TABLE CameraData
    ADD CONSTRAINT uc_VideoFrameID_FK UNIQUE (VideoFrameID_FK)
GO

-- CameraPlatformDeployment -------------------------------------------
ALTER TABLE CameraPlatformDeployment
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

-- Concept ------------------------------------------------------------
ALTER TABLE Concept
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

UPDATE Concept
SET ParentConceptID_FK = NULL
WHERE ParentConceptID_FK = 0
GO

-- ConceptDelegate ----------------------------------------------------
ALTER TABLE ConceptDelegate
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

ALTER TABLE ConceptDelegate -- TODO this filas need to drop ConceptID_indx first then readd it after altering table
    ALTER COLUMN ConceptID_FK bigint NOT NULL
GO

ALTER TABLE ConceptDelegate -- TODO this fails
	ALTER UsageID_FK bigint NOT NULL
GO

-- Enforce the 1:1 relationship between Concept and ConceptDelegate tables
ALTER TABLE ConceptDelegate
    ADD CONSTRAINT uc_ConceptID_FK UNIQUE (ConceptID_FK)
GO

-- ConceptName --------------------------------------------------------
ALTER TABLE ConceptName
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

-- Allow case sensitive comparison. This is needed for the uc_ConceptName constraint to work correctly. 
-- TODO need to drop any indices on this column first 
ALTER TABLE ConceptName 
    ALTER COLUMN ConceptName VARCHAR(128) COLLATE SQL_Latin1_General_CP1_CS_AS
GO

ALTER TABLE ConceptName -- TODO fails unless case sensitive is added
    ADD CONSTRAINT uc_ConceptName UNIQUE (ConceptName)
GO

-- History ------------------------------------------------------------
ALTER TABLE History
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

-- Fix up History tabel to our liking
EXEC dbo.sp_rename N'[dbo].[History].ApprovalDTG' , N'ProcessedDTG', 'COLUMN'
GO

EXEC dbo.sp_rename N'[dbo].[History].ApproverName' , N'ProcessorName', 'COLUMN'
GO

EXEC dbo.sp_rename N'[dbo].[History].Rejected' , N'Approved', 'COLUMN'
GO

-- LinkRealization ----------------------------------------------------
ALTER TABLE LinkRealization
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

-- LinkTemplate -------------------------------------------------------
ALTER TABLE LinkTemplate
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

-- Media --------------------------------------------------------------
ALTER TABLE Media
    ADD LAST_UPDATED_TIME DATETIME NULL
GO
 -- Observation -------------------------------------------------------
ALTER TABLE Observation
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

ALTER TABLE Observation
    ADD X FLOAT NULL
GO

ALTER TABLE Observation
    ADD Y FLOAT NULL
GO

-- PhysicalData -------------------------------------------------------
ALTER TABLE PhysicalData
    ADD LogDTG DATETIME NULL
GO

-- Enforce the 1:1 relationship between VideoFrame and CameraData tables. TODO dupliate keys exist
ALTER TABLE PhysicalData
    ADD CONSTRAINT uc_VideoFrameID_FK UNIQUE (VideoFrameID_FK)
GO

ALTER TABLE PhysicalData
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

-- Usage --------------------------------------------------------------
ALTER TABLE USAGE
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

-- UserAccount --------------------------------------------------------
ALTER TABLE UserAccount
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

ALTER TABLE UserAccount
    ADD Affiliation varchar(50) NULL
GO
ALTER TABLE UserAccount
    ADD FirstName varchar(50) NULL

GO
ALTER TABLE UserAccount
    ADD LastName varchar(50) NULL
GO

ALTER TABLE UserAccount
    ADD Email varchar(50)
GO

-- VideoArchive -------------------------------------------------------
ALTER TABLE VideoArchive
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

ALTER TABLE VideoArchive -- TODO dupliate keys exist
    ADD CONSTRAINT uc_VideoArchiveName UNIQUE (VideoArchiveName)
GO

-- VideoArchiveSet ----------------------------------------------------
ALTER TABLE VideoArchiveSet
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

-- VideoFrame ---------------------------------------------------------
ALTER TABLE VideoFrame
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

-- --------------------------------------------------------------------
-- Reset ALL the LAST_UPDATED_TIME fields. This will take a very long time
UPDATE Association
	SET LAST_UPDATED_TIME='2009-01-01 00:00:00'
	WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE CameraData
    SET LAST_UPDATED_TIME='2009-01-01 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE CameraPlatformDeployment
    SET LAST_UPDATED_TIME='2009-01-01 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE Concept
    SET LAST_UPDATED_TIME='2009-01-01 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE ConceptDelegate
    SET LAST_UPDATED_TIME='2009-01-01 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE ConceptName
    SET LAST_UPDATED_TIME='2009-01-01 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE History
    SET LAST_UPDATED_TIME='2009-01-01 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE LinkRealization
    SET LAST_UPDATED_TIME='2009-01-01 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE LinkTemplate
    SET LAST_UPDATED_TIME='2009-01-01 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE Media
    SET LAST_UPDATED_TIME='2009-01-01 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE Observation
    SET LAST_UPDATED_TIME='2009-01-01 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO


UPDATE PhysicalData
    SET LAST_UPDATED_TIME='2009-01-01 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE USAGE
    SET LAST_UPDATED_TIME='2009-01-01 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE UserAccount
    SET LAST_UPDATED_TIME='2009-01-01 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE VideoArchive
    SET LAST_UPDATED_TIME='2009-01-01 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE VideoArchiveSet
    SET LAST_UPDATED_TIME='2009-01-01 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE VideoFrame
    SET LAST_UPDATED_TIME='2009-01-01 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

-- Update my testing password
UPDATE
    UserAccount
SET
    Password = 'Vzt4WKZEiP+mfOTrYxBcW7HjcC+erlB1'
WHERE
    UserName = 'brian'

-- Insert a starting value into the primary key table for artifact objects
INSERT INTO UniqueID VALUES ('Artifact' , 100)