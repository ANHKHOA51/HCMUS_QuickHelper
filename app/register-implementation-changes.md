# Register Implementation Changes

## Summary
Implemented the Registration feature using Supabase Auth, following the Feature-based Architecture and MVVM pattern. Integrated user metadata (Full Name and Phone Number) during the sign-up process.

## Changes Made

### 1. Feature: `auth` (Extensions)
- **`datasource/AuthRemoteDataSource.kt`**: Added `registerWithEmail` function using `SupabaseClient.client.auth.signUpWith(Email)`. It includes user metadata (`full_name` and `phone`) using `buildJsonObject`.
- **`repository/AuthRepository.kt`**: Added `register` method to bridge the ViewModel and Data Source, returning a `Result<Unit>`.
- **`viewmodel/AuthViewModel.kt`**: Added `registerResult` LiveData and `register` function to manage the registration flow and UI state.
- **`ui/RegisterFragment.kt`**: Created a new Fragment for registration.
    - Implemented manual dependency injection for `AuthViewModel` (consistent with `LoginFragment`).
    - Handled input validation for Full Name, Email, Phone, and Password.
    - Observed `registerResult` to provide user feedback via Toasts.

### 2. UI & Resources
- **Resource Definitions**:
    - Updated `strings.xml` with registration-specific strings: `register_title`, `register_subtitle`, `label_fullname`, `label_phone`, etc.
- **Layout**: 
    - Created `fragment_register.xml` using `ConstraintLayout` and `NestedScrollView`.
    - Included a tab switcher (Login/Register) and social login placeholders to match the design.
    - Added fields for Full Name and Phone Number as per the database schema requirements.

### 3. Architecture & Patterns
- **MVVM**: Maintained strict separation between UI (Fragment), Logic (ViewModel), and Data (Repository/DataSource).
- **State Management**: Used `LiveData` to communicate loading states and operation results to the UI.
- **Supabase Integration**: Leveraged `signUpWith(Email)` and provided additional user data through the `data` parameter.

### 4. Technical Details
- **User Metadata**: Mapped "Full Name" to `full_name` and "Phone" to `phone` in the Supabase Auth user metadata.
- **Error Handling**: Implemented comprehensive error logging and user-friendly error messages using `Result` failure states.

## Next Steps
- [ ] Implement Navigation Component to switch between `LoginFragment` and `RegisterFragment`.
- [ ] Implement Email Confirmation handling.
- [ ] Add field-specific validation (e.g., email format, password strength).
- [ ] Connect the "Back" button and tab switching logic.