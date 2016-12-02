ALTER TABLE CameraPlatformDeployment DROP CONSTRAINT CmrPltfVdrchvStDFK
ALTER TABLE VideoArchive DROP CONSTRAINT VdrchvVdrchvStIDFK
ALTER TABLE VideoFrame DROP CONSTRAINT VdFrmeVdrchiveIDFK
ALTER TABLE PhysicalData DROP CONSTRAINT PhysclDtaVdFrmIDFK
ALTER TABLE CameraData DROP CONSTRAINT CmrDataVdFrameIDFK
ALTER TABLE Observation DROP CONSTRAINT bsrvtionVdFrmeIDFK
ALTER TABLE Association DROP CONSTRAINT ssctonbsrvtionIDFK
ALTER TABLE Artifact DROP CONSTRAINT rtfctCncptDlgtIDFK
ALTER TABLE Concept DROP CONSTRAINT CncptPrntCncptIDFK
ALTER TABLE ConceptDelegate DROP CONSTRAINT CncptDlgteCncptDFK
ALTER TABLE ConceptName DROP CONSTRAINT CncptNameCncptIDFK
ALTER TABLE History DROP CONSTRAINT HstryCncptDlgtIDFK
ALTER TABLE LinkRealization DROP CONSTRAINT LnkRlzCncptDlgtDFK
ALTER TABLE LinkTemplate DROP CONSTRAINT LnkTmpCncptDlgtDFK
ALTER TABLE Media DROP CONSTRAINT MdCncptDlegateIDFK
ALTER TABLE Usage DROP CONSTRAINT sgCncptDlegateIDFK
DROP INDEX idx_VideoArchiveSet_LUT
DROP TABLE VideoArchiveSet
DROP INDEX idx_CameraDeployment_FK1
DROP INDEX idx_CameraDeployment_LUT
DROP TABLE CameraPlatformDeployment
DROP INDEX idx_VideoArchive_FK1
DROP INDEX idx_VideoArchive_name
DROP INDEX idx_VideoArchive_LUT
DROP TABLE VideoArchive
DROP INDEX idx_VideoFrame_FK1
DROP INDEX idx_VideoFrame_LUT
DROP TABLE VideoFrame
DROP INDEX idx_PhysicalData_FK1
DROP INDEX idx_PhysicalData_LUT
DROP TABLE PhysicalData
DROP INDEX idx_CameraData_FK1
DROP INDEX idx_CameraData_LUT
DROP TABLE CameraData
DROP INDEX idx_Observation_FK1
DROP INDEX idx_Observation_concept
DROP INDEX idx_Observation_LUT
DROP TABLE Observation
DROP INDEX idx_Association_FK1
DROP INDEX idx_Association_LUT
DROP TABLE Association
DROP TABLE Artifact
DROP INDEX idx_Concept_FK1
DROP INDEX idx_Concept_LUT
DROP TABLE Concept
DROP INDEX idx_ConceptDelegate_FK1
DROP INDEX idx_ConceptDelegate_LUT
DROP TABLE ConceptDelegate
DROP INDEX idx_ConceptName_name
DROP INDEX idx_ConceptName_FK1
DROP INDEX idx_ConceptName_LUT
DROP TABLE ConceptName
DROP INDEX idx_History_FK1
DROP INDEX idx_History_LUT
DROP TABLE History
DROP INDEX idx_LinkRealization_FK1
DROP INDEX idx_LinkRealization_LUT
DROP TABLE LinkRealization
DROP INDEX idx_LinkTemplate_FK1
DROP INDEX idx_LinkTemplate_LUT
DROP TABLE LinkTemplate
DROP INDEX idx_Media_FK1
DROP INDEX idx_Media_LUT
DROP TABLE Media
DROP TABLE Usage
DROP TABLE UserAccount
DROP TABLE Prefs
DELETE FROM UniqueID WHERE TableName = 'Observation'
DELETE FROM UniqueID WHERE TableName = 'LinkRealization'
DELETE FROM UniqueID WHERE TableName = 'ConceptDelegate'
DELETE FROM UniqueID WHERE TableName = 'History'
DELETE FROM UniqueID WHERE TableName = 'Concept'
DELETE FROM UniqueID WHERE TableName = 'CameraData'
DELETE FROM UniqueID WHERE TableName = 'ConceptName'
DELETE FROM UniqueID WHERE TableName = 'VideoFrame'
DELETE FROM UniqueID WHERE TableName = 'CameraPlatformDeployment'
DELETE FROM UniqueID WHERE TableName = 'LinkTemplate'
DELETE FROM UniqueID WHERE TableName = 'VideoArchive'
DELETE FROM UniqueID WHERE TableName = 'UserName'
DELETE FROM UniqueID WHERE TableName = 'Usage'
DELETE FROM UniqueID WHERE TableName = 'Artifact'
DELETE FROM UniqueID WHERE TableName = 'Association'
DELETE FROM UniqueID WHERE TableName = 'PhysicalData'
DELETE FROM UniqueID WHERE TableName = 'VideoArchiveSet'
DELETE FROM UniqueID WHERE TableName = 'Media'
