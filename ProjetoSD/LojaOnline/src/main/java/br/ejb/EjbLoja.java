package br.ejb;

import bri.Compra;
import jakarta.ejb.Stateless;
import jakarta.ejb.LocalBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * EJB para a loja de comida.
 */
@Stateless
@LocalBean
public class EjbLoja implements bri.ILoja {

    private List<Compra> listaCompras;
    private List<Compra> listaComprasSimulacao; 
    private Lock lock;
    private final Map<String, ReentrantLock> produtoLocks;
    private Map<String, Integer> estoque;
    private boolean semEstoque;

    public EjbLoja() {
        this.listaCompras = new ArrayList<>();
        this.listaComprasSimulacao = new ArrayList<>();
        this.lock = new ReentrantLock();
        this.estoque = new HashMap<>();
        this.produtoLocks = new HashMap<>();

        estoque.put("Farinha de Mandioca", 10);
        estoque.put("Refri", 20);
        estoque.put("Batata", 30);
        estoque.put("Maça", 40);
        
        //Locks Individuais para cada produto
        for (String produto : estoque.keySet()) {
            produtoLocks.put(produto, new ReentrantLock());
        }
    }
    
    @Override
    public synchronized boolean realizarCompra(String produto, int quantidade, double valorUnitario) {
        ReentrantLock produtoLock = produtoLocks.get(produto);
        produtoLock.lock(); // Adquira o bloqueio específico do produto
        lock.lock();
        System.out.println("Bloqueio Realizado (Compra)");

        try {
            System.out.println("Realizando Compra...");
            if (estoque.containsKey(produto) && estoque.get(produto) >= quantidade) {
                
                Compra compra = new Compra();
                compra.setProduto(produto);
                compra.setQuantidade(quantidade);
                compra.setValorUnitario(valorUnitario);
                listaCompras.add(compra);
               
                
                int estoqueAtual = estoque.get(produto);
                estoque.put(produto, estoqueAtual - quantidade);
                    
                return true;
            } else {
                
                System.out.println("Produto Sem estoque");
                setSemEstoque(true);
                return false;
            }
        } finally {
            lock.unlock();
            produtoLock.unlock();
            System.out.println("Desbloqueio Realizado (Compra)");
        }
    }
    
 
    
    public Map<String, Integer> getEstoque() {
        return estoque;
    }
    
    public void setSemEstoque(boolean semEstoque) {
        this.semEstoque = semEstoque;
    }
    @Override
    public synchronized List<Compra> consultarCompras() {
        
        lock.lock(); 
        System.out.println("Bloqueio Realizado (Consulta)");
        try {
            System.out.println("Consultando...");
            return listaCompras;
        } finally {
            lock.unlock();
            System.out.println("Desbloqueio Realizado (Consulta)");
        }
    }
    
    @Override
    public synchronized List<Compra> consultarComprasSimulacao() {
        lock.lock();
        System.out.println("Bloqueio Realizado (Consulta)");
        try {
            System.out.println("Consultando...");
            return listaComprasSimulacao;
        } finally {
            lock.unlock();
            System.out.println("Desbloqueio Realizado (Consulta)");
        }
    }
}
