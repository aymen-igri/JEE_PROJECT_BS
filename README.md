# Integrity Healthcare Backend System

A comprehensive healthcare management system backend built with Spring Boot 4.0.0 and Java 21, designed to manage medical cabinets, appointments, consultations, patient records, billing, and subscriptions.

## Table of Contents

- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Configuration](#configuration)
- [Database Schema](#database-schema)
- [API Documentation](#api-documentation)
- [User Roles & Permissions](#user-roles--permissions)
- [Security](#security)
- [Email Integration](#email-integration)
- [File Storage](#file-storage)
- [Subscription Management](#subscription-management)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)

## Overview

The Integrity Healthcare Backend is a RESTful API service that powers a complete healthcare practice management system. It provides endpoints for managing users, medical cabinets, patient records, appointments, consultations, prescriptions, diagnostics, billing, and subscription plans.

### Key Capabilities

- **Multi-tenant Support**: Each doctor can manage multiple medical cabinets
- **Role-Based Access Control**: Super Admin, Admin, Doctor, and Secretary roles
- **Complete Patient Management**: Patient records, medical history, and document management
- **Appointment Scheduling**: Flexible appointment system with multiple status tracking
- **Consultation Management**: Medical consultations spanning multiple appointments
- **Prescription & Diagnostic Management**: Complete medical documentation
- **Billing & Receipts**: Per-appointment billing with PDF receipt generation
- **Subscription System**: SaaS model with subscription plans for cabinet management
- **Activity Logging**: Comprehensive audit trail for all system actions
- **Email Notifications**: Automated email notifications using Brevo SMTP

## Technology Stack

### Core Framework
- **Spring Boot**: 4.0.0
- **Java**: 21 (OpenJDK)
- **Maven**: 3.x

### Database
- **PostgreSQL**: 18.x
- **Spring Data JPA**: For ORM and database operations
- **Hibernate**: 7.1.8.Final

### Security
- **Spring Security**: Session-based authentication with BCrypt password encoding
- **CORS**: Configured for cross-origin requests from frontend

### Additional Libraries
- **Lombok**: For reducing boilerplate code
- **Jakarta Validation**: For request validation
- **JavaMail**: For email notifications
- **Spring DevTools**: For development hot-reload

### Build Tool
- **Maven**: Dependency management and build automation

## Architecture

### Layered Architecture

```
┌─────────────────────────────────────┐
│         Controllers Layer           │ REST API Endpoints
├─────────────────────────────────────┤
│          Services Layer             │ Business Logic
├─────────────────────────────────────┤
│        Repositories Layer           │ Data Access
├─────────────────────────────────────┤
│         Database Layer              │ PostgreSQL
└─────────────────────────────────────┘
```

### Package Structure

```
com.backend.backend/
├── config/                    # Configuration classes
│   ├── DataInitializer.java  # Database seeding
│   ├── SecurityConfig.java   # Security configuration
│   ├── SchedulingConfig.java # Task scheduling
│   └── WebConfig.java        # Web configuration
├── controller/               # REST API Controllers
├── dto/                      # Data Transfer Objects
│   ├── request/             # Request DTOs
│   └── response/            # Response DTOs
├── entity/                   # JPA Entities
│   ├── User/                # User entities
│   ├── patient/             # Patient entities
│   ├── practice/            # Cabinet & application entities
│   ├── perscription/        # Prescription entities
│   ├── subscription/        # Subscription entities
│   └── activity/            # Activity log entities
├── enums/                    # Enumeration types
├── interceptors/            # HTTP interceptors
├── mapper/                   # Entity-DTO mappers
├── repository/              # Spring Data JPA repositories
├── security/                # Security components
└── service/                 # Business logic services
```

### Key Design Patterns

- **Repository Pattern**: Data access abstraction
- **DTO Pattern**: Separating internal models from API contracts
- **Service Layer Pattern**: Encapsulating business logic
- **Builder Pattern**: For entity construction (using Lombok)
- **Factory Pattern**: For creating complex objects
- **Mapper Pattern**: For entity-DTO conversions

## Features

### 1. User Management

#### User Types
- **Super Admin**: System-wide administration and user management
- **Admin**: Cabinet management and doctor registration
- **Doctor**: Medical practice management
- **Secretary**: Administrative tasks, patient management, appointment scheduling

#### Capabilities
- User registration and authentication
- Role-based access control
- Profile management
- Activity tracking

### 2. Medical Cabinet Management

- **Cabinet Creation**: Doctors can create and manage multiple cabinets
- **Cabinet Configuration**: Name, logo, specialty, description, default consultation price
- **Subscription Management**: SaaS model with subscription plans
- **Status Tracking**: Active/Inactive cabinet status

### 3. Doctor Application System

- **Application Submission**: Doctors can apply with required documents
  - Diploma
  - Medical license
  - CV
- **Application Review**: Admin review and approval/rejection
- **Document Storage**: Secure file storage for uploaded documents
- **Email Notifications**: Automated notifications on application status

### 4. Patient Management

- **Patient Registration**: Complete patient demographics
- **Medical Records**: Comprehensive medical history
- **Doctor-Patient Linking**: Multi-doctor patient relationships
- **Patient Search**: Advanced search and filtering

### 5. Appointment System

#### Features
- **Flexible Scheduling**: Date, time, duration management
- **Appointment Types**: Initial, follow-up, emergency, routine
- **Status Tracking**: Scheduled, confirmed, completed, cancelled, no-show
- **Secretary Scheduling**: Secretaries schedule appointments on behalf of doctors
- **Price Management**: Custom or default cabinet pricing
- **Payment Integration**: Payment status tracking

#### Appointment Workflow
1. Secretary creates appointment with patient, doctor, and cabinet
2. Appointment can be optionally linked to a consultation
3. Appointment can be rescheduled or cancelled
4. Payment processed marks appointment as completed

### 6. Consultation Management

#### Key Concepts
- **Date-Agnostic**: Consultations don't have fixed dates
- **Multi-Appointment**: Can span multiple appointments
- **Status-Driven**: IN_PROGRESS, COMPLETED, CANCELLED

#### Components
- **Chief Complaint**: Main reason for consultation
- **Symptoms**: Patient-reported symptoms
- **Vital Signs**: Stored as JSON
- **Physical Examination**: Doctor's findings
- **Diagnostics**: Multiple diagnoses with dates
- **Prescriptions**: Medication prescriptions
- **Analysis**: Lab test requests

### 7. Diagnostic Management

- **Per-Consultation**: Multiple diagnostics per consultation
- **Dated**: Each diagnostic has its own date
- **Severity Levels**: Low, moderate, high, critical
- **Grace Period**: 30-minute modification window
- **Security**: Only the creating doctor can modify

### 8. Prescription Management

- **Medicament Database**: Searchable medicament library
- **Prescription Items**: Multiple medications per prescription
- **Dosage Instructions**: Detailed medication instructions
- **Analysis Requests**: Lab test prescriptions

#### Medicament Management
- **Admin-Only**: Only admins and super admins can manage medicaments
- **Complete Details**: Name, form, strength, manufacturer
- **Active/Inactive**: Status management
- **Search**: Name-based search functionality

### 9. Billing & Receipt System

#### Key Features
- **Per-Appointment Billing**: Each appointment has separate billing
- **Secretary-Managed**: Only secretaries can create billings
- **Automatic Status Update**: Payment processing marks appointment as completed
- **Discount Support**: Optional discount with reason
- **Receipt Generation**: Unique receipt numbers (REC-YYYYMMDD-XXXX)
- **Payment Types**: Cash, credit card, insurance
- **PDF Receipts**: Generated and stored

### 10. Subscription Management

#### Subscription Plans
- **Configurable Plans**: Admin-created subscription plans
- **Billing Cycles**: Monthly, quarterly, annual
- **Resource Limits**: Max doctors, max secretaries
- **Feature Lists**: Plan-specific features
- **Pricing**: Flexible pricing per plan

#### Subscription Lifecycle
1. **Creation**: Cabinet subscribes to a plan
2. **Active**: Within subscription period
3. **Auto-Renewal**: Optional automatic renewal
4. **Grace Period**: Post-expiration grace period
5. **Cancellation**: Can be cancelled by admin
6. **Upgrade/Downgrade**: Plan changes supported

### 11. Activity Logging

- **Comprehensive Tracking**: All system actions logged
- **Entity Types**: User, Patient, Appointment, Consultation, etc.
- **Audit Trail**: Who, what, when, where
- **Success/Failure**: Action outcome tracking
- **Searchable**: Query by entity type, user, date

### 12. Statistics & Dashboard

- **Doctor Statistics**: Total, active, inactive counts
- **Secretary Statistics**: Activity metrics
- **Patient Statistics**: Registration trends
- **Appointment Analytics**: Scheduling patterns
- **Billing Reports**: Revenue tracking

## Prerequisites

- **Java 21** or higher (OpenJDK recommended)
- **Maven 3.8+**
- **PostgreSQL 14+** (tested with 18.1)
- **Git** (for version control)
- **SMTP Account** (for email notifications - Brevo recommended)

## Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/aymen-igri/JEE_PROJECT_BS.git
cd JEE_PROJECT_BS
```

### 2. Configure Database

Create a PostgreSQL database:

```sql
CREATE DATABASE postgres;
```

### 3. Configure Application Properties

Edit `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=your_username
spring.datasource.password=your_password

# Email Configuration
spring.mail.host=smtp-relay.brevo.com
spring.mail.port=587
spring.mail.username=your_brevo_username
spring.mail.password=your_brevo_api_key
```

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

The application will start on **http://localhost:8080**

### 6. Verify Installation

Check the application health:

```bash
curl http://localhost:8080/api/auth/me
```

## Configuration

### Database Configuration

```properties
# PostgreSQL Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### File Upload Configuration

```properties
# File Upload
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=30MB
spring.servlet.multipart.file-size-threshold=2MB
```

### Email Configuration

```properties
# SMTP Settings (Brevo)
spring.mail.host=smtp-relay.brevo.com
spring.mail.port=587
spring.mail.username=your_username@smtp-brevo.com
spring.mail.password=your_api_key
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Business Logic Configuration

```properties
# Diagnostic Grace Period (minutes)
app.diagnostic.grace-period-minutes=30

# Receipt Storage Path
app.receipts.path=uploads/receipts
```

### Security Configuration

The application uses session-based authentication with the following settings:

- **Session Policy**: IF_REQUIRED
- **Max Sessions**: 1 per user
- **Password Encoding**: BCrypt
- **CORS**: Configured for http://localhost:3000

## Database Schema

### Core Entities

#### Users Table Hierarchy
```
users (base table)
├── super_admins
├── admins
├── doctors
└── secretaries
```

#### Patient Management
- **patients**: Patient demographics and contact information
- **medical_records**: Patient medical history
- **doctor_patient_links**: Doctor-patient relationships

#### Appointment & Consultation
- **appointments**: Scheduled patient visits
- **consultations**: Medical consultations (multi-appointment)
- **diagnostics**: Medical diagnoses
- **prescriptions**: Medication prescriptions
- **prescription_items**: Individual medications
- **analysis**: Lab test requests

#### Practice Management
- **cabinets**: Medical practice offices
- **doctor_applications**: Doctor registration applications

#### Billing & Subscription
- **appointment_billings**: Payment receipts
- **subscriptions**: Cabinet subscriptions
- **subscription_plans**: Available plans
- **payments**: Subscription payments
- **invoices**: Payment invoices

#### System
- **activity_logs**: Audit trail
- **medicaments**: Medication database

### Key Relationships

- One Doctor → Many Cabinets
- One Cabinet → Many Appointments
- One Patient → Many Appointments
- One Consultation → Many Diagnostics
- One Consultation → Many Prescriptions
- One Appointment → One Billing
- One Cabinet → One Active Subscription

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication Endpoints

#### POST `/auth/login`
**Public** - User login
```json
{
  "identifier": "email or username",
  "password": "password"
}
```

#### POST `/auth/logout`
**Authenticated** - User logout

#### GET `/auth/me`
**Authenticated** - Get current user info

### User Management

#### GET `/admin/all`
**Role: SUPER_ADMIN** - Get all admins

#### POST `/admin/createAccount`
**Role: SUPER_ADMIN** - Create admin account

#### POST `/admin/changeStatus`
**Role: ADMIN** - Change user status

### Doctor Management

#### POST `/doctorApp/apply`
**Public** - Submit doctor application (multipart/form-data)
- `application`: Doctor info JSON
- `auth`: Credentials JSON
- `diploma`: File
- `license`: File
- `cv`: File

#### GET `/doctor/all`
**Role: ADMIN** - Get all doctors

#### GET `/doctor/me`
**Role: DOCTOR** - Get doctor profile

#### PUT `/doctor/me`
**Role: DOCTOR** - Update doctor profile

### Cabinet Management

#### POST `/office/create`
**Role: DOCTOR** - Create cabinet

#### GET `/office/my-cabinets`
**Role: DOCTOR** - Get my cabinets

#### PUT `/office/{cabinetId}`
**Role: DOCTOR** - Update cabinet

### Patient Management

#### POST `/patient/create`
**Role: SECRETARY** - Create patient

#### GET `/patient/{patientId}`
**Authenticated** - Get patient details

#### GET `/patient/cabinet/{cabinetId}`
**Role: DOCTOR, SECRETARY** - Get cabinet patients

#### PUT `/patient/{patientId}`
**Role: SECRETARY** - Update patient

### Appointment Management

#### POST `/appointments`
**Role: SECRETARY** - Create appointment

#### GET `/appointments/{id}`
**Authenticated** - Get appointment details

#### PUT `/appointments/{id}/reschedule`
**Role: SECRETARY** - Reschedule appointment

#### PUT `/appointments/{id}/cancel`
**Role: SECRETARY, DOCTOR** - Cancel appointment

#### GET `/appointments/doctor/{doctorId}`
**Role: DOCTOR** - Get doctor appointments

### Consultation Management

#### POST `/consultations`
**Role: DOCTOR** - Create consultation

#### GET `/consultations/{id}`
**Role: DOCTOR** - Get consultation details

#### PUT `/consultations/{id}`
**Role: DOCTOR** - Update consultation

#### GET `/consultations/{id}/detail`
**Role: DOCTOR** - Get full consultation with diagnostics and prescriptions

#### GET `/consultations/patient/{patientId}`
**Role: DOCTOR** - Get patient consultations

### Diagnostic Management

#### POST `/diagnostics`
**Role: DOCTOR** - Create diagnostic

#### PUT `/diagnostics/{id}`
**Role: DOCTOR** - Update diagnostic (within grace period)

#### GET `/diagnostics/{id}`
**Role: DOCTOR** - Get diagnostic details

#### GET `/diagnostics/{id}/can-modify`
**Role: DOCTOR** - Check if diagnostic can be modified

### Prescription Management

#### POST `/prescriptions`
**Role: DOCTOR** - Create prescription

#### GET `/prescriptions/{id}`
**Role: DOCTOR** - Get prescription details

#### GET `/prescriptions/consultation/{consultationId}`
**Role: DOCTOR** - Get consultation prescriptions

#### GET `/prescriptions/patient/{patientId}`
**Role: DOCTOR** - Get patient prescriptions

### Medicament Management

#### POST `/admin/medicaments/create`
**Role: ADMIN, SUPER_ADMIN** - Create medicament

#### GET `/admin/medicaments`
**Role: ADMIN, SUPER_ADMIN** - Get all medicaments

#### GET `/admin/medicaments/search`
**Role: ADMIN, SUPER_ADMIN** - Search medicaments

#### PUT `/admin/medicaments/{id}`
**Role: ADMIN, SUPER_ADMIN** - Update medicament

#### PUT `/admin/medicaments/{id}/deactivate`
**Role: ADMIN, SUPER_ADMIN** - Deactivate medicament

### Billing Management

#### POST `/billings`
**Role: SECRETARY** - Create billing/receipt

#### GET `/billings/{id}`
**Role: SECRETARY** - Get billing details

#### GET `/billings/{id}/receipt`
**Role: SECRETARY** - Download PDF receipt

#### GET `/billings/appointment/{appointmentId}`
**Role: SECRETARY** - Get appointment billing

### Subscription Management

#### POST `/subscriptions`
**Role: DOCTOR** - Create subscription

#### GET `/subscriptions/cabinet/{cabinetId}/active`
**Role: DOCTOR** - Get active cabinet subscription

#### PUT `/subscriptions/{id}/upgrade`
**Role: DOCTOR** - Upgrade subscription

#### PUT `/subscriptions/{id}/cancel`
**Role: ADMIN** - Cancel subscription

#### PUT `/subscriptions/{id}/renew`
**Role: DOCTOR** - Renew subscription

### Statistics

#### GET `/statistics/doctors`
**Role: ADMIN, SUPER_ADMIN** - Get doctor statistics

#### GET `/statistics/secretaries`
**Role: ADMIN, SUPER_ADMIN** - Get secretary statistics

## User Roles & Permissions

### Super Admin
- Full system access
- Create/manage admins
- View all system statistics
- Manage subscription plans

### Admin
- Create/manage doctors
- Review doctor applications
- Change user status
- Manage medicaments
- View activity logs

### Doctor
- Manage personal profile
- Create/manage cabinets
- Create/update consultations
- Create diagnostics and prescriptions
- View patient medical records
- View appointments
- Manage subscriptions

### Secretary
- Create/manage patients
- Schedule/reschedule appointments
- Process billing/payments
- View patient basic information
- Link patients to doctors

## Security

### Authentication
- **Session-Based**: Uses HTTP sessions with cookies
- **Password Encoding**: BCrypt with strength 10
- **Session Management**: Max 1 concurrent session per user

### Authorization
- **Method-Level Security**: `@PreAuthorize` annotations
- **Role-Based Access**: Hierarchical role system
- **Resource Ownership**: Users can only access their own resources

### CORS Configuration
```java
- Allowed Origins: http://localhost:3000
- Allowed Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
- Allowed Headers: *
- Credentials: true
```

### Security Best Practices
- Passwords are never returned in API responses
- Sensitive data is masked in logs
- File uploads are validated for size and type
- SQL injection prevention through JPA
- XSS protection through input validation

## Email Integration

### Email Service
- **Provider**: Brevo (formerly Sendinblue)
- **Protocol**: SMTP
- **Port**: 587 (STARTTLS)

### Email Triggers
1. **Doctor Application Submitted**: Confirmation email
2. **Secretary Account Created**: Welcome email
3. **Application Status Changed**: Notification email

### Email Configuration
```properties
spring.mail.host=smtp-relay.brevo.com
spring.mail.port=587
spring.mail.username=your_username@smtp-brevo.com
spring.mail.password=your_api_key
```

**Note**: Make sure the sender email in `EmailService.java` matches a verified sender in your Brevo account.

## File Storage

### Storage Configuration
- **Base Directory**: `uploads/`
- **Subdirectories**:
  - `doctor-applications/`: Doctor application documents
  - `logos/`: Cabinet logos
  - `receipts/`: PDF receipts

### File Types
- **Doctor Applications**: PDF, JPEG, PNG (max 10MB each)
- **Cabinet Logos**: JPEG, PNG (max 2MB)
- **Receipts**: PDF (auto-generated)

### File Naming
- Format: `{UUID}_{type}.{extension}`
- Example: `a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8_diploma.pdf`

## Subscription Management

### Subscription Lifecycle

1. **Creation**
   - Doctor selects a plan
   - Subscription created with start/end dates
   - Status: ACTIVE

2. **Auto-Renewal** (if enabled)
   - Daily cron job checks for expiring subscriptions
   - Automatically renews 1 day before expiration
   - Sends renewal confirmation email

3. **Grace Period**
   - 7 days after expiration
   - Cabinet remains accessible
   - Warning notifications sent

4. **Expiration**
   - After grace period ends
   - Status: EXPIRED
   - Cabinet access restricted

5. **Cancellation**
   - Admin can cancel subscription
   - Status: CANCELLED
   - Refund logic (if applicable)

### Subscription Plans

Plans include:
- **Plan Name**: Unique identifier
- **Price**: Monthly/Annual pricing
- **Billing Cycle**: Monthly, Quarterly, Annual
- **Max Doctors**: Number of doctors allowed
- **Max Secretaries**: Number of secretaries allowed
- **Features**: List of included features

## Testing

### Default Test Users

The application seeds default users on first run:

#### Super Admin
- **Username**: `superadmin`
- **Password**: `superadmin123`
- **Email**: `superadmin@gmail.com`

#### Admin
- **Username**: `cbinit`
- **Password**: `cbinit123`
- **Email**: `cbinit@gmail.com`

#### Doctor
- **Username**: `testdoctor`
- **Password**: `doctor123`
- **Email**: `SaiidFarhaan450@gmail.com`
- **Specialty**: Cardiology

#### Secretary
- **Username**: `secretary1`
- **Password**: `secretarypassword123`
- **Email**: `secretary1@gmail.com`

### Test Data
- 10 sample patients created automatically
- Sample cabinet for test doctor
- Sample medical records

## Troubleshooting

### Common Issues

#### 1. Database Connection Error
```
ERROR: Connection to localhost:5432 refused
```
**Solution**: Ensure PostgreSQL is running:
```bash
sudo systemctl start postgresql
```

#### 2. Port Already in Use
```
ERROR: Port 8080 is already in use
```
**Solution**: Change port in `application.properties`:
```properties
server.port=8081
```

#### 3. Email Sending Failed
```
MailSendException: Authentication failed
```
**Solution**: 
- Verify Brevo credentials in `application.properties`
- Ensure sender email in `EmailService.java` is verified in Brevo

#### 4. File Upload Failed
```
FileSizeLimitExceededException: Maximum upload size exceeded
```
**Solution**: Check file size limits in `application.properties`

#### 5. Medicament Created_By Type Mismatch
```
ERROR: column "created_by" of type integer does not match UUID
```
**Solution**: This is a known schema issue. Run migration script:
```sql
ALTER TABLE medicaments ALTER COLUMN created_by TYPE uuid USING created_by::uuid;
```

### Logging

Enable debug logging for specific packages:

```properties
# Debug Spring Security
logging.level.org.springframework.security=DEBUG

# Debug Hibernate SQL
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Debug Email
logging.level.org.springframework.mail=DEBUG
logging.level.com.backend.backend.service.Email=DEBUG
```

## API Response Format

### Success Response
```json
{
  "data": { ... },
  "message": "Success message",
  "timestamp": "2026-01-14T00:00:00"
}
```

### Error Response
```json
{
  "error": "Error type",
  "message": "Error description",
  "timestamp": "2026-01-14T00:00:00",
  "path": "/api/endpoint"
}
```

## Contributing

Contributions are welcome! To contribute:

1. Fork the repository from https://github.com/aymen-igri/JEE_PROJECT_BS.git
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

Feel free to open issues for bugs, feature requests, or questions.

## License

This project currently has no license. All rights reserved by the authors.

## Contact

For questions or support, please contact the development team.

---

**Built with ❤️ for modern healthcare management**
