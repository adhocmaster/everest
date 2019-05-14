#
# Dockerfile to build/Initialize collector UI container
#
# Initialization:
#

FROM python:3.7-alpine AS base
FROM base as builder
RUN mkdir /install
WORKDIR /install
RUN apk update
RUN apk add bash ca-certificates wget openssl iputils && update-ca-certificates
RUN rm -rf /var/cache/apk/*

FROM base

COPY --from=builder /install /usr
COPY src /app
WORKDIR /app

RUN apk add build-base

RUN pip install -r /app/requirements.txt
# RUN apk del build-base

EXPOSE 9999

#start app
CMD ["python", "route_guide_server.py"]



