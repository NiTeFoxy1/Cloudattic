CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    created_at TIMESTAMP
);

CREATE TABLE folders (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    created_at TIMESTAMP,
    user_id BIGINT,
    parent_folder_id BIGINT
);

CREATE TABLE files (
    id BIGSERIAL PRIMARY KEY,
    original_name VARCHAR(255),
    stored_name VARCHAR(255),
    size BIGINT,
    mime_type VARCHAR(255),
    upload_date TIMESTAMP,
    path VARCHAR(255),
    user_id BIGINT,
    folder_id BIGINT
);

CREATE TABLE share_links (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255),
    expiration_date TIMESTAMP,
    public_access BOOLEAN,
    file_id BIGINT
);

CREATE TABLE download_history (
    id BIGSERIAL PRIMARY KEY,
    download_time TIMESTAMP,
    ip_address VARCHAR(255),
    file_id BIGINT,
    user_id BIGINT
);

CREATE TABLE file_tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE user_roles (
    user_id BIGINT,
    role_id BIGINT
);

CREATE TABLE file_tag_relations (
    file_id BIGINT,
    tag_id BIGINT
);

ALTER TABLE folders
ADD CONSTRAINT fk_folder_user
FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE folders
ADD CONSTRAINT fk_parent_folder
FOREIGN KEY (parent_folder_id) REFERENCES folders(id);

ALTER TABLE files
ADD CONSTRAINT fk_file_user
FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE files
ADD CONSTRAINT fk_file_folder
FOREIGN KEY (folder_id) REFERENCES folders(id);

ALTER TABLE share_links
ADD CONSTRAINT fk_share_file
FOREIGN KEY (file_id) REFERENCES files(id);

ALTER TABLE download_history
ADD CONSTRAINT fk_download_file
FOREIGN KEY (file_id) REFERENCES files(id);

ALTER TABLE download_history
ADD CONSTRAINT fk_download_user
FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE user_roles
ADD CONSTRAINT fk_user_roles_user
FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE user_roles
ADD CONSTRAINT fk_user_roles_role
FOREIGN KEY (role_id) REFERENCES roles(id);

ALTER TABLE file_tag_relations
ADD CONSTRAINT fk_file_tag_file
FOREIGN KEY (file_id) REFERENCES files(id);

ALTER TABLE file_tag_relations
ADD CONSTRAINT fk_file_tag_tag
FOREIGN KEY (tag_id) REFERENCES file_tags(id);