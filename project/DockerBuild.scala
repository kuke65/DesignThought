/*
package sbtdocker

import sbt._
import staging.{DockerfileProcessor, StagedDockerfile}

import scala.sys.process.{Process, ProcessLogger}

object DockerBuildFixed {
  /**
   * Build a Dockerfile using a provided docker binary.
   *
   * @param dockerfile Dockerfile to build
   * @param processor processor to create a staging directory for the Dockerfile
   * @param imageNames names of the resulting image
   * @param stageDir stage dir
   * @param dockerPath path to the docker binary
   * @param buildOptions options for the build command
   * @param log logger
   */
  def apply(dockerfile: DockerfileLike, processor: DockerfileProcessor, imageNames: Seq[ImageName],
            buildOptions: BuildOptions, stageDir: File, dockerPath: String, log: Logger): ImageId = {
    val staged = processor(dockerfile, stageDir)

    apply(staged, imageNames, buildOptions, stageDir, dockerPath, log)
  }

  /**
   * Build a Dockerfile using a provided docker binary.
   *
   * @param staged a staged Dockerfile to build.
   * @param imageNames names of the resulting image
   * @param stageDir stage dir
   * @param dockerPath path to the docker binary
   * @param buildOptions options for the build command
   * @param log logger
   */
  def apply(staged: StagedDockerfile, imageNames: Seq[ImageName], buildOptions: BuildOptions, stageDir: File, dockerPath: String, log: Logger): ImageId = {
    log.debug("Building Dockerfile:\n" + staged.instructionsString)

    log.debug(s"Preparing stage directory '${stageDir.getPath}'")

    clean(stageDir)
    createDockerfile(staged, stageDir)
    prepareFiles(staged)
    buildAndTag(imageNames, stageDir, dockerPath, buildOptions, log)
  }

  private[sbtdocker] def clean(stageDir: File) = {
    IO.delete(stageDir)
  }

  private[sbtdocker] def createDockerfile(staged: StagedDockerfile, stageDir: File) = {
    IO.write(stageDir / "Dockerfile", staged.instructionsString)
  }

  private[sbtdocker] def prepareFiles(staged: StagedDockerfile) = {
    staged.stageFiles.foreach {
      case (source, destination) =>
        source.stage(destination)
    }
  }

  private val SuccessfullyBuilt = "^Successfully built ([0-9a-f]+)$".r

  private[sbtdocker] def buildAndTag(imageNames: Seq[ImageName], stageDir: File, dockerPath: String, buildOptions: BuildOptions, log: Logger): ImageId = {
    val processLogger = ProcessLogger({ line =>
      log.info(line)
    }, { line =>
      log.info(line)
    })

    val imageId = build(stageDir, dockerPath, buildOptions, log, processLogger)

    imageNames.foreach { name =>
      DockerTag(imageId, name, dockerPath, log)
    }
    
    imageId
  }

  private[sbtdocker] def build(stageDir: File, dockerPath: String, buildOptions: BuildOptions, log: Logger, processLogger: ProcessLogger): ImageId = {
    val flags = buildFlags(buildOptions)
    val command = dockerPath :: "build" :: flags ::: "." :: Nil
    log.debug(s"Running command: '${command.mkString(" ")}' in '${stageDir.absString}'")

    val processOutput = Process(command, stageDir).lineStream(processLogger)
    processOutput.foreach { line =>
      log.info(line)
    }

    val imageId = processOutput.collect {
      case SuccessfullyBuilt(id) => ImageId(id)
    }.lastOption

    imageId match {
      case Some(id) =>
        id
      case None =>
        sys.error("Could not parse image id")
    }
  }

  private[sbtdocker] def buildFlags(buildOptions: BuildOptions): List[String] = {
    val cacheFlag = "--no-cache=" + !buildOptions.cache
    val removeFlag = {
      buildOptions.removeIntermediateContainers match {
        case BuildOptions.Remove.Always =>
          "--force-rm=true"
        case BuildOptions.Remove.Never =>
          "--rm=false"
        case BuildOptions.Remove.OnSuccess =>
          "--rm=true"
      }
    }
    val pullFlag = {
      val value = buildOptions.pullBaseImage match {
        case BuildOptions.Pull.Always => true
        case BuildOptions.Pull.IfMissing => false
      }
      "--pull=" + value
    }
    //workaround https://github.com/marcuslonnberg/sbt-docker/issues/74
    val addHosts = List("--add-host", "jenkins.scala-sbt.org:127.0.0.1")

    cacheFlag :: removeFlag :: pullFlag :: addHosts
  }
}
*/
