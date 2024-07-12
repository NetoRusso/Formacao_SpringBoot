package br.com.alura.desafio01tabelafipe.principal;

import br.com.alura.desafio01tabelafipe.model.Dados;
import br.com.alura.desafio01tabelafipe.model.Modelos;
import br.com.alura.desafio01tabelafipe.model.Veiculo;
import br.com.alura.desafio01tabelafipe.service.ConsumoApi;
import br.com.alura.desafio01tabelafipe.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner userInput = new Scanner(System.in);

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();


    public void exibeMenu() {
        String menu = """
                ****Opções***
                Carro
                Moto
                Caminhão

                Digite uma das opções:
                                
                """;

        System.out.println(menu);
        var opcao = userInput.nextLine();

        String endereco;

        if (opcao.toLowerCase().equals("carro")) {
            endereco = URL_BASE + "carros/marcas";
        } else if (opcao.toLowerCase().equals("moto")) {
            endereco = URL_BASE + "motos/marcas";
        } else {
            endereco = URL_BASE + "caminhoes/marcas";
        }

        var json = consumo.obterDados(endereco);

        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\n Informe o código da marca que deseja consultar:");
        var codigoMarca = userInput.nextLine();

        endereco = endereco + "/" + codigoMarca + "/modelos";

        json = consumo.obterDados(endereco);
        var modeloLista = conversor.obterDados(json, Modelos.class);




        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);


        System.out.println("\n Informe um trecho do nome do carro que deseja consultar:");
        var nomeVeiculo = userInput.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos encontrados:");
        modelosFiltrados.forEach(System.out::println);
        System.out.println("Digie por favor o código do modelo que deseja consultar:");
        var codigoModelo = userInput.nextLine();

        endereco = endereco + "/" + codigoModelo + "/anos";

        json = consumo.obterDados(endereco);
        List<Dados> anos = conversor.obterLista(json, Dados.class);

        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
            var enderecoAno = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAno);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\nVeiculos encontrados com avaliações por ano:\n");

        veiculos.forEach(System.out::println);
    }

}
