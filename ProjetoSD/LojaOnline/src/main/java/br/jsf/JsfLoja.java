package br.jsf;

import br.ejb.EjbLoja;
import bri.Compra;
import bri.Produto;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ejb.EJB;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import jakarta.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import bri.ILoja;
import jakarta.annotation.Resource;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;

/**
 *
 * @author PC
 */
@Named(value = "jsfLoja")
@RequestScoped
public class JsfLoja {

    @Resource(lookup = "java:comp/DefaultJMSConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "java/Fila")
    private Queue fila;

    @EJB
    private ILoja ejbLoja;

    @Setter
    @Getter
    private String produto;

    @Setter
    @Getter
    private int quantidade;

    @Setter
    @Getter
    private double valorUnitario;

    @Getter
    private List<Compra> listaCompras;
    private List<Produto> produtos;
    private boolean semEstoque;
    private ArrayList<Future<Boolean>> resultadosCompras = new ArrayList<>();

    @PostConstruct
    public void init() {
        produtos = new ArrayList<>();
        produtos.add(new Produto("Farinha de Mandioca", 7.0));
        produtos.add(new Produto("Refri", 4.0));
        produtos.add(new Produto("Batata", 8.0));
        produtos.add(new Produto("Maça", 9.0));
    }

    public void send(String compra) {
        try {
            JMSContext context = connectionFactory.createContext();
            context.createProducer().send((Destination) fila, compra);
        } catch (Exception e) {
            System.out.println("ERRO");
            System.out.println(e.getMessage());
        }
    }

    public List<SelectItem> getProdutosDisponiveis() {
        List<SelectItem> produtosSelect = new ArrayList<>();
        for (Produto produto : produtos) {
            produtosSelect.add(new SelectItem(produto.getNome(), produto.getNome()));
        }
        return produtosSelect;
    }

    public boolean isSemEstoque() {
        return semEstoque;
    }

    public void setSemEstoque(boolean semEstoque) {
        this.semEstoque = semEstoque;
    }

    public void atualizarValor() {
        for (Produto p : produtos) {
            if (p.getNome().equals(produto)) {
                valorUnitario = p.getValor();
                break;
            }
        }
    }

    /**
     * Creates a new instance of JsfCalculadora
     */
    public JsfLoja() {
    }

    public void realizarCompra() {
        boolean compraRealizada = ejbLoja.realizarCompra(produto, quantidade, valorUnitario);

        if (compraRealizada) {
            listaCompras = ejbLoja.consultarCompras();
            setSemEstoque(false);

            String mensagemCompra = produto;
            send(mensagemCompra);

        } else {
            setSemEstoque(true);
        }
    }

    public void consultarCompras() {
        listaCompras = ejbLoja.consultarCompras();
    }

    private int gerarQuantidadeAleatoria() {
        Random random = new Random();
        return random.nextInt(10) + 1;
    }

    public void simularComprasConcorrentes() throws ExecutionException {
        int numClientes = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numClientes);

        for (int i = 0; i < numClientes; i++) {
            for (Produto produto : produtos) {
                int quantidadeAleatoria = gerarQuantidadeAleatoria();
                Future<Boolean> resultadoCompra = executorService.submit(() -> realizarCompraConcorrente(produto.getNome(), quantidadeAleatoria, 4)
                );
                resultadosCompras.add(resultadoCompra);
            }
        }

        // Aguardar a conclusão de todas as compras
        for (Future<Boolean> resultadoCompra : resultadosCompras) {
            try {
                resultadoCompra.get(); // Aguarda a conclusão da compra
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
    }

    private boolean realizarCompraConcorrente(String produto, int quantidade, double valorUnitario) {
        boolean compraRealizada = ejbLoja.realizarCompra(produto, quantidade, valorUnitario);

        if (compraRealizada) {
            System.out.println(Thread.currentThread().getName() + " comprou com sucesso: " + produto + " - Quantidade: " + quantidade);
            listaCompras = ejbLoja.consultarCompras();
            setSemEstoque(false);
        } else {
            setSemEstoque(true);
            System.out.println(Thread.currentThread().getName() + " falhou na compra: " + produto + " - Quantidade: " + quantidade);
        }

        return compraRealizada;
    }

    public void exibirEstoque() {
        Map<String, Integer> estoque = ejbLoja.getEstoque();
        System.out.println("Estoque Atual:");
        for (Map.Entry<String, Integer> entry : estoque.entrySet()) {
            System.out.println("Produto: " + entry.getKey() + ", Quantidade em Estoque: " + entry.getValue());
        }
    }

    public List<Compra> getListaComprasSimulacao() {
        return ejbLoja.consultarComprasSimulacao();
    }

}
