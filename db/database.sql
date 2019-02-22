-- ================
-- create database and user (as CREATEDB user)
create user conduit with encrypted password 'conduit';
create database conduit with owner conduit encoding 'UTF-8';
grant all privileges on database conduit to conduit;
create extension if not exists "uuid-ossp"; -- useless?

-- ================
-- create conduit db structure (as conduit)

create table if not exists conduit_user (
  id_user uuid not null unique primary key,
  email varchar(128) not null unique,
  username varchar(64) not null unique,
  passwd varchar(128) not null,
  bio text,
  img text
);
-- indexes?
