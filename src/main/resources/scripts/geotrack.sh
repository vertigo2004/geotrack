#!/bin/sh
newIP=$(curl ifconfig.me)
sed -r -i "s/^mosquitto\.host=.*$/mosquitto\.host=$newIP/g" broker.properties
sudo java -jar geotrack-0.0.1-SNAPSHOT.jar server --spring.config.name=application,broker
