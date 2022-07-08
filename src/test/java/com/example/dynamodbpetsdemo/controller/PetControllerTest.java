package com.example.dynamodbpetsdemo.controller;

import com.example.dynamodbpetsdemo.entity.Pet;
import com.example.dynamodbpetsdemo.exception.InvalidInputException;
import com.example.dynamodbpetsdemo.service.PetService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class PetControllerTest {

    private PetController petController;
    private PetService petServiceMock;

    @Captor
    private ArgumentCaptor<Pet> petArgumentCaptor;

    @BeforeEach
    void beforeMethod() {
        petServiceMock = Mockito.mock(PetService.class);
        petController = new PetController(petServiceMock);
    }

    @Nested
    class SavePetMethodTests {
        @Test
        void theServicesSavePetMethodShouldBeCalled() throws InvalidInputException {
            //given
            Pet pet = new Pet("abc123", "Lucy", 6);
            //when
            when(petServiceMock.savePet(pet)).thenReturn(pet);
            petController.save(pet);
            //then
            verify(petServiceMock).savePet(pet);
        }

        @Test
        void petsInfoStaysTheSame() throws InvalidInputException {
            //given
            String id = "abc123";
            Pet pet = new Pet(id, "Lucy", 6);
            //when
            when(petServiceMock.savePet(pet)).thenReturn(pet);
            petController.save(pet);
            //then
            verify(petServiceMock).savePet(petArgumentCaptor.capture());

            Pet capturedPet = petArgumentCaptor.getValue();
            assertAll(
                    () -> assertEquals(id, capturedPet.getId()),
                    () -> assertEquals("Lucy", capturedPet.getName()),
                    () -> assertEquals(6, capturedPet.getAge())
            );
        }

    }

    @Nested
    class FindByIdMethodTests {

        @Test
        void theServicesFindByIdMethodShouldBeCalled() {
            //given
            String id = "abc123";
            Pet pet = new Pet(id, "Lucy", 6);
            //when
            when(petServiceMock.findPetById(id)).thenReturn(pet);
            ResponseEntity entity = petController.findById(id);
            Pet returnedPet = (Pet) entity.getBody();
            //then
            verify(petServiceMock).findPetById(id);
            assertEquals(returnedPet, pet);
        }

        @Test
        void petsInfoStaysTheSame() throws InvalidInputException {
            //given
            String id = "abc123";
            Pet pet = new Pet(id, "Lucy", 6);
            //when
            when(petServiceMock.savePet(pet)).thenReturn(pet);
            petController.save(pet);
            //then
            verify(petServiceMock).savePet(petArgumentCaptor.capture());

            Pet capturedPet = petArgumentCaptor.getValue();
            assertAll(
                    () -> assertEquals(id, capturedPet.getId()),
                    () -> assertEquals("Lucy", capturedPet.getName()),
                    () -> assertEquals(6, capturedPet.getAge())
            );
        }

    }

    @Nested
    class FindAllMethodTests {

        @Test
        void findAll() {
            //given
            List<Pet> pets = new ArrayList<Pet>();
            pets.add(new Pet("abc123", "Lucy", 6));
            pets.add(new Pet("asd456", "Max", 1));
            pets.add(new Pet("zxc890", "Zeus", 10));

            //when
            when(petServiceMock.getAllPets()).thenReturn(pets);
            ResponseEntity entity = petController.findAll();
            List<Pet> returnedPets = (List<Pet>) entity.getBody();
            //then
            //check to see if the found pets are returned
            assertEquals(pets, returnedPets);
            verify(petServiceMock).getAllPets();
        }
    }

    @Nested
    class UpdateMethodTests {
        @Test
        void theServicesUpdatePetInfoMethodShouldBeCalled() {
            //given
            String id = "abc123";
            Pet pet = new Pet(id, "Lucy", 6);
            Pet updatedPet = new Pet(id, "Lucy", 7);
            //when
            when(petServiceMock.updatePetInfo(id, pet)).thenReturn(updatedPet);
            Pet returnedPet = petController.update(id, pet).getBody();
            //then
            //Check to see if the updated pet is returned
            assertEquals(updatedPet, returnedPet);
            verify(petServiceMock).updatePetInfo("abc123", pet);
        }

    }

    @Nested
    class deleteMethodTests {
        @Test
        void theReposDeletePetMethodShouldBeCalled() {
            //given
            String id = "abc123";
            Pet deletedPet = new Pet(id, "Lucy", 6);
            //when
            when(petServiceMock.deletePet(id)).thenReturn(deletedPet);
            Pet returnedPet = petController.delete(id).getBody();
            //then
            assertEquals(returnedPet, deletedPet);
            verify(petServiceMock).deletePet(id);
        }
    }

}