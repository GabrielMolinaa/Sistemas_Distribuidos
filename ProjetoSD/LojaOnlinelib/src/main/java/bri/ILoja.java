package bri;

import jakarta.ejb.Remote;
import java.util.List;
import java.util.Map;


@Remote
public interface ILoja {

    // Operações de Compra
    boolean realizarCompra(String produto, int quantidade, double valorUnitario);
    
    // Operações de Consulta
    List<Compra> consultarCompras();

    public Map<String, Integer> getEstoque();

    public List<Compra> consultarComprasSimulacao();
    
}
