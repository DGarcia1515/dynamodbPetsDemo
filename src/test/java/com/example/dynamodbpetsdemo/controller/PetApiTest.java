package com.example.dynamodbpetsdemo.controller;

import com.example.dynamodbpetsdemo.entity.Pet;
import com.example.dynamodbpetsdemo.exception.InvalidInputException;
import com.example.dynamodbpetsdemo.repository.PetRepo;
import com.example.dynamodbpetsdemo.service.PetService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;


import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
public class PetApiTest {
    @Autowired
    PetService petService; //dependency injection on static fields isn't supported

    @BeforeEach
    void setUp() {
        if(petService.findPetById("test123") != null) {
            petService.deletePet("test123");
        }
    }

    @AfterEach
    void teardown() {
        if(petService.findPetById("test123") != null) {
            petService.deletePet("test123");
        }
    }

    @Test
    void nonexistentEndpointTest() {
        System.out.println("\n\nTest 1\n\n");
        given().get("http://localhost:8080/pet").
                then().
                statusCode(404).
                log().all();

        given().get("http://localhost:8080/blah").
                then().
                statusCode(404).
                log().all();

        given().get("http://localhost:8080/").
                then().
                statusCode(404).
                log().all();
    }

    @Nested
    class apiSaveEndpointTests {

        @Test
        void successfulResponseTest() {

            JSONObject petJSON = new JSONObject();
            petJSON.put("age", 2);
            petJSON.put("name", "Test");
            petJSON.put("id", "test123");

            //baseURI = "http://localhost:8080/pets";

            given().header("Content-Type", "application/json").
                    contentType(ContentType.JSON).
                    accept(ContentType.JSON).
                    body(petJSON.toJSONString()).when().
                    post("http://localhost:8080/pets").
                    then().
                    statusCode(HttpStatus.OK.value()).
                    body("name", equalTo("TEST")).
                    body("age", equalTo(2)).
                    body("id", equalTo("test123")).
                    log().all();
        }

        @Test
        void nameNotGivenTest() {

            JSONObject petJSON = new JSONObject();
            petJSON.put("age", -2);
            petJSON.put("name", "Test");
            petJSON.put("id", "test123");

            //baseURI = "http://localhost:8080/pets";

            given().header("Content-Type", "application/json").
                    contentType(ContentType.JSON).
                    accept(ContentType.JSON).
                    body(petJSON.toJSONString()).when().
                    post("http://localhost:8080/pets").
                    then().
                    statusCode(HttpStatus.BAD_REQUEST.value()).
                    log().all();
        }

        @Test
        void negativeAgeTest() {

            JSONObject petJSON = new JSONObject();
            petJSON.put("age", 2);
            petJSON.put("id", "test123");

            //baseURI = "http://localhost:8080/pets";

            given().header("Content-Type", "application/json").
                    contentType(ContentType.JSON).
                    accept(ContentType.JSON).
                    body(petJSON.toJSONString()).when().
                    post("http://localhost:8080/pets").
                    then().
                    statusCode(HttpStatus.BAD_REQUEST.value()).
                    log().all();
        }

    }

    @Nested
    class apiFindByIdEndpointTests {

        @Test
        void successfulFindByIdEndpointTest() throws InvalidInputException {
            petService.savePet(new Pet("test123", "Test", 2));

            baseURI = "http://localhost:8080/pets";
            given().get("/test123").
                    then().
                    statusCode(HttpStatus.OK.value()).
                    body("id", equalTo("test123")).
                    body("name", equalTo("TEST")).
                    body("age", equalTo(2)).
                    body("dogAge", equalTo(14)).
                    log().all();
            //jsonpathfinder.com can be used to find the path of a json element
        }

        @Test
        void invalidIdTest() {
            baseURI = "http://localhost:8080/pets";
            given().get("/123test").
                    then().
                    statusCode(HttpStatus.NOT_FOUND.value()).
                    log().all();
        }

    }

    @Nested
    class apiFindAllEndpointTests {

        @Test
        void successfulFindAllEndpointTest() {
            given().get("http://localhost:8080/pets").
                    then().
                    statusCode(HttpStatus.OK.value());

        }

    }

    @Nested
    class apiUpdateEndpointTests {

        @Test
        void successfulUpdateEndpointTest() throws InvalidInputException {
            petService.savePet(new Pet("test123", "Test", 2));
            JSONObject pet = new JSONObject();
            pet.put("id", "test123");
            pet.put("age", 8);
            pet.put("name", "UpdatedTest");

            //System.out.println(pet.toJSONString());

            //baseURI = "http://localhost:8080/pets";

            given().header("Content-Type", "application/json").
                    contentType(ContentType.JSON).
                    accept(ContentType.JSON).
                    body(pet.toJSONString()).when().
                    put("http://localhost:8080/pets/update/test123").
                    then().
                    statusCode(HttpStatus.OK.value()).log().all();
        }

        @Test
        void invalidIdTest() throws InvalidInputException {
            petService.savePet(new Pet("test123", "Test", 2));
            JSONObject pet = new JSONObject();
            pet.put("id", "test123");
            pet.put("age", 8);
            pet.put("name", "UpdatedTest");

            //System.out.println(pet.toJSONString());

            //baseURI = "http://localhost:8080/pets";

            given().header("Content-Type", "application/json").
                    contentType(ContentType.JSON).
                    accept(ContentType.JSON).
                    body(pet.toJSONString()).when().
                    put("http://localhost:8080/pets/update/123test").
                    then().
                    statusCode(HttpStatus.NOT_FOUND.value()).log().all();
        }

        @Test
        void invalidBodyTest() throws InvalidInputException {
            petService.savePet(new Pet("test123", "Test", 2));
            JSONObject pet = new JSONObject();
            pet.put("name", "UpdateTest");
            pet.put("id", "test123");
            pet.put("age", "-3");

            given().header("Content-Type", "application/json").
                    contentType(ContentType.JSON).
                    accept(ContentType.JSON).
                    body(pet.toJSONString()).when().
                    put("http://localhost:8080/pets/update/test123").
                    then().
                    statusCode(HttpStatus.BAD_REQUEST.value()).log().all();
        }

    }

    @Nested
    class apiDeleteEndpointTests {

        @Test
        void successfulDeleteEndpointTest() throws InvalidInputException {
            petService.savePet(new Pet("test123", "Test", 2));
            RestAssured.when().delete("http://localhost:8080/pets/delete/test123").
                    then().
                    statusCode(HttpStatus.OK.value()).
                    log().all();
        }

        @Test
        void invalidIdTest() {
            baseURI = "http://localhost:8080/pets";
            RestAssured.when().delete("/delete/123test").
                    then().
                    statusCode(HttpStatus.NOT_FOUND.value()).
                    log().all();
        }

    }

}
