SELECT DISTINCT obs.id
FROM 
  Observation AS obs LEFT OUTER JOIN 
  VideoFrame AS vf ON vf.id = obs.VideoFrameID_FK LEFT OUTER JOIN
  CameraData AS cd ON vf.id = cd.VideoFrameID_FK LEFT OUTER JOIN
  VideoArchive AS va ON va.id = vf.VideoArchiveID_FK LEFT OUTER JOIN
  VideoArchiveSet AS vas ON vas.id = va.VideoArchiveSetID_FK LEFT JOIN
  CameraPlatformDeployment AS cpd ON cpd.VideoArchiveSetID_FK = vas.id
WHERE
  cd.Direction = 'transect' AND
  vf.RecordedDtg BETWEEN '1993-01-01 00:00:00' AND '2013-12-31 23:59:59' AND 
  cpd.ChiefScientist IN ('Bill Hamner',
    'Bruce Robison',
    'Caren Braby',
    'Drazen, Jeff',
    'George Matsumoto',
    'Haddock, Steve',
    'Hamner, Bill',
    'Henk-Jan Hoving',
    'Jay Hunt',
    'Jeff Drazen',
    'Karen Osborn',
    'Kevin Raskoff',
    'Kim Reisenbichler',
    'Larry Madin',
    'Madin, Larry',
    'Reisenbichler, Kim',
    'Rob Sherlock',
    'Robison, Bruce',
    'Rodgers, Kris',
    'Russ Hopcroft',
    'Sherlock, Rob',
    'Stephanie Bush',
    'Steve Haddock')