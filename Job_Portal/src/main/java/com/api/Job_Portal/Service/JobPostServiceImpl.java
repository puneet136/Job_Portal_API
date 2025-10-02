package com.api.Job_Portal.Service;

import com.api.Job_Portal.Entity.*;
import com.api.Job_Portal.Exception.ResourceNotFoundException;
import com.api.Job_Portal.Repository.JobPostRepository;
import com.api.Job_Portal.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobPostServiceImpl implements JobPostService {

    @Autowired
    private JobPostRepository jobPostRepository;


    public Page<JobPost> getAllJobs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return listJobs(pageable);
    }

    @Override
    public Page<JobPost> listJobs(Pageable pageable) {
        return jobPostRepository.findAll(pageable);
    }

    @Override
    public List<JobPost> getAllJobsUnpaginated() {
        return jobPostRepository.findAll();
    }

    @Override
    @Transactional
    public JobPost createJob(JobPost jobPost, User employer) {
        if (jobPost == null || employer == null || !employer.getRole().getName().equals("EMPLOYER")) {
            throw new IllegalArgumentException("Invalid job post or employer");
        }
        jobPost.setEmployer(employer);
        if (jobPost.getId() != null) {
            jobPost.setId(null);
        }
        return jobPostRepository.save(jobPost);
    }

    @Override
    public JobPost updateJob(Long jobId, JobPost jobPost, User employer) {
        JobPost existingJob = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        if (!existingJob.getEmployer().equals(employer)) {
            throw new AccessDeniedException("You can only update your own jobs");
        }
        existingJob.setTitle(jobPost.getTitle());
        existingJob.setDescription(jobPost.getDescription());
        existingJob.setSkills(jobPost.getSkills());
        existingJob.setSalary(jobPost.getSalary());
        existingJob.setCategory(jobPost.getCategory());
        return jobPostRepository.save(existingJob);
    }

    @Override
    public JobPost getJobById(Long id) {
        return jobPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job post not found with id: " + id));
    }

    @Override
    public void deleteJob(Long jobId, User employer) {
        JobPost jobPost = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        if (!jobPost.getEmployer().equals(employer)) {
            throw new AccessDeniedException("You can only delete your own jobs");
        }
        jobPostRepository.delete(jobPost);
    }
}
