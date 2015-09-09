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

val dives = Seq("V0148", "V0150", "V0155", "V0445", "V0670", "V0967", "V1705", 
    "V1732", "D0693", "V0188", "V0189", "V0200", "V0228", "V0265", "V0336", 
    "V0372", "V0376", "V0380", "V0380", "V0412", "V0417", "V0451", "V0513", 
    "V0513", "V0585", "V0637", "V0682", "V0710", "V0721", "V0754", "V0772", 
    "V0783", "V0788", "V0830", "V0845", "V0902", "V0974", "V0987", "V1019", 
    "V1039", "V1039", "V1107", "V1127", "V1128", "V1253", "V1253", "V1272", 
    "V1273", "V1315", "V1349", "V1405", "V1528", "V1582", "V1696", "V1705", "V1777", 
    "V2217", "V2613")

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



