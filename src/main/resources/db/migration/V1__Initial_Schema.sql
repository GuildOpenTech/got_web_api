CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       enabled BOOLEAN DEFAULT true,
                       version BIGINT DEFAULT 0,
                       created_at TIMESTAMP WITH TIME ZONE,
                       updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE permissions (
                             id BIGSERIAL PRIMARY KEY,
                             name VARCHAR(255) NOT NULL UNIQUE,
                             type VARCHAR(50) NOT NULL,
                             description VARCHAR(255)
);

CREATE TABLE user_roles (
                            user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                            role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
                            PRIMARY KEY (user_id, role_id)
);

CREATE TABLE role_permissions (
                                  role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
                                  permission_id BIGINT REFERENCES permissions(id) ON DELETE CASCADE,
                                  PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE acl_entries (
                             id BIGSERIAL PRIMARY KEY,
                             user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                             role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
                             resource VARCHAR(255) NOT NULL,
                             action VARCHAR(255) NOT NULL,
                             allowed BOOLEAN NOT NULL DEFAULT false,
                             created_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_acl_user_resource ON acl_entries(user_id, resource);
CREATE INDEX idx_acl_role_resource ON acl_entries(role_id, resource);