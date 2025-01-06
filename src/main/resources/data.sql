-- Insert test users
INSERT INTO users (email, name)
VALUES 
    ('jasmina@ibu.ba', 'Jasmina'),
    ('profesor@ibu.ba', 'Professor'),
    ('test@example.com', 'Test User'),
    ('admin@example.com', 'Admin User')
ON DUPLICATE KEY UPDATE 
    name = VALUES(name);

-- Add test tasks
INSERT INTO tasks (user_email, subject, grade, lesson_unit, material_type, content, language)
VALUES 
    ('jasmina@ibu.ba', 'Math', '5', 'Algebra', 'Exercise', 'Sample math content', 'en'),
    ('profesor@ibu.ba', 'Physics', '8', 'Mechanics', 'Lecture', 'Sample physics content', 'en'),
    ('test@example.com', 'Science', '6', 'Biology', 'Notes', 'Sample science content', 'en'),
    ('admin@example.com', 'History', '7', 'World War II', 'Quiz', 'Sample history content', 'en');
