#!/bin/bash

# Script interactif pour tester toutes les configurations email
clear
echo "🧪 TESTEUR DE CONFIGURATION EMAIL - COLOR RUN"
echo "=============================================="
echo ""

show_menu() {
    echo "📧 Choisissez votre fournisseur d'email :"
    echo ""
    echo "1. 🔵 Outlook/Hotmail (Recommandé - plus simple)"
    echo "2. 🔴 Gmail (nécessite mot de passe app)"
    echo "3. 🟣 Yahoo Mail (nécessite mot de passe app)"
    echo "4. ⚙️  Serveur SMTP personnalisé"
    echo "5. 🧪 Mode test local (sans vraie connexion)"
    echo "6. 📋 Voir toutes les configurations"
    echo "0. ❌ Quitter"
    echo ""
    echo -n "Votre choix [1-6] : "
}

configure_outlook() {
    echo ""
    echo "🔵 CONFIGURATION OUTLOOK/HOTMAIL"
    echo "================================="
    echo ""
    echo -n "Email Outlook/Hotmail : "
    read outlook_email
    echo -n "Mot de passe : "
    read -s outlook_password
    echo ""
    
    export OUTLOOK_USERNAME="$outlook_email"
    export OUTLOOK_PASSWORD="$outlook_password"
    
    echo "✅ Configuration Outlook enregistrée"
    echo "📧 Email: $outlook_email"
    echo ""
    echo "🚀 Lancement avec Outlook..."
    ./start-email-config-outlook.sh
}

configure_gmail() {
    echo ""
    echo "🔴 CONFIGURATION GMAIL"
    echo "======================"
    echo ""
    echo "⚠️  Pour Gmail, vous devez :"
    echo "1. Activer l'authentification à 2 facteurs"
    echo "2. Créer un mot de passe d'application"
    echo "3. Utiliser ce mot de passe d'application (pas votre mot de passe normal)"
    echo ""
    echo -n "Email Gmail : "
    read gmail_email
    echo -n "Mot de passe d'application (16 caractères) : "
    read -s gmail_app_password
    echo ""
    
    export GMAIL_USERNAME="$gmail_email"
    export GMAIL_APP_PASSWORD="$gmail_app_password"
    
    echo "✅ Configuration Gmail enregistrée"
    echo "📧 Email: $gmail_email"
    echo ""
    echo "🚀 Lancement avec Gmail..."
    ./start-email-config.sh
}

configure_yahoo() {
    echo ""
    echo "🟣 CONFIGURATION YAHOO MAIL"
    echo "============================"
    echo ""
    echo "⚠️  Pour Yahoo, vous devez :"
    echo "1. Activer l'authentification à 2 facteurs"
    echo "2. Créer un mot de passe d'application"
    echo ""
    echo -n "Email Yahoo : "
    read yahoo_email
    echo -n "Mot de passe d'application Yahoo : "
    read -s yahoo_app_password
    echo ""
    
    export YAHOO_USERNAME="$yahoo_email"
    export YAHOO_APP_PASSWORD="$yahoo_app_password"
    
    echo "✅ Configuration Yahoo enregistrée"
    echo "📧 Email: $yahoo_email"
    echo ""
    echo "🚀 Lancement avec Yahoo..."
    ./start-email-config-yahoo.sh
}

configure_custom() {
    echo ""
    echo "⚙️  CONFIGURATION SMTP PERSONNALISÉE"
    echo "===================================="
    echo ""
    echo -n "Serveur SMTP (ex: smtp.monserveur.com) : "
    read smtp_host
    echo -n "Port SMTP (ex: 587, 25, 465) : "
    read smtp_port
    echo -n "Username (optionnel) : "
    read smtp_username
    echo -n "Password (optionnel) : "
    read -s smtp_password
    echo ""
    echo -n "Email expéditeur : "
    read smtp_from
    echo -n "Utiliser TLS ? (true/false) [true] : "
    read smtp_tls
    
    # Valeurs par défaut
    smtp_tls=${smtp_tls:-true}
    
    export SMTP_HOST="$smtp_host"
    export SMTP_PORT="$smtp_port"
    export SMTP_USERNAME="$smtp_username"
    export SMTP_PASSWORD="$smtp_password"
    export SMTP_FROM="$smtp_from"
    export SMTP_TLS="$smtp_tls"
    
    echo ""
    echo "✅ Configuration SMTP personnalisée enregistrée"
    echo "🔧 Serveur: $smtp_host:$smtp_port"
    echo "📧 From: $smtp_from"
    echo ""
    echo "🚀 Lancement avec SMTP personnalisé..."
    ./start-email-config-custom.sh
}

test_mode() {
    echo ""
    echo "🧪 MODE TEST LOCAL"
    echo "=================="
    echo ""
    echo "⚠️  En mode test, les emails ne seront pas vraiment envoyés."
    echo "Les messages seront affichés dans la console."
    echo ""
    echo "🚀 Lancement en mode test..."
    mvn tomcat7:run \
      -Dcolorrun.email.enabled=false \
      -Dcolorrun.email.test.mode=true
}

show_all_configs() {
    echo ""
    echo "📋 RÉCAPITULATIF DE TOUTES LES CONFIGURATIONS"
    echo "============================================="
    echo ""
    
    echo "🔵 OUTLOOK/HOTMAIL (Le plus simple)"
    echo "   Serveur: smtp.office365.com:587"
    echo "   Exemple: votre-email@outlook.com"
    echo "   Mot de passe: votre mot de passe normal"
    echo ""
    
    echo "🔴 GMAIL"
    echo "   Serveur: smtp.gmail.com:587"
    echo "   Exemple: votre-email@gmail.com"
    echo "   Mot de passe: mot de passe d'application (16 caractères)"
    echo "   Prérequis: 2FA activé + mot de passe app"
    echo ""
    
    echo "🟣 YAHOO MAIL"
    echo "   Serveur: smtp.mail.yahoo.com:587"
    echo "   Exemple: votre-email@yahoo.com"
    echo "   Mot de passe: mot de passe d'application Yahoo"
    echo "   Prérequis: 2FA activé + mot de passe app"
    echo ""
    
    echo "⚙️  SMTP PERSONNALISÉ"
    echo "   Serveur: votre choix"
    echo "   Port: 25, 587, 465..."
    echo "   Auth: selon votre serveur"
    echo ""
    
    echo "🧪 MODE TEST"
    echo "   Pas d'envoi réel d'emails"
    echo "   Affichage console uniquement"
    echo ""
    
    echo -n "Appuyez sur Entrée pour continuer..."
    read
}

# Boucle principale
while true; do
    show_menu
    read choice
    
    case $choice in
        1)
            configure_outlook
            break
            ;;
        2)
            configure_gmail
            break
            ;;
        3)
            configure_yahoo
            break
            ;;
        4)
            configure_custom
            break
            ;;
        5)
            test_mode
            break
            ;;
        6)
            show_all_configs
            ;;
        0)
            echo "👋 Au revoir !"
            exit 0
            ;;
        *)
            echo "❌ Choix invalide. Veuillez choisir entre 1 et 6."
            echo ""
            ;;
    esac
done

echo ""
echo "🎉 Configuration terminée !"
echo ""
echo "📍 Pour tester votre configuration :"
echo "   👉 http://localhost:8080/runton-color/test-email"
echo ""
echo "📝 Pour faire une inscription test :"
echo "   👉 http://localhost:8080/runton-color/"
echo "" 