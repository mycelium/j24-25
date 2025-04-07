# Requirements

## Functional

1. Authorization and authentication

1.1.  Authenticate with login and password

2. Generate images

2.1. Using prompt - text from user
- prompt must be 1-100 symbols

2.2. Realistic style

3. Image generation request quota

3.1. Quota must be 10 requests for each user

4. User list

4.1. List of all users
- login
- quota

## Technical

1. Protocol
- TCP
- JSON
2. 10 user requests simultaneously

## Security

1. Passwords must me hashed
2. Generate image request and user list request must be under authorization (secured)

## System

### Server

1. Database
2. Java app
- External libraries
  - GSON
  - Logback
  - SLF4J
- Modules
  - DAO
  - common

### Client
1. Java app

### Hardware
- CPU >= 1 core
- Disk >= 10 Gb
- RAM >= 4 Gb
- Net >= 100 Mbit/s

### oS
- Windows 11
- Ubuntu Server 24.04