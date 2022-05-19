package com.internship.Meetings;

import com.internship.Meetings.Meeting.Meeting;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@SpringBootApplication
public class MeetingsApplication {
	// https://dzone.com/articles/how-to-create-rest-api-with-spring-boot
	public static void main(String[] args) {
		SpringApplication.run(MeetingsApplication.class, args);
	}

}
