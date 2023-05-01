create sequence address_SEQ start with 1 increment by 1;

create table address
(
    id bigint not null primary key,
    street varchar(255)
);

create sequence phone_SEQ start with 1 increment by 1;

create table phone
(
    id bigint not null primary key,
    number varchar(255)
);

alter table if exists address
    add column client_id bigint;

alter table if exists address
    add constraint fk_address_client
    foreign key (client_id) references client;

alter table if exists phone
    add column client_id bigint;

alter table if exists phone
    add constraint fk_phone_client
    foreign key (client_id) references client;
