package org.got.web.gotweb.user.domain.security;

/**
 * Opérateurs de comparaison pour chaque critère.
 */
public enum Operator {
    /**
     * L'ensemble de la propriété de l'utilisateur doit contenir toutes les valeurs spécifiées.
     * Exemple : roles.contains(1,2,3) signifie que l'utilisateur doit avoir les rôles 1, 2 et 3.
     */
    ALL_OF,

    /**
     * L'ensemble de la propriété de l'utilisateur ne doit contenir aucune des valeurs spécifiées.
     */
    NONE_OF,

    /**
     * L'ensemble de la propriété de l'utilisateur doit contenir au moins une des valeurs spécifiées.
     */
    ONE_OF,

    /**
     * La propriété de l'utilisateur (qui doit être unique) doit être exactement égale à la valeur spécifiée.
     */
    EQUALS,

    /**
     * La propriété de l'utilisateur (unique) ne doit pas être égale à la valeur spécifiée.
     */
    NOT_EQUALS
}

