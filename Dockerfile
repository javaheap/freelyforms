FROM openjdk:21-jdk-slim

# Install necessary tools
RUN apt-get update && apt-get -y upgrade
RUN apt-get install -y inotify-tools dos2unix

# Set working directory
ENV HOME=/app
WORKDIR $HOME

# Copy the project files into the Docker image
COPY . $HOME

# Convert line endings of scripts to Unix format
RUN dos2unix $HOME/run.sh $HOME/mvnw

# Ensure scripts have execute permissions
RUN chmod +x $HOME/run.sh $HOME/mvnw

# Expose the necessary ports
EXPOSE 8080 5005

# Set the entrypoint
ENTRYPOINT ["./run.sh"]
