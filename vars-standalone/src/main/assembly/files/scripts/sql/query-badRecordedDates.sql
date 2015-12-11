SELECT
    vas.PlatformName AS RovName,
    cpd.SeqNumber AS DiveNumber,
    COUNT(*) AS BadDates
FROM
    VideoArchiveSet as vas LEFT JOIN
    VideoArchive AS va ON va.VideoArchiveSetID_FK = vas.id LEFT JOIN
    VideoFrame AS vf ON vf.VideoArchiveID_FK = va.id LEFT JOIN
    CameraPlatformDeployment AS cpd ON cpd.VideoArchiveSetID_FK = vas.id
WHERE
    vf.RecordedDtg = '1970-01-01 00:00:00'
GROUP BY
    vas.PlatformName,
    cpd.SeqNumber
ORDER BY
    BadDates DESC