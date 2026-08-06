@ECHO OFF
WHERE gradle >NUL 2>NUL
IF ERRORLEVEL 1 (
  ECHO Gradle no esta instalado. Abra el proyecto con Android Studio o instale Gradle 8.9+.
  EXIT /B 1
)
gradle %*
