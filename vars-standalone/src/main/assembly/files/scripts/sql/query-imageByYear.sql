SELECT
  the_year,
  COUNT(*) AS image_count
FROM
  (SELECT
     id,
     YEAR(RecordedDTG) as the_year
   FROM
     VideoFrame) AS ts LEFT JOIN
  CameraData AS cd ON cd.VideoFrameID_FK = ts.id

WHERE
  cd.StillImageURL IS NOT NULL
GROUP BY
  ts.the_year