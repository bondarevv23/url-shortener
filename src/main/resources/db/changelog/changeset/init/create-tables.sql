create sequence if not exists reduction_seq increment 10 start 1;

create table if not exists reduction_tab (
    id bigint primary key,
    alias varchar(255) not null unique,
    url text not null
);
