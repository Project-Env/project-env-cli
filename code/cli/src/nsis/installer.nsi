!define PRODUCT_NAME "ProjectEnv.ProjectEnvCli"
!define PRODUCT_VERSION "@project.version@"
!define PRODUCT_PUBLISHER "repolevedavaj"
!define PRODUCT_WEB_SITE "https://projectenv.io"
!define PRODUCT_DIR_REGKEY "Software\Microsoft\Windows\CurrentVersion\App Paths\project-env-cli.exe"
!define PRODUCT_UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
!define PRODUCT_UNINST_ROOT_KEY "HKLM"

Name "${PRODUCT_NAME} ${PRODUCT_VERSION}"
OutFile "@project.build.directory@\cli-@project.version@-@cli.assembly.id@-setup.exe"
InstallDir "$PROGRAMFILES64\Project-Env\cli"
Icon "@project.build.directory@\nsis\installer.ico"
UninstallIcon "${NSISDIR}\Contrib\Graphics\Icons\nsis1-uninstall.ico"
SilentInstall silent
SilentUninstall silent
InstallDirRegKey HKLM "${PRODUCT_DIR_REGKEY}" ""

Section "Hauptgruppe" SEC01
  SetOutPath "$INSTDIR"
  SetOverwrite on
  File "@project.build.directory@\project-env-cli.exe"
  File "@project.build.directory@\vcruntime140.dll"
  File "@project.build.directory@\vcruntime140_1.dll"
  EnVar::SetHKLM
  EnVar::AddValue "Path" "$INSTDIR"
SectionEnd

Section -Post
  WriteUninstaller "$INSTDIR\uninst.exe"
  WriteRegStr HKLM "${PRODUCT_DIR_REGKEY}" "" "$INSTDIR\project-env-cli.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\uninst.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayIcon" "$INSTDIR\project-env-cli.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "URLInfoAbout" "${PRODUCT_WEB_SITE}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
SectionEnd

Section Uninstall
  EnVar::SetHKLM
  EnVar::DeleteValue "Path" "$INSTDIR"
  Delete "$INSTDIR\uninst.exe"
  Delete "$INSTDIR\vcruntime140_1.dll"
  Delete "$INSTDIR\vcruntime140.dll"
  Delete "$INSTDIR\project-env-cli.exe"

  RMDir "$INSTDIR"

  DeleteRegKey ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}"
  DeleteRegKey HKLM "${PRODUCT_DIR_REGKEY}"
  SetAutoClose true
SectionEnd
