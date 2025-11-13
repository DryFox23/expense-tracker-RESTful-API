show databases ;
create database expense_tracker_db;
use expense_tracker_db;

create table users(
    id varchar(100) not null,
    email varchar(100) not null,
    password varchar(100) not null,
    username varchar(250),
    token varchar(250) unique,
    created_at timestamp,
    updated_at timestamp default current_timestamp,
    primary key (id)
)engine = InnoDB;
desc users;

create table expenses(
    id varchar(100) not null,
    user_id varchar(100) not null,
    title varchar(100) not null,
    expense_type enum ('EXPENSE', 'INCOME') not null,
    category enum ('FOOD', 'SHOPING', 'TRANSPORT', 'ENTERTAIMENT', 'OTHER') default 'OTHER',
    amount decimal(15,2) not null,
    payment_method enum ('CASH', 'DEBIT', 'CREDIT', 'E-WALLET', 'TRANSFER') default 'CASH',
    description text,
    date datetime,
    created_at timestamp default current_timestamp,
    updated_at timestamp on update current_timestamp,
    primary key (id),
    foreign key fk_users_expenses (user_id) references users (id)
)engine = InnoDB;

desc expenses;

alter table users
add constraint UNIQUE (email);

ALTER TABLE expenses
    MODIFY expense_type ENUM('EXPENSE', 'INCOME') NOT NULL;