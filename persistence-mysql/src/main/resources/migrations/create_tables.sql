--liquibase formatted sql

--changeset flux:1

CREATE TABLE IF NOT EXISTS `StateMachines` (
  `id` VARCHAR(100) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `version` SMALLINT UNSIGNED NOT NULL,
  `description` VARCHAR(300) DEFAULT NULL,
  `createdAt` TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt` TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`)
)
ENGINE=InnoDB
ROW_FORMAT=DEFAULT
DEFAULT CHARSET=utf8
AUTO_INCREMENT=1;

--rollback drop table StateMachines;

--changeset flux:2

CREATE TABLE IF NOT EXISTS `States` (
  `id` VARCHAR(100) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `version` SMALLINT UNSIGNED NOT NULL,
  `description` VARCHAR(300) DEFAULT NULL,
  `stateMachineId` VARCHAR(100) DEFAULT NULL,
  `dependencies` VARCHAR(1000) DEFAULT NULL,
  `onEntryHook` varchar(500) DEFAULT NULL,
  `task` VARCHAR(500) DEFAULT NULL,
  `onExitHook` varchar(500) DEFAULT NULL,
  `retryCount` TINYINT UNSIGNED DEFAULT NULL,
  `timeout` SMALLINT UNSIGNED DEFAULT NULL,
  `createdAt` TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt` TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_sm_states` FOREIGN KEY (`stateMachineId`) REFERENCES `StateMachines` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)
ENGINE=InnoDB
ROW_FORMAT=DEFAULT
DEFAULT CHARSET=utf8
AUTO_INCREMENT=1;

--rollback drop table States;

--changeset flux:3

CREATE TABLE IF NOT EXISTS `AuditRecords` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `stateMachineInstanceId` VARCHAR(100) NOT NULL,
  `stateId` VARCHAR(100) NOT NULL,
  `retryAttempt` TINYINT UNSIGNED DEFAULT NULL,
  `stateStatus` VARCHAR(100) DEFAULT NULL,
  `stateRollbackStatus` VARCHAR(100) DEFAULT NULL,
  `errors` VARCHAR(1000) DEFAULT NULL,
  `createdAt` TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  KEY `index_audit_on_SM_instance_id` (`stateMachineInstanceId`),
  CONSTRAINT `FK_sm_audit` FOREIGN KEY (`stateMachineInstanceId`) REFERENCES `StateMachines` (`id`),
  CONSTRAINT `FK_state_audit` FOREIGN KEY (`stateId`) REFERENCES `States` (`id`)
)
ENGINE=InnoDB
ROW_FORMAT=DEFAULT
DEFAULT CHARSET=utf8
AUTO_INCREMENT=1;

--rollback drop table AuditRecords;

--changeset flux:4

CREATE TABLE IF NOT EXISTS `Events` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `type` VARCHAR(100) NOT NULL,
  `status` VARCHAR(100) DEFAULT NULL,
  `stateMachineInstanceId` VARCHAR(255) NOT NULL,
  `eventData` BLOB,
  `eventSource` VARCHAR(100) DEFAULT NULL,
  `createdAt` TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt` TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_sm_events` FOREIGN KEY (`stateMachineInstanceId`) REFERENCES `StateMachines` (`id`)
)
ENGINE=InnoDB
ROW_FORMAT=DEFAULT
DEFAULT CHARSET=utf8
AUTO_INCREMENT=1;

--rollback drop table Events;

commit;
