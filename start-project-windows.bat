@echo off
setlocal EnableDelayedExpansion

echo ===============================================
echo    RUNTON COLOR - DEMARRAGE DU PROJET
echo ===============================================

echo.
echo [1/6] Arret des processus Java/Tomcat en cours...
taskkill /f /im "java.exe" 2>nul || echo Aucun processus Java en cours

echo.
echo [2/6] Nettoyage du cache Maven et recompilation...
if exist "target" (
    rmdir /s /q "target"
    echo Repertoire target supprime
)

if exist "%USERPROFILE%\.m2\repository\fr\esgi\runton-color" (
    rmdir /s /q "%USERPROFILE%\.m2\repository\fr\esgi\runton-color"
    echo Cache Maven nettoye
)

echo.
echo [3/6] Reinitialisation de la base de donnees...
rem Note: Les scripts update-database.sh et insert-test-data.sh ne sont pas presents
rem Vous devrez executer manuellement les scripts SQL dans le repertoire db/ si necessaire
echo Scripts de base de donnees disponibles dans le repertoire db/:
dir /b db\*.sql

echo.
echo [4/6] Compilation du projet avec Maven...
call mvn clean package
if errorlevel 1 (
    echo ERREUR: La compilation a echoue!
    echo Verifiez que Maven est installe et configure correctement.
    pause
    exit /b 1
)

echo.
echo [5/6] Verification du port 8090...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8090"') do (
    set "pid=%%a"
    if defined pid (
        echo Port 8090 utilise par le processus !pid!. Arret du processus...
        taskkill /f /pid !pid! 2>nul
        timeout /t 2 /nobreak >nul
    )
)

echo.
echo [6/6] Demarrage de Tomcat sur le port 8090...
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