INSERT INTO task
(`id`, `description`, `is_reminder_set`, `is_task_open`, `created_on`, `started_on`, `finished_on`, `time_interval`,
 `time_taken`)
VALUES (111, 'test todo', false, false, CURRENT_TIME(), CURRENT_TIME(), CURRENT_TIME(), '0d', 0);

INSERT INTO task
(`id`, `description`, `is_reminder_set`, `is_task_open`, `created_on`, `started_on`, `finished_on`, `time_interval`,
 `time_taken`)
VALUES (222, 'second todo', false, false, CURRENT_TIME(), CURRENT_TIME(), CURRENT_TIME(), '2d', 2);

INSERT INTO task
(`id`, `description`, `is_reminder_set`, `is_task_open`, `created_on`, `started_on`, `finished_on`, `time_interval`,
 `time_taken`)
VALUES (333, 'third todo', false, true, CURRENT_TIME(), CURRENT_TIME(), CURRENT_TIME(), '2d', 2);