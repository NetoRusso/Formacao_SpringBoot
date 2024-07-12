package br.com.alura.desafio01tabelafipe;

import br.com.alura.desafio01tabelafipe.principal.Principal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Desafio01tabelafipeApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Desafio01tabelafipeApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\nIniciando aplicação...");

        System.out.println("\n");
        Principal principal = new Principal();
        principal.exibeMenu();

        System.out.println("\nFim da execução do aplicativo");

    }

}
