-- Insertion des rôles de base
INSERT INTO roles (name) VALUES
    ('ROLE_ADMIN'),
    ('ROLE_USER'),
    ('ROLE_MANAGER');

-- Insertion des permissions
INSERT INTO permissions (name, type, description) VALUES
    ('READ_USER', 'USER', 'Permission to read user details'),
    ('CREATE_USER', 'USER', 'Permission to create new users'),
    ('UPDATE_USER', 'USER', 'Permission to update existing users'),
    ('DELETE_USER', 'USER', 'Permission to delete users'),
    ('READ_USERS', 'USER', 'Permission to read all users'),
    ('MANAGE_ROLES', 'ADMIN', 'Permission to manage roles and permissions'),
    ('VIEW_METRICS', 'SYSTEM', 'Permission to view system metrics');

-- Attribution des permissions aux rôles
WITH admin_role AS (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'),
     user_perms AS (SELECT id FROM permissions WHERE type = 'USER'),
     admin_perms AS (SELECT id FROM permissions WHERE type = 'ADMIN'),
     system_perms AS (SELECT id FROM permissions WHERE type = 'SYSTEM')
INSERT INTO role_permissions (role_id, permission_id)
SELECT admin_role.id, p.id
FROM admin_role, permissions p;

WITH user_role AS (SELECT id FROM roles WHERE name = 'ROLE_USER'),
     basic_perms AS (SELECT id FROM permissions WHERE name IN ('READ_USER', 'UPDATE_USER'))
INSERT INTO role_permissions (role_id, permission_id)
SELECT user_role.id, basic_perms.id
FROM user_role, basic_perms;