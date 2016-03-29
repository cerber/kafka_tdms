import java.io.File

import net.liftweb.json._
import net.liftweb.json.Serialization._
import net.liftweb.json.JsonDSL._
import tdms.{TdmsChannel, TdmsGroup, TdmsParser}

/**
  * Created by dnezh on 3/29/16.
  */

object TdmsReader {

  def main(args: Array[String]): Unit = {
    System.loadLibrary("tdms")

    val fileName = getListOfFiles(args(0)).head.getAbsolutePath

    println(convTdms2Json(fileName))

  }

  def test(fileName: String): Unit = {
    println("FileName: " + fileName)

    val parser = new TdmsParser(fileName)

    if(parser.fileOpeningError == 1) {
      println("Input error: please provide a valid *.tdms file name!")
      sys.exit
    }

    parser.read(false)

    val groupCount = parser.getGroupCount
    printf("Number of groups: %d\n", groupCount)

    for (i <- 0 until groupCount.toInt) {
      val group = parser.getGroup(i)
      val channelCount = group.getGroupSize
      printf("Group %s has %d channels\n", group.getName, channelCount)

      for (j <- 0 until channelCount.toInt) {
        val channel = group.getChannel(j)
        printf("  Channel %s (unit: %s) has %d values, type: %d properties: %s",
          channel.getName,
          channel.getUnit,
          channel.getDataCount,
          channel.getDataType,
          channel.propertiesToString)
        val data = channel.getDataVector
        printf("\t% 10.2f ... % 10.2f\n", data.get(0), data.get((data.size - 1).toInt))
      }
    }
  }

  def convTdms2Json(fileName: String, isVerbose: Boolean = false): String = {
    val parser = new TdmsParser(fileName)
    if(parser.fileOpeningError == 1) {
      return ""
    }

    parser.read(isVerbose)
//    val groups = getTdmsGroups(parser)
//    val channels = groups.map(g => getTdmsChannels(g))
//    val data = channels.map(lc => lc.map(c => getTdmsDataVector(c)))


/*
    val d = getTdmsGroups(parser).map(group => {
      Group(group.getName, getTdmsChannels(group).map(channel => {
        Channel(channel.getName, channel.getUnit, channel.getDataType.toInt, getTdmsDataVector(channel))
      }))
    })
*/

    val d = getTdmsGroups(parser).map(group => {
      val groupName = group.getName
      val lc = getTdmsChannels(group)
      val c = lc.map(channel => {
        val channelName = channel.getName
        val channelUnit = channel.getUnit
        val channelDataType = channel.getDataType.toInt
        val dataVector = getTdmsDataVector(channel)
        Channel(channelName, channelUnit, channelDataType, dataVector)
      })
      Group(groupName, c)
    })


    implicit val formats = DefaultFormats
    val json = write(d)
    pretty(render(json))
  }

  def getTdmsGroups(parser: TdmsParser): List[TdmsGroup] = {
    for (i <- (0 until parser.getGroupCount.toInt).toList) yield parser.getGroup(i)
  }

  def getTdmsChannels(group: TdmsGroup): List[TdmsChannel] = {
    for (i <- (0 until group.getGroupSize.toInt).toList) yield group.getChannel(i)
  }

  def getTdmsDataVector(channel: TdmsChannel): List[Double] = {
    val data = channel.getDataVector
    for (i <- (0 until (data.size - 1).toInt).toList) yield data.get(i)
  }

  def getListOfFiles(dir: String): List[File] = {
    val d = new File(dir)
    if(d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }
}

case class Group(name: String, channels: List[Channel])
case class Channel(name: String, unit: String, dataType: Int, data: List[Double])