/* (C)2025 */
package com.o5d.mockify.service;

import com.o5d.mockify.model.Endpoint;
import com.o5d.mockify.model.Header;
import com.o5d.mockify.repository.EndpointRepository;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EndpointService {
    private final EndpointRepository endpointRepository;

    public Optional<Endpoint> getEndpointByPathAndMethod(String path, String method) {
        return endpointRepository.findByPathAndMethod(path, method);
    }

    public List<Endpoint> getAllEndpoints() {
        return endpointRepository.findByStatus(true);
    }

    public Optional<Endpoint> getEndpointById(Long id) {
        return endpointRepository.findByIdAndStatus(id, true);
    }

    public Endpoint saveEndpoint(Endpoint endpoint) {
        return endpointRepository.save(endpoint);
    }

    public Optional<Endpoint> updateEndpoint(Endpoint newEndpoint, Long id) {
        return getEndpointById(id).map(existing -> {

            existing.setName(newEndpoint.getName());
            existing.setDescription(newEndpoint.getDescription());
            existing.setPath(newEndpoint.getPath());
            existing.setMethod(newEndpoint.getMethod());
            existing.setDelay(newEndpoint.getDelay());
            existing.setExpirationDate(newEndpoint.getExpirationDate());
            existing.setEncoding(newEndpoint.getEncoding());
            existing.setResponseType(newEndpoint.getResponseType());
            existing.setResponseStatus(newEndpoint.getResponseStatus());
            existing.setJwt(newEndpoint.getJwt());
            existing.setBody(newEndpoint.getBody());
            existing.setStatus(newEndpoint.isStatus());
            existing.setSecurity(newEndpoint.isSecurity());
            existing.getHeaders().clear();

            if (newEndpoint.getHeaders() != null) {
                newEndpoint.getHeaders().forEach(header -> {
                    Header h = new Header();
                    h.setKey(header.getKey());
                    h.setValue(header.getValue());
                    h.setEndpoint(existing);
                    existing.getHeaders().add(h);
                });
            }

            return endpointRepository.save(existing);
        });
    }


    public void deleteEndpoint(Long id) {
        endpointRepository.findById(id).ifPresent(endpoint -> {
            endpoint.setStatus(false);
            endpointRepository.save(endpoint);
        });
    }
}
