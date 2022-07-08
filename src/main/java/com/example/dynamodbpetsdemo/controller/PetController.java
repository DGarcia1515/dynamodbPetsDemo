package com.example.dynamodbpetsdemo.controller;

import com.example.dynamodbpetsdemo.entity.Pet;
import com.example.dynamodbpetsdemo.exception.InvalidInputException;
import com.example.dynamodbpetsdemo.service.PetService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ControllerAdvice
@RequestMapping("/pets")
public class PetController {

    private PetService petService;

    @Autowired
    public PetController(PetService service) {
        petService = service;
    }

    @PostMapping
    public ResponseEntity<Pet> save(@RequestBody Pet pet) {
        try {
            Pet newPet;
            if(pet.getName() == null) {
                return new ResponseEntity<>(pet, HttpStatus.BAD_REQUEST);
            }

            newPet = petService.savePet(pet);
            return new ResponseEntity<>(newPet, HttpStatus.OK);

        } catch(InvalidInputException e) {
            System.out.println(e);
            return new ResponseEntity<>(pet, HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> findById(@PathVariable(value = "id") String id) {
        Pet pet = petService.findPetById(id);
        if(pet != null) {
            return new ResponseEntity<>(pet, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Pet>> findAll() {
        List<Pet> pets = petService.getAllPets();
        return new ResponseEntity<>(pets, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Pet> update(@PathVariable(value = "id") String id, @RequestBody Pet pet) {
        if(petService.findPetById(id) == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else if(pet.getName() == null || pet.getAge() < 0) {
            return new ResponseEntity<>(pet, HttpStatus.BAD_REQUEST);
        }
        Pet returnedPet = petService.updatePetInfo(id, pet);
        return new ResponseEntity<>(returnedPet, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Pet> delete(@PathVariable(value = "id") String id) {
        Pet returnedPet = petService.deletePet(id);
        if(returnedPet != null) {
            return new ResponseEntity<>(returnedPet, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

    }

}
