INSERT INTO _user (id, first_name, last_name, email, _username, _password, role)
VALUES (35, 'John', 'Doe', 'john.doe@email.com', 'johndoe', '$2a$10$yQM3Q3jIy6PcB6AXf2cYeu2dV3sCYJXCg4U/8rFpJ4OR/6dxgEm9m', 'USER');

INSERT INTO task
(`id`, `description`, `is_reminder_set`, `is_task_open`, `created_on`, `started_on`, `finished_on`, `time_interval`,
 `time_taken`, `user_id`)
VALUES (222, 'second todo', false, false, CURRENT_TIME(), CURRENT_TIME(), CURRENT_TIME(), '2d', 2, 35);

INSERT INTO task
(`id`, `description`, `is_reminder_set`, `is_task_open`, `created_on`, `started_on`, `finished_on`, `time_interval`,
 `time_taken`, `user_id`)
VALUES (111, 'test todo', false, false, CURRENT_TIME(), CURRENT_TIME(), CURRENT_TIME(), '0d', 0, 35);

INSERT INTO task
(`id`, `description`, `is_reminder_set`, `is_task_open`, `created_on`, `started_on`, `finished_on`, `time_interval`,
 `time_taken`, `user_id`)
VALUES (333, 'third todo', false, true, CURRENT_TIME(), CURRENT_TIME(), CURRENT_TIME(), '2d', 2, 35);