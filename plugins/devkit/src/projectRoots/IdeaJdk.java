/*
 * Copyright 2000-2005 JetBrains s.r.o.
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
package org.jetbrains.idea.devkit.projectRoots;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.devkit.DevKitBundle;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

/**
 * User: anna
 * Date: Nov 22, 2004
 */
public class IdeaJdk extends SdkType implements ApplicationComponent {
  public static final Icon ADD_SDK = IconLoader.getIcon("/add_sdk.png");
  public static final Icon SDK_OPEN = IconLoader.getIcon("/sdk_open.png");
  public static final Icon SDK_CLOSED = IconLoader.getIcon("/sdk_closed.png");

  private static final Logger LOG = Logger.getInstance("#org.jetbrains.idea.devkit.projectRoots.IdeaJdk");
  @NonNls private static final String JAVA_HOME_PROPERTY = "java.home";
  @NonNls private static final String LIB_DIR_NAME = "lib";
  @NonNls private static final String SRC_DIR_NAME = "src";
  @NonNls private static final String JRE_DIR_NAME = "jre";

  public IdeaJdk() {
    super("IDEA JDK");
  }

  public Icon getIcon() {
    return SDK_CLOSED;
  }

  public Icon getIconForExpandedTreeNode() {
    return SDK_OPEN;
  }

  public Icon getIconForAddAction() {
    return ADD_SDK;
  }

  public String suggestHomePath() {
    return PathManager.getHomePath().replace(File.separatorChar, '/');
  }

  public boolean isValidSdkHome(String path) {
    if (isFromIDEAProject(path)) {
      return true;
    }
    File home = new File(path);
    if (!home.exists()) {
      return false;
    }
    @NonNls final String openapiJar = "openapi.jar";
    if (getBuildNumber(path) == null || !new File(new File(home, LIB_DIR_NAME), openapiJar).exists()) {
      return false;
    }
    return true;
  }

  public static boolean isFromIDEAProject(String path) {
    File home = new File(path);
    File[] openapiDir = home.listFiles(new FileFilter() {
      public boolean accept(File pathname) {
        @NonNls final String name = pathname.getName();
        if (name.equals("openapi") && pathname.isDirectory()) return true; //todo
        return false;
      }
    });
    if (openapiDir == null || openapiDir.length == 0) {
      return false;
    }
    return true;
  }

  @Nullable
  public final String getVersionString(final String sdkHome) {
    final Sdk internalJavaSdk = getInternalJavaSdk(sdkHome);
    return internalJavaSdk != null ? internalJavaSdk.getVersionString() : null;
  }

  @Nullable
  private String getInternalToolsPath(final String sdkHome){
    @NonNls final String toolsJar = "tools.jar";
    if (SystemInfo.isLinux || SystemInfo.isWindows) {
      final File tools = new File(new File(new File(sdkHome, JRE_DIR_NAME), LIB_DIR_NAME), toolsJar);
      if (tools.exists()){
        return tools.getPath();
      }
    }

    final String javaHome = System.getProperty(JAVA_HOME_PROPERTY);
    File tools = new File(new File(javaHome, LIB_DIR_NAME), toolsJar);
    if (tools.exists()){ // java home points to jdk
      return tools.getPath();
    } else {
      tools = new File(new File (new File(javaHome).getParentFile(), LIB_DIR_NAME), toolsJar);
      if (tools.exists()){
        return tools.getPath();
      }
    }
    ProjectJdk jdk = JavaSdk.getInstance().createJdk("", javaHome);
    if (jdk.getVersionString() != null){
      return jdk.getToolsPath();
    }
    return null;
  }

  @Nullable
  private String getInternalRtPath(final String homePath) {
    @NonNls String rtPath;
    @NonNls final String rtJar = "rt.jar";
    if (SystemInfo.isLinux || SystemInfo.isWindows) {
      rtPath = homePath + File.separator + JRE_DIR_NAME + File.separator + LIB_DIR_NAME + File.separator + rtJar;
      if (new File(rtPath).exists()) {
        return rtPath;
      }
    }
    final String javaHome = System.getProperty(JAVA_HOME_PROPERTY);
    File rt = new File(new File(javaHome, LIB_DIR_NAME), rtJar);
    if (rt.exists()){ // java home points to jre
      return rt.getPath();
    } else {
      rt = new File(new File (new File(javaHome, JRE_DIR_NAME), LIB_DIR_NAME), rtJar);
      if (rt.exists()){
        return rt.getPath();
      }
    }
    return null;
  }

  @Nullable
  private Sdk getInternalJavaSdk(final String sdkHome) {
    String jreHome = getJreHome(sdkHome);
    ProjectJdk internalJdk = JavaSdk.getInstance().createJdk("", jreHome);
    if (internalJdk.getVersionString() != null){ //internal jdk is valid
      final String internalToolsPath = getInternalToolsPath(sdkHome);
      if (internalToolsPath != null) {
        final VirtualFile tools = LocalFileSystem.getInstance().findFileByPath(FileUtil.toSystemIndependentName(internalToolsPath));
        if (tools != null) {
          final SdkModificator sdkModificator = internalJdk.getSdkModificator();
          sdkModificator.addRoot(tools, ProjectRootType.CLASS);
          sdkModificator.commitChanges();
        }
      }
    } else {
      return null;
    }
    return internalJdk;
  }

  private String getJreHome(final String sdkHome) {
    @NonNls String jreHome;
    if (SystemInfo.isLinux || SystemInfo.isWindows) {
      jreHome = sdkHome + File.separator + JRE_DIR_NAME;
      if (new File(jreHome).exists()){
        return jreHome;
      }
    }
    return System.getProperty(JAVA_HOME_PROPERTY);
  }

  public String suggestSdkName(String currentSdkName, String sdkHome) {
    @NonNls final String idea = "IDEA ";
    return idea + (getBuildNumber(sdkHome) != null ? getBuildNumber(sdkHome) : "");
  }

  private String getBuildNumber(String ideaHome) {
    try {
      @NonNls final String buildTxt = "/build.txt";
      BufferedReader reader = new BufferedReader(new FileReader(ideaHome + buildTxt));
      return reader.readLine().trim();
    }
    catch (IOException e) {
      return null;
    }

  }

  private VirtualFile[] getIdeaLibrary(String home) {
    ArrayList<VirtualFile> result = new ArrayList<VirtualFile>();
    JarFileSystem jfs = JarFileSystem.getInstance();
    File lib = new File(home + File.separator + LIB_DIR_NAME);
    File[] jars = lib.listFiles();
    if (jars != null) {
      for (File jar : jars) {
        @NonNls String name = jar.getName();
        if (jar.isFile() && !name.equals("idea.jar") && (name.endsWith(".jar") || name.endsWith(".zip"))) {
          result.add(jfs.findFileByPath(jar.getPath() + JarFileSystem.JAR_SEPARATOR));
        }

      }
    }
    return result.toArray(new VirtualFile[result.size()]);
  }


  public void setupSdkPaths(Sdk sdk) {
    final SdkModificator sdkModificator = sdk.getSdkModificator();
    final File home = new File(sdk.getHomePath());

    //roots from internal jre
    addClasses(sdkModificator);
    addDocs(sdkModificator);
    addSources(sdkModificator);

    //roots for openapi and other libs
    if (!isFromIDEAProject(sdk.getHomePath())) {
      final VirtualFile[] ideaLib = getIdeaLibrary(sdk.getHomePath());
      if (ideaLib != null) {
        for (VirtualFile aIdeaLib : ideaLib) {
          sdkModificator.addRoot(aIdeaLib, ProjectRootType.CLASS);
        }
      }
      addSources(home, sdkModificator);
      addDocs(home, sdkModificator);
    }
    sdkModificator.commitChanges();
  }

  public static void addSources(File file, SdkModificator sdkModificator) {
    final File src = new File(new File(file, LIB_DIR_NAME), SRC_DIR_NAME);
    if (!src.exists()) return;
    File[] srcs = src.listFiles(new FileFilter() {
      public boolean accept(File pathname) {
        @NonNls final String path = pathname.getPath();
        if (path.indexOf("generics") > -1) return false;
        if (path.endsWith(".jar") || path.endsWith(".zip")) return true;
        return false;
      }
    });
    for (int i = 0; srcs != null && i < srcs.length; i++) {
      File jarFile = srcs[i];
      if (jarFile.exists()) {
        JarFileSystem jarFileSystem = JarFileSystem.getInstance();
        String path = jarFile.getAbsolutePath().replace(File.separatorChar, '/') + JarFileSystem.JAR_SEPARATOR;
        jarFileSystem.setNoCopyJarForPath(path);
        VirtualFile vFile = jarFileSystem.findFileByPath(path);
        sdkModificator.addRoot(vFile, ProjectRootType.SOURCE);
      }
    }
  }

  public static void addDocs(File file, SdkModificator sdkModificator) {
    @NonNls final String help = "help";
    @NonNls final String openapi = "openapi";
    File docFile = new File(new File(file, help), openapi);
    if (docFile.exists() && docFile.isDirectory()) {
      sdkModificator.addRoot(LocalFileSystem.getInstance().findFileByIoFile(docFile), ProjectRootType.JAVADOC);
      return;
    }
    @NonNls final String openapiHelpJar = "openapihelp.jar";
    File jarfile = new File(new File(file, help), openapiHelpJar);
    if (jarfile.exists()) {
      JarFileSystem jarFileSystem = JarFileSystem.getInstance();
      String path = jarfile.getAbsolutePath().replace(File.separatorChar, '/') + JarFileSystem.JAR_SEPARATOR + openapi;
      jarFileSystem.setNoCopyJarForPath(path);
      VirtualFile vFile = jarFileSystem.findFileByPath(path);
      sdkModificator.addRoot(vFile, ProjectRootType.JAVADOC);
    }
  }

  private void addClasses(SdkModificator sdkModificator) {
    addOrderEntries(OrderRootType.CLASSES, ProjectRootType.CLASS, getInternalJavaSdk(sdkModificator.getHomePath()), sdkModificator);
  }

  private void addDocs(SdkModificator sdkModificator) {
    if (!addOrderEntries(OrderRootType.JAVADOC, ProjectRootType.JAVADOC, getInternalJavaSdk(sdkModificator.getHomePath()), sdkModificator) &&
        SystemInfo.isMac){
      ProjectJdk [] jdks = ProjectJdkTable.getInstance().getAllJdks();
      for (ProjectJdk jdk : jdks) {
        if (jdk.getSdkType() instanceof JavaSdk) {
          addOrderEntries(OrderRootType.JAVADOC, ProjectRootType.JAVADOC, jdk, sdkModificator);
          break;
        }
      }
    }
  }

  private void addSources(SdkModificator sdkModificator) {
    final Sdk internalJavaSdk = getInternalJavaSdk(sdkModificator.getHomePath());
    if (internalJavaSdk != null) {
      if (!addOrderEntries(OrderRootType.SOURCES, ProjectRootType.SOURCE, internalJavaSdk, sdkModificator)){
        if (SystemInfo.isMac) {
          ProjectJdk [] jdks = ProjectJdkTable.getInstance().getAllJdks();
          for (ProjectJdk jdk : jdks) {
            if (jdk.getSdkType() instanceof JavaSdk) {
              addOrderEntries(OrderRootType.SOURCES, ProjectRootType.SOURCE, jdk, sdkModificator);
              break;
            }
          }
        }
        else {
          final File jdkHome = new File(internalJavaSdk.getHomePath()).getParentFile();
          @NonNls final String srcZip = "src.zip";
          final File jarFile = new File(jdkHome, srcZip);
          if (jarFile.exists()){
            JarFileSystem jarFileSystem = JarFileSystem.getInstance();
            String path = jarFile.getAbsolutePath().replace(File.separatorChar, '/') + JarFileSystem.JAR_SEPARATOR;
            jarFileSystem.setNoCopyJarForPath(path);
            sdkModificator.addRoot(jarFileSystem.findFileByPath(path), ProjectRootType.SOURCE);
          }
        }
      }
    }
  }

  private boolean addOrderEntries(OrderRootType orderRootType, ProjectRootType projectRootType, Sdk sdk, SdkModificator toModificator){
    boolean wasSmthAdded = false;
    final String[] entries = sdk.getRootProvider().getUrls(orderRootType);
    for (String entry : entries) {
      VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl(entry);
      toModificator.addRoot(virtualFile, projectRootType);
      wasSmthAdded = true;
    }
    return wasSmthAdded;
  }

  public AdditionalDataConfigurable createAdditionalDataConfigurable(final SdkModel sdkModel, SdkModificator sdkModificator) {
    return new IdeaJdkConfigurable();
  }

  @Nullable
  public String getBinPath(Sdk sdk) {
    final Sdk internalJavaSdk = getInternalJavaSdk(sdk.getHomePath());
    return internalJavaSdk == null ? null : JavaSdk.getInstance().getBinPath(internalJavaSdk);
  }

  @Nullable
  public String getToolsPath(Sdk sdk) {
    return getInternalToolsPath(sdk.getHomePath());
  }

  @Nullable
  public String getVMExecutablePath(Sdk sdk) {
    final Sdk internalJavaSdk = getInternalJavaSdk(sdk.getHomePath());
    return internalJavaSdk == null ? null : JavaSdk.getInstance().getVMExecutablePath(internalJavaSdk);
  }

  @Nullable
  public String getRtLibraryPath(Sdk sdk) {
    return getInternalRtPath(sdk.getHomePath());
  }

  public void saveAdditionalData(SdkAdditionalData additionalData, Element additional) {
    if (additionalData instanceof Sandbox) {
      try {
        ((Sandbox)additionalData).writeExternal(additional);
      }
      catch (WriteExternalException e) {
        LOG.error(e);
      }
    }
  }

  public SdkAdditionalData loadAdditionalData(Element additional) {
    Sandbox sandbox = new Sandbox();
    try {
      sandbox.readExternal(additional);
    }
    catch (InvalidDataException e) {
      LOG.error(e);
    }
    return sandbox;
  }

  public String getPresentableName() {
    return DevKitBundle.message("sdk.title");
  }

  public String getComponentName() {
    return getName();
  }

  public void initComponent() {}

  public void disposeComponent() {}
}
