package org.got.web.gotweb.user.service.security;

import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.exception.AccessConditionGroupException;
import org.got.web.gotweb.user.domain.security.AccessConditionGroup;
import org.got.web.gotweb.user.repository.security.AccessConditionGroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccessConditionGroupService {

    private final AccessConditionGroupRepository conditionGroupRepository;

    public List<AccessConditionGroup> getAllConditionGroups() {
        return conditionGroupRepository.findAll();
    }

    public AccessConditionGroup getConditionGroupById(Long id) {
        return conditionGroupRepository.findById(id)
                .orElseThrow(() -> new AccessConditionGroupException.ConditionGroupNotFoundException(id));
    }

    public AccessConditionGroup createConditionGroup(AccessConditionGroup conditionGroup) {
        return conditionGroupRepository.save(conditionGroup);
    }

    public AccessConditionGroup updateConditionGroup(Long id, AccessConditionGroup updatedGroup) {
        AccessConditionGroup existingGroup = getConditionGroupById(id);
        existingGroup.setConditions(updatedGroup.getConditions());
        existingGroup.setCombinationOperator(updatedGroup.getCombinationOperator());
        existingGroup.setDescription(updatedGroup.getDescription());
        return conditionGroupRepository.save(existingGroup);
    }

    public void deleteConditionGroup(Long id) {
        AccessConditionGroup existingGroup = getConditionGroupById(id);
        conditionGroupRepository.delete(existingGroup);
    }
}

