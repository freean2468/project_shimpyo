-- create schema shimpyo;
use shimpyo;

drop table if exists `account_table`;
create table if not exists `account_table`(
	`no` varchar(12) not null unique,
    constraint `pk_no` primary key(`no`)
) engine=innodb default character set = utf8;

drop table if exists `question_table`;
create table if not exists `question_table`(
	`dayOfYear` int not null unique,
    `question` varchar(50) not null,
    constraint `pk_dayOfYear` primary key(`dayOfYear`)
) engine=innodb default character set = utf8;

-- 그 날을 대표하는 사진 한 장만 저장할 수 있다.
drop table if exists `diary_table`;
create table if not exists `diary_table`(
	`no` varchar(12) not null unique,
    `dayOfYear` int not null unique,
    `answer` varchar(2048) null,
    `photo` mediumblob null,
    constraint `fk_no` foreign key(`no`) references `account_table`(`no`) on delete cascade on update cascade,
    constraint `fk_dayOfYear` foreign key(`dayOfYear`) references `question_table`(`dayOfYear`) on delete restrict on update cascade
) engine=innodb default character set = utf8;

-- 삭제될 때 트리거를 이용해 삭제된 데이터를 저장해두자...? 굳이?