WITH 
video_tapes AS
  (
    SELECT 
      vas.id,
      va.videoArchiveName AS TapeName,
      vas.LAST_UPDATED_TIME,
      vas.FormatCode
    FROM
      CameraPlatformDeployment AS cpd RIGHT JOIN 
      VideoArchiveSet AS vas ON cpd.VideoArchiveSetID_FK = vas.id  LEFT JOIN 
      VideoArchive AS va ON va.VideoArchiveSetID_FK = vas.id
    WHERE
      cpd.SeqNumber = 451 AND
      vas.PlatformName = 'Doc Ricketts'
  ),
merge_status AS
  (
    SELECT TOP 1
      vt.id,
      ms.MergeDate
    FROM 
      EXPDMergeHistory AS ms JOIN
      video_tapes AS vt ON vt.id = ms.VideoArchiveSetID_FK
    ORDER BY
      ms.MergeDate DESC
  )
SELECT
  video_tapes.TapeName,
  merge_status.MergeDate,
  video_tapes.LAST_UPDATED_TIME,
  video_tapes.FormatCode
FROM
  video_tapes LEFT JOIN
  merge_status ON merge_status.id = video_tapes.id
ORDER BY
  TapeName
      

