DROP DATABASE IF EXISTS rest_project;
CREATE DATABASE rest_project;

USE rest_project;

create table sushi_type (
                            type_id char(36) primary key,
                            type_name varchar(50) not null);

create table sushi (
                       sushi_id char(36) primary key,
                       sushi_name varchar(50) not null,
                       price decimal(3, 2) not null,
                       description varchar(1000) not null,
                       type_id char(36) not null,
                       foreign key (type_id) references sushi_type(type_id));

create table orders (
                        order_id char(36) primary key,
                        status enum('in_process', 'need_confirmation', 'confirmed', 'cancelled') not null,
                        total_price decimal(4,2) not null);

create table order_component (
                                 order_id char(36),
                                 sushi_id char(36),
                                 amount int,
                                 foreign key (order_id) references orders(order_id),
                                 foreign key (sushi_id) references sushi(sushi_id),
                                 PRIMARY KEY (order_id, sushi_id));

INSERT INTO sushi_type VALUES
                           ("4b74fe5d-7491-47c8-9dd0-5035d30ae020", "Маки"),
                           ("75e04e8e-7ca3-4402-837c-e97217d70f9d", "Запеченные роллы"),
                           ("6cda33dd-59fc-403f-8b8d-61f05de42f9e", "Темпура роллы"),
                           ("f07ab70b-6caa-45fe-b1a5-0a12a2969d66", "Урамаки"),
                           ("1e29143a-f952-4b09-937c-057d5b3e7f66", "Хосомаки");

INSERT INTO sushi VALUES
                      ("e6f1aa53-9e59-4c33-a804-a7885bd40996", "Ролл с лососем и авокадо", 20.40, "Лосось свежий, сыр творожный, авокадо", "f07ab70b-6caa-45fe-b1a5-0a12a2969d66"),
                      ("209d1668-606a-4066-a43a-99260f878535", "Токио маки", 22.60, "Креветка тигровая, творожный сыр, помидор, авокадо, икра летучей рыбы красная", "f07ab70b-6caa-45fe-b1a5-0a12a2969d66"),
                      ("49dd0b88-641c-4fe0-bd21-57b1ac78a0bd", "Яки тай маки", 13.40, "Окунь жареный, творожный сыр, помидор, маринованный редис Такуан, сыр Джугас, майонез, соус Терияки, японский омлет", "75e04e8e-7ca3-4402-837c-e97217d70f9d"),
                      ("75e04e8e-7ca3-4402-837c-e97217d70f9d", "Унаги маки", 11.90, "Угорь копченый, огурец", "4b74fe5d-7491-47c8-9dd0-5035d30ae020");

INSERT INTO orders VALUES
                       ("843a81b1-b0fe-4ec4-8505-bcf264fefef1", 'in_process', 11.90),
                       ("183517ea-114d-43db-a782-ca22e7addc6f", 'in_process', 50.60),
                       ("a20315f4-2fe2-4dca-b69c-a8ad9e298b46", 'in_process', 36.70);

INSERT INTO order_component VALUES
                                ("843a81b1-b0fe-4ec4-8505-bcf264fefef1", "75e04e8e-7ca3-4402-837c-e97217d70f9d", 1),
                                ("183517ea-114d-43db-a782-ca22e7addc6f", "e6f1aa53-9e59-4c33-a804-a7885bd40996", 1),
                                ("183517ea-114d-43db-a782-ca22e7addc6f", "49dd0b88-641c-4fe0-bd21-57b1ac78a0bd", 2),
                                ("a20315f4-2fe2-4dca-b69c-a8ad9e298b46", "e6f1aa53-9e59-4c33-a804-a7885bd40996", 1),
                                ("75e04e8e-7ca3-4402-837c-e97217d70f9d", "209d1668-606a-4066-a43a-99260f878535", 1);