
INSERT INTO app_user (username, email, password, role) VALUES
                                                       ('admin', 'admin@elearning.com', 'admin123', 'ADMIN'),
                                                       ('teona', 'teona@student.com', 'parola123', 'USER'),
                                                       ('andrei', 'andrei@student.com', 'student456', 'USER');


INSERT INTO user_profile (user_id, first_name, last_name, phone_number) VALUES
                                                                            (1, 'Sistem', 'Administrator', '0711111111'),
                                                                            (2, 'Teona', 'Christiana', '0722222222'),
                                                                            (3, 'Andrei', 'Popescu', '0733333333');

INSERT INTO category (name, description) VALUES
                                             ('Programare', 'Cursuri despre Java, Python, C# și dezvoltare software.'),
                                             ('Design', 'UI/UX, modelare 3D și editare foto/video.'),
                                             ('Business', 'Marketing digital, antreprenoriat și management.');

INSERT INTO course (category_id, title, description) VALUES
                                                         (1, 'Introducere in Spring Boot', 'Invață să construiești aplicații enterprise cu Java.'),
                                                         (1, 'React de la Zero la Expert', 'Dezvoltă interfețe web moderne și interactive.'),
                                                         (2, 'Fundamente UI/UX', 'Descoperă secretele unui design curat în Figma.');


INSERT INTO lesson (course_id, title, content_url, duration_minutes) VALUES
                                                                         (1, '1. Setup Proiect si Spring Initializr', 'http://video.elearning.com/spring/1', 15),
                                                                         (1, '2. Maparea Entitatilor cu JPA', 'http://video.elearning.com/spring/2', 30),
                                                                         (2, '1. Introducere in Componente', 'http://video.elearning.com/react/1', 20);


INSERT INTO user_course (user_id, course_id) VALUES
                                                 (2, 1),
                                                 (2, 2),
                                                 (3, 1);


INSERT INTO review (user_id, course_id, rating, comment) VALUES
                                                             (2, 1, 5, 'Un curs excelent pentru facultate! Foarte bine explicat.'),
                                                             (3, 1, 4, 'Bun cursul, dar as fi vrut mai multe exemple practice.');