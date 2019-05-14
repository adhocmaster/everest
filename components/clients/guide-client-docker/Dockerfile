#
# Dockerfile to build/Initialize collector UI container
#
# Initialization:
#

FROM mhart/alpine-node AS build
WORKDIR /app
RUN mkdir -p ./guide-client
COPY guide-client ./guide-client
WORKDIR /app/guide-client
RUN npm install

#start app
CMD ["node", "route_guide_client.js", "--db_path ./route_guide_db.json"]



