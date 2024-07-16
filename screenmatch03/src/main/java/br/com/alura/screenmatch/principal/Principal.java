package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;


public class Principal {

    private Scanner userInput = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "http://www.omdbapi.com/?t=";
    private final String OMDb_KEY = System.getenv("OMDB_KEY");
    private final String API_KEY = "&apikey=" + OMDb_KEY;
    private SerieRepository repositorio;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    private List<Serie> series = new ArrayList<>();
    private Optional<Serie>  serieBusca;


    public void exibeMenu() {

        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    ********************************📽️Screenmatch📽️********************************
                                        
                    1. Buscar Séries
                    2. Buscar Episódios
                    3. Listar Séries Buscadas
                    4. buscar Série por título
                    5. Buscar Séries por ator
                    6. Top 5 Séries
                    7. Buscar séries por categoria
                    8. Buscar por temporada e avaliação
                    9. Buscar Episódio por trecho
                    10. Top 5 Episódios por Série
                    11. Episódios a partir de uma data
                   
                                        
                    0. Sair
                                        
                    ********************************📽️Screenmatch📽️********************************
                    """;
            System.out.println(menu);
            opcao = userInput.nextInt();
            userInput.nextLine();
            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSerieBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Serie();
                    break;
                case 7:
                    buscarSeriePorCategoria();
                    break;
                case 8:
                    filtrarSeriesPorTemporadaEAvaliacao();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    topEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodioDepoisDeUmaData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    userInput.close();
                    break;
                default:
                    System.out.println("Opção inválida");
                    break;
            }
        }
    }

    private void buscarEpisodioDepoisDeUmaData() {
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            System.out.println("Digite o ano limite de lançamento:");
            var anoLancamento = userInput.nextInt();
            userInput.nextLine();
            List<Episodio> episodiosAno = repositorio.episodiosPorSerieEAno(serie,anoLancamento);
            episodiosAno.forEach(System.out::println);
        }
    }

    private void topEpisodiosPorSerie() {
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()) {
           Serie serie = serieBusca.get();
           List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);
           topEpisodios.forEach(e ->
                   System.out.printf("Série: %s Temporada %s - Episódio %s - %s - Avaliação: %s\n",
                           e.getSerie().getTitulo(), e.getTemporada(),
                           e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
        }

    }

    private void filtrarSeriesPorTemporadaEAvaliacao(){
        System.out.println("Filtrar séries até quantas temporadas? ");
        var totalTemporadas = userInput.nextInt();
        userInput.nextLine();
        System.out.println("Com avaliação a partir de que valor? ");
        var avaliacao = userInput.nextDouble();
        userInput.nextLine();
        List<Serie> filtroSeries = repositorio.seriesPorTemporadaEAvaliacao(totalTemporadas, avaliacao);
        System.out.println("*** Séries filtradas ***");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + "  - avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriePorCategoria() {
        System.out.println("Digite a categoria/genero que deseja consultar:");
        var nomeGenero = userInput.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);

        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Séries de " + nomeGenero + ":");
        seriesPorCategoria.forEach(s -> System.out.println(s.getTitulo() +" avaliação: " + s.getAvaliacao()));
    }

    private void buscarTop5Serie() {
        List<Serie> seriesTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        seriesTop.forEach(s -> System.out.println(s.getTitulo() +" avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriePorAtor() {
        System.out.println("Digite o nome do ator que deseja consultar:");
        var nomeAtor = userInput.nextLine();
        System.out.println("Avalição apartir de que valor?");
        var avaliacao = userInput.nextDouble();
        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("Series em que " + nomeAtor + " atuou:");
        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() +" avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Digite o nome da Série que deseja consultar:");
        var nomeSerie = userInput.nextLine();
        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        if (serieBusca.isPresent()) {
            System.out.println("Dados da Série: " + serieBusca.get());
        } else {
            System.out.println("Série não encontrada");
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repositorio.save(serie);

        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da Série que deseja consultar:");
        var nomeSerie = userInput.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSerieBuscadas();
        System.out.println("Escolha uma Série pelo nome: ");
        var nomeSerie = userInput.nextLine();

        Optional<Serie> serie =  repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {

            var serieEncontrada = serie.get();

            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);

        } else {
            System.out.println("Série não encontrada");
        }
    }


    private void listarSerieBuscadas() {
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Digite o nome do episódio que deseja consultar:");
        var trechoEpisodio = userInput.nextLine();

        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getNumeroEpisodio(), e.getTitulo()));
    }
}


