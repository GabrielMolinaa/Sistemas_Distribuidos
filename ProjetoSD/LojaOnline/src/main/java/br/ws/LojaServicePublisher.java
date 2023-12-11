package br.ws;

import jakarta.xml.ws.Endpoint;

/**
 * Publica o serviço web na URL http://localhost:8080/LojaService
 */
public class LojaServicePublisher {

    public static void main(String[] args) {
        // URL de publicação do serviço web
        String url = "http://localhost:8080/LojaService";

        // Cria uma instância do serviço web
        LojaService service = new LojaService();

        // Publica o serviço web na URL especificada
        Endpoint.publish(url, service);

        // Imprime a URL do serviço web
        System.out.println("LojaService publicado em: " + url);
    }
}
