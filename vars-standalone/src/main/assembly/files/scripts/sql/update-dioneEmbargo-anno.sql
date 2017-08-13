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
      OR ( -- Delete embargoes by selectedConcept
        ConceptName IN (
          'Aegina sp. 1',
          'Ctenophora',
          'Cydippida 2',
          'Cydippida',
          'Intacta',
          'Llyria',
          'Lyrocteis',
          'Lyroctenidae',
          'Mertensia',
          'Mertensiidae sp. A',
          'Mystery Mollusc',
          'Mystery Mollusc',
          'Physonectae sp. 1',
          'Platyctenida sp. 1',
          'Platyctenida',
          'Thalassocalycida sp. 1',
          'Thalassocalycida',
          'Thliptodon sp. A',
          'Tjalfiella tristoma',
          'Tjalfiella',
          'Tjalfiellidae',
          'Tuscarantha braueri',
          'Tuscarantha luciae',
          'Tuscarantha',
          'Tuscaretta globosa',
          'Tuscaretta',
          'Tuscaridium cygneum',
          'Tuscaridium',
          'Tuscarilla campanella',
          'Tuscarilla nationalis',
          'Tuscarilla similis',
          'Tuscarilla',
          'Tuscarora',
          'Tuscaroridae'
        )
      )
    )
  )
GO

-- DELETE All bioluminescent asociations ----------------------------
DELETE FROM
  Association
WHERE
  LinkName = 'is-bioluminescent'

-- DELETE any associations with value of mysterey mollus -------------
DELETE FROM
  Observation
WHERE
  id IN (
    SELECT DISTINCT
      ObservationID_FK
    FROM
      Annotations
    WHERE
      LinkValue LIKE '%mysterey%mollusc%'
  )
  


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

-- Delete Benthic labs's transect data -------------------------------
DELETE
  Observation
WHERE
  VideoFrameID_FK IN (
    SELECT
      Vf.id
    FROM
      CameraPlatformDeployment as cpd RIGHT JOIN
      VideoArchiveSet AS vas ON vas.id = cpd.VideoArchiveSetID_FK LEFT JOIN
      VideoArchive AS va ON va.VideoArchiveSetID_FK = va.id LEFT JOIN
      VideoFrame AS vf ON vf.VideoArchiveID_FK = va.id LEFT JOIN
      CameraData AS cd ON cd.VideoFrameID_FK = vf.id
    WHERE
       cd.Direction IN ('transect', 'diel transect', 'starttransect', 'endtransect') AND
       (cpd.ChiefScientist LIKE '%barry%' OR
        cpd.ChiefScientist LIKE '%whaling%' OR
        cpd.ChiefScientist LIKE '%smith%' OR
        cpd.ChiefScientist LIKE '%clague%' OR
        cpd.ChiefScientist LIKE '%kuhnz%')
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
