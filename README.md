# 🎓 University Course Scheduler Backend

A comprehensive backend system for university course scheduling and timetable management, built with **Spring Boot** and **MySQL**. This system handles everything from user authentication to automated course scheduling across departments.

## ✨ Features

### 🔐 Authentication & User Management
- **JWT-based Authentication** with refresh token support
- **Multi-role System**: HOD (Head of Department) and DAPU (Departmental Academic Planning Unit) roles
- **OTP Verification** for secure login and password reset
- **Email Integration** for user onboarding and notifications

### 📚 Academic Management
- **Course Management**: Create, update, and manage university courses
- **Program Management**: Organize courses by academic programs
- **Department Structure**: Multi-department support with hierarchical organization
- **Venue Management**: Handle lecture halls, labs, and other academic venues
- **Venue Constraints**: Define time-based availability for venues

### 📅 Schedule & Timetable Generation
- **Automated Timetable Generation**: AI-powered course scheduling
- **Course Assignment**: Assign lecturers to courses with conflict detection
- **Time Slot Management**: Flexible time slot configuration
- **Schedule Optimization**: Handles constraints for venues, lecturers, and student capacity

### 🏢 Infrastructure Management
- **College Building Management**: Multi-building campus support
- **Invitation System**: Streamlined user onboarding process
- **Comprehensive Audit Logging**: Track all system activities

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.3.5
- **Language**: Java 21
- **Database**: MySQL 8.0+
- **Security**: Spring Security with JWT
- **Email**: Spring Mail with Thymeleaf templates
- **Build Tool**: Maven
- **Additional Libraries**:
  - Lombok for boilerplate reduction
  - OpenCSV for data import/export
  - Jakarta Validation for input validation
  - H2 Database for testing

## 🚀 Getting Started

### Prerequisites
- **Java 21** or higher
- **Maven 3.6+**
- **MySQL 8.0+**
- **SMTP Server** (for email functionality)

### Installation & Setup

1. **Clone the repository**
   ```bash
   git clone git@github.com:devhnry/course-schedular-backend.git
   cd course-schedular-backend
   ```

2. **Set up the database**
   ```bash
   # Start MySQL (or use Docker)
   docker compose up -d
   
   # Or create database manually
   mysql -u root -p
   CREATE DATABASE unics;
   ```

3. **Configure environment variables**
   Create a `.env` file or set the following environment variables:
   ```env
   DATABASE_USERNAME=your_mysql_username
   DATABASE_PASSWORD=your_mysql_password
   EMAIL_HOST=your_smtp_host
   EMAIL_SENDER=your_email@domain.com
   EMAIL_PASSWORD=your_email_password
   ```

4. **Build and run the application**
   ```bash
   # Build the project
   mvn clean install

   # Run the application
   mvn spring-boot:run
   ```

5. **Access the application**
   - **API Base URL**: `http://localhost:6050/api/v1`
   - **Health Check**: `http://localhost:6050/actuator/health`

```bash
# Build and run the Spring Boot application
mvn spring-boot:run
```

## 📖 API Documentation

### Postman Collection

**One-Click Setup**: Import [`postman_collection.json`](postman_collection.json) and start testing immediately!

**Features**:
- ✅ **Auto-configured environment variables**
- ✅ **Automatic JWT token extraction** after login
- ✅ **No manual setup required**
- ✅ **All endpoints included** with proper authentication

**Import Steps**:
1. Open Postman
2. Click "Import" 
3. Select [`postman_collection.json`](postman_collection.json)
4. Start testing!

### Authentication Flow

#### DAPU Account Flow (Backend-Created)
1. **Register DAPU User** → Account created directly by backend
2. **DAPU Login** → Receive OTP via email  
3. **Verify Login OTP** → JWT token **automatically saved**!
4. **Access all DAPU features** → Token automatically used

#### HOD Account Flow (Invitation-Based)
1. **DAPU sends invitation** → `/api/v1/invite/send` (DAPU only)
2. **HOD receives email** → Contains invitation link
3. **HOD accepts invitation** → `/api/v1/invite/accept`
4. **HOD creates account** → `/api/v1/auth/onboard` (only after invitation)
5. **HOD Login** → `/api/v1/auth/login` + OTP verification
6. **Access HOD features** → Department-specific permissions

### Authentication Endpoints
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/v1/auth/onboard` | Register new HOD user | **After invitation only** |
| POST | `/api/v1/auth/onboard-dapu` | Register new DAPU user | Backend/Admin |
| POST | `/api/v1/auth/login` | User login (returns OTP requirement) | All users |
| POST | `/api/v1/auth/login/verify-otp` | Verify OTP and complete login | All users |
| POST | `/api/v1/auth/reset-password/send-otp` | Send OTP for password reset | All users |
| POST | `/api/v1/auth/reset-password` | Reset user password | All users |
| GET | `/api/v1/auth-check` | Validate JWT token | Authenticated users |
| POST | `/api/v1/logout` | User logout | Authenticated users |

### Invitation System (DAPU Only)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/invite/send` | Send invitation to new HOD |
| POST | `/api/v1/invite/accept` | Accept HOD invitation |
| GET | `/api/v1/invite/details` | Get invitation details |
| GET | `/api/v1/hods` | List all HODs and their status |

### Course Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/courses` | Create new course |
| GET | `/api/v1/courses` | Get all courses |
| GET | `/api/v1/courses/{id}` | Get course by ID |
| PUT | `/api/v1/courses/{id}` | Update course |
| DELETE | `/api/v1/courses/{id}` | Delete course |

### Department Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/departments` | Create new department |
| GET | `/api/v1/departments` | Get all departments |
| GET | `/api/v1/departments/{id}` | Get department by ID |
| PUT | `/api/v1/departments/{id}` | Update department |
| DELETE | `/api/v1/departments/{id}` | Delete department |

### Program Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/programs` | Create new program |
| GET | `/api/v1/programs` | Get all programs |
| GET | `/api/v1/programs/{id}` | Get program by ID |
| PUT | `/api/v1/programs/{id}` | Update program |
| DELETE | `/api/v1/programs/{id}` | Delete program |

### Venue Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/venues` | Create new venue |
| GET | `/api/v1/venues` | Get all venues |
| GET | `/api/v1/venues/{id}` | Get venue by ID |
| PUT | `/api/v1/venues/{id}` | Update venue |
| DELETE | `/api/v1/venues/{id}` | Delete venue |

### Lecturer Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/lecturers` | Create new lecturer |
| GET | `/api/v1/lecturers` | Get all lecturers |
| GET | `/api/v1/lecturers/{id}` | Get lecturer by ID |
| PUT | `/api/v1/lecturers/{id}` | Update lecturer |
| DELETE | `/api/v1/lecturers/{id}` | Delete lecturer |

### Course Assignment Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/course-assignments` | Create course assignment |
| GET | `/api/v1/course-assignments/all` | Get all assignments |
| GET | `/api/v1/course-assignments/by-department/{id}` | Get assignments by department |
| GET | `/api/v1/course-assignments/by-lecturer/{id}` | Get assignments by lecturer |
| PUT | `/api/v1/course-assignments/{id}` | Update assignment |
| DELETE | `/api/v1/course-assignments/{id}` | Delete assignment |

### Venue Constraints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/venue-constraints` | Create venue constraint |
| GET | `/api/v1/venue-constraints` | Get all constraints |
| GET | `/api/v1/venue-constraints/venue/{id}` | Get constraints by venue |
| GET | `/api/v1/venue-constraints/department/{id}` | Get constraints by department |
| PUT | `/api/v1/venue-constraints/{id}` | Update constraint |
| DELETE | `/api/v1/venue-constraints/{id}` | Delete constraint |
| POST | `/api/v1/venue-constraints/generate/building-based` | Generate building-based constraints |
| POST | `/api/v1/venue-constraints/generate/program-specific` | Generate program-specific constraints |

### College Building Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/buildings` | Create new building |
| GET | `/api/v1/buildings` | Get all buildings |
| GET | `/api/v1/buildings/{id}` | Get building by ID |
| PUT | `/api/v1/buildings/{id}` | Update building |
| DELETE | `/api/v1/buildings/{id}` | Delete building |

### HOD Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/hods` | List all HODs (paginated) |
| PATCH | `/api/v1/hods/{userId}/access` | Update HOD access permissions |
| DELETE | `/api/v1/hods/{userId}` | Delete HOD user |

### Invitation System
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/invite/send` | Send invitation to new HOD |

### 🚀 Schedule Generation & Testing
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/test/timetable/generate` | Generate complete timetable |
| GET | `/api/v1/test/scheduler/run` | Run scheduler test with mock data |

## 🏗️ Architecture Overview

The application follows a **layered architecture** pattern:

```
├── Controllers (REST API Layer)
├── Services (Business Logic Layer)
│   ├── Auth Services
│   ├── Core Services
│   ├── Job Services (Scheduling)
│   └── Messaging Services
├── Repositories (Data Access Layer)
├── Models (Entity & DTO Layer)
└── Configuration (Security, Mail, etc.)
```

### Key Design Patterns
- **Repository Pattern** for data access
- **Service Layer Pattern** for business logic
- **DTO Pattern** for API data transfer
- **JWT Authentication** with role-based access control

## 🔧 Configuration

### Application Properties
The application uses profile-based configuration:
- `application.yml` - Base configuration
- `application-dev.yml` - Development environment

### Security Configuration
- **JWT Token Expiration**: Configurable
- **CORS**: Configured for frontend integration
- **Password Encryption**: BCrypt with custom salt

### Database Configuration
- **Connection Pool**: HikariCP (default)
- **JPA/Hibernate**: DDL auto-update in development
- **Migration**: Automatic table creation

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UniversitycourseschedularApplicationTests
```

## 🛠️ Troubleshooting

### Database Seeding Issues

If you encounter database seeding errors:

1. **Missing Building/Entity Errors**:
   - Ensure your database is clean before seeding
   - Check that all environment variables are set correctly
   - Verify MySQL is running and accessible

2. **Common Solutions**:
   ```bash
   # Clean build and restart
   mvn clean package
   mvn spring-boot:run
   
   # Or reset database if needed and run again
   mvn spring-boot:run
   ```

### Authentication Flow Issues

**Issue**: Management of other services
**Solution**: Contact me at `devthenry@gmaill.com`

#### "HOD account creation returns tokens but can't login"
**Issue**: Using `/api/v1/auth/onboard` without invitation
**Solution**: Follow proper HOD flow:
1. DAPU must send invitation via `/api/v1/invite/send`
2. HOD accepts invitation via `/api/v1/invite/accept`  
3. Then HOD can create account via `/api/v1/auth/onboard`
4. HOD must login with OTP verification

#### "TransientPropertyValueException during HOD registration"
**Issue**: Missing department validation or invitation
**Solution**: Ensure:
- Valid invitation exists for the email
- Department code exists in seeded data (CIS, ARC, MAT, etc.)
- Invitation hasn't expired or been used

#### "No OTP received during login"
**Issue**: Email service disabled or configuration missing
**Solution**: Check `application-dev.yml`:
```yaml
email:
  active: false  # OTP emails disabled for development
```
When `email.active: false`, OTP verification is bypassed for testing.

### Environment Setup Issues

- Copy `.env.template` to `.env` and fill in your credentials
- Ensure MySQL is running: `brew services start mysql` (macOS) or `sudo service mysql start` (Ubuntu)
- Check database connectivity: `mysql -u root -p -e "SHOW DATABASES;"`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 👨‍💻 Author
**Henry** - *Backend Developer*
