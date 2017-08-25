#!/bin/bash

if [[ -L $0 ]]; then
    BASE_DIR="$(dirname $(readlink $0))"
else
    BASE_DIR="$( cd "$( dirname "$0" )" && pwd )"
fi

cd $BASE_DIR

$BASE_DIR/node_modules/.bin/lumo $BASE_DIR/lumo_blog/server.cljs $@
