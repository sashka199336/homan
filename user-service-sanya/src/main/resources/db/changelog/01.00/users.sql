CREATE TABLE IF NOT EXISTS users (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    position VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create update trigger function
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Attach trigger to table
CREATE TRIGGER update_timestamp
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_modified_column();

CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    action VARCHAR(50) DEFAULT 'REGISTER',
    affected_user_id UUID REFERENCES users(id),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE audit_logs ADD CONSTRAINT ch_log_action
    CHECK (action in ('CHANGE_STATUS', 'REGISTER'));

CREATE TABLE IF NOT EXISTS roles (
    role_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS privileges (
    privilege_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    privilege_name VARCHAR(100) NOT NULL,
    access_type VARCHAR(50),
    service VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (role_id) REFERENCES roles (role_id)
);

CREATE TABLE IF NOT EXISTS users_privileges (
    user_id UUID NOT NULL,
    privilege_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (privilege_id) REFERENCES privileges (privilege_id)
);

INSERT INTO roles (role_name, description) VALUES
('admin', 'полный доступ к управлению пользователями и аудиту'),
('user', 'работник, не имеющий доступа к работе с сотрудниками');

INSERT INTO privileges (privilege_name, access_type, service, description) VALUES
--('user:create', 'создание учетных записей', 'User Service', 'Доступ на создание пользователей.'),
--('user:edit', 'редактирование профилей', 'User Service', 'Доступ на редактирование профилей.'),
--('user:delete', 'деактивация пользователей', 'User Service', 'Доступ на деактивацию пользователей.'),
--('privilege:assign', 'назначение привилегий', 'User Service', 'Доступ на назначение привилегий.'),
--('audit:view', 'просмотр логов действий', 'User Service', 'Доступ к просмотру логов действий.'),
('claim:view', 'просмотр заявок', 'User Service', 'Доступ на просмотр заявок.'),
('claim:update', 'редактирование заявок', 'User Service', 'Доступ на редактирование заявок.'),
('claim:reject', 'отклонение заявок', 'User Service', 'Доступ на отклонение заявок.');
