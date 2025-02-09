package org.got.web.gotweb.security.evaluator;

import lombok.Builder;

import java.util.Set;

/**
 * Cette classe regroupe l’ensemble des helpers, de sorte qu’elle servira de racine lors de l’évaluation de l’expression SpEL.
 *
 * @param roles
 * @param permissions
 * @param departments
 * @param contexts
 */
@Builder
public record RuleContext(Set<Long> roles,
                          Set<Long> permissions,
                          Set<Long> departments,
                          Set<Long> contexts,
                          Long user) {
}
