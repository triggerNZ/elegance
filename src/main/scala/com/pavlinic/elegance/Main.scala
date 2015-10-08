package com.pavlinic.elegance

import ammonite.ops._

import com.twitter.util.Eval

import scala.tools.nsc.Global
import scalaz._
import Scalaz._
import effect._

import scalariform._
import parser._

import IOUtils._
import Checker._

object Main extends SafeApp {
  import ConfigParser._
  import FileFinder._
  import IOChecker._

  override def runl(args: List[String]): IO[Unit] = {
    val wd = cwd
    val configFile = cwd / "elegance.config"

    if (exists(configFile)) {
      val res = for {
        config <- parseConfig(configFile)
        files   = scalaFiles(wd)
        _      <- doCheck(files)(config)
      } yield ()
      res.except(die(_))
    } else die(s"$configFile is required")
  }
}

object FileFinder {
  def scalaFiles(wd: Path): List[Path] = (ls.rec! wd).filter(_.ext === "scala").toList
}

object IOChecker {
  def doCheck(files: List[Path])(implicit config: EleganceConfig): IO[Unit] = {
    val seq: List[IO[Unit]] = files.map { f=>
      implicit val codeFile = CodeFile(f, read! f)
      val astOpt = ScalaParser.parse(codeFile.rawText, ScalaVersions.Scala_2_11.toString)

      astOpt.fold(die("If it doesn't parse, you have bigger problems than style")) { ast =>
        IO {
          val result = check(ast).filter(_.isFailure)
          result.flatMap { r =>
            errorMessages(r)
          }.foreach(println)

        }
      }
    }
    implicitly[Monad[IO]].sequence(seq) >> IO(())
  }

  def errorMessages(r: Result): Seq[String] = {
    r match {
      case UnfixableRuleFailure(r, n, positions) => positions.map(pos => s"${n.file}:${pos.lineNumber}: Unfixable: ${r.message} ")
      case FixableRuleFailure  (r, n) => Seq(s"${n.file}:${n.lineNumber}: Fixable :  ${r.message}} ")
      case _                          => Seq()
    }
  }
}

object ConfigParser {
  def parseConfig(configFile: Path): IO[EleganceConfig] = IO {
    val fileString = read(configFile)
    val snippet = s"""
       {
         import com.pavlinic.elegance.Rules._
         object conf extends com.pavlinic.elegance.EleganceConfig {
            $fileString
         }
         conf
       }
     """
    new Eval().apply[EleganceConfig](snippet)
  }
}

object IOUtils {
  def die(msg: String = ""): IO[Unit] = IO {
    println(msg)
    System.exit(1)
  }
  def die(e: Throwable): IO[Unit] = IO {
    e.printStackTrace()
    System.exit(1)
  }
}

