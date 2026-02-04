# Currency Conversion API Assignment

## Project Overview

Create a site backend by either a **RESTful** or **GraphQL API** which will get three inputs:

- A source currency
- A target currency
- A monetary value

The API must leverage the exchange rates provided at [https://swop.cx/](https://swop.cx/) and leverage that to return a converted value. So if your input was 30, USD and GBP, you would need to return the calculated result.

## Core Requirements

- Expose your logic behind a **RESTful** or **GraphQL API**
- The API has to be written in **Java**. Feel free to use a framework of your choice.
- Develop the API as you would be developing actual production software:
  - Leverage validation
  - Leverage testing
  - Leverage caching
- We would like to see and test your API, so make the code available to us together with the link to the Git repository
- Format the resultant value using the **Web i18n framework**

## Extra Points

> **Extra points:** Implement caching as a part of your solution to optimize performance. This will be considered for extra points.

> **Extra Points:** Containerize your application with Docker for consistent deployment across different environments.

> **Extra Points:** Add instrumentation to your codebase. Use InfluxDB to log data from the backend and integrate it with Grafana for real-time monitoring and analytics.

> **Extra Points:** Develop a Vue.js user interface to facilitate smooth interactions with the currency conversion service.

You are more than welcome to bring your own ideas to the implementation that make sense to you. Please document everything extra that has been added to your solution in the **README** file.

## Delivery

- Provide a GitHub repository link containing your code, Dockerfile, and any scripts needed for building and running your application.
- Ensure the README file includes detailed instructions on how to set up and run your application, how to interact with the API, and how to deploy the Docker container.
