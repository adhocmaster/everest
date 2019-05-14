# Copyright 2015 gRPC authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""The Python implementation of the gRPC route guide server."""

import os

from concurrent import futures
import time
import math
import logging

import grpc

import route_guide_pb2
import route_guide_pb2_grpc
import route_guide_resources

import numpy

_ONE_DAY_IN_SECONDS = 60 * 60 * 24
DEFAULT_GUIDE_PORT = '9999'
GUIDE_PORT = DEFAULT_GUIDE_PORT
VERBOSE = False
MESSAGE_NUM = 0

def get_feature(feature_db, point):
    """Returns Feature at given location or None."""
    for feature in feature_db:
        if feature.location == point:
            return feature
    return None


def get_distance(start, end):
    """Distance between two points."""
    coord_factor = 10000000.0
    lat_1 = start.latitude / coord_factor
    lat_2 = end.latitude / coord_factor
    lon_1 = start.longitude / coord_factor
    lon_2 = end.longitude / coord_factor
    lat_rad_1 = math.radians(lat_1)
    lat_rad_2 = math.radians(lat_2)
    delta_lat_rad = math.radians(lat_2 - lat_1)
    delta_lon_rad = math.radians(lon_2 - lon_1)

    # Formula is based on http://mathforum.org/library/drmath/view/51879.html
    a = (pow(math.sin(delta_lat_rad / 2), 2) +
         (math.cos(lat_rad_1) * math.cos(lat_rad_2) * pow(
             math.sin(delta_lon_rad / 2), 2)))
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    R = 6371000
    # metres
    return R * c


class RouteGuideServicer(route_guide_pb2_grpc.RouteGuideServicer):
    """Provides methods that implement functionality of route guide server."""

    def __init__(self):
        self.db = route_guide_resources.read_route_guide_database()

    def GetFeature(self, request, context):
        if(VERBOSE is True):
            print("GetFeature")
        feature = get_feature(self.db, request)
        if feature is None:
            return route_guide_pb2.Feature(name="", location=request)
        else:
            return feature

    def GetFeatureHeavy(self, request, context):
        if(VERBOSE is True):
            print("GetFeatureHeavy: start ...")
        
        feature = get_feature(self.db, request)
        if feature is None:
            # row = 1024
            # col = 1024
            row = 256
            col = 256

            if(request.latitude > 0):
                row = (request.latitude % row)
            if(request.longitude > 0):
                col = (request.longitude % col)
            if(VERBOSE is True):
                print("GetFeatureHeavy: start allocating memory {} x {} ".format(row, col))
            result = [numpy.random.bytes(row*col) for x in range(row*col)] 
            if(VERBOSE is True):
                print("GetFeatureHeavy: DONE allocating memory {} x {} ".format(row, col))

            return route_guide_pb2.Feature(name="", location=request)
        else:
            return feature


    def ListFeatures(self, request, context):
        if(VERBOSE is True):
            print("ListFeatures")
        left = min(request.lo.longitude, request.hi.longitude)
        right = max(request.lo.longitude, request.hi.longitude)
        top = max(request.lo.latitude, request.hi.latitude)
        bottom = min(request.lo.latitude, request.hi.latitude)
        for feature in self.db:
            if (feature.location.longitude >= left and
                    feature.location.longitude <= right and
                    feature.location.latitude >= bottom and
                    feature.location.latitude <= top):
                yield feature

    def RecordRoute(self, request_iterator, context):
        if(VERBOSE is True):
            print("RecordRoute")
        point_count = 0
        feature_count = 0
        distance = 0.0
        prev_point = None

        start_time = time.time()
        for point in request_iterator:
            point_count += 1
            if get_feature(self.db, point):
                feature_count += 1
            if prev_point:
                distance += get_distance(prev_point, point)
            prev_point = point

        elapsed_time = time.time() - start_time
        return route_guide_pb2.RouteSummary(
            point_count=point_count,
            feature_count=feature_count,
            distance=int(distance),
            elapsed_time=int(elapsed_time))

    def RouteChat(self, request_iterator, context):
        global MESSAGE_NUM
        if(VERBOSE is True):
            print("Route Chat")
        prev_notes = []
        for new_note in request_iterator:
            for prev_note in prev_notes:
                if prev_note.location == new_note.location:
                    mnum = " Server Message Number {}".format(MESSAGE_NUM)
                    MESSAGE_NUM += 1
                    # if(VERBOSE is True):
                    #     print("Route Chat sending back ===> {}{} ".format(prev_note, mnum))
                    prev_note.message += mnum
                    yield prev_note
            prev_notes.append(new_note)


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    route_guide_pb2_grpc.add_RouteGuideServicer_to_server(
        RouteGuideServicer(), server)
    
    server.add_insecure_port('[::]:'+ GUIDE_PORT)
    server.start()
    print("Guide GRPC started on port " + GUIDE_PORT) 
    try:
        while True:
            time.sleep(_ONE_DAY_IN_SECONDS)
    except KeyboardInterrupt:
        server.stop(0)


if __name__ == '__main__':
    if "EVEREST_GUIDE_PORT" in os.environ:
        GUIDE_PORT = os.environ.get('EVEREST_GUIDE_PORT')
    if "EVEREST_GUIDE_VERBOSE" in os.environ:
        VERBOSE = os.environ.get('EVEREST_GUIDE_VERBOSE') == 'True' or os.environ.get('EVEREST_GUIDE_VERBOSE') == 'true' 
    logging.basicConfig()
    serve()
