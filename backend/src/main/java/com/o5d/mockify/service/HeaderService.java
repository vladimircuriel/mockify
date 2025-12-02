/* (C)2025 */
package com.o5d.mockify.service;

import com.o5d.mockify.model.Header;
import com.o5d.mockify.repository.HeaderRepository;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HeaderService {
    private final HeaderRepository headerRepository;

    public List<Header> getAllHeaders() {
        return headerRepository.findAll();
    }

    public Optional<Header> getHeaderById(Long id) {
        return headerRepository.findById(id);
    }

    public Header saveHeader(Header header) {
        return headerRepository.save(header);
    }

    public Optional<Header> deleteHeader(Long id) {
        return headerRepository
                .findById(id)
                .map(
                        header -> {
                            headerRepository.delete(header);
                            return header;
                        });
    }
}
