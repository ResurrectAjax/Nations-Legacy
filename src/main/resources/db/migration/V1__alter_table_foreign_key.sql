ALTER TABLE Players DROP CONSTRAINT fk_Players_Ranks

PRAGMA foreign_keys = OFF;

BEGIN TRANSACTION;

CREATE TABLE Players_new( 
    `UUID` varchar(36) PRIMARY KEY, 
    `Killpoints` int NOT NULL, 
    `NationID` int, 
    `Rank` varchar(32) not null, 
    foreign key(NationID) references Nations(NationID) on delete set null
);

INSERT INTO Players_new SELECT * FROM Players;

DROP TABLE Players;

ALTER TABLE Players_new RENAME TO Players;

COMMIT;

PRAGMA foreign_keys = ON;