# Home Media Server

Welcome to the Home Media Server project. A self-hosted media management platform built with Java Spring Boot and MongoDB. This server allows users to manage their media files, offering features such as ClamAV file scanning, caching via Redis, JWT-based authentication, role-based authorization, and a robust CLI for server configuration and management.

# Features

**File and Folder Management:** Users can upload, move, and delete media files and organize them into folders.

**File Scanning with ClamAV:** Automatically scan files for vulnerabilities upon upload using ClamAV.

**Redis Caching:** Ensuring enhanced performance and swift response times with Redis caching.

**JWT Authentication & Authorization:** Secure user authentication using JWTs and role-based access control for streamlined user and admin experiences.

**Admin Control Panel:** Empower admins with functionalities like registering new admins, promoting/demoting users, and banning functionalities.

**Command-Line Interface (CLI):** Directly modify server configurations, handle file upload settings, database connections, and control ClamAV settings from the terminal.

# Controllers Overview
**AuthController:** Manages user login and registration processes.

**AdminController:** Caters to admin-specific tasks such as registering new admins and user promotions/demotions.

**FileController:** Entrusted with file uploads, deletions, and movements between folders.

**FolderController:** Handles the lifecycle of folders from creation to deletion.

**ClamAVCommands (CLI):** Enables configurations related to ClamAV settings and their schedules.

**DatabaseCommands (CLI):** Connect to your MongoDB database and trigger backup functionalities.

**FileUploaderCommands (CLI):** Modify the server address and port designated for file uploads.

**FileCommands (CLI):** Tweak settings related to permissible file sizes and extensions.

# Installation
TODO

