SELECT 
  MAX(expd.MergeDate) AS LastMergeDate,
  va.videoArchiveName
FROM
  CameraPlatformDeployment AS cpd RIGHT JOIN 
  VideoArchiveSet AS vas ON cpd.VideoArchiveSetID_FK = vas.id LEFT JOIN 
  VideoArchive AS va ON va.VideoArchiveSetID_FK = vas.id LEFT JOIN 
  EXPDMergeHistory AS expd ON expd.VideoArchiveSetID_FK = vas.id 
WHERE
  (cpd.SeqNumber = 124) AND
  (vas.PlatformName ='Tiburon') 
GROUP By
  va.videoArchiveName
