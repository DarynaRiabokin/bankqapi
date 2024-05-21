FROM ubuntu:latest
LABEL authors="vn"

ENTRYPOINT ["top", "-b"]