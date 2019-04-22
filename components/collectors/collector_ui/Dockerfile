#
# Dockerfile to build/Initialize collector UI container
#
# Initialization:
#

FROM mhart/alpine-node AS build
WORKDIR /app
RUN mkdir -p ./backend
COPY backend/src ./backend
COPY backend/package.json ./backend/package.json
RUN mkdir -p ./ui/build
COPY ui/build ./ui/build
WORKDIR /app/backend
RUN npm install

EXPOSE 8888

#start app
CMD ["node", "server.js"]



