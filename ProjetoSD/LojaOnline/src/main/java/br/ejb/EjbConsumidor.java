package br.ejb;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import jakarta.ejb.EJB;
import java.util.Map;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(
            propertyName = "destinationLookup",
            propertyValue = "java/Fila"),
    @ActivationConfigProperty(
            propertyName = "destinationType",
            propertyValue = "jakarta.jms.Queue")
})
public class EjbConsumidor implements MessageListener {

    @EJB
    private EjbLoja ejbLoja;

    @Override
    public void onMessage(Message msg) {

        try {

            TextMessage tm = (TextMessage) msg;
            String produto = tm.getText();
           
            Map<String, Integer> estoque = ejbLoja.getEstoque();
            System.out.println(estoque);
            int estoqueAtual = estoque.getOrDefault(produto, 0);
            if (estoqueAtual <= 0) {
                System.out.println("Produto em falta: " + produto);
            } else {
                System.out.println("Estoque ainda disponível para o produto: " + produto);
            }
        } catch (Exception e) {
        }
    }
}
