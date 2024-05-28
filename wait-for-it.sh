# wait-for-it.sh
#!/bin/bash

host="$1"
shift
port="$1"
shift
timeout="${WAIT_FOR_IT_TIMEOUT:-30}"

cmd="$@"

# Check if host:port is up
for i in $(seq $timeout); do
  nc -z "$host" "$port" && break
  echo "Waiting for $host:$port... $i"
  sleep 1
done

# Execute command if host:port is up
if [ $i -eq $timeout ]; then
  echo "Timeout waiting for $host:$port"
  exit 1
else
  echo "$host:$port is up"
  exec $cmd
fi

