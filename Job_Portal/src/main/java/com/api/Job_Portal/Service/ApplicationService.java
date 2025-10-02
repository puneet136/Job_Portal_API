package com.api.Job_Portal.Service;

import com.api.Job_Portal.Entity.Application;
import com.api.Job_Portal.Entity.JobPost;
import com.api.Job_Portal.Entity.User;
import com.api.Job_Portal.dto.ApplicationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApplicationService {
    /**
     * Applies for a job based on the provided application DTO.
     * @param applicationDto The DTO containing application details.
     * @return The saved Application entity.
     * @throws IllegalArgumentException if job seeker ID or job post ID is invalid.
     * @throws RuntimeException if the job seeker or job post is not found, or a duplicate application exists.
     */
    Application applyForJob(ApplicationDTO applicationDto);

    /**
     * Retrieves a page of applications for a specific job seeker.
     * @param jobSeekerId The ID of the job seeker.
     * @param pageable Pagination information.
     * @return A page of Application entities.
     * @throws IllegalArgumentException if jobSeekerId is invalid.
     */
    Page<Application> getApplicationsByJobSeeker(Long jobSeekerId, Pageable pageable);

    /**
     * Retrieves a page of applications for a specific job post.
     * @param jobPostId The ID of the job post.
     * @param pageable Pagination information.
     * @return A page of Application entities.
     * @throws IllegalArgumentException if jobPostId is invalid.
     */
    Page<Application> getApplicationsByJobPost(Long jobPostId, Pageable pageable);

    /**
     * Saves an application entity.
     * @param application The application to save.
     * @return The saved Application entity.
     * @throws IllegalArgumentException if jobseeker or job post is null.
     */
    Application saveApplication(Application application);

    /**
     * Checks if an application exists for the given jobseeker and job post.
     * @param jobSeeker The jobseeker.
     * @param jobPost The job post.
     * @return True if an application exists, false otherwise.
     */
    boolean existsByJobSeekerAndJobPost(User jobSeeker, JobPost jobPost);
}