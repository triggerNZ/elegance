//   Copyright 2015 Tin Pavlinic
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
package com.pavlinic.elegance

import ammonite.ops._
import com.pavlinic.elegance.Node.RichNode

import com.twitter.util.Eval

import scala.tools.nsc.Global
import scalaz._
import Scalaz._
import effect._

import IOUtils._
import Checker._
import util._

object Main extends SafeApp {



  import ConfigParser._
  import FileFinder._
  import IOChecker._

  case class CliInput(fix: Boolean = false)

  def parseCli(args: List[String]): Option[CliInput] = {
    val parser = new scopt.OptionParser[CliInput]("elegance") {
      head("elegance", "0.1")
      opt[Unit]('f', "fix") action { (x, c) =>
        c.copy(fix = true)
      }
    }
    parser.parse(args, CliInput())
  }

  override def runl(args: List[String]): IO[Unit] = {
    val wd = cwd
    val configFile = cwd / "elegance.config"

    parseCli(args).fold(die("Could not parse command line args")) { cliInput =>
      if (exists(configFile)) {
        val res = for {
          config <- parseConfig(configFile)
          files = scalaFiles(wd)
          _ <- doCheck(files, cliInput.fix)(config.rules)
        } yield ()
        res.except(die(_))
      } else die(s"$configFile is required")
    }
  }
}

object FileFinder {
  def scalaFiles(wd: Path): List[Path] = (ls.rec! wd).filter(_.ext === "scala").toList
}

object IOChecker {
  import Parser._
  def doCheck(files: List[Path], fix: Boolean)(implicit rules: Seq[Rule]): IO[Unit] = {
    val seq: List[IO[Unit]] = files.map { f=>
      val astOpt = parseFile(f)
      astOpt.fold(die("If it doesn't parse, you have bigger problems than style")) { ast =>
        val result = check(ast).filter(_.isFailure)
        IO {
          result.flatMap { res =>
            errorMessages(res, fix)
          }.foreach(println)
        } >> IO {
          if (fix) {
            write.over(f, fixNode(ast).codeFile.rawText)
          }
        }
      }
    }
    implicitly[Monad[IO]].sequence(seq) >> IO(())
  }

  def errorMessages(r: Result, doFix: Boolean): Seq[String] = {
    r match {
      case UnfixableRuleFailure(r, n, positions) => positions.map { pos =>
        s"${n.file}:${pos.lineNumber}: Unfixable: ${r.message} ".red
      }
      case FixableRuleFailure  (r, n, positions) => positions.map { pos =>
        if (doFix) {
          s"${n.file}:${pos.lineNumber}: Fixing :  ${r.message} ".green
        } else {
          s"${n.file}:${pos.lineNumber}: Fixable :  ${r.message} ".yellow
        }
      }
      case _ => Seq()
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

