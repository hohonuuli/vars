DELETE FROM
  Observation
WHERE
  id IN (
    SELECT DISTINCT
      ObservationID_FK
    FROM
      Annotations
    WHERE (
      (  -- Delete last 2 years of annotations
        RecordedDate > DATEADD([year], - 2, GETDATE()) OR
        RecordedDate IS NULL OR
        RecordedDate < CAST('1970-01-02' AS datetime)
      )
      OR ( -- Delete embargoes by dive
        RovName = 'Ventana' AND
        DiveNumber IN (50, 217, 218, 248)
      )
      OR (
        RovName = 'Tiburon' AND
        DiveNumber IN (1001, 1029, 1030, 1031, 1032, 1033, 1034)
      )
      OR ( -- Delete embargoes by concept
        ConceptName IN (
          'Xenoturbellida',
          'Xenoturbellidae',
          'Xenoturbella',
          'Mystery Mollusc',
          'Ctenophora',
          'Cydippida',
          'Cydippida 2',
          'Llyria',
          'Mertensia',
          'Platyctenida sp. 1',
          'Lyroctenidae',
          'Lyrocteis',
          'Tjalfiellidae',
          'Tjalfiella',
          'Tjalfiella tristoma',
          'Thalassocalycida',
          'Thalassocalycida sp. 1',
          'Intacta',
          'Aegina sp. 1',
          'Erenna sp. A',
          'Physonectae sp. 1',
          'Tuscaroridae',
          'Tuscarantha',
          'Tuscarantha luciae',
          'Tuscarantha braueri',
          'Tuscaretta',
          'Tuscaretta globosa',
          'Tuscaridium',
          'Tuscaridium cygneum',
          'Tuscarilla',
          'Tuscarilla nationalis',
          'Tuscarilla similis',
          'Tuscarilla campanella',
          'Tuscarora'
        )
      )
    )
  )
GO

-- DELETE Bob's Xeno annotations --------------------------------------
DELETE FROM
  Observation
WHERE
  id IN (
     SELECT DISTINCT
       ObservationID_FK
     FROM
       Annotations
     WHERE
       ConceptName IN (
         'Xenophyophoroidea',
         'Psamminidae',
         'Psamminida'
       ) AND
       (ChiefScientist LIKE '%Vrijenhoek%' OR ChiefScientist LIKE '%Clague%')
  )

GO

-- Delete Midwater lab's transect data --------------------------------
DELETE FROM
  Observation
WHERE
  VideoFrameID_FK IN (
    SELECT
      vf.id
    FROM
      CameraPlatformDeployment as cpd RIGHT JOIN
      VideoArchiveSet AS vas ON vas.id = cpd.VideoArchiveSetID_FK LEFT JOIN
      VideoArchive AS va ON va.VideoArchiveSetID_FK = va.id LEFT JOIN
      VideoFrame AS vf ON vf.VideoArchiveID_FK = va.id LEFT JOIN
      CameraData AS cd ON cd.VideoFrameID_FK = vf.id
    WHERE
      cd.Direction IN ('transect', 'diel transect', 'starttransect', 'endtransect') AND
      (cpd.ChiefScientist LIKE '%robison%' OR
       cpd.ChiefScientist LIKE '%sherlock%' OR
       cpd.ChiefScientist LIKE '%raskoff%' OR
       cpd.ChiefScientist LIKE '%zeidberg%' OR
       cpd.ChiefScientist LIKE '%hopcroft%' OR
       cpd.ChiefScientist LIKE '%hunt%' OR
       cpd.ChiefScientist LIKE '%hamner%')
  )
  
GO

-- Remove Camera directions -------------------------------------------
UPDATE
  CameraData
SET
  direction = NULL
GO

-- Change images to use external URL ----------------------------------
UPDATE
  CameraData
SET
  StillImageURL = REPLACE(StillImageURL,  'http://search.mbari.org/ARCHIVE', 'http://dsg.mbari.org')
WHERE
  StillImageURL IS NOT NULL
GO

-- Remove video lab comments ------------------------------------------
DELETE FROM
  Association
WHERE
  LinkName = 'video-lab-only-comment'
GO
