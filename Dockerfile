# Use an official Node.js runtime as the base image
FROM node:18.19 AS build

# Set the working directory in the container
WORKDIR /app

# Copy package.json and package-lock.json to the container
COPY package*.json ./

# Install Angular CLI globally (if not already installed)
RUN npm install -g @angular/cli

# Install dependencies
RUN npm install --legacy-peer-deps

# Copy the rest of the application code to the container
COPY . .

# Build the Angular application for production
RUN ng build

# Stage 2: Use Nginx for serving the Angular application
FROM nginx:alpine

# Copy the built Angular application to the Nginx server directory
COPY --from=build /app/dist/ultima/ /usr/share/nginx/html

# Expose port 80 to the outside world
# EXPOSE 80

# Start Nginx server when the container starts
CMD ["nginx", "-g", "daemon off;"]