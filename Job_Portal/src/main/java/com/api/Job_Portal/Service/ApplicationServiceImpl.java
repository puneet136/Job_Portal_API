package com.api.Job_Portal.Service;

import com.api.Job_Portal.Entity.Application;
import com.api.Job_Portal.Entity.JobPost;
import com.api.Job_Portal.Entity.User;
import com.api.Job_Portal.Exception.ResourceNotFoundException;
import com.api.Job_Portal.Repository.ApplicationRepository;
import com.api.Job_Portal.Repository.JobPostRepository;
import com.api.Job_Portal.Repository.UserRepository;
import com.api.Job_Portal.dto.ApplicationDTO;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationServiceImpl.class);
    private static final String DEFAULT_STATUS = "Applied";

    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JobPostRepository jobPostRepository;

    @Override
    public Application applyForJob(ApplicationDTO applicationDto) {
        logger.info("Applying for job with jobSeekerId: {} and jobPostId: {}", applicationDto.getJobSeekerId(), applicationDto.getJobPostId());
        if (applicationDto.getJobSeekerId() == null || applicationDto.getJobPostId() == null) {
            throw new IllegalArgumentException("Job seeker ID and job post ID are required");
        }
        User jobSeeker = userRepository.findById(applicationDto.getJobSeekerId())
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker not found with id: " + applicationDto.getJobSeekerId()));

        JobPost jobPost = jobPostRepository.findById(applicationDto.getJobPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Job post not found with id: " + applicationDto.getJobPostId()));

        if (existsByJobSeekerAndJobPost(jobSeeker, jobPost)) {
            throw new RuntimeException("You have already applied for this job");
        }

        Application application = new Application();
        application.setJobSeeker(jobSeeker);
        application.setJobPost(jobPost);
        application.setResumeUrl(applicationDto.getResumeUrl());
        application.setStatus(applicationDto.getStatus() != null && !applicationDto.getStatus().trim().isEmpty()
                ? applicationDto.getStatus() : DEFAULT_STATUS);

        Application savedApplication = applicationRepository.save(application);
        logger.info("Successfully applied for job, application ID: {}", savedApplication.getId());
        return savedApplication;
    }


    @Override
    public Page<Application> getApplicationsByJobSeeker(Long jobSeekerId , Pageable pageable) {
        if (jobSeekerId == null) {
            throw new IllegalArgumentException("Job seeker ID is required");
        }
        logger.debug("Fetching applications for jobSeekerId: {}", jobSeekerId);
        return applicationRepository.findByJobSeekerId(jobSeekerId, pageable);
    }

    @Override
    public Page<Application> getApplicationsByJobPost(Long jobPostId, Pageable pageable) {
        if (jobPostId == null) {
            throw new IllegalArgumentException("Job post ID is required");
        }
        logger.debug("Fetching applications for jobPostId: {}", jobPostId);
        return applicationRepository.findByJobPostId(jobPostId, pageable);
    }

    @Override
    @Transactional
    public Application saveApplication(Application application) {
        // Validate that job_seeker and job_post are set
        if (application.getJobSeeker() == null || application.getJobPost() == null) {
            throw new IllegalArgumentException("Job seeker and job post must be set");
        }
        logger.info("Saving application with ID: {}", application.getId());
        return applicationRepository.save(application);
    }

    @Override
    public boolean existsByJobSeekerAndJobPost(User jobSeeker, JobPost jobPost) {
        if (jobSeeker == null || jobPost == null) {
            throw new IllegalArgumentException("Job seeker and job post must not be null");
        }
        logger.debug("Checking existence of application for jobSeeker: {} and jobPost: {}", jobSeeker.getId(), jobPost.getId());
        return applicationRepository.existsByJobSeekerAndJobPost(jobSeeker, jobPost);
    }


}

