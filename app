#!/bin/bash

function run_tests {
    suite=$1
    if [[ -z $suite ]]; then
        suite=core
    fi

    $BASE_DIR/node_modules/.bin/lumo -c src -m "${main}.test.${suite}" $@
    exit $?
}

if [[ -L $0 ]]; then
    BASE_DIR="$(dirname $(readlink $0))"
else
    BASE_DIR="$( cd "$( dirname "$0" )" && pwd )"
fi

cd $BASE_DIR

main=lumo-blog

command=$1
shift

case $command in
    start|serve|run) ns=core;;
    test|tst) run_tests $@;;
    migrate) ns=db.migration;;
    seed) ns=db.seeder;;
    *) echo 'start|serve|run - test - migrate - seed'; exit 1;;
esac

$BASE_DIR/node_modules/.bin/lumo -c src -m "${main}.${ns}" $@
