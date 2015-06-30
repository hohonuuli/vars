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

DECLARE @conceptId bigint 

DECLARE MyCursor CURSOR FOR 
  SELECT ConceptID_FK FROM ConceptName WHERE ConceptName = 'Xenophyophorea' 

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

DECLARE @conceptId bigint 

DECLARE MyCursor CURSOR FOR 
  SELECT ConceptID_FK FROM ConceptName WHERE ConceptName = 'Bathyteuthis' 

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

DECLARE @conceptId bigint 

DECLARE MyCursor CURSOR FOR 
  SELECT ConceptID_FK FROM ConceptName WHERE ConceptName = 'Grimalditeuthis' 

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

DECLARE @conceptId bigint 

DECLARE MyCursor CURSOR FOR 
  SELECT ConceptID_FK FROM ConceptName WHERE ConceptName = 'Octopoteuthis' 

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

DECLARE @conceptId bigint 

DECLARE MyCursor CURSOR FOR 
  SELECT ConceptID_FK FROM ConceptName WHERE ConceptName = 'Planctoteuthis' 

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

UPDATE 
  Media 
SET 
  Url = REPLACE(Url, 'http://dsg.mbari.org/images/dsg/', 'http://dsg.mbari.org/images/dsg/external/') 
WHERE 
  Url NOT LIKE 'http://dsg.mbari.org/images/dsg/external/%' AND 
  Url LIKE 'http://dsg.mbari.org/images/dsg/%'

DELETE FROM 
  LinkRealization 
WHERE 
  LinkName = 'internal-video-lab-only-comment'