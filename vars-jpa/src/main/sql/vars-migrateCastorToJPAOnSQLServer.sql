ALTER TABLE Association
	ADD LAST_UPDATED_TIME DATETIME NULL
GO

ALTER TABLE CameraData
    ADD LogDTG DATETIME NULL
GO

ALTER TABLE CameraData
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

ALTER TABLE CameraPlatformDeployment
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

ALTER TABLE Concept
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

ALTER TABLE ConceptDelegate
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

ALTER TABLE ConceptName
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

ALTER TABLE History
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

ALTER TABLE LinkRealization
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

ALTER TABLE LinkTemplate
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

ALTER TABLE Media
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

ALTER TABLE Observation
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

ALTER TABLE PhysicalData
    ADD LogDTG DATETIME NULL
GO

ALTER TABLE PhysicalData
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

ALTER TABLE USAGE
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

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

ALTER TABLE VideoArchive
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

ALTER TABLE VideoArchiveSet
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

ALTER TABLE VideoFrame
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

ALTER TABLE ConceptDelegate
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

