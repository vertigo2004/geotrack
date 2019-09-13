#!/bin/sh
sudo systemctl daemon-reload
sudo systemctl enable geotrack.service
sudo systemctl start geotrack
sudo systemctl status geotrack
