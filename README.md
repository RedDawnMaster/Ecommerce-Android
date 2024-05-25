# Mobile E-commerce App

## Overview

This project is a mobile e-commerce application designed for Android devices. It provides users with a seamless shopping experience, featuring product browsing, detailed product views, and a streamlined checkout process. The app leverages a Spring Boot backend to handle business logic and data management, with MySQL for data storage and Firebase for image hosting.

## Features

### User Features

- **Registration and Authentication**: Secure user registration and login functionalities.
- **Browsing Products**: Users can browse through various products.
- **Checkout**: Streamlined checkout process.
- **Cart Management**: Users can add, remove, and manage items in their cart.
- **Order Management**: Users can view their orders and check order status.
- **Refund Requests**: Users can request refunds for their orders.
- **Profile Settings**: Users can update their profile information.
- **Search**: Users can search for products, users, orders, or items in the cart depending on the current displayed fragment.

### Administrator Features

- **Managing Products**: Administrators can add, update, and delete products.
- **Statistics**: View statistics related to sales, user activity, etc.
- **Delivery and Refund Settings**: Change delivery and refund durations.
- **Managing Users**: Administrators can manage user accounts.
- **Managing Categories**: Administrators can add categories.

## Tech Stack

### Frontend

- **Android**: The app is built using Android SDK, providing a native mobile experience.
- **Retrofit2**: A type-safe HTTP client for Android and Java, used for making API calls to the backend.

### Backend

- **Spring Boot**: A robust backend framework for building enterprise-level applications. It simplifies the development of RESTful web services.
- **MySQL**: A reliable and scalable relational database used for storing user data, product information, orders, etc.
- **Firebase**: Used in the Android app for storing and retrieving product images efficiently.

## API Integration

- The app uses **Retrofit2** to interact with the backend API.
- Ensure that the backend server is running before using the app to enable full functionality.

## License

This project is licensed under the MIT License. See the LICENSE file for details.
