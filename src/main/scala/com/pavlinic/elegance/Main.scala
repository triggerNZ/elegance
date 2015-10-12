package com.pavlinic.elegance

import ammonite.ops._
import com.pavlinic.elegance.Node.RichNode

import com.twitter.util.Eval

import scala.tools.nsc.Global
import scalaz._
import Scalaz._
import effect._

import scalariform._
import parser._

import IOUtils._
import Checker._
import util._

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
        _      <- doCheck(files)(config.rules)
      } yield ()
      res.except(die(_))
    } else die(s"$configFile is required")
  }
}

object FileFinder {
  def scalaFiles(wd: Path): List[Path] = (ls.rec! wd).filter(_.ext === "scala").toList
}

object IOChecker {
  import Parser._
  def doCheck(files: List[Path])(implicit rules: Seq[Rule]): IO[Unit] = {
    val seq: List[IO[Unit]] = files.map { f=>
      val astOpt = parseFile(f)
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
      case UnfixableRuleFailure(r, n, positions) => positions.map(pos => s"${n.file}:${pos.lineNumber}: Unfixable: ${r.message} ".red)
      case FixableRuleFailure  (r, n, positions) => positions.map(pos => s"${n.file}:${pos.lineNumber}: Fixable :  ${r.message} ".yellow)
      case _                          => Seq()
    }
  }
}

object Parser {
  def parseFile(f: Path): Option[RichNode] = CodeFile(f, read! f).parse
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

