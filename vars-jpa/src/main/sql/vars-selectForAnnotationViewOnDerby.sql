SELECT 
    obs.ObservationDTG AS ObservationDate,
    obs.Observer,
    obs.ConceptName,
    obs.Notes,
    obs.X AS XPixelInImage,
    obs.Y AS YPixelInImage,
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
    obs.id AS ObservationID_FK,
    ass.id AS AssociationID_FK,
    ass.LinkName,
    ass.ToConcept,
    ass.LinkValue,
    ass.LinkName || ' | ' || ass.ToConcept || ' | ' || ass.LinkValue AS Associations,
    vf.HDTimeCode AS HighdefTimeCode,
    vf.id AS VideoFrameID_FK,
    pd.id AS PhysicalDataID_FK,
    cd.id AS CameraDataID_FK,
    va.id AS VideoArchiveID_FK,
    vas.id AS VideoArchiveSetID_FK 
FROM 
    VideoArchiveSet vas 
        LEFT OUTER JOIN CameraPlatformDeployment cpd 
        ON cpd.VideoArchiveSetID_FK = vas.id 
        LEFT OUTER JOIN VideoArchive va 
        ON vas.id = va.VideoArchiveSetID_FK 
        LEFT OUTER JOIN VideoFrame vf 
        ON va.id = vf.VideoArchiveID_FK 
        LEFT OUTER JOIN CameraData cd 
        ON cd.VideoFrameID_FK = vf.id 
        LEFT OUTER JOIN PhysicalData pd 
        ON pd.VideoFrameID_FK = vf.id 
        LEFT OUTER JOIN Observation obs 
        ON obs.VideoFrameID_FK = vf.id 
        LEFT OUTER JOIN Association ass 
        ON ass.ObservationID_FK = obs.id