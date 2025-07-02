@echo off

echo ===================================================
echo     ACCES A LA BASE DE DONNEES H2 COLOR RUN
echo ===================================================
echo.

rem Chemin vers la base
set "DB_PATH=.\dbFiles\Runton_color_Prod"
set "H2_JAR=%USERPROFILE%\.m2\repository\com\h2database\h2\2.1.214\h2-2.1.214.jar"

echo 🗄️  Base de donnees : %DB_PATH%
echo 👤 Utilisateur : Runton
echo 🔑 Mot de passe : (vide)
echo.

rem Verifier si H2 est telecharge
if not exist "%H2_JAR%" (
    echo ⚠️  H2 JAR non trouve. Telechargement via Maven...
    call mvn dependency:get -Dartifact=com.h2database:h2:2.1.214
    if errorlevel 1 (
        echo ERREUR: Impossible de telecharger H2. Verifiez Maven.
        pause
        exit /b 1
    )
)

echo 🚀 Demarrage de la console H2...
echo 🌐 Console disponible sur : http://localhost:8082
echo.
echo Configuration de connexion :
echo   JDBC URL: jdbc:h2:%CD%/dbFiles/Runton_color_Prod
echo   User Name: Runton
echo   Password: (laissez vide)
echo.
echo Appuyez sur Ctrl+C pour arreter la console
echo.

rem Demarrer la console H2
java -cp "%H2_JAR%" org.h2.tools.Server -web -browser -webPort 8082

echo.
echo La console H2 a ete arretee.
pause 