/*
 *
 * Copyright 2015 gRPC authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

var PROTO_PATH = __dirname + '/protos/route_guide.proto';

var async = require('async');
var fs = require('fs');
var parseArgs = require('minimist');
var path = require('path');
var _ = require('lodash');
var grpc = require('grpc');
var protoLoader = require('@grpc/proto-loader');
var packageDefinition = protoLoader.loadSync(
    PROTO_PATH,
    {keepCase: true,
     longs: String,
     enums: String,
     defaults: true,
     oneofs: true
    });
var routeguide = grpc.loadPackageDefinition(packageDefinition).routeguide;
// var client = new routeguide.RouteGuide('localhost:50051',
var argv = parseArgs(process.argv, {
  string: 'db_path'
});
const DEFAULT_VERBOSE = true
const DEFAULT_HOST = 'localhost'
const DEFAULT_PORT = 9999
var VERBOSE = process.env.EVEREST_GUIDE_CLIENT_VERBOSE || DEFAULT_VERBOSE
var GUIDE_HOST = process.env.EVEREST_GUIDE_CLIENT_HOST || DEFAULT_HOST
var GUIDE_PORT = process.env.EVEREST_GUIDE_CLIENT_PORT || DEFAULT_PORT

var client = new routeguide.RouteGuide(`${GUIDE_HOST}:${GUIDE_PORT}`,
                                       grpc.credentials.createInsecure());

if(VERBOSE) {
  console.log(`Guide Grpc Client connecting to ${GUIDE_HOST}:${GUIDE_PORT} ...`)
}

var COORD_FACTOR = 1e7;

/**
 * Run the getFeature demo. Calls getFeature with a point known to have a
 * feature and a point known not to have a feature.
 * @param {function} callback Called when this demo is complete
 */
function runGetFeature(callback) {
  var next = _.after(2, callback);
  function featureCallback(error, feature) {
    if (error) {
      callback(error);
      return;
    }
    if (feature.name === '') {
      console.log('Found no feature at ' +
          feature.location.latitude/COORD_FACTOR + ', ' +
          feature.location.longitude/COORD_FACTOR);
    } else {
      console.log('Found feature called "' + feature.name + '" at ' +
          feature.location.latitude/COORD_FACTOR + ', ' +
          feature.location.longitude/COORD_FACTOR);
    }
    next();
  }
  var point1 = {
    latitude: 409146138,
    longitude: -746188906
  };
  var point2 = {
    latitude: 0,
    longitude: 0
  };
  client.getFeature(point1, featureCallback);
  client.getFeature(point2, featureCallback);
}

/**
 * Run the getFeature demo. Calls getFeature with a point known to have a
 * feature and a point known not to have a feature.
 * @param {function} callback Called when this demo is complete
 */
function runGetFeatureHeavy(callback) {
  var next = _.after(4, callback);
  function featureCallbackHeavy(error, feature) {
    if (error) {
      callback(error);
      return;
    }
    if (feature.name === '') {
      console.log('Found no feature at ' +
          feature.location.latitude/COORD_FACTOR + ', ' +
          feature.location.longitude/COORD_FACTOR);
    } else {
      console.log('Found feature called "' + feature.name + '" at ' +
          feature.location.latitude/COORD_FACTOR + ', ' +
          feature.location.longitude/COORD_FACTOR);
    }
    next();
  }
  var point1 = {
    // latitude: 409146138,
    latitude: 100,
    longitude: -746188906
  };
  var point2 = {
    latitude: 0,
    longitude: 0
  };
  var point3 = {
    latitude: -1,
    longitude: -10
  };
  var point4 = {
    latitude: 419611318,
    longitude: -746524769
  };
  client.getFeatureHeavy(point1, featureCallbackHeavy);
  client.getFeatureHeavy(point2, featureCallbackHeavy);
  client.getFeatureHeavy(point3, featureCallbackHeavy);
  client.getFeatureHeavy(point4, featureCallbackHeavy);
}

/**
 * Run the listFeatures demo. Calls listFeatures with a rectangle containing all
 * of the features in the pre-generated database. Prints each response as it
 * comes in.
 * @param {function} callback Called when this demo is complete
 */
function runListFeatures(callback) {
  var rectangle = {
    lo: {
      latitude: 400000000,
      longitude: -750000000
    },
    hi: {
      latitude: 420000000,
      longitude: -730000000
    }
  };
  console.log('Looking for features between 40, -75 and 42, -73');
  var call = client.listFeatures(rectangle);
  call.on('data', function(feature) {
      console.log('Found feature called "' + feature.name + '" at ' +
          feature.location.latitude/COORD_FACTOR + ', ' +
          feature.location.longitude/COORD_FACTOR);
  });
  call.on('end', callback);
}

/**
 * Run the recordRoute demo. Sends several randomly chosen points from the
 * pre-generated feature database with a variable delay in between. Prints the
 * statistics when they are sent from the server.
 * @param {function} callback Called when this demo is complete
 */
function runRecordRoute(callback) {
  console.log(`db_path =${argv.db_path}=`)

  fs.readFile(path.resolve(argv.db_path), function(err, data) {
    if (err) {
      callback(err);
      return;
    }
    var feature_list = JSON.parse(data);

    var num_points = 10;
    var call = client.recordRoute(function(error, stats) {
      if (error) {
        callback(error);
        return;
      }
      console.log('Finished trip with', stats.point_count, 'points');
      console.log('Passed', stats.feature_count, 'features');
      console.log('Travelled', stats.distance, 'meters');
      console.log('It took', stats.elapsed_time, 'seconds');
      callback();
    });
    /**
     * Constructs a function that asynchronously sends the given point and then
     * delays sending its callback
     * @param {number} lat The latitude to send
     * @param {number} lng The longitude to send
     * @return {function(function)} The function that sends the point
     */
    function pointSender(lat, lng) {
      /**
       * Sends the point, then calls the callback after a delay
       * @param {function} callback Called when complete
       */
      return function(callback) {
        console.log('Visiting point ' + lat/COORD_FACTOR + ', ' +
            lng/COORD_FACTOR);
        call.write({
          latitude: lat,
          longitude: lng
        });
        _.delay(callback, _.random(500, 1500));
      };
    }
    var point_senders = [];
    for (var i = 0; i < num_points; i++) {
      var rand_point = feature_list[_.random(0, feature_list.length - 1)];
      point_senders[i] = pointSender(rand_point.location.latitude,
                                     rand_point.location.longitude);
    }
    async.series(point_senders, function() {
      call.end();
    });
  });
}

const CHAT_REPEAT = 1
/**
 * Run the routeChat demo. Send some chat messages, and print any chat messages
 * that are sent from the server.
 * @param {function} callback Called when the demo is complete
 */
function runRouteChat(callback) {
  var call = client.routeChat();
  call.on('data', function(note) {
    console.log('Got message "' + note.message + '" at ' +
        note.location.latitude + ', ' + note.location.longitude);
  });

  call.on('end', callback);

  var notes = [{
    location: {
      latitude: 0,
      longitude: 0
    },
    message: 'First message'
  }, {
    location: {
      latitude: 0,
      longitude: 1
    },
    message: 'Second message'
  }, {
    location: {
      latitude: 1,
      longitude: 0
    },
    message: 'Third message'
  }, {
    location: {
      latitude: 0,
      longitude: 0
    },
    message: 'Fourth message'
  }];
  for (var i = 0; i < notes.length * CHAT_REPEAT; i++) {
    var note = notes[i % notes.length];
    console.log('Sending message "' + note.message + '" at ' +
        note.location.latitude + ', ' + note.location.longitude);
    call.write(note);
  }
  call.end();
}

/**
 * Run all of the demos in order
 */
function main() {
  async.series([
    runGetFeature,
    runGetFeatureHeavy,
    runListFeatures,
    runRecordRoute,
    runRouteChat
  ]);
}

if (require.main === module) {
  main();
}

exports.runGetFeature = runGetFeature;

exports.runGetFeatureHeavy = runGetFeatureHeavy;

exports.runListFeatures = runListFeatures;

exports.runRecordRoute = runRecordRoute;

exports.runRouteChat = runRouteChat;
