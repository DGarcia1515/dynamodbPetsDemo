package com.example.dynamodbpetsdemo.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.example.dynamodbpetsdemo.entity.Pet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PetRepo  {
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    public PetRepo(DynamoDBMapper mapper) {
        dynamoDBMapper = mapper;
    }

    public Pet save(Pet pet) {
        dynamoDBMapper.save(pet);
        return pet;
    }

    public Pet findById(String id) {
        return dynamoDBMapper.load(Pet.class, id);
    }

    public List<Pet> findAll() {
        return dynamoDBMapper.scan(Pet.class, new DynamoDBScanExpression());
    }

    public Pet update(String id, Pet pet) {
        dynamoDBMapper.save(pet, new DynamoDBSaveExpression().withExpectedEntry("id", new ExpectedAttributeValue(new AttributeValue().withS(id))));
        return pet;
    }
    public Pet delete(String id) {
        //dynamoDBMapper.delete(id);
        Pet pet = findById(id);
        if(pet != null) {
            dynamoDBMapper.delete(pet);
            return pet;
        } else {
            return null;
        }
    }

    public int getAge(String id) {
        Pet pet = findById(id);
        return pet.getAge();
    }

}
