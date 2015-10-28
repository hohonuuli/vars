-- Artifact -----------------------------------------------------------
CREATE TABLE ARTIFACT  ( 
    id                BIGINT NOT NULL,
    ConceptDelegateID_FK bigint NOT NULL,
    GROUPID           VARCHAR(64),
    ARTIFACTID        VARCHAR(256),
    VERSION           VARCHAR(64),
    CLASSIFIER        VARCHAR(64),
    DESCRIPTION       VARCHAR(2048),
    MIMETYPE          VARCHAR(32),
    CAPTION           VARCHAR(1024),
    REFERENCE         VARCHAR(1024),
    CREDIT            VARCHAR(1024),
    CREATIONDATE      TIMESTAMP,
    LAST_UPDATED_TIME TIMESTAMP,
    CONSTRAINT ARTIFACT_PK PRIMARY KEY(id)
)
GO


CREATE UNIQUE INDEX idx_Artifact_CK
    ON Artifact(GroupId, ArtifactId, Version, Classifier)
GO

CREATE INDEX idx_Artifact_FK1
    ON Artifact(ConceptDelegateID_FK)
GO


-- Concept
ALTER TABLE CONCEPT
    ADD LAST_UPDATED_TIME TIMESTAMP
GO

CREATE INDEX idx_Concept_FK1
    ON Concept(ParentConceptID_FK)
GO

CREATE INDEX idx_Concept_LUT
    ON Concept(LAST_UPDATED_TIME)
GO

ALTER TABLE Concept
    DROP COLUMN PrimaryConceptNameID_FK
GO

ALTER TABLE Concept
    DROP COLUMN ConceptDelegateID_FK
GO

UPDATE Concept
    SET LAST_UPDATED_TIME='2015-10-26 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO


-- ConceptDelegate
ALTER TABLE ConceptDelegate
    ADD LAST_UPDATED_TIME TIMESTAMP
GO

ALTER TABLE ConceptDelegate
    DROP COLUMN UsageID_FK
GO

CREATE INDEX idx_ConceptDelegate_FK1
    ON ConceptDelegate(ConceptID_FK)
GO

CREATE INDEX idx_ConceptDelegate_LUT
    ON ConceptDelegate(LAST_UPDATED_TIME)
GO

UPDATE ConceptDelegate
    SET LAST_UPDATED_TIME='2015-10-26 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO


-- ConceptName
ALTER TABLE ConceptName
    ADD LAST_UPDATED_TIME TIMESTAMP
GO

CREATE INDEX idx_ConceptName_FK1
    ON ConceptName(ConceptID_FK)
GO

CREATE INDEX idx_ConceptName_name
    ON ConceptName(ConceptName)
GO

UPDATE ConceptName
    SET LAST_UPDATED_TIME='2015-10-26 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

-- TODO clean up duplicate names
ALTER TABLE ConceptName
    ADD CONSTRAINT uc_ConceptName_name
    UNIQUE (ConceptName)
GO


-- History
-- TODO: RENAME APPROVALDTG to PROCESSEDDTG
RENAME COLUMN "VARSUSER"."HISTORY"."APPROVALDTG" TO "PROCESSEDDTG"
GO

RENAME COLUMN "VARSUSER"."HISTORY"."APPROVERNAME" TO "PROCESSORNAME"
GO

RENAME COLUMN "VARSUSER"."HISTORY"."REJECTED" TO "APPROVED"
GO

-- TODO: Flip value in rejected

ALTER TABLE History
    ADD LAST_UPDATED_TIME TIMESTAMP
GO

CREATE INDEX idx_History_FK1
    ON History(ConceptDelegateID_FK)
GO

CREATE INDEX idx_History_LUT
    ON History(LAST_UPDATED_TIME)
GO

UPDATE History
    SET LAST_UPDATED_TIME='2015-10-26 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO


-- LinkRealization
ALTER TABLE LinkRealization
    ADD LAST_UPDATED_TIME TIMESTAMP
GO

ALTER TABLE LinkRealization
    DROP COLUMN ParentLinkRealizationID_FK
GO

CREATE INDEX idx_LinkRealization_FK1
    ON LinkRealization(ConceptDelegateID_FK)
GO

CREATE INDEX idx_LinkRealization_LUT
    ON LinkRealization(LAST_UPDATED_TIME)
GO

UPDATE LinkRealization
    SET LAST_UPDATED_TIME='2015-10-26 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

-- LinkTemplate
ALTER TABLE LinkTemplate
    ADD LAST_UPDATED_TIME TIMESTAMP
GO

CREATE INDEX idx_LinkTemplate_FK1
    ON LinkTemplate(ConceptDelegateID_FK)
GO

CREATE INDEX idx_LinkTemplate_LUT
    ON LinkTemplate(LAST_UPDATED_TIME)
GO

UPDATE LinkTemplate
    SET LAST_UPDATED_TIME='2015-10-26 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO

-- Media
ALTER TABLE Media
    ADD LAST_UPDATED_TIME TIMESTAMP
GO

CREATE INDEX idx_Media_FK1
    ON Media(ConceptDelegateID_FK)
GO

CREATE INDEX idx_Media_LUT
    ON Media(LAST_UPDATED_TIME)
GO

UPDATE Media
    SET LAST_UPDATED_TIME='2015-10-26 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO


-- Prefs
CREATE INDEX idx_Prefs
    ON Prefs(NodeName, PrefKey)
GO


-- SectionInfo
ALTER TABLE SectionInfo
    ADD LAST_UPDATED_TIME TIMESTAMP
GO

CREATE INDEX idx_SectionInfo_FK1
    ON SectionInfo(ConceptDelegateID_FK)
GO

UPDATE SectionInfo
    SET LAST_UPDATED_TIME='2015-10-26 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO


-- UniqueID
ALTER TABLE UniqueID
    ADD CONSTRAINT UniqueID_PK
    PRIMARY KEY (tablename)
GO


-- Usage
ALTER TABLE Usage
    ADD LAST_UPDATED_TIME TIMESTAMP
GO

CREATE INDEX idx_Usage_FK1
    ON Usage(ConceptDelegateID_FK)
GO

CREATE INDEX idx_Usage_LUT
    ON Usage(LAST_UPDATED_TIME)
GO

UPDATE Usage
    SET LAST_UPDATED_TIME='2015-10-26 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO


-- UserAccount
ALTER TABLE UserAccount
    ADD Affiliation varchar(50)
GO

ALTER TABLE UserAccount
    ADD FirstName varchar(50)
GO

ALTER TABLE UserAccount
    ADD LastName varchar(50)
GO

ALTER TABLE UserAccount
    ADD Email varchar(50)
GO

ALTER TABLE UserAccount
    ADD LAST_UPDATED_TIME TIMESTAMP
GO

ALTER TABLE UserAccount
    ADD CONSTRAINT uc_UserAccount_UserName
    UNIQUE (UserName)
GO

CREATE INDEX idx_UserAccount_UserName
    ON UserAccount(UserName)
GO

UPDATE UserAccount
    SET LAST_UPDATED_TIME='2015-10-26 00:00:00'
    WHERE LAST_UPDATED_TIME IS NULL
GO


