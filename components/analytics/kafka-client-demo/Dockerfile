FROM python:3.6-alpine as base
FROM base as builder
RUN mkdir /install
WORKDIR /install
RUN apk update
RUN apk add bash ca-certificates wget openssl iputils && update-ca-certificates
RUN rm -rf /var/cache/apk/*

FROM base

# RUN pip install --install-option="--prefix=/install" -r /requirements.txt

COPY --from=builder /install /usr
COPY src /app
COPY requirements.txt /app/requirements.txt
RUN pip install -r /app/requirements.txt

WORKDIR /app
# ENV KAFKA_CLIENT_BOOTSTRAPPER bootstrap.kafka.svc.cluster.local:9092
# ENV KAFKA_CLIENT_VERBOSE 1
# ENV KAFKA_CLIENT_ITER 1
# ENV KAFKA_CLIENT_NUM_MESSAGES 1000

CMD ["sh", "-c", "tail -f /dev/null"]