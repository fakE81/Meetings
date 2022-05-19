package com.internship.Meetings.Meeting;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MeetingControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    // 1) Create a new meeting Unit Test.
    @Test
    void createNewMeeting(){

        String url = "http://localhost:"+port+"/meeting/petras/submit";
        Meeting meeting = new Meeting("Paskaita","Paskaitos aprasymas", "Short","InPerson","2022-05-10","2022-05-11");
        HttpEntity<Meeting> request = new HttpEntity<>(meeting);
        ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.POST, request, Integer.class);
        // Getting Created status
        Assert.assertEquals(response.getStatusCode(),HttpStatus.CREATED);
        // Getting meeting information
        System.out.println(response.getBody()+"\n");
        Assert.assertNotNull(response.getBody());

    }

    //2) Create a new meeting but with bad category
    @Test
    void createBadMeeting(){
        String url = "http://localhost:"+port+"/meeting/petras/submit";
        Meeting meeting = new Meeting("Paskaita","Paskaitos aprasymas", "no","InPerson","2022-05-10","2022-05-11");
        HttpEntity<Meeting> request = new HttpEntity<>(meeting);
        ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        // Getting Bad request status
        Assert.assertEquals(response.getStatusCode(),HttpStatus.BAD_REQUEST);
        // Getting meeting information
        System.out.println(response.getBody()+"\n");
        Assert.assertNotNull(response.getBody());
    }

    // 3) Delete meeting.
    @Test
    void deleteMeeting(){
        // Create
        String url = "http://localhost:"+port+"/meeting/juozas/submit";
        Meeting meeting = new Meeting("Paskaita","Paskaitos aprasymas", "Short","InPerson","2022-05-10","2022-05-11");
        HttpEntity<Meeting> request = new HttpEntity<>(meeting);
        ResponseEntity<?> createResponse = restTemplate.exchange(url, HttpMethod.POST, request, Integer.class);


        // First trying to delete without permission, second with permission. Body contains meeting ID.
        String deleteUrl = "http://localhost:"+port+"/meeting/petras/delete/"+createResponse.getBody();
        ResponseEntity<?> deleteResponse = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, null, String.class);
        // Checking if can other person delete.
        Assert.assertEquals(deleteResponse.getStatusCode(),HttpStatus.BAD_REQUEST);
        System.out.println(deleteResponse.getBody()+"\n");
        deleteUrl = "http://localhost:"+port+"/meeting/juozas/delete/"+createResponse.getBody();
        deleteResponse = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, null, Integer.class);
        Assert.assertEquals(deleteResponse.getStatusCode(),HttpStatus.OK);
        Assert.assertEquals(deleteResponse.getBody(),createResponse.getBody());
    }

    // 4) Add a person to meeting
    @Test
    void addPersonToMeeting(){
        // Create meeting:
        String url = "http://localhost:"+port+"/meeting/ignas/submit";
        Meeting meeting = new Meeting("Paskaita","Paskaitos aprasymas", "Short","InPerson","2022-05-10","2022-05-11");
        HttpEntity<Meeting> request = new HttpEntity<>(meeting);
        ResponseEntity<?> createResponse = restTemplate.exchange(url, HttpMethod.POST, request, Integer.class);

        //Add Attendees. Response gives us meeting ID.
        String person1 = "http://localhost:"+port+"/meeting/"+createResponse.getBody()+"/add?nickname=ignas";
        String person2 = "http://localhost:"+port+"/meeting/"+createResponse.getBody()+"/add?nickname=Laurynas";
        String person3 = "http://localhost:"+port+"/meeting/"+createResponse.getBody()+"/add?nickname=Juozas";
        ResponseEntity<?> response = restTemplate.exchange(person1, HttpMethod.POST, null, String.class);
        System.out.println(response.getBody()+"\n");
        Assert.assertEquals(response.getStatusCode(),HttpStatus.NOT_ACCEPTABLE);
        response = restTemplate.exchange(person2, HttpMethod.POST, null, String.class);
        Assert.assertEquals(response.getStatusCode(),HttpStatus.OK);
        response = restTemplate.exchange(person3, HttpMethod.POST, null, String.class);
        Assert.assertEquals(response.getStatusCode(),HttpStatus.OK);
    }

    // 5) Remove a person from the meeting
    @Test
    void removePerson(){
        String url = "http://localhost:"+port+"/meeting/gabija/submit";
        Meeting meeting = new Meeting("Paskaita","Paskaitos aprasymas", "Short","InPerson","2022-05-10","2022-05-11");
        HttpEntity<Meeting> request = new HttpEntity<>(meeting);
        ResponseEntity<?> createResponse = restTemplate.exchange(url, HttpMethod.POST, request, Integer.class);
        String person2Add = "http://localhost:"+port+"/meeting/"+createResponse.getBody()+"/add?nickname=Laurynas";
        ResponseEntity<?> response = restTemplate.exchange(person2Add, HttpMethod.POST, null, String.class);


        String person1 = "http://localhost:"+port+"/meeting/"+createResponse.getBody()+"/remove?nickname=gabija";
        String person2 = "http://localhost:"+port+"/meeting/"+createResponse.getBody()+"/remove?nickname=Laurynas";

        // Responsible person of Meeting 0 is gabija.
        response = restTemplate.exchange(person1, HttpMethod.DELETE, null, String.class);
        System.out.println(response.getBody());
        Assert.assertEquals(response.getStatusCode(),HttpStatus.NOT_ACCEPTABLE);
        response = restTemplate.exchange(person2, HttpMethod.DELETE, null, String.class);
        Assert.assertEquals(response.getStatusCode(),HttpStatus.OK);
        Assert.assertEquals(response.getBody(),"Laurynas");
    }

    @Test
    void filters(){
        //Create meeting:
        String url = "http://localhost:"+port+"/meeting/kamile/submit";
        Meeting meeting = new Meeting("Paskaita","Paskaitos aprasymas", "Short","InPerson","2022-05-10","2022-05-12");
        HttpEntity<Meeting> request = new HttpEntity<>(meeting);
        ResponseEntity<?> createResponse = restTemplate.exchange(url, HttpMethod.POST, request, Integer.class);

        String filterByDesc = "http://localhost:"+port+"/meeting/list/filter?desc=aprasymas";
        String filterByRPerson = "http://localhost:"+port+"/meeting/list/filter?rperson=kamile";
        String filterByCategory = "http://localhost:"+port+"/meeting/list/filter?category=Short";
        String filterByType= "http://localhost:"+port+"/meeting/list/filter?type=InPerson";
        String filterByStartDate = "http://localhost:"+port+"/meeting/list/filter?startDate=2021-05-09";
        String filterByStartEndDate = "http://localhost:"+port+"/meeting/list/filter?startDate=2021-05-01&endDate=2021-05-15";
        String filterByAttendees =  "http://localhost:"+port+"/meeting/list/filter?nAttendees=2";

        filterResponse(filterByDesc);
        filterResponse(filterByRPerson);
        filterResponse(filterByCategory);
        filterResponse(filterByType);
        filterResponse(filterByStartDate);
        filterResponse(filterByStartEndDate);
        filterResponse(filterByAttendees);
    }

    public void filterResponse(String url){
        ResponseEntity<List<Meeting>> filterResponse = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Meeting>>() {});
        System.out.println(filterResponse.getBody()+"\n");
        Assert.assertNotNull(filterResponse.getBody());
    }
    @Test
    void getMeetingsAll() {
        String url = "http://localhost:"+port+"/meeting/list";
        ResponseEntity<List<Meeting>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Meeting>>() {});
        //Check if Status is OK.
        Assert.assertEquals(response.getStatusCode(),HttpStatus.OK);
        //Check if Exists meetings.
        Assert.assertNotNull(response.getBody());
    }
}