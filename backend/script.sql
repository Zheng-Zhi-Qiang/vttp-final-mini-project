drop database if exists buddyfinder;

create database buddyfinder;

use buddyfinder;

create table user (
    user_id varchar(256) not null,
    email varchar(128) not null,
    first_name varchar(128) not null,
    last_name varchar(128) not null,
    push_messaging_token varchar(512),
    verified boolean default false,

    primary key(user_id)
);

create table conversation (
    convo_id char(8) not null,
    user_id_1 varchar(256) not null,
    user_id_2 varchar(256) not null,
    listing_id varchar(256) not null,
    deleted boolean default false,

    primary key(convo_id),
    constraint fk_user_id_1 foreign key(user_id_1) references user(user_id),
    constraint fk_user_id_2 foreign key(user_id_2) references user(user_id)
);

create table message (
    convo_id char(8) not null,
    sender varchar(256) not null,
    receiver varchar(256) not null,
    message text not null,
    date timestamp default current_timestamp,

    constraint fk_convo_id foreign key(convo_id) references conversation(convo_id),
    constraint fk_sender foreign key(sender) references user(user_id),
    constraint fk_receiver foreign key(receiver) references user(user_id)
);

create table transaction (
    transaction_id mediumint not null auto_increment,
    listing_id varchar(256) not null,
    payer varchar(256) not null,
    payee varchar(256) not null,
    amount float not null,
    date timestamp default current_timestamp,

    primary key(transaction_id),
    constraint fk_payer foreign key(payer) references user(user_id),
    constraint fk_payee foreign key(payer) references user(user_id)
);

grant all privileges on buddyfinder.* to fred@'%';

flush privileges;