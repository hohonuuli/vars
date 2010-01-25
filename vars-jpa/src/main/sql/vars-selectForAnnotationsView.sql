SELECT TOP 100 PERCENT 
    obs.ObservationDTG AS ObservationDate,
    obs.Observer,
    obs.ConceptName,
    obs.Notes,
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
    ass.LinkName + ' | ' + ass.ToConcept + ' | ' + ass.LinkValue AS Associations,
    vf.HDTimeCode AS HighdefTimeCode,
    expd.IsNavigationEdited,
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
        LEFT OUTER JOIN EXPDMergeStatus expd 
        ON expd.VideoArchiveSetID_FK = vas.id