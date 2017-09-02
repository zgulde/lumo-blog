# Lumo blog

Basic blog application built with clojurescript running on lumo.

Using express for the web server, node-mysql to talk to the database, and
requests to test the api.

`app` is a small shell script wrapper for the application's functionality

## Setup

1. Clone this + `npm install`

1. Create a database for the application

1. Copy the sample `env` file and fill in your values

    ```
    cp lumo_blog/env.example.cljs lumo_blog/env.cljs
    ```

1. Run the tests

    ```
    ./app test
    ```

1. Start the webserver (will run on `localhost:1312`)

    ```
    ./app start
    ```

## TODO

- validate post updates
- let logged in users change their passwords
- use something different for sessions, the current cookie package can be
  tampered with client-side, so we shouldn't use this for auth
