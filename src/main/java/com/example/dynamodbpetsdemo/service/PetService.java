package com.example.dynamodbpetsdemo.service;

import com.example.dynamodbpetsdemo.entity.Pet;
import com.example.dynamodbpetsdemo.exception.InvalidInputException;
import com.example.dynamodbpetsdemo.repository.PetRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.json.simple.JSONObject;

import java.util.List;

@Service
public class PetService {

    private PetRepo petRepo;

    @Autowired
    public PetService(PetRepo repo) {
        petRepo = repo;
    }
    public Pet savePet(Pet pet) throws InvalidInputException {
        if(pet.getAge() < 0) {
            throw new InvalidInputException("Age cannot be negative");
        }

        pet.setName(pet.getName().toUpperCase());
        return petRepo.save(pet);
    }

    public Pet findPetById(String id) {
        Pet pet = petRepo.findById(id);
        if(pet != null) {
            pet.setDogAge(convertToDogYears(pet.getAge()));
            return pet;
        } else {
            return null;
        }
    }

    public List<Pet> getAllPets() {
        List<Pet> pets = petRepo.findAll();
        for (Pet pet : pets) {
            pet.setDogAge(convertToDogYears(pet.getAge()));
        }

        return pets;
    }

    public Pet updatePetInfo(String id, Pet pet) {
        return petRepo.update(id, pet);
    }

    public Pet deletePet(String id) {
        return petRepo.delete(id);
    }

    public int convertToDogYears(int age) {
        return age * 7;
    }
}
