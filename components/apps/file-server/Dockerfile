#
# Dockerfile to build/Initialize collector UI container
#
# Initialization:
#

FROM mhart/alpine-node AS build
WORKDIR /app
RUN mkdir -p ./src
COPY src ./src
WORKDIR /app/src
RUN npm install

EXPOSE 9000 9001

#start app
CMD ["node", "server.js"]



