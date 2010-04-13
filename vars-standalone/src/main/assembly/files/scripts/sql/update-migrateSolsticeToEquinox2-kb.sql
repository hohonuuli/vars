-- Artifact -----------------------------------------------------------
CREATE TABLE [dbo].[Artifact]  ( 
    [ConceptDelegateID_FK]	bigint NOT NULL,
    [GroupId]             	varchar(64) NOT NULL,
    [ArtifactId]          	varchar(256) NOT NULL,
    [Version]             	varchar(64) NOT NULL,
    [Classifier]          	varchar(64) NULL,
    [Description]         	varchar(2048) NULL,
    [MimeType]            	varchar(32) NULL,
    [Caption]             	varchar(1024) NULL,
    [Reference]           	varchar(1024) NOT NULL,
    [Credit]              	varchar(1024) NULL,
    [id]                  	bigint NOT NULL,
    [LAST_UPDATED_TIME]   	datetime NOT NULL,
    [CreationDate]        	datetime NULL,
    CONSTRAINT [Artifact_PK] PRIMARY KEY([id])
)
ON [PRIMARY]
GO
ALTER TABLE [dbo].[Artifact]
    ADD CONSTRAINT [Artifact_FK1]
	FOREIGN KEY([ConceptDelegateID_FK])
	REFERENCES [dbo].[ConceptDelegate]([id])
	ON DELETE NO ACTION 
	ON UPDATE NO ACTION 
GO
CREATE INDEX [idx_Artifact_LUT]
    ON [dbo].[Artifact]([LAST_UPDATED_TIME])
GO
CREATE UNIQUE INDEX [idx_Artifact_CK]
    ON [dbo].[Artifact]([GroupId], [ArtifactId], [Version], [Classifier])
GO
CREATE INDEX [idx_Artifact_FK1]
    ON [dbo].[Artifact]([ConceptDelegateID_FK])
GO


-- Concept

ALTER TABLE [dbo].[Concept]
	ADD CONSTRAINT [Concept_PK]
	PRIMARY KEY ([id])
GO

CREATE INDEX [idx_Concept_FK1]
	ON [dbo].[Concept]([ParentConceptID_FK])
GO

CREATE INDEX [idx_Concept_LUT]
	ON [dbo].[Concept]([LAST_UPDATED_TIME])
GO

ALTER TABLE [dbo].[Concept]
	DROP COLUMN [PrimaryConceptNameID_FK], [ConceptDelegateID_FK]
GO


-- ConceptDelegate
ALTER TABLE [dbo].[ConceptDelegate]
	DROP COLUMN [UsageID_FK]
GO

ALTER TABLE [dbo].[ConceptDelegate]
	ADD CONSTRAINT [ConceptDelegate_PK]
	PRIMARY KEY ([id])
GO

CREATE INDEX [idx_ConceptDelegate_FK1]
	ON [dbo].[ConceptDelegate]([ConceptID_FK])
GO

CREATE INDEX [idx_ConceptDelegate_LUT]
	ON [dbo].[ConceptDelegate]([LAST_UPDATED_TIME])
GO

ALTER TABLE [dbo].[ConceptDelegate]
	ADD CONSTRAINT [ConceptDelegate_FK1]
	FOREIGN KEY([ConceptID_FK])
	REFERENCES [dbo].[Concept]([id])
GO

-- ConceptName
ALTER TABLE [dbo].[ConceptName]
	ADD CONSTRAINT [ConceptName_PK]
	PRIMARY KEY ([id])
GO

ALTER TABLE [dbo].[ConceptName]
	ADD CONSTRAINT [ConceptName_FK1]
	FOREIGN KEY([ConceptID_FK])
	REFERENCES [dbo].[Concept]([id])
GO

CREATE INDEX [idx_ConceptName_FK1]
	ON [dbo].[ConceptName]([ConceptID_FK])
GO

CREATE INDEX [idx_ConceptName_LUT]
	ON [dbo].[ConceptName]([LAST_UPDATED_TIME])
GO

CREATE INDEX [idx_ConceptName_name]
	ON [dbo].[ConceptName]([ConceptName])
GO

-- TODO clean up duplicate names
ALTER TABLE [dbo].[ConceptName]
	ADD CONSTRAINT [uc_ConceptName_name]
	UNIQUE ([ConceptName])
GO


-- History
ALTER TABLE [dbo].[History]
	ADD CONSTRAINT [History_PK]
	PRIMARY KEY ([id])
GO

ALTER TABLE [dbo].[History]
	ADD CONSTRAINT [History_FK1]
	FOREIGN KEY([ConceptDelegateID_FK])
	REFERENCES [dbo].[ConceptDelegate]([id])
GO

CREATE INDEX [idx_History_FK1]
	ON [dbo].[History]([ConceptDelegateID_FK])
GO

CREATE INDEX [idx_History_LUT]
	ON [dbo].[History]([LAST_UPDATED_TIME])
GO


-- LinkRealization
ALTER TABLE [dbo].[LinkRealization]
	ADD CONSTRAINT [LinkRealization_PK]
	PRIMARY KEY ([id])
GO

ALTER TABLE [dbo].[LinkRealization]
	ADD CONSTRAINT [LinkRealization_FK1]
	FOREIGN KEY([ConceptDelegateID_FK])
	REFERENCES [dbo].[ConceptDelegate]([id])
GO

ALTER TABLE [dbo].[LinkRealization]
	DROP COLUMN [ParentLinkRealizationID_FK]
GO

CREATE INDEX [idx_LinkRealization_FK1]
	ON [dbo].[LinkRealization]([ConceptDelegateID_FK])
GO

CREATE INDEX [idx_LinkRealization_LUT]
	ON [dbo].[LinkRealization]([LAST_UPDATED_TIME])
GO

-- LinkTemplate
ALTER TABLE [dbo].[LinkTemplate]
	ADD CONSTRAINT [LinkTemplate_PK]
	PRIMARY KEY ([id])
GO

ALTER TABLE [dbo].[LinkTemplate]
	ADD CONSTRAINT [LinkTemplate_FK1]
	FOREIGN KEY([ConceptDelegateID_FK])
	REFERENCES [dbo].[ConceptDelegate]([id])
GO

CREATE INDEX [idx_LinkTemplate_FK1]
	ON [dbo].[LinkTemplate]([ConceptDelegateID_FK])
GO

CREATE INDEX [idx_LinkTemplate_LUT]
	ON [dbo].[LinkTemplate]([LAST_UPDATED_TIME])
GO


-- Media
ALTER TABLE [dbo].[Media]
	ADD CONSTRAINT [Media_PK]
	PRIMARY KEY ([id])
GO

ALTER TABLE [dbo].[Media]
	ADD CONSTRAINT [Media_FK1]
	FOREIGN KEY([ConceptDelegateID_FK])
	REFERENCES [dbo].[ConceptDelegate]([id])
GO

CREATE INDEX [idx_Media_LUT]
	ON [dbo].[Media]([LAST_UPDATED_TIME])
GO


-- Prefs
CREATE INDEX [idx_Prefs]
	ON [dbo].[Prefs]([NodeName], [PrefKey])
GO


-- SectionInfo
ALTER TABLE [dbo].[SectionInfo]
	ADD CONSTRAINT [SectionInfo_PK]
	PRIMARY KEY ([id])
GO

ALTER TABLE [dbo].[SectionInfo]
	ADD CONSTRAINT [SectionInfo_FK1]
	FOREIGN KEY([ConceptDelegateID_FK])
	REFERENCES [dbo].[ConceptDelegate]([id])
GO

CREATE INDEX [idx_SectionInfo_FK1]
	ON [dbo].[SectionInfo]([ConceptDelegateID_FK])
GO


-- UniqueID
ALTER TABLE [dbo].[UniqueID]
	ADD CONSTRAINT [UniqueID_PK]
	PRIMARY KEY ([tablename])
GO


-- Usage
ALTER TABLE [dbo].[Usage]
	ADD CONSTRAINT [Usage_PK]
	PRIMARY KEY ([id])
GO

ALTER TABLE [dbo].[Usage]
	ADD CONSTRAINT [Usage_FK1]
	FOREIGN KEY([ConceptDelegateID_FK])
	REFERENCES [dbo].[ConceptDelegate]([id])
GO

CREATE INDEX [idx_Usage_FK1]
	ON [dbo].[Usage]([ConceptDelegateID_FK])
GO

CREATE INDEX [idx_Usage_LUT]
	ON [dbo].[Usage]([LAST_UPDATED_TIME])
GO


-- UserAccount
ALTER TABLE [dbo].[UserAccount]
	ADD CONSTRAINT [UserAccount_PK]
	PRIMARY KEY ([id])
GO

ALTER TABLE [dbo].[UserAccount]
	ADD CONSTRAINT [uc_UserAccount_UserName]
	UNIQUE ([UserName])
GO

CREATE INDEX [idx_UserAccount_UserName]
	ON [dbo].[UserAccount]([UserName])
GO




