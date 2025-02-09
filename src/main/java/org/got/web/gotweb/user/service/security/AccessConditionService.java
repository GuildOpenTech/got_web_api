package org.got.web.gotweb.user.service.security;

import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.exception.AccessConditionException;
import org.got.web.gotweb.user.domain.security.AccessCondition;
import org.got.web.gotweb.user.repository.security.AccessConditionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccessConditionService {

    private final AccessConditionRepository conditionRepository;

    public List<AccessCondition> getAllConditions() {
        return conditionRepository.findAll();
    }

    public AccessCondition getConditionById(Long id) {
        return conditionRepository.findById(id)
                .orElseThrow(() -> new AccessConditionException.ConditionNotFoundException(id));
    }

    public AccessCondition createCondition(AccessCondition condition) {
        return conditionRepository.save(condition);
    }

    public AccessCondition updateCondition(Long id, AccessCondition condition) {
        AccessCondition existing = getConditionById(id);
        existing.setType(condition.getType());
        existing.setOperator(condition.getOperator());
        existing.setValues(condition.getValues());
        existing.setNegate(condition.isNegate());
        return conditionRepository.save(existing);
    }

    public void deleteCondition(Long id) {
        AccessCondition existing = getConditionById(id);
        conditionRepository.delete(existing);
    }
}
