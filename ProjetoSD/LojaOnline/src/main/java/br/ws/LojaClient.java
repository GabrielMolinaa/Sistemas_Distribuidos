package br.ws;

import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebServiceException;
import java.net.URL;

/**
 * Cliente para testar o serviço web.
 */
public class LojaClient {

    public static void main(String[] args) {
        try {
            // URL do WSDL do serviço web
            URL wsdlURL = new URL("http://localhost:8080/Aula4Pratica3Web/LojaService?wsdl");
            

            // Nome do serviço web e da port (porta)
            String namespace = "http://localhost:8080/Aula4Pratica3Web/LojaService";
            String serviceName = "LojaService";

            // Criação da instância do serviço web
            Service service = Service.create(wsdlURL, new javax.xml.namespace.QName(namespace, serviceName));

            // Obtém o proxy para a port (porta) do serviço web
            LojaService port = service.getPort(LojaService.class);

            // Chama os métodos do serviço web
            String resultHello = port.hello("World");
            System.out.println("Result Hello: " + resultHello);

            String resultComprar = port.comprar("Lucio Goes");
            System.out.println("Result Comprar: " + resultComprar);
        } catch (WebServiceException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
