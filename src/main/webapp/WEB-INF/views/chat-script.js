// JavaScript pour la fonctionnalité de chat
function sendMessage(event) {
    event.preventDefault();
    
    const messageInput = document.getElementById('messageInput');
    const courseId = document.getElementById('courseId');
    
    if (!messageInput || !courseId) {
        return false;
    }
    
    const messageText = messageInput.value.trim();
    const courseIdValue = courseId.value;
    
    if (!messageText || !courseIdValue) {
        return false;
    }
    
    // Désactiver le formulaire pendant l'envoi
    messageInput.disabled = true;
    
    // Envoyer via AJAX
    fetch(window.location.pathname, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'courseId=' + encodeURIComponent(courseIdValue) + '&content=' + encodeURIComponent(messageText)
    })
    .then(response => {
        if (response.ok) {
            messageInput.value = '';
            // Recharger la page pour voir le nouveau message
            window.location.reload();
        } else {
            throw new Error('Erreur lors de l\'envoi du message');
        }
    })
    .catch(error => {
        console.error('Erreur:', error);
        alert('Erreur lors de l\'envoi du message. Veuillez réessayer.');
    })
    .finally(() => {
        messageInput.disabled = false;
        if (messageInput) {
            messageInput.focus();
        }
    });
    
    return false;
}

// Supprimer un message
function deleteMessage(messageId) {
    if (!confirm('Êtes-vous sûr de vouloir supprimer ce message ?')) {
        return;
    }
    
    fetch('/message/delete', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'action=delete&messageId=' + encodeURIComponent(messageId)
    })
    .then(response => {
        if (response.ok) {
            // Recharger la page pour voir la suppression
            window.location.reload();
        } else {
            throw new Error('Erreur lors de la suppression');
        }
    })
    .catch(error => {
        console.error('Erreur:', error);
        alert('Erreur lors de la suppression du message. Veuillez réessayer.');
    });
}

// Auto-focus sur le champ de saisie
document.addEventListener('DOMContentLoaded', function() {
    const messageInput = document.getElementById('messageInput');
    const chatContainer = document.getElementById('chat-messages');
    
    if (messageInput) {
        messageInput.focus();
    }
    
    // Faire défiler vers le bas du chat
    if (chatContainer) {
        chatContainer.scrollTop = chatContainer.scrollHeight;
    }
}); 