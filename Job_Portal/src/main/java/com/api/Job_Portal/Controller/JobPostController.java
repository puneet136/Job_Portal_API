package com.api.Job_Portal.Controller;

import com.api.Job_Portal.Entity.Application;
import com.api.Job_Portal.Entity.JobPost;
import com.api.Job_Portal.Entity.User;
import com.api.Job_Portal.Repository.UserRepository;
import com.api.Job_Portal.Service.ApplicationService;
import com.api.Job_Portal.Service.JobPostService;
import com.api.Job_Portal.dto.JobPostDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/jobs")
public class JobPostController {
    private final JobPostService jobPostService;
    private final UserRepository userRepository;
    private final ApplicationService applicationService;

    @Autowired
    public JobPostController(JobPostService jobPostService, UserRepository userRepository, ApplicationService applicationService) {
        this.jobPostService = jobPostService;
        this.userRepository = userRepository;
        this.applicationService = applicationService;
    }

    @GetMapping
    public ResponseEntity<Page<JobPostDTO>> getAllJobs(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JobPost> jobs = jobPostService.getAllJobs(page, size);
        Page<JobPostDTO> dtoPage = jobs.map(this::convertToDto);
        return ResponseEntity.ok(dtoPage);
    }

    private static final Logger logger = LoggerFactory.getLogger(JobPostController.class);
    @GetMapping("/{id}")
    public ResponseEntity<JobPostDTO> getJobById(@PathVariable Long id) {
        logger.info("Fetching job with id: {}", id);
        JobPost jobPost = jobPostService.getJobById(id);
        JobPostDTO jobPostDTO = convertToDto(jobPost);
        return ResponseEntity.ok(jobPostDTO);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobPost> updateJob(@PathVariable Long id, @Valid @RequestBody JobPost jobPost, Principal principal) {
        String email = principal.getName();
        logger.info("Updating job {} by employer {}", id, email);
        User employer = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthController.UserNotFoundException("Employer not found with email: " + email));
        JobPost updatedJob = jobPostService.updateJob(id, jobPost, employer);
        return ResponseEntity.ok(updatedJob);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id, Principal principal) {
        String email = principal.getName();
        logger.info("Deleting job {} by employer {}", id, email);
        User employer = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthController.UserNotFoundException("Employer not found with email: " + email));
        jobPostService.deleteJob(id, employer);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/apply")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Application> applyForJob(@PathVariable Long id, @Valid @RequestBody Application application, Principal principal) {
        String email = principal.getName();
        User jobSeeker = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthController.UserNotFoundException("Job seeker not found with email: " + email));

        // Validate job existence
        JobPost jobPost = jobPostService.getJobById(id);

        if (applicationService.existsByJobSeekerAndJobPost(jobSeeker, jobPost)) {
            throw new RuntimeException("You have already applied for this job");
        }

        // Set jobseeker and job post
        application.setJobSeeker(jobSeeker);
        application.setJobPost(jobPost);

        // Set initial status (e.g., "PENDING")
        if (application.getStatus() == null) {
            application.setStatus("PENDING");
        }

        // Save the application
        Application savedApplication = applicationService.saveApplication(application);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedApplication);
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobPost> createJob(@Valid @RequestBody JobPost jobPost, Principal principal) {
        String email = principal.getName();
        System.out.println("Fetching Employer With Email :" + email);
        User employer = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthController.UserNotFoundException("Employer not found with email: " + email));
        return ResponseEntity.status(HttpStatus.CREATED).body(jobPostService.createJob(jobPost, employer));
    }

    private JobPostDTO convertToDto(JobPost jobPost) {
        String employerName = (jobPost.getEmployer() != null) ? jobPost.getEmployer().getUsername() : "Unknown";
        Long categoryId = (jobPost.getCategory() != null) ? jobPost.getCategory().getId() : null;
        String categoryName = (jobPost.getCategory() != null) ? jobPost.getCategory().getName() : "Not Specified";
        String categoryDescription = (jobPost.getCategory() != null) ? jobPost.getCategory().getDescription() : "Not Specified";

        return new JobPostDTO(
                jobPost.getId(),
                jobPost.getTitle(),
                jobPost.getDescription(),
                jobPost.getLocation() != null ? jobPost.getLocation() : "Not_Specified",
                jobPost.getSkills(),
                jobPost.getSalary(),
                employerName,
                categoryId,
                categoryName,
                categoryDescription
        );
    }

}