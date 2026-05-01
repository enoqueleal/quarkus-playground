package br.com.playground.service;

import br.com.playground.model.RandomNamesResponse;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@ApplicationScoped
public class RandomNamesService {

    private static final int DEFAULT_TOTAL = 5;

    private static final List<String> FIRST_NAMES = List.of(
            "Ana",
            "Bruno",
            "Carlos",
            "Daniela",
            "Eduardo",
            "Fernanda",
            "Gabriel",
            "Helena",
            "Igor",
            "Juliana"
    );

    private static final List<String> LAST_NAMES = List.of(
            "Silva",
            "Souza",
            "Costa",
            "Oliveira",
            "Pereira",
            "Rodrigues",
            "Almeida",
            "Nascimento",
            "Gomes",
            "Martins"
    );

    @Inject
    DownstreamService downstreamService;

    private RandomNamesResponse cachedResponse;

    @PostConstruct
    void init() {
        List<String> names = generateUniqueNames(DEFAULT_TOTAL);
        cachedResponse = new RandomNamesResponse("random-names", "ready", names.size(), names);
    }

    public RandomNamesResponse getCachedResponse() {
        return cachedResponse;
    }

    public RandomNamesResponse getRandomNamesFromDownstream() {
        return downstreamService.getRandomNamesFromDownstream();
    }

    private List<String> generateUniqueNames(int amount) {
        Set<String> names = new LinkedHashSet<>();

        while (names.size() < amount) {
            names.add(randomFullName());
        }

        return List.copyOf(names);
    }

    private String randomFullName() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        String firstName = FIRST_NAMES.get(random.nextInt(FIRST_NAMES.size()));
        String lastName = LAST_NAMES.get(random.nextInt(LAST_NAMES.size()));
        return firstName + " " + lastName;
    }

}

