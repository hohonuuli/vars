/*

This script should be run immediatly after copying the annotation 
database over. It updates the tables to have the fields expected
by VARS-REDUX

 */

-- Association --------------------------------------------------------
ALTER TABLE ASSOCIATION
    ALTER LINKNAME SET DATA TYPE VARCHAR(128)
GO

ALTER TABLE ASSOCIATION
    ALTER LINKVALUE SET DATA TYPE VARCHAR(1024)
GO

ALTER TABLE Association
    ADD LAST_UPDATED_TIME TIMESTAMP
GO

-- CameraData ---------------------------------------------------------
ALTER TABLE CameraData
    ADD LogDTG TIMESTAMP
GO

ALTER TABLE CameraData
    ADD LAST_UPDATED_TIME TIMESTAMP
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
    ADD LAST_UPDATED_TIME TIMESTAMP
GO

 -- Observation -------------------------------------------------------
ALTER TABLE Observation
    ADD LAST_UPDATED_TIME TIMESTAMP
GO

ALTER TABLE Observation
    ADD X FLOAT
GO

ALTER TABLE Observation
    ADD Y FLOAT
GO

-- PhysicalData -------------------------------------------------------
ALTER TABLE PhysicalData
    ADD LogDTG TIMESTAMP
GO

ALTER TABLE PhysicalData
    ADD Altitude FLOAT
GO

ALTER TABLE PhysicalData
    ADD LAST_UPDATED_TIME TIMESTAMP
GO

-- VideoArchive -------------------------------------------------------
ALTER TABLE VideoArchive
    ADD LAST_UPDATED_TIME TIMESTAMP
GO


-- VideoArchiveSet ----------------------------------------------------
ALTER TABLE VideoArchiveSet
    ADD LAST_UPDATED_TIME TIMESTAMP
GO

-- VideoFrame ---------------------------------------------------------
ALTER TABLE VideoFrame
    ADD LAST_UPDATED_TIME TIMESTAMP
GO

-- --------------------------------------------------------------------
-- Reset ALL the LAST_UPDATED_TIME fields. This will take a very long time
UPDATE Association
    SET LAST_UPDATED_TIME='2015-10-26 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE CameraData
    SET LAST_UPDATED_TIME='2015-10-26 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE CameraPlatformDeployment
    SET LAST_UPDATED_TIME='2015-10-26 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE Observation
    SET LAST_UPDATED_TIME='2015-10-26 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE PhysicalData
    SET LAST_UPDATED_TIME='2015-10-26 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE VideoArchive
    SET LAST_UPDATED_TIME='2015-10-26 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE VideoArchiveSet
    SET LAST_UPDATED_TIME='2015-10-26 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

UPDATE VideoFrame
    SET LAST_UPDATED_TIME='2015-10-26 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

-- --------------------------------------------------------------------
-- Add INDEX to ALL LAST_UPDATED_TIME fields. 
CREATE INDEX IDX_ASSOCIATION_LUT
    ON ASSOCIATION(LAST_UPDATED_TIME)
GO

CREATE INDEX IDX_CAMERADATA_LUT
    ON CAMERADATA(LAST_UPDATED_TIME)
GO

CREATE INDEX IDX_CPD_LUT
    ON CAMERAPLATFORMDEPLOYMENT(LAST_UPDATED_TIME)
GO

CREATE INDEX IDX_OBSERVATION_LUT
    ON OBSERVATION(LAST_UPDATED_TIME)
GO

CREATE INDEX IDX_PHYSICALDATA_LUT
    ON PHYSICALDATA(LAST_UPDATED_TIME)
GO

CREATE INDEX IDX_VIDEOARCHIVE_LUT
    ON VIDEOARCHIVE(LAST_UPDATED_TIME)
GO

CREATE INDEX IDX_VIDEOARCHIVESET_LUT
    ON VIDEOARCHIVESET(LAST_UPDATED_TIME)
GO

CREATE INDEX IDX_VIDEOFRAME_LUT
    ON VIDEOFRAME(LAST_UPDATED_TIME)
GO

-- --------------------------------------------------------------------
-- ADD INDEX TO OTHER FIELDS
CREATE INDEX IDX_VIDEOFRAME_RECORDEDDTG
    ON VIDEOFRAME(RECORDEDDTG)
GO

CREATE INDEX IDX_VIDEOFRAME_TIMECODE
    ON VIDEOFRAME(TAPETIMECODE)
GO
