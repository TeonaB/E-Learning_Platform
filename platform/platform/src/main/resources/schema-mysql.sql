DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS user_profile;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS lesson;
DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS user_course;

-- 1. Tabela Users
CREATE TABLE user (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      username VARCHAR(50) NOT NULL UNIQUE,
                      email VARCHAR(100) NOT NULL UNIQUE,
                      password VARCHAR(255) NOT NULL,
                      role VARCHAR(20) NOT NULL
);

-- 2. Tabela User Profiles
CREATE TABLE user_profile (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              user_id BIGINT NOT NULL,
                              first_name VARCHAR(50),
                              last_name VARCHAR(50),
                              phone_number VARCHAR(20),
                              FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- 3. Tabela Categories
CREATE TABLE category (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(50) NOT NULL UNIQUE,
                          description TEXT
);

-- 4. Tabela Courses
CREATE TABLE course (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        category_id BIGINT NOT NULL,
                        title VARCHAR(100) NOT NULL,
                        description TEXT,
                        FOREIGN KEY (category_id) REFERENCES category(id)
);

-- 5. Tabela Lessons
CREATE TABLE lesson (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        course_id BIGINT NOT NULL,
                        title VARCHAR(100) NOT NULL,
                        content_url VARCHAR(255) NOT NULL,
                        duration_minutes INT,
                        FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
);

-- 6. Tabela Reviews
CREATE TABLE review (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        course_id BIGINT NOT NULL,
                        rating INT NOT NULL,
                        comment TEXT,
                        FOREIGN KEY (user_id) REFERENCES user(id),
                        FOREIGN KEY (course_id) REFERENCES course(id)
);

-- 7. Tabela de legătură Many-to-Many (user_courses)
CREATE TABLE user_course (
                             user_id BIGINT NOT NULL,
                             course_id BIGINT NOT NULL,
                             PRIMARY KEY (user_id, course_id),
                             FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
                             FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
);