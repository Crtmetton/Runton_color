@echo off

echo ===============================================
echo   RUNTON COLOR - DEMARRAGE RAPIDE
echo ===============================================

echo.
echo Verification du port 8090...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8090" 2^>nul') do (
    set "pid=%%a"
    if defined pid (
        echo Port 8090 utilise par le processus %%a. Arret du processus...
        taskkill /f /pid %%a 2>nul
        timeout /t 2 /nobreak >nul
    )
)

echo.
echo Demarrage de Tomcat sur le port 8090...
echo.
echo ===============================================
echo   APPLICATION DEMARREE
echo   URL: http://localhost:8090/runton-color
echo ===============================================
echo.
echo Appuyez sur Ctrl+C pour arreter le serveur
echo.

call mvn tomcat7:run -Dmaven.tomcat.port=8090

echo.
echo Le serveur a ete arrete.
pause 