yellow-network-listener
=======================

## Установка

    $ git clone git@github.com:ovcharik/yellow-network-listener.git
    $ cd yellow-network-listener
    $ make

Также для работы необходимо поставить postrgesql и java (oracle 1.7)


## Конфигурация

Все настройки содержатся в `yellow.conf`

### Конфигурация базы данных

    $ sudo -u postgres psql
    > CREATE USER tender PASSWORD 'tender';
    > CREATE DATABASE tender_yellow;
    > GRANT ALL PRIVILEGES ON DATABSE tender_yellow TO tender;
    > \q


## Запуск

После установки и конфигурации нужно скомандовать

    $ ./YellowTender start

И браузере по адресу `localhost:1111` должна отобразиться страница входа. Стандартный логин пароль `root:root`

## Документы

Документы, которые использовались при разработке, можно найти в папке `documents`.
