package com.qa.person.demo.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qa.person.demo.domain.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql(scripts = {"classpath:person-schema.sql", "classpath:person-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@ActiveProfiles("test")
public class PersonIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    // converts JSON to java and java to JSON
    private ObjectMapper mapper;


    @Test
    void testCreate() throws Exception {
//        RequestBuilder req = MockMvcRequestBuilders.post("/create").content("JSON DATA").contentType(MediaType.APPLICATION_JSON);

        Person testPerson = new Person("Jordan", 29, "Trainer");
        String reqBody = this.mapper.writeValueAsString(testPerson);
        System.out.println("PERSON: " + testPerson);
        System.out.println("JSON: " + reqBody);

        RequestBuilder req = post("/create").content(reqBody).contentType(MediaType.APPLICATION_JSON);

        ResultMatcher checkStatus = status().isCreated();
        testPerson.setId(2);
        String resBody = this.mapper.writeValueAsString(testPerson);
        System.out.println("SAVED PERSON: " + testPerson);
        System.out.println("RES JSON: " + reqBody);
        ResultMatcher checkBody = content().json(resBody);

        mvc.perform(req).andExpect(checkStatus).andExpect(checkBody);
    }

    @Test
    void testCreate2() throws Exception {
        String reqBody = this.mapper.writeValueAsString(new Person("Jordan", 29, "Trainer"));

        String resBody = this.mapper.writeValueAsString(new Person(2, "Jordan", 29, "Trainer"));

        mvc.perform(post("/create").content(reqBody).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andExpect(content().json(resBody));
    }

    @Test
    void testRead() throws Exception {
        String resBody = this.mapper.writeValueAsString(new Person(1, "Barry", 40, "Plumber"));
        this.mvc.perform(MockMvcRequestBuilders.get("/get/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(resBody));
    }

    @Test
    void testReadAll() throws Exception {
        String resBody = this.mapper.writeValueAsString(List.of(new Person(1, "Barry", 40, "Plumber")));
        this.mvc.perform(MockMvcRequestBuilders.get("/getAll"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(resBody));
    }

    @Test
    void testUpdate() throws Exception {
        Person updated = new Person(1, "Larry", 30, "Plumber");
        String resBody = this.mapper.writeValueAsString(updated);

        this.mvc.perform(patch("/update")
                        .queryParam("id", String.valueOf(updated.getId()))
                        .queryParam("name", updated.getName())
                        .queryParam("age",  String.valueOf(updated.getAge()))
                        .queryParam("jobTitle", updated.getJobTitle()))
                .andExpect(status().isOk())
                .andExpect(content().json(resBody));
    }

    @Test
    void testRemove() throws Exception{

        this.mvc.perform(delete("/remove/1")).andExpect(status().isOk())
                .andExpect(content().string("Person removed"));
    }
}
