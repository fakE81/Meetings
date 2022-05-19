package com.internship.Meetings.Meeting;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unbescape.html.HtmlEscape;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/meeting")
public class MeetingController {


    private final  MeetingRepository meetingRepository;

    @Autowired
    public MeetingController(MeetingRepository meetingRepository) {

        this.meetingRepository = meetingRepository;
    }


    @GetMapping(path = "/list", produces="application/json")
    public ResponseEntity<List<Meeting>> getMeetingsAll(){

        return new ResponseEntity<List<Meeting>>(meetingRepository.getMeetings(),HttpStatus.OK);
        //return meetingRepository.getMeetings();
    }


    // /list/filter?name=&desc=& ir t.t.
    @GetMapping("/list/filter")
    public List<Meeting> getMeetingsByFilter(@RequestParam("desc")Optional<String> description,
                                             @RequestParam("rperson") Optional<String> rPerson,
                                             @RequestParam("category") Optional<String> category,
                                             @RequestParam("type") Optional<String> type,
                                             @RequestParam("startDate") Optional<String> startDate,
                                             @RequestParam("endDate") Optional<String> endDate,
                                             @RequestParam("nAttendees") Optional<Integer> numberAttendees){
        return meetingRepository.getMeetingsByFilter(description,rPerson,category,type,startDate,endDate,numberAttendees);
    }

    //Creating new meeting and in URL including responsiblePerson, giving all Meeting body excluding responsible person.
    @PostMapping("/{responsiblePerson}/submit")
    public ResponseEntity<?> addNewMeeting(@PathVariable("responsiblePerson") String rperson,
            @RequestBody Meeting meeting
    ) throws IOException {
        // Check if Category and Type is Valid
        if(!meetingRepository.isValid(meeting.getCategory(), meeting.getType())){
            return new ResponseEntity<String>("Category or Type is not valid!",HttpStatus.BAD_REQUEST);
        }
        meeting.setResponsiblePerson(rperson);
        meetingRepository.addNewMeeting(meeting);
        return new ResponseEntity<Integer>(meeting.getId(), HttpStatus.CREATED);
    }

    //Add a person to the meeting:
    @PostMapping("/{meetingId}/add")
    public ResponseEntity<?> addPersonToMeeting(@PathVariable("meetingId") int id, @RequestParam("nickname") String nickname) throws IOException {
        Person person = new Person(nickname);
        boolean isGood  = meetingRepository.addPersonToMeeting(id,person);
        if(!isGood){
            return new ResponseEntity<String>("This person exists in meeting!!!", HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<Person>(person, HttpStatus.OK);
    }

    //Delete meeting in URL including responsiblePerson and giving meeting id
    @DeleteMapping("/{responsiblePerson}/delete/{id}")
    public ResponseEntity<?> deleteMeeting(@PathVariable("responsiblePerson") String rperson,@PathVariable("id") int id) throws IOException {
        boolean isRemoved = meetingRepository.deleteMeeting(rperson,id);

        if(!isRemoved){
            return new ResponseEntity<String>("Don't have permission to delete",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Integer>(id,HttpStatus.OK);
    }
    @DeleteMapping("/{meetingId}/remove")
    public ResponseEntity<String> removePerson(@PathVariable("meetingId") int id,@RequestParam("nickname") String nickname){
        boolean isRemoved = meetingRepository.removePerson(id,nickname);

        if(!isRemoved){
            return new ResponseEntity<String>("Can't remove responsible person!",HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(nickname,HttpStatus.OK);
    }
}
