ALTER TABLE folders ADD COLUMN parent_id BIGINT;

ALTER TABLE folders
ADD CONSTRAINT fk_folder_parent
FOREIGN KEY (parent_id) REFERENCES folders(id);