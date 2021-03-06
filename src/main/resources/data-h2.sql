-- create charta
CREATE TABLE IF NOT EXISTS charta (
    id bigint generated by default as identity,
    file_path varchar(255),
    fileuuid varchar(255),
    height integer not null,
    width integer not null,
    primary key (id)
);

-- create charta_lock
CREATE TABLE IF NOT EXISTS charta_lock(
    id bigint generated by default as identity,
    fileuuid varchar(255),
    lock boolean default false,
    primary key (id)
);