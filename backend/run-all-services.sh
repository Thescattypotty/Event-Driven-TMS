#!/bin/bash

# Array of service directories
services=(
    "config-server"
    "registry-service"
    "gateway-service"
    "user-service"
    "auth-service"
    "project-service"
    "task-service"
    "file-service"
)


# Function to run a service in a new terminal tab
run_service() {
  local service_dir=$1
  echo "Starting $service_dir..."
  gnome-terminal --tab --title="$service_dir" -- zsh -c "mvn -pl $service_dir spring-boot:run; exec zsh"
}

# Iterate over each service and run it
for service in "${services[@]}"; do
  run_service "$service"
  sleep 5
done