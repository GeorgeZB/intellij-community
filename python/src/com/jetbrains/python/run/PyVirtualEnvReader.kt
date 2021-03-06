/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jetbrains.python.run

import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.EnvironmentUtil
import java.io.File

/**
 * @author traff
 */


class PyVirtualEnvReader(virtualEnvSdkPath: String) : EnvironmentUtil.ShellEnvReader() {
  val activate = findActivateScript(virtualEnvSdkPath, shell)

  override fun readShellEnv(): MutableMap<String, String> {
    if (SystemInfo.isUnix) {
      return super.readShellEnv()
    }
    else {
      return readVirtualEnvOnWindows();
    }
  }

  private fun readVirtualEnvOnWindows(): MutableMap<String, String> {
    val activateFile = FileUtil.createTempFile("pycharm-virualenv-activate.", ".bat", false)
    val envFile = FileUtil.createTempFile("pycharm-virualenv-envs.", ".tmp", false)
    try {
      FileUtil.copy(File(activate), activateFile);
      FileUtil.appendToFile(activateFile, "\n\nset")
      val command = listOf<String>(activateFile.path, ">", envFile.absolutePath)

      return runProcessAndReadEnvs(command, envFile, "\r\n")
    }
    finally {
      FileUtil.delete(activateFile)
      FileUtil.delete(envFile)
    }

  }

  override fun getShellProcessCommand(): MutableList<String>? {
    val shellPath = shell

    if (shellPath == null || !File(shellPath).canExecute()) {
      throw Exception("shell:" + shellPath)
    }

    return if (activate != null) mutableListOf(shellPath, "--rcfile", activate, "-i")
    else super.getShellProcessCommand()
  }

}

fun findActivateScript(path: String?, shellPath: String?): String? {
  val shellName = if (shellPath != null) File(shellPath).name else null
  val activate = if (SystemInfo.isWindows) File(File(path).parentFile, "activate.bat")
  else if (shellName == "fish" || shellName == "csh") File(File(path).parentFile, "activate." + shellName)
  else File(File(path).parentFile, "activate")

  return if (activate.exists()) activate.absolutePath else null
}