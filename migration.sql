drop table if exists posts;
drop table if exists users;

create table users(
    id int unsigned not null auto_increment,
    email text,
    password text,
    primary key (id)
);

create table posts (
    id int unsigned not null auto_increment,
    title text,
    body text,
    user_id int unsigned,
    primary key (id),
    foreign key (user_id) references users(id) on delete set null
);

