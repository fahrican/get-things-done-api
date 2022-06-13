INSERT INTO task
(`id`, `description`, `is_reminder_set`, `is_task_open`, `created_on`, `started_on`, `finished_on`, `time_interval`,
 `time_taken`)
VALUES (111, 'test todo', false, false, CURRENT_TIME(), CURRENT_TIME(), CURRENT_TIME(), '0d', 0);