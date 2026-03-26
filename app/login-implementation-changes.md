md
# Login Implementation Changes

## Summary
Implemented the Login feature using Supabase Auth, following the Feature-based Architecture and MVVM pattern. Successfully resolved resource linking and View Binding issues.

## Changes Made

### 1. Feature: `auth`
- Created `features/auth` package structure:
    - `datasource/AuthRemoteDataSource.kt`: Interacts with Supabase Auth using `SupabaseClient.client.auth.signInWith(Email)`.
    - `repository/AuthRepository.kt`: Bridge between Data Source and ViewModel, wraps calls in Kotlin's `Result` type for consistent error handling.
    - `viewmodel/AuthViewModel.kt`: Manages UI state (Loading/Result) using `MutableLiveData` and `viewModelScope`.
    - `ui/LoginFragment.kt`: UI layer handling user interaction, observing ViewModel state, and performing input validation.

### 2. UI & Resources (Fixes)
- **View Binding**: Enabled `viewBinding = true` in `build.gradle.kts` to resolve "Unresolved reference: databinding".
- **Resource Definitions**:
    - Added missing colors in `colors.xml` (`orange_primary`, `gray_hint`, `gray_light`).
    - Added full Vietnamese/English string set in `strings.xml` covering the login title, subtitles, input labels, hints, and social login buttons.
    - Created `bg_input_field.xml` drawable for consistent input styling.
- **Layout**: Implemented `fragment_login.xml` using `ConstraintLayout` to handle complex UI elements like tab switchers and social buttons.

### 3. Architecture & Patterns
- **Manual Dependency Injection**: Implemented a custom `ViewModelProvider.Factory` inside `LoginFragment` to inject `AuthRepository` and `AuthRemoteDataSource` without using external DI libraries (Hilt/Koin) yet.
- **Coroutines**: Used `viewModelScope` for thread-safe asynchronous calls to Supabase.
- **State Management**: Used `LiveData` to communicate between ViewModel and View, ensuring the UI remains responsive during network calls.

### 4. Technical Details
- **Supabase Integration**: Utilized the `auth-kt` library with `Email` provider.
- **Naming Conventions**: Adhered to `AuthViewModel`, `AuthRepository`, and `AuthRemoteDataSource` as per `android-feature-template.md`.
- **Error Handling**: Implemented `.onSuccess` and `.onFailure` blocks in the Fragment to handle Supabase authentication errors (e.g., invalid credentials).

## Next Steps
- [ ] Implement the "Register" tab functionality.
- [ ] Integrate Google Sign-In logic.
- [ ] Setup Navigation Component to transition from Login to Home after successful auth.