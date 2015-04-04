DROP SCHEMA IF EXISTS TransitBuddy;
CREATE SCHEMA TransitBuddy;
USE TransitBuddy

CREATE TABLE TransitBuddy.routes
(
  id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  route_id VARCHAR(200),
  agency_id VARCHAR(200) REFERENCES agency(agency_id),
  route_short_name VARCHAR(200),
  route_long_name  VARCHAR(200),
  route_desc VARCHAR(200),
  route_type INT,
  route_url VARCHAR(200),
  route_color VARCHAR(10),
  route_text_color VARCHAR(10)
);

CREATE TABLE TransitBuddy.calendar_dates
(
  id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  service_id  VARCHAR(200),
  date DATE,
  exception_type INT
);

CREATE TABLE TransitBuddy.calendar
(
  id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  service_id VARCHAR(200),
  monday BOOL,
  tuesday BOOL,
  wednesday BOOL,
  thursday BOOL,
  friday BOOL,
  saturday BOOL,
  sunday BOOL,
  start_date DATE,
  end_date DATE
);

CREATE TABLE TransitBuddy.stop_times
(
  id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  trip_id VARCHAR(200) REFERENCES trips(trip_id),
  arrival_time TIME,
  departure_time TIME,
  stop_id  VARCHAR(200) REFERENCES stops(stop_id),
  stop_sequence INT,
  stop_headsign VARCHAR(200),
  pickup_type INT,
  drop_off_type INT
);

CREATE TABLE TransitBuddy.stops
(
  id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  stop_id VARCHAR(200),
  stop_code VARCHAR(200),
  stop_name VARCHAR(200),
  stop_desc VARCHAR(200),
  stop_lat FLOAT,
  stop_lon FLOAT,
  zone_id VARCHAR(200),
  stop_url VARCHAR(200),
  location_type VARCHAR(200),
  parent_station VARCHAR(200)
);

CREATE TABLE TransitBuddy.trips
(
  id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  route_id VARCHAR(200) REFERENCES routes(route_id),
  service_id VARCHAR(200),
  trip_id VARCHAR(200),
  trip_headsign VARCHAR(200),
  direction_id INT,
  block_id VARCHAR(200),
  shape_id VARCHAR(200)
);

CREATE TABLE TransitBuddy.agency
(
  id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  agency_id VARCHAR(200),
  agency_name VARCHAR(200),
  agency_url VARCHAR(200),
  agency_timezone VARCHAR(200),
  agency_lang VARCHAR(200),
  agency_phone VARCHAR(200)
);

CREATE TABLE TransitBuddy.cities
(
  id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  agency_id VARCHAR(200) REFERENCES agency(agency_id),
  city_name VARCHAR(200)
);

INSERT INTO TransitBuddy.cities (city_name, agency_id) VALUES ('Boston', '1');

CREATE TABLE TransitBuddy.transit_parsers
(
  agency_id VARCHAR(200) REFERENCES agency(agency_id),
  parser VARCHAR(200)
);

INSERT INTO TransitBuddy.transit_parsers (agency_id, parser) VALUES ('1', 'MBTA');

CREATE TABLE TransitBuddy.real_time
(
  agency_id VARCHAR(200) REFERENCES agency(agency_id),
  route_id VARCHAR(200) REFERENCES routes(route_id),
  stop_id  VARCHAR(200) REFERENCES stops(stop_id),
  last_updated TIMESTAMP,
  trip_id1 INT,
  epoch_time1 BIGINT,  
  trip_id2 INT,
  epoch_time2 BIGINT,  
  trip_id3 INT,
  epoch_time3 BIGINT,  
  trip_id4 INT,
  epoch_time4 BIGINT,
  trip_id5 INT,
  epoch_time5 BIGINT,
  PRIMARY KEY (agency_id, route_id, stop_id)
)