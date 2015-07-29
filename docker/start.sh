#!/usr/bin/env bash
docker build --rm=true -t therest:dev .
docker run --rm -p 8080:8080 --name=therest-dev therest:dev
