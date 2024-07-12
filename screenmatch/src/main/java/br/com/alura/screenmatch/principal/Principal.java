package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class Principal {

    private Scanner userInput = new Scanner(System.in);

    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "http://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=b5cc8c99";

    public void exibeMenu() {
        System.out.println("Digite o nome da Série que deseja consultar:");

        var nomeSerie = userInput.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }

//        temporadas.forEach(System.out::println);

//        for(int i = 0; i < temporadas.size(); i++) {
//            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for(int j = 0; j < episodiosTemporada.size(); j++) {
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }

//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.numero() + " - " + e.titulo())));

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());


//        System.out.println("\nTop 10 episodios com avaliação maior:");
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equals("N/A"))
//                .peek(e-> System.out.println("Primeiro filtro(N/A) " + e))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(e-> System.out.println("Ordenação " + e))
//                .limit(10)
//                .peek(e-> System.out.println("Limite " + e))
//                .map(e-> e.titulo().toUpperCase())
//                .peek(e-> System.out.println("Mapeamento " + e))
//                .forEach(System.out::println);


        System.out.println("\nEpisodios:");

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());

        episodios.forEach(System.out::println);


//        System.out.println("\n Digite o trecho que deseja pesquisar no título do episódio:");
//        var trechoTitulo = userInput.nextLine();
//
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                .findFirst();
//
//        if (episodioBuscado.isPresent()) {
//            System.out.println("Episódio encontrado: ");
//            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
//            System.out.println("Episodio: " + episodioBuscado.get().getNumeroEpisodio());
//        } else {
//            System.out.println("Episódio não encontrado");
//        }

//        System.out.println("\n Apartir de que ano deseja consultar os episodios?");
//        var ano = userInput.nextInt();
//        userInput.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);

//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                             "Temporada: " + e.getTemporada() +
//                                        " Episodio: " + e.getNumeroEpisodio() +
//                                        " Data de lançamento: " + e.getDataLancamento().format(formatador) +
//                                        " Avaliação: " + e.getAvaliacao() +
//                                        " Título: " + e.getTitulo()
//
//                ));

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy((Episodio::getTemporada),
                        Collectors.averagingDouble(Episodio::getAvaliacao)));


        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor episódio: " + est.getMax());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Quantidade de epsódios avaliados: " + est.getCount());
    }


}
