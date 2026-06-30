!include "MUI2.nsh"

Name "JCBudgetBuddy"
OutFile "JCBudgetBuddySetup.exe"
InstallDir "$PROGRAMFILES64\JCBudgetBuddy"
RequestExecutionLevel admin

Page directory
Page instfiles
UninstPage uninstConfirm
UninstPage instfiles

Section "Install"
  SetOutPath "$INSTDIR"
  File /r "..\build\package\*.*"
  CreateShortCut "$DESKTOP\JCBudgetBuddy.lnk" "$INSTDIR\JCBudgetBuddy.exe"
SectionEnd

Section "Uninstall"
  Delete "$DESKTOP\JCBudgetBuddy.lnk"
  RMDir /r "$INSTDIR"
SectionEnd
