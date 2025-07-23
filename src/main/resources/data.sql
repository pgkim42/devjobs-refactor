-- UTF-8 인코딩 테스트용 간단한 데이터
INSERT INTO skill (name) VALUES 
('Java'), ('Spring'), ('React'), ('Docker');

-- 개인 사용자 (비밀번호: password123)
INSERT INTO users (dtype, login_id, password, email, name, role) VALUES
('INDIVIDUAL', 'test1', '$2a$10$8H0OT8wgtALJkig6fmypi.Y7jzI5Y7W9PGgRKqnVeS2cLWGifwHF2', 'test1@test.com', '테스트', 'ROLE_INDIVIDUAL');

-- 기업 사용자
INSERT INTO users (dtype, login_id, password, email, name, role) VALUES
('COMPANY', 'company1', '$2a$10$8H0OT8wgtALJkig6fmypi.Y7jzI5Y7W9PGgRKqnVeS2cLWGifwHF2', 'company@test.com', '테스트회사', 'ROLE_COMPANY');

INSERT INTO company_users (id, company_name, company_code, ceo_name) VALUES
((SELECT id FROM users WHERE login_id = 'company1'), '테스트회사', '123-45-67890', '김대표');