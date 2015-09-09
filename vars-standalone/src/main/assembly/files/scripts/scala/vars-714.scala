#!/usr/bin/env scalas

/*
  Bug fix for https://mbari1.atlassian.net/browse/VARS-714
 */

/***
scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "net.sourceforge.jtds" % "jtds" % "1.3.1")

resolvers in ThisBuild ++= Seq(
  Resolver.mavenLocal)
*/

import java.sql._

val pwd = "vars4mbari"
val user = "varsuser"
val url = "jdbc:jtds:sqlserver://equinox.shore.mbari.org:1433/VARS"

val dives = Seq("V0285", "V0400", "V0429", "V0434", "V0449", "V0570", "V0774",
  "V0782", "V1285", "D0693", "V1755", "T0141", "V1796", "V1880", "V1893", 
  "D0319", "D0550", "D0612", "V3806", "V1428", "V1796", "V1880", "V3692", 
  "V3707", "V3742", "D0544", "V3827")

val connection = DriverManager.getConnection(url, user, pwd)

for (dive <- dives) {
  print(s"Setting recordedDates to NULL for $dive ... ")
  val sql = s"""
    | UPDATE
    |   VideoFrame 
    | SET
    |   RecordedDtg = NULL 
    | WHERE
    |   id IN ( 
    |     SELECT
    |       vf.id 
    |     FROM
    |       VideoFrame AS vf LEFT JOIN 
    |       VideoArchive AS va ON va.id = vf.VideoArchiveID_FK 
    |     WHERE
    |       va.videoArchiveName LIKE '${dive}%' 
    |   )""".stripMargin('|')
 
  val statement = connection.createStatement()
  val n = statement.executeUpdate(sql)
  print(s"$n rows were affected\n")
  statement.close()
}

connection.close()



