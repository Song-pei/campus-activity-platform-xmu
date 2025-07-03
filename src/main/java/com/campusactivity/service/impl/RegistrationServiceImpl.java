package com.campusactivity.service.impl;

import com.campusactivity.entity.Registration;
import com.campusactivity.repository.RegistrationRepository;
import com.campusactivity.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    @Autowired
    private RegistrationRepository registrationRepository;

    @Override
    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    @Override
    public Registration getRegistrationById(Long id) {
        return registrationRepository.findById(id).orElse(null);
    }

    @Override
    public Registration createRegistration(Registration registration) {
        return registrationRepository.save(registration);
    }

    @Override
    public Registration updateRegistration(Long id, Registration registration) {
        if (registrationRepository.existsById(id)) {
            registration.setId(id);
            return registrationRepository.save(registration);
        }
        return null;
    }

    @Override
    public void deleteRegistration(Long id) {
        registrationRepository.deleteById(id);
    }

    @Override
    public List<Registration> getRegistrationsByActivityId(Long activityId) {
        return registrationRepository.findByActivityId(activityId);
    }

    @Override
    public long countRegistrationsByActivityId(Long activityId) {
        return registrationRepository.countByActivityId(activityId);
    }

    @Override
    public Map<Long, Long> countRegistrationsForAllActivities() {
        List<Registration> all = getAllRegistrations();
        Map<Long, Long> map = new HashMap<>();
        for (Registration reg : all) {
            Long actId = reg.getActivity().getId();
            map.put(actId, map.getOrDefault(actId, 0L) + 1);
        }
        return map;
    }
    
}