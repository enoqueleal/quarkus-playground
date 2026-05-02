package br.com.playground.service;

import br.com.playground.model.People;
import br.com.playground.repository.PeopleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class PeopleService {

    @Inject
    PeopleRepository peopleRepository;

    public List<People> getAllPeople() {
        return peopleRepository.findAllPeople();
    }

    public People getPeopleById(Long id) {
        return peopleRepository.findById(id);
    }

    @Transactional
    public People createPeople(String firstName, String lastName) {
        People people = new People(firstName, lastName);
        return peopleRepository.createPeople(people);
    }

    @Transactional
    public People createPeople(People people) {
        return peopleRepository.createPeople(people);
    }

    @Transactional
    public People updatePeople(Long id, String firstName, String lastName) {
        People people = peopleRepository.findById(id);
        if (people != null) {
            people.setFirstName(firstName);
            people.setLastName(lastName);
            return peopleRepository.updatePeople(people);
        }
        return null;
    }

    @Transactional
    public People updatePeople(People people) {
        return peopleRepository.updatePeople(people);
    }

    @Transactional
    public void deletePeople(Long id) {
        peopleRepository.deletePeople(id);
    }

}