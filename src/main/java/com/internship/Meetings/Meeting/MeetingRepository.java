package com.internship.Meetings.Meeting;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;



@Component
public class MeetingRepository {

    List categoryFixedValues = Arrays.asList(new String[]{"codemonkey","hub","short","teambuilding"});
    List typeFixedValues = Arrays.asList(new String[]{"live", "inperson"});
    private List<Meeting> meetings = new ArrayList<Meeting>();

    MeetingRepository() throws IOException {
        loadFromJson();
    }

    public Meeting addNewMeeting(Meeting meeting) throws IOException {
        meeting.setId(getNextId());
        meeting.addAttendees(new Person(meeting.getResponsiblePerson()));
        meetings.add(meeting);
        saveToJson();
        return meeting;
    }

    public boolean isValid(String category, String type){
        return categoryFixedValues.contains(category.toLowerCase()) && typeFixedValues.contains(type.toLowerCase());
    }

    public boolean removePerson(int id,String nickname){
        if(!meetings.get(id).getResponsiblePerson().equals(nickname)){
            boolean isRemoved = meetings.get(id).removeAttendee(nickname);
            if(isRemoved)
                return true;
            return false;
        }
        return false;
    }
    private int getNextId(){
        if(meetings != null)
            return meetings.size();
        return 0;
    }

    private void saveToJson() throws IOException {
        Gson gson = new Gson();
        Writer writer = new FileWriter("Meetings.json");
        gson.toJson(meetings,writer);
        writer.close();
    }

    public List<Meeting> getMeetingsByFilter(Optional<String> description,
                                             Optional<String> rperson,
                                             Optional<String> category,
                                             Optional<String> type,
                                             Optional<String> startDate,
                                             Optional<String> endDate,
                                             Optional<Integer> nAttendees){
        List<Meeting> result = new ArrayList<Meeting>();
        if(!description.isEmpty()){
            result = getMeetingsByDesc(description.get());
        }
        else if(!rperson.isEmpty()){
            result = getMeetingsByRPerson(rperson.get());
        }
        else if(!category.isEmpty()){
            result = getMeetingsByCategory(category.get());
        }
        else if(!type.isEmpty()){
            result = getMeetingsByType(type.get());
        }
        else if(!startDate.isEmpty() && !endDate.isEmpty()){
            result = getMeetingsByDates(startDate.get(),endDate.get());
        }
        else if(!startDate.isEmpty()){
            result = getMeetingsByDate(startDate.get());
        }
        else if(!nAttendees.isEmpty()){
            result = getMeetingsByAttendees(nAttendees.get());
        }
        return result;
    }
    // Two dates 2021-05-02 - 2021-06-02. Checking only startDate
    public List<Meeting> getMeetingsByDates(String date1,String date2){
        List<Meeting> result = new ArrayList<Meeting>();
        for (Meeting meeting: meetings) {
            if(LocalDate.parse(meeting.getStartDate()).compareTo(LocalDate.parse(date1))>0 &&
                    LocalDate.parse(date2).compareTo(LocalDate.parse(meeting.getEndDate()))<0){
                result.add(meeting);
            }
        }
        return result;
    }

    public List<Meeting> getMeetingsByDate(String date){
        List<Meeting> result = new ArrayList<Meeting>();
        for (Meeting meeting: meetings) {
            if(LocalDate.parse(meeting.getStartDate()).compareTo(LocalDate.parse(date))>0){
                result.add(meeting);
            }
        }
        return result;
    }

    public List<Meeting> getMeetingsByAttendees(int number){
        List<Meeting> result = new ArrayList<Meeting>();
        for (Meeting meeting: meetings) {
            if(meeting.getAttendees().size()>=number){
                result.add(meeting);
            }
        }
        return result;
    }
    public List<Meeting> getMeetingsByDesc(String description){
        List<Meeting> result = new ArrayList<Meeting>();
        for (Meeting meeting: meetings) {
            if(meeting.getDescription().toLowerCase().contains(description.toLowerCase())){
                result.add(meeting);
            }
        }
        return result;
    }

    public List<Meeting> getMeetingsByCategory(String category){
        List<Meeting> result = new ArrayList<Meeting>();
        for (Meeting meeting: meetings) {
            if(meeting.getCategory().equals(category)){
                result.add(meeting);
            }
        }
        return result;
    }

    public List<Meeting> getMeetingsByRPerson(String rperson){
        List<Meeting> result = new ArrayList<Meeting>();
        for (Meeting meeting: meetings) {
            if(meeting.getResponsiblePerson().equals(rperson)){
                result.add(meeting);
            }
        }
        return result;
    }

    public List<Meeting> getMeetingsByType(String type){
        List<Meeting> result = new ArrayList<Meeting>();
        for (Meeting meeting: meetings) {
            if(meeting.getType().equals(type)){
                result.add(meeting);
            }
        }
        return result;
    }

    public boolean deleteMeeting(String rperson, int id) throws IOException {
        for (Meeting meeting: meetings) {
            if(meeting.getResponsiblePerson().equals(rperson)&&meeting.getId()==id){
                meetings.remove(meeting);
                saveToJson();
                return true;
            }
        }
        return false;
    }

    public boolean addPersonToMeeting(int id, Person person) throws IOException {
        boolean exists = meetings.get(id).addAttendees(person);
        if(exists){
            return false;
        }
        saveToJson();
        return true;
    }

    public List<Meeting> getMeetings(){
        return meetings;
    }


    public void loadFromJson() throws IOException {
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Path.of("meetings.json"));
        meetings = gson.fromJson(reader,new TypeToken<List<Meeting>>(){}.getType()); // Kas per TypeToken?
        if(meetings==null){
            meetings = new ArrayList<Meeting>();
        }
    }

}
