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

execute_maven_command() {
  local service_dir=$1
  local command=$2
  echo "Executing '$command' for $service_dir..."
  gnome-terminal --tab --title="$service_dir" -- zsh -c "mvn -pl $service_dir $command; exec zsh"
  sleep 3
}

if [ "$#" -lt 1 ]; then
  echo "Usage: $0 <command>"
  echo "Commands: run, test, compile, run-without-test, install"
  exit 1
fi

command=$1

case $command in
  run)
    for service in "${services[@]}"; do
      execute_maven_command "$service" "spring-boot:run"
    done
    ;;
  test)
    for service in "${services[@]}"; do
      execute_maven_command "$service" "test"
    done
    ;;
  compile)
    for service in "${services[@]}"; do
      execute_maven_command "$service" "compile"
    done
    ;;
  run-without-test)
    for service in "${services[@]}"; do
      execute_maven_command "$service" "spring-boot:run -DskipTests"
    done
    ;;
  install)
    for service in "${services[@]}"; do
      execute_maven_command "$service" "install"
    done
    ;;
  *)
    echo "Unknown command: $command"
    echo "Commands: run, test, compile, run-without-test, install"
    exit 1
    ;;
esac
