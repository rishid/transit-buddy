DROP SCHEMA IF EXISTS TransitBuddy;
CREATE SCHEMA TransitBuddy;
USE TransitBuddy;

CREATE TABLE TransitBuddy.routes
(
  route_id VARCHAR(200) PRIMARY KEY,
  agency_id VARCHAR(200) REFERENCES agency(agency_id),
  route_short_name VARCHAR(200),
  route_long_name  VARCHAR(200),
  route_desc VARCHAR(200),
  route_type INT,
  route_url VARCHAR(200),
  route_color VARCHAR(10),
  route_text_color VARCHAR(10)
);

CREATE INDEX index_routes_on_route_type ON routes(route_type);
CREATE INDEX index_routes_on_route_short_name ON routes(route_short_name);

CREATE TABLE TransitBuddy.calendar_dates
(
  service_id VARCHAR(200) REFERENCES calendar(service_id),
  date DATE,
  exception_type INT
);

CREATE INDEX index_calendar_dates_on_date_and_exception_type ON calendar_dates(date, exception_type);
CREATE INDEX index_calendar_dates_on_service_id_and_exception_type ON calendar_dates(service_id, exception_type);

CREATE TABLE TransitBuddy.calendar
(
  service_id VARCHAR(200) PRIMARY KEY,
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
  trip_id VARCHAR(200) REFERENCES trips(trip_id),
  arrival_time TIME,
  departure_time TIME,
  stop_id  VARCHAR(200) REFERENCES stops(stop_id),
  stop_sequence INT,
  stop_headsign VARCHAR(200),
  pickup_type INT,
  drop_off_type INT
);

CREATE INDEX index_stop_times_on_arrival_time ON stop_times(arrival_time);
CREATE INDEX index_stop_times_on_departure_time ON stop_times(departure_time);
CREATE INDEX index_stop_times_on_stop_id ON stop_times(stop_id);
CREATE INDEX index_stop_times_on_trip_id ON stop_times(trip_id);

CREATE TABLE TransitBuddy.stops
(
  stop_id VARCHAR(200) PRIMARY KEY,
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

CREATE INDEX index_stops_on_stop_id ON stops(stop_id);

CREATE TABLE TransitBuddy.trips
(
  route_id VARCHAR(200) REFERENCES routes(route_id),
  service_id VARCHAR(200),
  trip_id VARCHAR(200) PRIMARY KEY,
  trip_headsign VARCHAR(200),
  direction_id INT,
  block_id VARCHAR(200),
  shape_id VARCHAR(200)
);

CREATE INDEX index_trips_on_trip_id ON trips(trip_id);
CREATE INDEX index_trips_on_route_id ON trips(route_id);
CREATE INDEX index_trips_on_service_id ON trips(service_id);

CREATE TABLE TransitBuddy.agency
(
  agency_id VARCHAR(200) PRIMARY KEY,
  agency_name VARCHAR(200),
  agency_url VARCHAR(200),
  agency_timezone VARCHAR(200),
  agency_lang VARCHAR(200),
  agency_phone VARCHAR(200)
);

CREATE TABLE TransitBuddy.cities
(
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
  route_tag VARCHAR(200),
  headsign VARCHAR(200),
  direction_name VARCHAR(200),
  stop_tag VARCHAR(200),
  stop_title VARCHAR(200),
  last_updated TIMESTAMP,  
  arrival_time1 BIGINT,
  vehicle1 VARCHAR(20),
  arrival_time2 BIGINT,
  vehicle2 VARCHAR(20),
  arrival_time3 BIGINT,
  vehicle3 VARCHAR(20),
  arrival_time4 BIGINT,
  vehicle4 VARCHAR(20),
  arrival_time5 BIGINT,
  vehicle5 VARCHAR(20),
  PRIMARY KEY (agency_id, route_tag, headsign, stop_tag)
)