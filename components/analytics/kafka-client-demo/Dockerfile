FROM python:3.7-alpine AS base
#FROM python:3.6-alpine as base
FROM base as builder
RUN mkdir /install
WORKDIR /install
RUN apk update
RUN apk add bash ca-certificates wget openssl iputils curl && update-ca-certificates
RUN rm -rf /var/cache/apk/*

FROM base

# RUN pip install --install-option="--prefix=/install" -r /requirements.txt

COPY --from=builder /install /usr
COPY src /app
COPY requirements.txt /app/requirements.txt
RUN pip install -r /app/requirements.txt

ADD https://storage.googleapis.com/kubernetes-release/release/v1.6.4/bin/linux/amd64/kubectl /usr/local/bin/kubectl
RUN chmod +x /usr/local/bin/kubectl 
# Basic check it works.
RUN  kubectl version --client

WORKDIR /app
# ENV KAFKA_CLIENT_BOOTSTRAPPER bootstrap.kafka.svc.cluster.local:9092
# ENV KAFKA_CLIENT_VERBOSE 1
# ENV KAFKA_CLIENT_ITER 1
# ENV KAFKA_CLIENT_NUM_MESSAGES 1000

CMD ["sh", "-c", "tail -f /dev/null"]