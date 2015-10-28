package org.mbari.vars.integration

import java.time.Instant
import java.util.Date

import com.google.inject.{Guice, Injector}
import org.junit.runner.RunWith
import org.mbari.vcr4j.time.Timecode
import org.scalatest.junit.JUnitRunner
import org.scalatest.{PrivateMethodTester, Matchers, FlatSpec}
import vars.ToolBox
import vars.annotation.jpa.{AnnotationFactoryImpl}
import vars.annotation.{AnnotationDAOFactory, VideoArchiveSet}
import vars.jpa.VarsJpaTestModule
import scala.collection.JavaConverters._

/**
 *
 *
 * @author Brian Schlining
 * @since 2015-10-13T10:03:00
 */
@RunWith(classOf[JUnitRunner])
class GenericMergeSpec extends FlatSpec with Matchers with PrivateMethodTester {

  val loadGenericData = PrivateMethod[Seq[GenericData]]('loadGenericData)
  val url = getClass.getResource("/org/mbari/vars/integration/genericmergedata.csv")
  val eps = 0.00001
  val deltaSeconds = 7.5

  val injector: Injector = Guice.createInjector(new VarsJpaTestModule)
  implicit val daoFactory: AnnotationDAOFactory = injector.getInstance(classOf[AnnotationDAOFactory])

  "GenericMerge" should "parse a CSV log correctly" in {
    val gm = new GenericMerge(url)
    val data = gm invokePrivate loadGenericData()
    data.size should be(20)
    val datum = data.head
    datum.depth should be(100F +- eps.toFloat)
    datum.salinity should be(35.1F +- eps.toFloat)
    datum.temperature should be (10F +- eps.toFloat)
    datum.latitude should be (36D +- eps)
    datum.longitude should be(-121D +- eps)
    datum.date should be (GenericMergeSpec.MIN_DATE)
  }

  it should "collate with VideoFrames correctly" in {

    // --- Insert data into database
    val vas = GenericMergeSpec.newVideoArchiveSet
    val dao = daoFactory.newVideoArchiveSetDAO()
    dao.startTransaction()
    dao.persist(vas)
    dao.endTransaction()

    // --- Collate from database
    val gm = new GenericMerge(url)
    val data = gm.collate(GenericMergeSpec.DEFAULT_NAME, deltaSeconds)
    dao.close()

    // --- Evaluate the collation
    data.size should be (11)
    for ((vf, gdOpt) <- data.init) {
      gdOpt shouldBe 'isDefined
    }

    data.last._2 shouldBe 'isEmpty

    // --- Check that dates align
    val deltaMillis = math.round(deltaSeconds * 1000)
    for ((vf, gdOpt) <- data.init) {
      vf.getRecordedDate.getTime should be (gdOpt.get.date.getTime +- deltaMillis)
    }

  }

  it should "update values in the database correctly" in {
    // --- Insert data into database
    // Data was inserted in spec above

    // --- Do merge
    val gm = new GenericMerge(url)
    gm(GenericMergeSpec.DEFAULT_NAME, deltaSeconds)

    // --- Lookup data and verify that it was merged
    val dao = daoFactory.newVideoArchiveDAO()
    dao.startTransaction()
    val va = dao.findByName(GenericMergeSpec.DEFAULT_NAME)
    val videoFrames = va.getVideoFrames.asScala.sortBy(_.getRecordedDate)

    // Check data that had a matching log record
    for (vf <- videoFrames.init) {
      val pd = vf.getPhysicalData
      pd.getDepth should not be null
      pd.getLatitude should not be null
      pd.getLongitude should not be null
      pd.getTemperature should not be null
      pd.getSalinity should not be null
      pd.getOxygen should not be null
    }

    // Check data that did not have a matching record
    val vf = videoFrames.last
    val pd = vf.getPhysicalData
    pd.getDepth should be (null) // Depth was explicitly set to a value. Should be null after merge though
    pd.getLatitude should be (null)
    pd.getLongitude should be (null)
    pd.getTemperature should be (null)
    pd.getSalinity should be (null)
    pd.getOxygen should be (null)

  }

  it should "merge with data missing a column" in {

    // --- Put dataset in database
    val name = "http://copacetic.ravioli"
    val vas = GenericMergeSpec.newVideoArchiveSet
    vas.getVideoArchives.asScala.head.setName(name)
    val dao = daoFactory.newVideoArchiveSetDAO()
    dao.startTransaction()
    dao.persist(vas)
    dao.endTransaction()

    // --- Merge
    val newUrl = getClass.getResource("/org/mbari/vars/integration/genericmergedata-missing02.csv")
    val gm = new GenericMerge(newUrl)
    gm(GenericMergeSpec.DEFAULT_NAME, deltaSeconds)

    // --- Evaluate
    val vfDao = daoFactory.newVideoArchiveDAO(dao.getEntityManager)
    dao.startTransaction()
    val va = vfDao.findByName(GenericMergeSpec.DEFAULT_NAME)
    val videoFrames = va.getVideoFrames.asScala.sortBy(_.getRecordedDate)
    dao.endTransaction()
    // Check data that had a matching log record
    for (vf <- videoFrames.init) {
      val pd = vf.getPhysicalData
      pd.getDepth should not be null
      pd.getLatitude should not be null
      pd.getLongitude should not be null
      pd.getTemperature should not be null
      pd.getSalinity should not be null
      pd.getOxygen should be (null)
    }
    dao.close()

  }

  it should "merge with sporadically missing or bad values in columns" in {

    // --- Put dataset in database
    val name = "http://paranha.serenity"
    val vas = GenericMergeSpec.newVideoArchiveSet
    vas.getVideoArchives.asScala.head.setName(name)
    val dao = daoFactory.newVideoArchiveSetDAO()
    dao.startTransaction()
    dao.persist(vas)
    dao.endTransaction()

    // --- Merge
    val newUrl = getClass.getResource("/org/mbari/vars/integration/genericmergedata-missingvalues.csv")
    val gm = new GenericMerge(newUrl)
    gm(GenericMergeSpec.DEFAULT_NAME, deltaSeconds)

    // --- Evaluate
    val vfDao = daoFactory.newVideoArchiveDAO(dao.getEntityManager)
    dao.startTransaction()
    val va = vfDao.findByName(GenericMergeSpec.DEFAULT_NAME)
    val videoFrames = va.getVideoFrames.asScala.sortBy(_.getRecordedDate)
    dao.endTransaction()
    // Check data that had a matching log record
    for (vf <- videoFrames.init) {
      val pd = vf.getPhysicalData
      pd.getDepth should not be null
      pd.getLatitude should not be null
      pd.getLongitude should not be null
      pd.getSalinity should not be null
      pd.getOxygen should not be (null)
    }
    dao.close()

  }

}

object GenericMergeSpec {

  private[this] val toolbox = new ToolBox
  private[this] val factory = new AnnotationFactoryImpl()

  val MIN_DATE = Date.from(Instant.parse("2010-01-01T00:00:00Z"))
  val DEFAULT_NAME = "http://foo.com/mymovie.mov"

  def newVideoArchiveSet: VideoArchiveSet = {

    val vas = factory.newVideoArchiveSet()
    vas.setPlatformName("Test Platform")
    val cd = factory.newCameraDeployment()
    cd.setSequenceNumber(1)
    vas.addCameraDeployment(cd)
    val va = factory.newVideoArchive()
    va.setName(DEFAULT_NAME)
    vas.addVideoArchive(va)
    for (i <- 0 to 9) {
      val vf = factory.newVideoFrame()
      val tc = new Timecode(i * 10 * 30, 30)
      vf.setTimecode(tc.toString)
      vf.setAlternateTimecode(tc.toString)
      vf.setRecordedDate(new Date(math.round(MIN_DATE.getTime + tc.getSeconds * 1000D)))
      va.addVideoFrame(vf)
    }

    // --- Create an outlier
    val vf = factory.newVideoFrame()
    val tc = new Timecode("23:59:59:00", 30)
    vf.setTimecode(tc.toString)
    vf.setAlternateTimecode(tc.toString)
    vf.setRecordedDate(new Date(math.round(MIN_DATE.getTime + tc.getSeconds * 1000D)))
    vf.getPhysicalData.setDepth(1000F)
    va.addVideoFrame(vf)
    vas
  }

}
