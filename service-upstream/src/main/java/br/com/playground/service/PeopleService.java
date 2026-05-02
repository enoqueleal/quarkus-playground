package br.com.playground.service;

import br.com.playground.model.People;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class PeopleService {

    @Inject
    DownstreamService downstreamService;

    public List<People> getAllPeople() {
        return downstreamService.getPeopleFromDownstream();
    }
}
