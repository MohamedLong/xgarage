#!/bin/bash

# List all Docker images (including untagged ones) with their IDs
docker images -a -q > image_ids.txt

# Read image IDs from the file line by line
while read -r image_id; do
  # Delete the image using the ID, forcing deletion even if in use
  docker rmi -f "$image_id"
  echo "Deleted image: $image_id"
done < image_ids.txt

# Remove the temporary file containing image IDs
rm image_ids.txt

echo "All Docker images deleted (if any)."
