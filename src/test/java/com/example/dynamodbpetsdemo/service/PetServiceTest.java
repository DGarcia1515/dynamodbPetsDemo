package com.example.dynamodbpetsdemo.service;

import com.example.dynamodbpetsdemo.entity.Pet;
import com.example.dynamodbpetsdemo.exception.InvalidInputException;
import com.example.dynamodbpetsdemo.repository.PetRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    private PetService petService;
    private PetRepo petRepoMock;

    @Captor
    private ArgumentCaptor<Pet> petArgumentCaptor;

    @Captor
    private ArgumentCaptor<Integer> ageArgumentCaptor;

    @BeforeEach
    void beforeMethod() {
        petRepoMock = Mockito.mock(PetRepo.class);
        petService = new PetService(petRepoMock);
    }

    @Nested
    class saveMethodTests {
        @Test
        void theReposSaveMethodShouldBeCalled() throws InvalidInputException {
            //given
            Pet pet = new Pet("abc123", "Lucy", 6);
            //when
            when(petRepoMock.save(pet)).thenReturn(pet);
            Pet returnedPet = petService.savePet(pet);
            //then
            assertEquals(pet, returnedPet);

            //check to see if PetRepo.save() is called with the same pet parameter
            verify(petRepoMock).save(pet);
        }

        //use arg capture to check and see if upper case name is set when a pet is passed into PetRepo.save()
        @Test
        void upperCaseNameShouldBeSet() throws InvalidInputException {
            //given
            String name = "Lucy";
            Pet pet = new Pet("abc123", name, 6);
            //ArgumentCaptor<Pet> petArgumentCaptor = ArgumentCaptor.forClass(Pet.class);
            //when
            when(petRepoMock.save(pet)).thenReturn(pet);
            Pet returnedPet = petService.savePet(pet);
            //then
            verify(petRepoMock).save(petArgumentCaptor.capture()); //captures the arguments that are passed into the mock method
            assertEquals("LUCY", petArgumentCaptor.getValue().getName());

            //Incorrect because this checks if the returned pet contains a capital name not the
            //pet that is passed into PetRepo.save()
            //assertEquals(name.toUpperCase(), returnedPet.getName());
        }

        @Test
        void shouldThrowInvalidInputExceptionForAge() {
            //given
            Pet pet = new Pet("abc123", "Lucy", -6);
            String expectedMessage = "Age cannot be negative";
            //when
            //then
            Exception exception = assertThrows(InvalidInputException.class, () -> petService.savePet(pet));
            String actualMessage = exception.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));

        }

    }

    @Nested
    class findByIdMethodTests{
        @Test
        void theReposFindByIdMethodShouldBeCalled () {
            //given
            String id = "abc123";
            Pet pet = new Pet(id, "Lucy", 6);
            //when
            when(petRepoMock.findById(id)).thenReturn(pet);
            Pet returnedPet = petService.findPetById(id);
            //then
            //check to see if the pet that is found is returned
            assertEquals(pet, returnedPet);
            verify(petRepoMock).findById(id);
        }

        @Test
        void shouldConvertTheCorrectPetAge () {
            //given
            String id = "abc123";
            int age = 6;
            Pet pet = new Pet(id, "Lucy", age);
            //when
            when(petRepoMock.findById(id)).thenReturn(pet);
            Pet returnedPet = petService.findPetById(id);
            //then
            assertEquals(6, returnedPet.getDogAge() / 7);

        }
    }

    @Nested
    class findAllMethodTests {
        @Test
        void theReposFindAllMethodShouldBeCalled () {
            //given
            List<Pet> pets = new ArrayList<Pet>();
            pets.add(new Pet("abc123", "Lucy", 6));
            pets.add(new Pet("asd456", "Max", 1));
            pets.add(new Pet("zxc890", "Zeus", 10));
            //when
            when(petRepoMock.findAll()).thenReturn(pets);
            List<Pet> returnedPets = petService.getAllPets();
            //then
            //check to see if the found pets are returned
            assertEquals(pets, returnedPets);
            verify(petRepoMock).findAll();
        }

        @Test
        void shouldConvertTheCorrectPetAge () {
            //given
            List<Pet> pets = new ArrayList<Pet>();
            int age1 = 6; int age2 = 1; int age3 = 10;
            pets.add(new Pet("abc123", "Lucy", age1));
            pets.add(new Pet("asd456", "Max", age2));
            pets.add(new Pet("zxc890", "Zeus", age3));
            //when
            when(petRepoMock.findAll()).thenReturn(pets);
            List<Pet> returnedPets = petService.getAllPets();
            //then
            assertAll(
                    () -> assertEquals(age1, returnedPets.get(0).getDogAge() / 7),
                    () -> assertEquals(age2, returnedPets.get(1).getDogAge() / 7),
                    () -> assertEquals(age3, returnedPets.get(2).getDogAge() / 7)
            );
        }
    }

    @Nested
    class updateMethodTests {
        @Test
        void theReposUpdateMethodShouldBeCalled() {
            //given
            String id = "abc123";
            Pet pet = new Pet(id, "Lucy", 6);
            Pet updatedPet = new Pet(id, "Lucy", 7);
            //when
            when(petRepoMock.update(id, pet)).thenReturn(updatedPet);
            Pet returnedPet = petService.updatePetInfo(id, pet);
            //then
            //Check to see if the updated pet is returned
            assertEquals(updatedPet, returnedPet);
            verify(petRepoMock).update("abc123", pet);
        }

        @Test
        void petsInfoStaysTheSame() {
            //given
            String id = "abc123";
            Pet pet = new Pet(id, "Lucy", 6);
            Pet updatedPet = new Pet(id, "Lucy", 7);
            //when
            when(petRepoMock.update(id, pet)).thenReturn(updatedPet);
            petService.updatePetInfo(id, pet);
            //then
            verify(petRepoMock).update(eq(id), petArgumentCaptor.capture());

            Pet capturedPet = petArgumentCaptor.getValue();
            assertAll(
                    () -> assertEquals(id, capturedPet.getId()),
                    () -> assertEquals("Lucy", capturedPet.getName()),
                    () -> assertEquals(6, capturedPet.getAge())
            );
        }
    }

    @Test
    void theReposDeleteMethodShouldBeCalled() {
        //given
        String id = "abc123";
        Pet deletedPet = new Pet(id, "Lucy", 6);
        //when
        when(petRepoMock.delete(id)).thenReturn(deletedPet);
        Pet returnedPet = petService.deletePet(id);
        //then
        assertEquals(returnedPet, deletedPet);
        verify(petRepoMock).delete(id);
    }

    @Test
    void convertToDogYearsCorrectly() {
        assertAll(
                () -> assertEquals(14, petService.convertToDogYears(2)),
                () -> assertEquals(0, petService.convertToDogYears(0)),
                () -> assertEquals(7, petService.convertToDogYears(1)),
                () -> assertEquals(49, petService.convertToDogYears(7)),
                () -> assertEquals(70, petService.convertToDogYears(10))
        );
    }
}