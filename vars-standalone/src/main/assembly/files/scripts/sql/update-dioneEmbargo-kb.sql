
-- DELETE branches of concepts

-- Xenophyophoroidea --------------------------------------------------
DECLARE @conceptId bigint

DECLARE MyCursor CURSOR FOR
  SELECT ConceptID_FK FROM ConceptName WHERE ConceptName = 'Xenophyophoroidea'

OPEN MyCursor
FETCH NEXT FROM MyCursor into @conceptId
WHILE @@FETCH_STATUS = 0
  BEGIN
    PRINT @conceptId
    FETCH NEXT FROM MyCursor INTO @conceptId

    -- Drop ConceptNames first
    DELETE FROM ConceptName WHERE id IN (SELECT cn.id FROM Concept AS c LEFT JOIN
      ConceptName AS cn ON cn.ConceptID_FK = c.id WHERE c.ParentConceptID_FK = @conceptId)

    -- Drop Concepts
    DELETE FROM Concept WHERE ParentConceptID_FK = @conceptId

    DELETE FROM ConceptName WHERE ConceptID_FK =@conceptId

    DELETE FROM Concept WHERE id = @conceptId

  END
CLOSE MyCursor
DEALLOCATE MyCursor

GO


-- DELETE Media and LinkRealizations from concepts

-- Xenoturbellida -----------------------------------------------------
DECLARE @conceptId bigint

DECLARE MyCursor CURSOR FOR 
  SELECT ConceptID_FK FROM ConceptName WHERE ConceptName = 'Xenoturbellida' 

OPEN MyCursor 
FETCH NEXT FROM MyCursor into @conceptId 
WHILE @@FETCH_STATUS = 0 
BEGIN 
    PRINT @conceptId 
    FETCH NEXT FROM MyCursor INTO @conceptId 

    -- Drop ConceptNames first 
    DELETE FROM ConceptName WHERE id IN (SELECT cn.id FROM Concept AS c LEFT JOIN 
      ConceptName AS cn ON cn.ConceptID_FK = c.id WHERE c.ParentConceptID_FK = @conceptId) 

    -- Drop Concepts 
    DELETE FROM Concept WHERE ParentConceptID_FK = @conceptId 

    DELETE FROM ConceptName WHERE ConceptID_FK =@conceptId 

    DELETE FROM Concept WHERE id = @conceptId 
     
END 
CLOSE MyCursor 
DEALLOCATE MyCursor 

GO 



-- Mystery Mollusc ----------------------------------------------------
DECLARE @conceptId bigint 

DECLARE MyCursor CURSOR FOR 
  SELECT ConceptID_FK FROM ConceptName WHERE ConceptName = 'Mystery Mollusc'

OPEN MyCursor 
FETCH NEXT FROM MyCursor into @conceptId 
WHILE @@FETCH_STATUS = 0 
BEGIN 
    PRINT @conceptId 
    FETCH NEXT FROM MyCursor INTO @conceptId 

    -- Drop ConceptNames first 
    DELETE FROM ConceptName WHERE id IN (SELECT cn.id FROM Concept AS c LEFT JOIN 
      ConceptName AS cn ON cn.ConceptID_FK = c.id WHERE c.ParentConceptID_FK = @conceptId) 

    -- Drop Concepts 
    DELETE FROM Concept WHERE ParentConceptID_FK = @conceptId 

    DELETE FROM ConceptName WHERE ConceptID_FK = @conceptId

    DELETE FROM Concept WHERE id = @conceptId 
     
END 
CLOSE MyCursor 
DEALLOCATE MyCursor 

GO

-- Cydippida 2 --------------------------------------------------------
DECLARE @conceptId bigint 

DECLARE MyCursor CURSOR FOR 
  SELECT ConceptID_FK FROM ConceptName WHERE ConceptName = 'Cydippida 2'

OPEN MyCursor 
FETCH NEXT FROM MyCursor into @conceptId 
WHILE @@FETCH_STATUS = 0 
BEGIN 
    PRINT @conceptId 
    FETCH NEXT FROM MyCursor INTO @conceptId 

    -- Drop ConceptNames first 
    DELETE FROM ConceptName WHERE id IN (SELECT cn.id FROM Concept AS c LEFT JOIN 
      ConceptName AS cn ON cn.ConceptID_FK = c.id WHERE c.ParentConceptID_FK = @conceptId) 

    -- Drop Concepts 
    DELETE FROM Concept WHERE ParentConceptID_FK = @conceptId 

    DELETE FROM ConceptName WHERE ConceptID_FK =@conceptId 

    DELETE FROM Concept WHERE id = @conceptId 
     
END 
CLOSE MyCursor 
DEALLOCATE MyCursor 

GO

-- Llyria -------------------------------------------------------------
DECLARE @conceptId bigint 

DECLARE MyCursor CURSOR FOR 
  SELECT ConceptID_FK FROM ConceptName WHERE ConceptName = 'Llyria'

OPEN MyCursor 
FETCH NEXT FROM MyCursor into @conceptId 
WHILE @@FETCH_STATUS = 0 
BEGIN 
    PRINT @conceptId 
    FETCH NEXT FROM MyCursor INTO @conceptId 

    -- Drop ConceptNames first 
    DELETE FROM ConceptName WHERE id IN (SELECT cn.id FROM Concept AS c LEFT JOIN 
      ConceptName AS cn ON cn.ConceptID_FK = c.id WHERE c.ParentConceptID_FK = @conceptId) 

    -- Drop Concepts 
    DELETE FROM Concept WHERE ParentConceptID_FK = @conceptId 

    DELETE FROM ConceptName WHERE ConceptID_FK =@conceptId 

    DELETE FROM Concept WHERE id = @conceptId 
     
END 
CLOSE MyCursor 
DEALLOCATE MyCursor 

GO

-- Mertensia ----------------------------------------------------------
DECLARE @conceptId bigint 

DECLARE MyCursor CURSOR FOR 
  SELECT ConceptID_FK FROM ConceptName WHERE ConceptName = 'Mertensia'

OPEN MyCursor 
FETCH NEXT FROM MyCursor into @conceptId 
WHILE @@FETCH_STATUS = 0 
BEGIN 
    PRINT @conceptId 
    FETCH NEXT FROM MyCursor INTO @conceptId 

    -- Drop ConceptNames first 
    DELETE FROM ConceptName WHERE id IN (SELECT cn.id FROM Concept AS c LEFT JOIN 
      ConceptName AS cn ON cn.ConceptID_FK = c.id WHERE c.ParentConceptID_FK = @conceptId) 

    -- Drop Concepts 
    DELETE FROM Concept WHERE ParentConceptID_FK = @conceptId 

    DELETE FROM ConceptName WHERE ConceptID_FK =@conceptId 

    DELETE FROM Concept WHERE id = @conceptId 
     
END 
CLOSE MyCursor 
DEALLOCATE MyCursor 

GO

-- Platyctenida sp. 1 -------------------------------------------------
DECLARE @conceptId bigint

DECLARE MyCursor CURSOR FOR
  SELECT ConceptID_FK FROM ConceptName WHERE ConceptName = 'Platyctenida sp. 1'

OPEN MyCursor
FETCH NEXT FROM MyCursor into @conceptId
WHILE @@FETCH_STATUS = 0
  BEGIN
    PRINT @conceptId
    FETCH NEXT FROM MyCursor INTO @conceptId

    -- Drop ConceptNames first
    DELETE FROM ConceptName WHERE id IN (SELECT cn.id FROM Concept AS c LEFT JOIN
      ConceptName AS cn ON cn.ConceptID_FK = c.id WHERE c.ParentConceptID_FK = @conceptId)

    -- Drop Concepts
    DELETE FROM Concept WHERE ParentConceptID_FK = @conceptId

    DELETE FROM ConceptName WHERE ConceptID_FK =@conceptId

    DELETE FROM Concept WHERE id = @conceptId

  END
CLOSE MyCursor
DEALLOCATE MyCursor

GO

-- Lyroctenidae -------------------------------------------------------
DECLARE @conceptId bigint

DECLARE MyCursor CURSOR FOR
  SELECT ConceptID_FK FROM ConceptName WHERE ConceptName = 'Lyroctenidae'

OPEN MyCursor
FETCH NEXT FROM MyCursor into @conceptId
WHILE @@FETCH_STATUS = 0
  BEGIN
    PRINT @conceptId
    FETCH NEXT FROM MyCursor INTO @conceptId

    -- Drop ConceptNames first
    DELETE FROM ConceptName WHERE id IN (SELECT cn.id FROM Concept AS c LEFT JOIN
      ConceptName AS cn ON cn.ConceptID_FK = c.id WHERE c.ParentConceptID_FK = @conceptId)

    -- Drop Concepts
    DELETE FROM Concept WHERE ParentConceptID_FK = @conceptId

    DELETE FROM ConceptName WHERE ConceptID_FK =@conceptId

    DELETE FROM Concept WHERE id = @conceptId

  END
CLOSE MyCursor
DEALLOCATE MyCursor

GO

-- Tjalfiellidae ------------------------------------------------------
DECLARE @conceptId bigint

DECLARE MyCursor CURSOR FOR
  SELECT ConceptID_FK FROM ConceptName WHERE ConceptName = 'Tjalfiellidae'

OPEN MyCursor
FETCH NEXT FROM MyCursor into @conceptId
WHILE @@FETCH_STATUS = 0
  BEGIN
    PRINT @conceptId
    FETCH NEXT FROM MyCursor INTO @conceptId

    -- Drop ConceptNames first
    DELETE FROM ConceptName WHERE id IN (SELECT cn.id FROM Concept AS c LEFT JOIN
      ConceptName AS cn ON cn.ConceptID_FK = c.id WHERE c.ParentConceptID_FK = @conceptId)

    -- Drop Concepts
    DELETE FROM Concept WHERE ParentConceptID_FK = @conceptId

    DELETE FROM ConceptName WHERE ConceptID_FK =@conceptId

    DELETE FROM Concept WHERE id = @conceptId

  END
CLOSE MyCursor
DEALLOCATE MyCursor

GO

-- Thalassocalycida sp. 1 ----------------------------------------------------------
DECLARE @conceptId bigint

DECLARE MyCursor CURSOR FOR
  SELECT ConceptID_FK FROM ConceptName WHERE ConceptName = 'Thalassocalycida sp. 1'

OPEN MyCursor
FETCH NEXT FROM MyCursor into @conceptId
WHILE @@FETCH_STATUS = 0
  BEGIN
    PRINT @conceptId
    FETCH NEXT FROM MyCursor INTO @conceptId

    -- Drop ConceptNames first
    DELETE FROM ConceptName WHERE id IN (SELECT cn.id FROM Concept AS c LEFT JOIN
      ConceptName AS cn ON cn.ConceptID_FK = c.id WHERE c.ParentConceptID_FK = @conceptId)

    -- Drop Concepts
    DELETE FROM Concept WHERE ParentConceptID_FK = @conceptId

    DELETE FROM ConceptName WHERE ConceptID_FK =@conceptId

    DELETE FROM Concept WHERE id = @conceptId

  END
CLOSE MyCursor
DEALLOCATE MyCursor

GO

-- Aegina sp. 1 ----------------------------------------------------------
DECLARE @conceptId bigint

DECLARE MyCursor CURSOR FOR
  SELECT ConceptID_FK FROM ConceptName WHERE ConceptName = 'Aegina sp. 1'

OPEN MyCursor
FETCH NEXT FROM MyCursor into @conceptId
WHILE @@FETCH_STATUS = 0
  BEGIN
    PRINT @conceptId
    FETCH NEXT FROM MyCursor INTO @conceptId

    -- Drop ConceptNames first
    DELETE FROM ConceptName WHERE id IN (SELECT cn.id FROM Concept AS c LEFT JOIN
      ConceptName AS cn ON cn.ConceptID_FK = c.id WHERE c.ParentConceptID_FK = @conceptId)

    -- Drop Concepts
    DELETE FROM Concept WHERE ParentConceptID_FK = @conceptId

    DELETE FROM ConceptName WHERE ConceptID_FK =@conceptId

    DELETE FROM Concept WHERE id = @conceptId

  END
CLOSE MyCursor
DEALLOCATE MyCursor

GO

-- Erenna sp. A -------------------------------------------------------
DECLARE @conceptId bigint

DECLARE MyCursor CURSOR FOR
  SELECT ConceptID_FK FROM ConceptName WHERE ConceptName = 'Erenna sp. A'

OPEN MyCursor
FETCH NEXT FROM MyCursor into @conceptId
WHILE @@FETCH_STATUS = 0
  BEGIN
    PRINT @conceptId
    FETCH NEXT FROM MyCursor INTO @conceptId

    -- Drop ConceptNames first
    DELETE FROM ConceptName WHERE id IN (SELECT cn.id FROM Concept AS c LEFT JOIN
      ConceptName AS cn ON cn.ConceptID_FK = c.id WHERE c.ParentConceptID_FK = @conceptId)

    -- Drop Concepts
    DELETE FROM Concept WHERE ParentConceptID_FK = @conceptId

    DELETE FROM ConceptName WHERE ConceptID_FK =@conceptId

    DELETE FROM Concept WHERE id = @conceptId

  END
CLOSE MyCursor
DEALLOCATE MyCursor

GO

-- Physonectae sp. 1 --------------------------------------------------
DECLARE @conceptId bigint

DECLARE MyCursor CURSOR FOR
  SELECT ConceptID_FK FROM ConceptName WHERE ConceptName = 'Physonectae sp. 1'

OPEN MyCursor
FETCH NEXT FROM MyCursor into @conceptId
WHILE @@FETCH_STATUS = 0
  BEGIN
    PRINT @conceptId
    FETCH NEXT FROM MyCursor INTO @conceptId

    -- Drop ConceptNames first
    DELETE FROM ConceptName WHERE id IN (SELECT cn.id FROM Concept AS c LEFT JOIN
      ConceptName AS cn ON cn.ConceptID_FK = c.id WHERE c.ParentConceptID_FK = @conceptId)

    -- Drop Concepts
    DELETE FROM Concept WHERE ParentConceptID_FK = @conceptId

    DELETE FROM ConceptName WHERE ConceptID_FK =@conceptId

    DELETE FROM Concept WHERE id = @conceptId

  END
CLOSE MyCursor
DEALLOCATE MyCursor

GO

-- Tuscaroridae -------------------------------------------------------
DECLARE @conceptId bigint

DECLARE MyCursor CURSOR FOR
  SELECT ConceptID_FK FROM ConceptName WHERE ConceptName = 'Tuscaroridae'

OPEN MyCursor
FETCH NEXT FROM MyCursor into @conceptId
WHILE @@FETCH_STATUS = 0
  BEGIN
    PRINT @conceptId
    FETCH NEXT FROM MyCursor INTO @conceptId

    -- Drop ConceptNames first
    DELETE FROM ConceptName WHERE id IN (SELECT cn.id FROM Concept AS c LEFT JOIN
      ConceptName AS cn ON cn.ConceptID_FK = c.id WHERE c.ParentConceptID_FK = @conceptId)

    -- Drop Concepts
    DELETE FROM Concept WHERE ParentConceptID_FK = @conceptId

    DELETE FROM ConceptName WHERE ConceptID_FK =@conceptId

    DELETE FROM Concept WHERE id = @conceptId

  END
CLOSE MyCursor
DEALLOCATE MyCursor

GO

-- Change Media references to external link ---------------------------
UPDATE 
  Media 
SET 
  Url = REPLACE(Url, 'http://dsg.mbari.org/images/dsg/', 'http://dsg.mbari.org/images/dsg/external/') 
WHERE 
  Url NOT LIKE 'http://dsg.mbari.org/images/dsg/external/%' AND 
  Url LIKE 'http://dsg.mbari.org/images/dsg/%'


-- Remove all internal comments ---------------------------------------
DELETE FROM 
  LinkRealization 
WHERE 
  LinkName = 'internal-video-lab-only-comment'