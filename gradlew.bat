@echo off
setlocal
set APP_HOME=%~dp0
where gradle >nul 2>nul
if %ERRORLEVEL%==0 (
  gradle -p "%APP_HOME%" %*
  exit /b %ERRORLEVEL%
)
echo ERROR: Gradle was not found on PATH.
echo Install Gradle 8.9 or open this project with Android Studio.
exit /b 1
