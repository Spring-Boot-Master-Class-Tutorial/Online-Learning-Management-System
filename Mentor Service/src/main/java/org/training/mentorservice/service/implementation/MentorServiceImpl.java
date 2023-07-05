package org.training.mentorservice.service.implementation;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.training.mentorservice.dto.MentorDto;
import org.training.mentorservice.dto.ResponseDto;
import org.training.mentorservice.entity.Mentor;
import org.training.mentorservice.exception.ResourceConflictException;
import org.training.mentorservice.repository.MentorRepository;
import org.training.mentorservice.service.MentorService;

import java.util.Optional;
import java.util.UUID;

@Service
public class MentorServiceImpl implements MentorService {

    @Autowired
    private MentorRepository mentorRepository;

    @Value("${spring.application.responseCode}")
    private String responseCode;

    @Override
    public ResponseDto addMentor(MentorDto mentorDto) {

        Optional<Mentor> mentor = mentorRepository.findMentorByContactNoOrEmailId(mentorDto.getContactNo(), mentorDto.getEmailId());
        mentor.ifPresent(existingMentor -> {
            if (existingMentor.getEmailId().equals(mentorDto.getEmailId()) && existingMentor.getContactNo().equals(mentorDto.getContactNo())) {
                throw new ResourceConflictException("Mentor with the same emailId and contact number exists");
            } else if (existingMentor.getEmailId().equals(mentorDto.getEmailId())) {
                throw new ResourceConflictException("Mentor with the same emailId already exists");
            } else if (existingMentor.getContactNo().equals(mentorDto.getContactNo())) {
                throw new ResourceConflictException("Mentor with the same contact number already exists");
            }
        });

        Mentor newMentor = new Mentor();
        BeanUtils.copyProperties(mentorDto, newMentor);
        newMentor.setMentorId(UUID.randomUUID().toString());
        mentorRepository.save(newMentor);
        return new ResponseDto(responseCode, "Mentor added Successfully");
    }
}