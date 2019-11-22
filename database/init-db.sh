#!/usr/bin/env sh

set -e

if [ -z "$POSTGRES_PASSWORD" ]; then
  echo "ERROR: Missing environment variables. Set value for '$POSTGRES_PASSWORD'."
  exit 1
fi

echo "Creating dbjuddata database . . ."

psql -v ON_ERROR_STOP=1 --username postgres --dbname postgres <<-EOSQL
  CREATE ROLE dbjuddata WITH PASSWORD 'dbjuddata';
  CREATE DATABASE dbjuddata ENCODING = 'UTF-8' CONNECTION LIMIT = -1;
  GRANT ALL PRIVILEGES ON DATABASE dbjuddata TO dbjuddata;
  ALTER ROLE dbjuddata WITH LOGIN;
EOSQL

echo "Done creating database dbjuddata."
