DELETE FROM
  Observation
WHERE
  id IN (
    SELECT DISTINCT
 ObservationID_FK
    FROM
 Annotations
    WHERE (
      (
        RecordedDate > DATEADD([year], - 2, GETDATE()) OR
        RecordedDate IS NULL OR
        RecordedDate < CAST('1970-01-02' AS datetime)
      )
      OR (
        RovName = 'Ventana' AND
        DiveNumber IN (50, 217, 218, 248)
      )
      OR (
        RovName = 'Tiburon' AND
        DiveNumber IN (1001, 1029, 1030, 1031, 1032, 1033, 1034)
      )
      OR (
        ConceptName = 'Bathochordaeus charon'
      )
      OR (
        ConceptName IN (
         'Bathyteuthis',
         'Bathyteuthis abyssicola',
         'Bathyteuthis berryi',
         'Grimalditeuthis',
         'Grimalditeuthis',
         'Grimalditeuthis bonpland',
         'Octopoteuthis',
         'Octopoteuthis deletron',
         'Planctoteuthis',
         'Planctoteuthis danae',
         'Planctoteuthis oligobessa'
        ) AND
        Observer = 'sbush'
      )
    )
  )
GO

UPDATE
  CameraData
SET
  StillImageURL = REPLACE(StillImageURL,  'http://search.mbari.org/ARCHIVE', 'http://dsg.mbari.org')
WHERE
  StillImageURL IS NOT NULL
GO

DELETE FROM
  Association
WHERE
  LinkName = 'video-lab-only-comment'
GO