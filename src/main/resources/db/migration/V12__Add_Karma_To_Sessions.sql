ALTER TABLE class_sessions 
ADD COLUMN karma_reward INT NOT NULL DEFAULT 0;

-- Defensive Constraint: A class cannot offer negative points
ALTER TABLE class_sessions
ADD CONSTRAINT chk_karma_reward_positive CHECK (karma_reward >= 0);
