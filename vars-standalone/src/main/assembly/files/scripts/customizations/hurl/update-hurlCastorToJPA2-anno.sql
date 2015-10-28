/*

This script can be run after you've executed the vars.migrations.CastorToJPAMigration.groovy 
class. It drops the uneeded fields and adds indexs and constraints

 */

-- VideoArchiveSet ----------------------------------------------------

-- VideoArchive -------------------------------------------------------

CREATE INDEX idx_VideoArchive_FK1
    ON VideoArchive(VideoArchiveSetID_FK)
GO

CREATE INDEX idx_VideoArchive_name
    ON VideoArchive(videoArchiveName)
GO

ALTER TABLE VIDEOARCHIVE
    ALTER VIDEOARCHIVENAME NOT NULL
GO

ALTER TABLE VideoArchive 
    ADD CONSTRAINT uc_VideoArchiveName 
    UNIQUE (VideoArchiveName)
GO


-- VideoFrame ---------------------------------------------------------
ALTER TABLE VideoFrame
    DROP COLUMN CameraDataID_FK
GO

ALTER TABLE VideoFrame
    DROP COLUMN PhysicalDataID_FK
GO

CREATE INDEX idx_VideoFrame_FK1
    ON VideoFrame(VideoArchiveID_FK)
GO


-- Observation --------------------------------------------------------
CREATE INDEX idx_Observation_FK1
    ON Observation(VideoFrameID_FK)
GO

CREATE INDEX idx_Observation_ConceptName
    ON Observation(ConceptName)
GO


-- CameraPlatformDeployment -------------------------------------------
CREATE INDEX idx_CameraDeployment_FK1
    ON CameraPlatformDeployment(VideoArchiveSetID_FK)
GO


-- PhysicalData -------------------------------------------------------
DELETE FROM PhysicalData WHERE VIDEOFRAMEID_FK IS NULL
GO

ALTER TABLE PHYSICALDATA
    ALTER VIDEOFRAMEID_FK NOT NULL
GO

CREATE INDEX idx_PhysicalData_FK1
    ON PhysicalData(VideoFrameID_FK)
GO

-- Enforce the 1:1 relationship between VideoFrame and CameraData tables. TODO dupliate keys exist
ALTER TABLE PhysicalData
    ADD CONSTRAINT uc_PhysicalData_FK1
    UNIQUE (VideoFrameID_FK)
GO


-- CameraData ---------------------------------------------------------
DELETE FROM CAMERADATA WHERE VIDEOFRAMEID_FK IS NULL
GO

ALTER TABLE CAMERADATA
    ALTER VIDEOFRAMEID_FK NOT NULL
GO

CREATE INDEX idx_CameraData_FK1
    ON CameraData(VideoFrameID_FK)
GO

-- Enforce the 1:1 relationship between VideoFrame and CameraData tables
ALTER TABLE CameraData
    ADD CONSTRAINT uc_VideoFrameID_FK UNIQUE (VideoFrameID_FK)
GO


-- Association --------------------------------------------------------
CREATE INDEX idx_Association_FK1
    ON Association(ObservationID_FK)
GO

CREATE INDEX idx_Association_ToConcept
    ON Association(ToConcept)
GO


-- Annotations View ---------------------------------------------------

CREATE VIEW Annotations
AS 
SELECT  
    obs.ObservationDTG AS ObservationDate,
    obs.Observer,
    obs.ConceptName,
    obs.Notes,
    obs.X AS XPixelInImage,
    obs.Y AS yPixelInImage,
    vf.TapeTimeCode,
    vf.RecordedDtg AS RecordedDate ,
    va.videoArchiveName,
    vas.TrackingNumber,
    vas.ShipName,
    vas.PlatformName AS RovName,
    vas.FormatCode AS AnnotationMode,
    cpd.SeqNumber AS DiveNumber,
    cpd.ChiefScientist,
    cd.NAME AS CameraName,
    cd.Direction AS CameraDirection,
    cd.Zoom,
    cd.Focus,
    cd.Iris,
    cd.FieldWidth,
    cd.StillImageURL AS Image,
    cd.X AS CameraX,
    cd.Y AS CameraY,
    cd.Z AS CameraZ,
    cd.Pitch AS CameraPitchRadians,
    cd.Roll AS CameraRollRadians,
    cd.Heading AS CameraHeadingRadians,
    cd.XYUnits AS CameraXYUnits,
    cd.ZUnits AS CameraZUnits,
    cd.VIEWWIDTH as CameraViewWidth,
    cd.ViewHeight AS CameraViewHeight,
    cd.ViewUnits AS CameraViewUnits,
    pd.DEPTH,
    pd.Temperature,
    pd.Salinity,
    pd.Oxygen,
    pd.Light,
    pd.Latitude,
    pd.Longitude,
    pd.Altitude,
    obs.id AS ObservationID_FK,
    ass.id AS AssociationID_FK,
    ass.LinkName,
    ass.ToConcept,
    ass.LinkValue,
    ass.LinkName || ' | ' || ass.ToConcept || ' | ' || ass.LinkValue AS Associations,
    vf.HDTimeCode AS AlternateTimecode,
    vf.id AS VideoFrameID_FK,
    pd.id AS PhysicalDataID_FK,
    cd.id AS CameraDataID_FK,
    va.id AS VideoArchiveID_FK,
    vas.id AS VideoArchiveSetID_FK 
FROM 
    Association ass 
    RIGHT JOIN Observation obs ON ass.ObservationID_FK = obs.id
    RIGHT JOIN VideoFrame vf ON obs.VideoFrameID_FK = vf.id 
    RIGHT JOIN VideoArchive va ON vf.VideoArchiveID_FK = va.id
    RIGHT JOIN VideoArchiveSet vas ON va.VideoArchiveSetID_FK = vas.id
    LEFT JOIN CameraPlatformDeployment cpd ON cpd.VideoArchiveSetID_FK = vas.id  
    LEFT JOIN CameraData cd ON cd.VideoFrameID_FK = vf.id 
    LEFT JOIN PhysicalData pd ON pd.VideoFrameID_FK = vf.id
WHERE
    obs.ConceptName IS NOT NULL
GO

