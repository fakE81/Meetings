package com.internship.Meetings.Meeting;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Meeting {

    private int id;
    private String name;
    private String responsiblePerson;
    private String description;

    private String category;
    private String type;
    private String startDate;
    private String endDate;

    private List<Person> attendees = new ArrayList<Person>();


    public Meeting() {
    }

    public Meeting(String name, String description, String category, String type, String startDate, String endDate) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public List<Person> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<Person> attendees) {
        this.attendees = attendees;
    }

    public boolean removeAttendee(String nickname) {
        for(Person person : attendees){
            if(person.getNickname().equals(nickname)){
                attendees.remove(person);
                return true;
            }
        }
        return false;
    }

    public boolean addAttendees(Person person){
        for(Person checkPerson : attendees ){
            if(checkPerson.getNickname().equals(person.getNickname())){
                return true;
            }
        }
        attendees.add(person);
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResponsiblePerson() {
        return responsiblePerson;
    }

    public void setResponsiblePerson(String responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }


    @Override
    public String toString() {
        return "Meeting{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", responsiblePerson='" + responsiblePerson + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", attendees=" + attendees +
                '}';
    }
}
