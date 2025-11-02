# Admin Approval System

## Overview

All new user registrations (Student, Teacher, Principal, IT Admin) require admin approval before they can access system features like booking exams.

## Approval Rules

1. **Admin Users**: Have full access immediately (no approval needed)
2. **All Other Roles**: Require admin approval
3. **Auto-Approval**: After 24 hours, if admin hasn't responded, user is automatically approved
4. **Booking Access**: Users can only book exams after approval

## User Approval Status

### Status Types
- `PENDING`: Waiting for admin approval (default for new registrations)
- `APPROVED`: Admin manually approved the user
- `REJECTED`: Admin rejected the registration
- `AUTO_APPROVED`: System auto-approved after 24 hours

### User Model Fields
```java
private boolean isApproved;
private LocalDateTime approvalDate;
private String approvedBy; // Admin user ID who approved
private ApprovalStatus approvalStatus;
```

### Access Logic
```java
public boolean canAccessSystem() {
    if (role == UserRole.ADMIN) {
        return true; // Admins don't need approval
    }

    // Auto-approve after 24 hours
    if (!isApproved && approvalStatus == ApprovalStatus.PENDING) {
        LocalDateTime autoApprovalTime = createdAt.plusHours(24);
        if (LocalDateTime.now().isAfter(autoApprovalTime)) {
            return true; // Auto-approved
        }
    }

    return isApproved;
}
```

## User Experience

### For Students/Teachers/Principals/IT Admins

1. **Registration**: User fills registration form with role selection
2. **Pending Status**: After registration, user sees:
   ```
   Your registration is pending admin approval.
   You will be automatically approved after 24 hours if admin doesn't respond.
   For immediate access, contact admin at: gajjelasuryateja2021@gmail.com
   ```
3. **Feature Access**:
   - ✅ Can login
   - ✅ Can view dashboard
   - ❌ Cannot book exams (shows approval pending message)
   - ❌ Cannot access other restricted features
4. **Post-Approval**: Full access to all features

### For Admins

1. **Approval Dashboard**: View all pending registrations
2. **Actions**:
   - ✅ Approve registration
   - ❌ Reject registration
3. **Notifications**: See pending approvals count
4. **User Details**: View all registration information before approval

## Contact Information

**Admin Email**: gajjelasuryateja2021@gmail.com

For immediate approval requests, users can contact the admin directly.

## Implementation Status

### ✅ Completed
- User model with approval fields
- Teacher, Principal, ITAdmin models created
- Auto-approval logic (24 hours)
- Approval status enum

### ⏳ To Be Implemented
- User repository
- Registration endpoints for all roles
- Admin approval endpoints (approve/reject)
- Frontend approval status UI
- Frontend admin approval dashboard
- Email notifications (optional)
- Scheduled task for auto-approvals
- Booking access restrictions

## API Endpoints (To Be Created)

### Registration Endpoints
```
POST /api/students/register    - Student registration
POST /api/teachers/register    - Teacher registration
POST /api/principals/register  - Principal registration
POST /api/it-admins/register   - IT Admin registration
```

### Admin Endpoints
```
GET  /api/admin/pending-approvals      - List pending users
POST /api/admin/approve-user/:userId   - Approve user
POST /api/admin/reject-user/:userId    - Reject user
GET  /api/admin/users                  - List all users
```

### Auth Endpoints
```
POST /api/auth/login  - Login (existing)
GET  /api/auth/me     - Get current user with approval status
```

## Database Collections

- `users` - Main user authentication and approval data
- `students` - Student-specific data (linked to users via userId)
- `teachers` - Teacher-specific data (linked to users via userId)
- `principals` - Principal-specific data (linked to users via userId)
- `it_admins` - IT Admin-specific data (linked to users via userId)
