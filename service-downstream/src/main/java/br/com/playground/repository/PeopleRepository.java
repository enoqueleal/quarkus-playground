package br.com.playground.repository;

import br.com.playground.model.People;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class PeopleRepository implements PanacheRepository<People> {

    public List<People> findAllPeople() {
        return listAll();
    }

    public People findById(Long id) {
        return findByIdOptional(id).orElse(null);
    }

    public People createPeople(People people) {
        persist(people);
        return people;
    }

    public People updatePeople(People people) {
        return getEntityManager().merge(people);
    }

    public void deletePeople(Long id) {
        deleteById(id);
    }

}