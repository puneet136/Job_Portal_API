package com.api.Job_Portal.Service;

import com.api.Job_Portal.Entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobPostService {
    Page<JobPost> listJobs(Pageable pageable);
    List<JobPost> getAllJobsUnpaginated();
    Page<JobPost> getAllJobs(int page, int size); // Add this method
    JobPost createJob(JobPost jobPost, User employer);
    JobPost updateJob(Long jobId, JobPost jobPost, User employer);
    JobPost getJobById(Long jobId);
    void deleteJob(Long jobId, User employer);
}