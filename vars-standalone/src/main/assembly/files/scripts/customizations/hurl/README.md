# README

This doc contains the steps performed to migrate the HURL VARS database to a more current version

## Preparation

- Edit _derbySetup_ and change the host to be `DERBY_SERVER_HOST=localhost`
- Delete the old derby jars and replace with latest version

## Database Migration

1. Run `update-hurlCastorToJPA1-anno.sql`
2. In `gsh` run `DestroyDuplicateFKFunction` and `CombineDuplicatesFunction`
3. Run `update-hurlCastorToJPA2-anno.sql`
4. Run `update-hurlCastorToJPA1-kb.sql`
