create table address
(
    id     bigserial    not null primary key,
    street varchar(100)
);

alter table client
    add column address_id bigint references address (id);

create table phone
(
    id        bigserial   not null primary key,
    number    varchar(50),
    client_id bigint      not null references client (id)
);
