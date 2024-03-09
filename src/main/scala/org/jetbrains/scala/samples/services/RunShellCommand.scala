package org.jetbrains.scala.samples.services

import sys.process._

object ShellCommand {

    def runShellCommand(command: String): String = command.!!

    def pwd(): String = runShellCommand("pwd")

    def dot(): String = runShellCommand("dot --help")

}
