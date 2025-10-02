package com.api.Job_Portal.Controller;

import com.api.Job_Portal.Entity.Application;
import com.api.Job_Portal.Entity.JobPost;
import com.api.Job_Portal.Entity.User;
import com.api.Job_Portal.Repository.UserRepository;
import com.api.Job_Portal.Service.ApplicationService;
import com.api.Job_Portal.Service.JobPostService;
import com.api.Job_Portal.dto.ApplicationDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserRepository userRepository;
    private final JobPostService jobPostService;

    @Autowired
    public ApplicationController(ApplicationService applicationService, UserRepository userRepository , JobPostService jobPostService) {
        this.applicationService = applicationService;
        this.userRepository = userRepository;
        this.jobPostService = jobPostService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get applications for the authenticated job seeker",
            description = "Retrieves a paginated list of applications for the currently authenticated job seeker.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved applications")
    @ApiResponse(responseCode = "403", description = "Access denied due to insufficient role")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<ApplicationDTO>> getMyApplications(Principal principal,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        String email = principal.getName();
        Long jobSeekerId = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthController.UserNotFoundException("Job seeker not found with email: " + email))
                .getId();
        Pageable pageable = PageRequest.of(page, size);
        Page<Application> applicationPage = applicationService.getApplicationsByJobSeeker(jobSeekerId, pageable);

        List<ApplicationDTO> applicationDTOs = applicationPage.getContent().stream().map(application -> {
            ApplicationDTO dto = new ApplicationDTO(
                    application.getId(),
                    application.getJobPost().getId(),
                    application.getJobPost().getTitle(),
                    application.getJobSeeker().getId(),
                    application.getJobSeeker().getUsername(), // Assuming username as jobSeekerName
                    application.getResumeUrl(),
                    application.getStatus()
            );
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(applicationDTOs);
    }

    @GetMapping("/job/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(summary = "Get applications for a specific job",
            description = "Retrieves a paginated list of applications for a specific job, restricted to the job's employer.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved applications")
    @ApiResponse(responseCode = "403", description = "Access denied due to insufficient role")
    @ApiResponse(responseCode = "403", description = "Access denied due to unauthorized employer")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<ApplicationDTO>> getApplicationsByJob(@Parameter(description = "ID of the job") @PathVariable Long id, Principal principal,@Parameter(description = "Page number, starting from 0")
                                                                     @RequestParam(defaultValue = "0") int page,@Parameter(description = "Number of items per page")
                                                                     @RequestParam(defaultValue = "10") int size) {
        String email = principal.getName();
        User employer = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthController.UserNotFoundException("Employer not found with email: " + email));

        // Validate job existence and ownership (optional: restrict to employer's jobs)
        JobPost jobPost = jobPostService.getJobById(id);
        if (!jobPost.getEmployer().getId().equals(employer.getId())) {
            throw new AccessDeniedException("You are not authorized to view applicants for this job");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Application> applicationPage = applicationService.getApplicationsByJobPost(id, pageable);

        List<ApplicationDTO> applicationDTOs = applicationPage.getContent().stream().map(application -> {
            ApplicationDTO dto = new ApplicationDTO(
                    application.getId(),
                    application.getJobPost().getId(),
                    application.getJobPost().getTitle(),
                    application.getJobSeeker().getId(),
                    application.getJobSeeker().getUsername(),
                    application.getResumeUrl(),
                    application.getStatus()
            );
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(applicationDTOs);
    }
}