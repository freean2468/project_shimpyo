-- create schema shimpyo;
use shimpyo;

drop table if exists `diary_table`;
drop table if exists `question_table`;
drop table if exists `account_table`;

create table if not exists `account_table`(
	`no` varchar(12) not null unique,
    `pw` varchar(20) null, 
    constraint `pk_no` primary key(`no`)
) engine=innodb default character set = utf8mb4;

create table if not exists `question_table`(
	`dayOfYear` int not null unique,
    `question` varchar(50) not null,
    constraint `pk_dayOfYear` primary key(`dayOfYear`)
) engine=innodb default character set = utf8mb4;

-- 그 날을 대표하는 사진 한 장만 저장할 수 있다.
create table if not exists `diary_table`(
	`no` varchar(12) not null,
    `dayOfYear` int not null,
    `answer` varchar(2048) null,
    `photo` mediumblob null,
    constraint `fk_no` foreign key(`no`) references `account_table`(`no`) on delete cascade on update cascade,
    constraint `fk_dayOfYear` foreign key(`dayOfYear`) references `question_table`(`dayOfYear`) on delete restrict on update cascade
) engine=innodb default character set = utf8mb4;

-- 삭제될 때 트리거를 이용해 삭제된 데이터를 저장해두자...?

drop procedure if exists `proc_init`;

delimiter //
create procedure `proc_init`()
begin
	declare day int default 1;
    
    while(day <= 365) do
		insert into `question_table` values(day, concat("question",convert(day, char(3))));
        set day = day + 1;
    end while;
end //
delimiter ;

call proc_init();

select * from `shimpyo`.`question_table`;
select * from `shimpyo`.`diary_table`;
select * from `shimpyo`.`account_table`;
select length(photo) from `shimpyo`.`diary_table`;

-- Slick에서 procedure 호출이 안 된다.
drop procedure if exists `proc_login`;
delimiter //
create procedure `proc_login`(in in_no varchar(12))
begin
	declare flag int default 0;
    
    select count(*) into flag from `account_table` where `no` = in_no;
    
    if (flag = 0) then
		insert into `account_table` values(in_no, null);
		insert into `diary_table` values(in_no, dayofyear(curdate()), "", null);
    end if;
    
    select * from `diary_table` where `no` = in_no;
end //
delimiter ;