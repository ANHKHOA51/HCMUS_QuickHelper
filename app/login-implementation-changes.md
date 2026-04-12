# Custom Authentication Implementation Plan

## Problem
The current implementation uses `supabase.auth`, which relies on the internal `auth.users` table. This causes `AuthRestException` when users only exist in the custom `public.users` table.

## Solution
Transition to a custom database-driven authentication system using the `public.users` table via Postgrest.

## Step-by-Step Implementation

### 1. Data Layer: `AuthRemoteDataSource.kt`
- **Remove**: Calls to `supabase.auth.signInWith(Email)` and `supabase.auth.signUp(Email)`.
- **Add**: 
    - `loginWithDatabase(email, password)`: Query `public.users` where email and password match.
    - `registerInDatabase(userMap)`: Insert a new row directly into `public.users`.

### 2. Domain Layer: `AuthRepository.kt`
- Update methods to handle database results instead of `AuthSession`.
- Map Postgrest exceptions to custom domain errors.

### 3. Logic Layer: `AuthViewModel.kt`
- Update `login` and `register` functions to trigger the new repository methods.
- Store user session data locally (e.g., in SharedPreferences) since Supabase Auth will no longer manage the session automatically.

### 4. UI Layer: `LoginFragment.kt` & `RegisterFragment.kt`
- **LoginFragment**: Update `observeViewModel` to handle standard database exceptions instead of `AuthRestException`.
- **RegisterFragment**: Ensure registration fields match the `public.users` table schema exactly.

### 5. Manual ID Management (Fix for Duplicate Key 0)
- **Problem**: Inserting into `public.users` fails because the app sends `id: 0`.
- **Solution**: 
    1. Query the `public.users` table for the record with the maximum `id`.
    2. Increment that value by 1.
    3. Use this new ID for the registration insert.
- **Note**: This is a workaround. The ideal fix is setting the `id` column in Supabase to `IDENTITY`.
