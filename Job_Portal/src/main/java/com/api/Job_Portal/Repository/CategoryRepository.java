package com.api.Job_Portal.Repository;

import com.api.Job_Portal.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category , Long> {
}
