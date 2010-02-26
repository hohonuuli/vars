/*

This script can be run after you've executed the vars.migrations.CastorToJPAMigration.groovy 
class. It drops the uneeded fields and adds indexs and constraints

 */

-- VideoArchiveSet ----------------------------------------------------
ALTER TABLE [dbo].[VideoArchiveSet]
	DROP COLUMN [rowguid]
GO

ALTER TABLE [dbo].[VideoArchiveSet]
	ADD CONSTRAINT [VideoArchiveSet_PK]
	PRIMARY KEY ([id])
GO

CREATE INDEX [idx_VideoArchiveSet_LUT]
	ON [dbo].[VideoArchiveSet]([LAST_UPDATED_TIME])
GO


-- VideoArchive -------------------------------------------------------
ALTER TABLE [dbo].[VideoArchive]
	DROP COLUMN [rowguid]
GO

ALTER TABLE [dbo].[VideoArchive] 
	CONSTRAINT [VideoArchive_PK]
	PRIMARY KEY ([id])
GO

ALTER TABLE [dbo].[VideoArchive]
    ADD CONSTRAINT [VideoArchive_FK1]
	FOREIGN KEY([VideoArchiveSetID_FK])
	REFERENCES [dbo].[VideoArchiveSet]([id])
	ON DELETE NO ACTION 
	ON UPDATE NO ACTION
GO

CREATE INDEX [idx_VideoArchive_FK1]
    ON [dbo].[VideoArchive]([VideoArchiveSetID_FK])
GO

CREATE INDEX [idx_VideoArchive_name]
	ON [dbo].[VideoArchive]([videoArchiveName])
GO

CREATE INDEX [idx_VideoArchive_LUT]
	ON [dbo].[VideoArchive]([LAST_UPDATED_TIME])
GO

ALTER TABLE VideoArchive 
    ADD CONSTRAINT uc_VideoArchiveName 
    UNIQUE (VideoArchiveName)
GO


-- VideoFrame ---------------------------------------------------------
ALTER TABLE [dbo].[VideoFrame]
	DROP COLUMN [CameraDataID_FK], [PhysicalDataID_FK], [rowguid]
GO

ALTER TABLE [dbo].[VideoFrame]
	CONSTRAINT [VideoFrame_PK]
	PRIMARY KEY ([id])
GO

ALTER TABLE [dbo].[VideoFrame]
    ADD CONSTRAINT [VideoFrame_FK1]
	FOREIGN KEY([VideoArchiveID_FK])
	REFERENCES [dbo].[VideoArchive]([id])
	ON DELETE NO ACTION 
	ON UPDATE NO ACTION
GO

CREATE INDEX [idx_VideoFrame_LUT]
	ON [dbo].[VideoFrame]([LAST_UPDATED_TIME])
GO

CREATE INDEX [idx_VideoFrame_FK1]
    ON [dbo].[VideoFrame]([VideoArchiveID_FK])
GO


-- Observation --------------------------------------------------------
ALTER TABLE [dbo].[Observation]
	DROP COLUMN [rowguid]
GO

ALTER TABLE [dbo].[Observation]
	CONSTRAINT [Observation_PK]
	PRIMARY KEY ([id])
GO

ALTER TABLE [dbo].[Observation]
    ADD CONSTRAINT [Observation_FK1]
	FOREIGN KEY([VideoFrameID_FK])
	REFERENCES [dbo].[VideoFrame]([id])
	ON DELETE NO ACTION 
	ON UPDATE NO ACTION
GO

CREATE INDEX [idx_Observation_LUT]
	ON [dbo].[Observation]([LAST_UPDATED_TIME])
GO

CREATE INDEX [idx_Observation_FK1]
	ON [dbo].[Observation]([VideoFrameID_FK])
GO

CREATE INDEX [idx_Observation_ConceptName]
	ON [dbo].[Observation]([ConceptName])
GO


-- CameraPlatformDeployment -------------------------------------------
ALTER TABLE [dbo].[CameraPlatformDeployment]
	DROP COLUMN [rowguid]
GO

ALTER TABLE [dbo].[CameraPlatformDeployment]
    ADD CONSTRAINT [CameraDeployment_FK1]
	FOREIGN KEY([VideoArchiveSetID_FK])
	REFERENCES [dbo].[VideoArchiveSet]([id])
	ON DELETE NO ACTION 
	ON UPDATE NO ACTION
GO

ALTER TABLE [dbo].[CameraPlatformDeployment]
    ADD CONSTRAINT [CameraDeployment_PK]
	PRIMARY KEY NONCLUSTERED ([id])
GO

CREATE INDEX [idx_CameraPlatformDeployment_LUT]
	ON [dbo].[CameraPlatformDeployment]([LAST_UPDATED_TIME])
GO

CREATE INDEX [idx_CameraDeployment_FK1]
    ON [dbo].[CameraPlatformDeployment]([VideoArchiveSetID_FK])
GO


-- PhysicalData -------------------------------------------------------
ALTER TABLE [dbo].[PhysicalData]
	DROP COLUMN [rowguid]
GO

ALTER TABLE [dbo].[PhysicalData]
    ADD CONSTRAINT [PhysicalData_PK]
	PRIMARY KEY NONCLUSTERED ([id])
GO

ALTER TABLE [dbo].[PhysicalData]
    ADD CONSTRAINT [PhysicalData_FK1]
	FOREIGN KEY([VideoFrameID_FK])
	REFERENCES [dbo].[VideoFrame]([id])
	ON DELETE NO ACTION 
	ON UPDATE NO ACTION
GO

CREATE INDEX [idx_PhysicalData_LUT]
	ON [dbo].[PhysicalData]([LAST_UPDATED_TIME])
GO

CREATE INDEX [idx_PhysicalData_FK1]
    ON [dbo].[PhysicalData]([VideoFrameID_FK])
GO

-- Enforce the 1:1 relationship between VideoFrame and CameraData tables. TODO dupliate keys exist
ALTER TABLE [dbo].[PhysicalData]
	ADD CONSTRAINT [uc_PhysicalData_FK1]
	UNIQUE CLUSTERED ([VideoFrameID_FK])
GO


-- CameraData ---------------------------------------------------------
ALTER TABLE [dbo].[CameraData]
	DROP COLUMN [rowguid]
GO

ALTER TABLE [dbo].[CameraData]
    ADD CONSTRAINT [CameraData_PK]
	PRIMARY KEY ([id])
GO

ALTER TABLE [dbo].[CameraData]
    ADD CONSTRAINT [CameraData_FK1]
	FOREIGN KEY([VideoFrameID_FK])
	REFERENCES [dbo].[VideoFrame]([id])
	ON DELETE NO ACTION 
	ON UPDATE NO ACTION
GO

CREATE INDEX [idx_CameraData_FK1]
    ON [dbo].[CameraData]([VideoFrameID_FK])
GO

CREATE INDEX [idx_CameraData_LUT]
    ON [dbo].[CameraData]([LAST_UPDATED_TIME])
GO

-- Enforce the 1:1 relationship between VideoFrame and CameraData tables
ALTER TABLE CameraData
    ADD CONSTRAINT uc_VideoFrameID_FK UNIQUE (VideoFrameID_FK)
GO


-- Association --------------------------------------------------------
ALTER TABLE [dbo].[Association]
	DROP COLUMN [rowguid]
GO

ALTER TABLE [dbo].[Association]
    ADD CONSTRAINT [Association_PK]
	PRIMARY KEY NONCLUSTERED ([id])
GO

ALTER TABLE [dbo].[Association]
    ADD CONSTRAINT [Association_FK1]
	FOREIGN KEY([ObservationID_FK])
	REFERENCES [dbo].[Observation]([id])
	ON DELETE NO ACTION 
	ON UPDATE NO ACTION
GO

CREATE INDEX [idx_Association_LUT]
	ON [dbo].[Association]([LAST_UPDATED_TIME])
GO

CREATE INDEX [idx_Association_FK1]
	ON [dbo].[Association]([ObservationID_FK])
GO

CREATE INDEX [idx_Association_ToConcept]
	ON [dbo].[Association]([ToConcept])
GO


-- EXPDMergeStatus ----------------------------------------------------
ALTER TABLE [dbo].[EXPDMergeStatus]
    ADD CONSTRAINT [MergeStatus_FK1]
	FOREIGN KEY([VideoArchiveSetID_FK])
	REFERENCES [dbo].[VideoArchiveSet]([id])
	ON DELETE NO ACTION 
	ON UPDATE NO ACTION
GO

