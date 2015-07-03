#!/usr/bin/env bash
docker build -t therest:dev .
docker run -p 8080:8080 --name=therest-dev therest:dev
