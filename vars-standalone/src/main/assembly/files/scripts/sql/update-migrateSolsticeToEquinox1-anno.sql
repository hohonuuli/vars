/*

This script should be run immediatly after copying the annotation 
database over. It updates the tables to have the fields expected
by VARS-REDUX

 */

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
    ADD Pitch FLOAT
GO

ALTER TABLE CameraData
    ADD Roll FLOAT
GO

ALTER TABLE CameraData
    ADD XYUnits VARCHAR(50)
GO

ALTER TABLE CameraData
    ADD ZUnits VARCHAR(50)
GO

ALTER TABLE CameraData
    ADD Heading FLOAT
GO

ALTER TABLE CameraData
    ADD ViewHeight FLOAT
GO

ALTER TABLE CameraData
    ADD ViewWidth FLOAT
GO

ALTER TABLE CameraData
    ADD ViewUnits VARCHAR(50)
GO

-- CameraPlatformDeployment -------------------------------------------
ALTER TABLE CameraPlatformDeployment
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


ALTER TABLE PhysicalData
    ADD LAST_UPDATED_TIME DATETIME NULL
GO

-- VideoArchive -------------------------------------------------------
ALTER TABLE VideoArchive
    ADD LAST_UPDATED_TIME DATETIME NULL
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

UPDATE Observation
    SET LAST_UPDATED_TIME='2009-01-01 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE PhysicalData
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
