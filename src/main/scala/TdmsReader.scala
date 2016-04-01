import java.io.File
import tdms.{TdmsChannel, TdmsGroup, TdmsParser}

/**
  * Created by dnezh on 3/29/16.
  */

object TdmsReader {

  def main(args: Array[String]): Unit = {
    System.loadLibrary("tdms")

    getListOfFiles(args(0)).foreach { f =>
      val path = f.getPath
      println("Processed file: " + path + "...")
      convTdms2Json(path, replaceToJsonExt(path))
    }
  }

  def convTdms2Json(fileName: String, outFile: String, isVerbose: Boolean = false): Unit = {

    import org.json4s._
    import org.json4s.JsonDSL._
    import org.json4s.jackson.JsonMethods._
//    import org.json4s.native.Serialization
//    import org.json4s.native.Serialization.{write, writePretty}
//    import org.json4s.NoTypeHints

    val parser = new TdmsParser(fileName)
    if(parser.fileOpeningError == 1) return

    parser.read(isVerbose)

//    implicit val formats = Serialization.formats(NoTypeHints)
    val output = new java.io.BufferedWriter(new java.io.FileWriter(new java.io.File(outFile)))

    val res = getTdmsGroups(parser).map { g =>
        "group" ->
          ("name" -> g.getName) ~
            ("channels" -> getTdmsChannels(g).map { c =>
              if (isVerbose) printf("Processing channel %s\n", c.getName)
              "channels" ->
                ("name" -> c.getName) ~
                  ("unit" -> c.getUnit) ~
                  ("dataType" -> c.getDataType) ~
                  ("data" -> getTdmsDataVector(c))
            })
    }

    output.write(compact(render(res)))
    output.flush
    output.close
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

  def replaceToJsonExt(path: String): String = {
    path.replaceAll("\\.[^.]*$", ".json")
  }
}
