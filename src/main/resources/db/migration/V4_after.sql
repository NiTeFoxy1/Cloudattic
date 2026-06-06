ALTER TABLE share_links
DROP CONSTRAINT fk_share_file;

ALTER TABLE share_links
ADD CONSTRAINT fk_share_file
FOREIGN KEY (file_id)
REFERENCES files(id)
ON DELETE CASCADE;

ALTER TABLE download_history
DROP CONSTRAINT fk_download_file;

ALTER TABLE download_history
ADD CONSTRAINT fk_download_file
FOREIGN KEY (file_id)
REFERENCES files(id)
ON DELETE CASCADE;