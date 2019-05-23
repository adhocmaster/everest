#!/bin/sh

echo "Start Eliot Running Simulator"

# run 100 sensors with STx:SIDy
(cd /app/sensors/sim/src; python sim.py)
## run 100 sensors with Sx:IDy
#(cd /app/sensors/sim/src; python registry_app.py -n -g 2 -s 50 -gp S -sp ID)
## run 100 sensors with GWx:IDy
#(cd /app/sensors/sim/src; python registry_app.py -n -g 2 -s 50 -gp GW -sp ID)
## run 100 sensors with Gx:Sy
#(cd /app/sensors/sim/src; python registry_app.py -n -g 2 -s 50 -gp G -sp S)
## run 100 sensors with BDx:Sy
#(cd /app/sensors/sim/src; python registry_app.py -n -g 2 -s 50 -gp BD -sp S)

echo "Eliot Simulator is DONE..."