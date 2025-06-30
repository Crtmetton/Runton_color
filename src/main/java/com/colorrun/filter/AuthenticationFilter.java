package com.colorrun.filter;

import com.colorrun.security.TokenManager;
import com.colorrun.util.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtre d'authentification qui ajoute automatiquement 
 * les informations de token sur toutes les requêtes
 */
public class AuthenticationFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Logger.info("AuthenticationFilter", "Filtre d'authentification initialisé");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Ajouter les informations token à chaque requête
        TokenManager.addTokenToRequest(httpRequest);
        
        // Continuer la chaîne de filtres
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        Logger.info("AuthenticationFilter", "Filtre d'authentification détruit");
    }
} 