/* (C)2025 */
package com.o5d.mockify.repository;

import com.o5d.mockify.model.Endpoint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityRepository extends JpaRepository<Endpoint, Long> {
    List<Endpoint> findByStatus(boolean status);

    Optional<Endpoint> findByIdAndStatus(Long id, boolean status);

    Optional<Endpoint> findByPathAndMethod(String path, String method);

    boolean existsByPathAndProjectId(String path, Long projectId);
}
