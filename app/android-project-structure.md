# Android Project Structure

## 1. Overview

Tài liệu này mô tả cấu trúc project Android theo kiến trúc:

* **MVVM**
* **Feature-based architecture**
* **Shared Core Layer**

Mục tiêu:

* Cho phép developer **phát triển feature độc lập**
* Giảm **Git conflict**
* Dễ **scale project**
* Code **dễ maintain**

---

# 2. High Level Project Structure

```
app
│
├── core
│   ├── network
│   ├── database
│   ├── model
│   ├── utils
│   └── ui
│
├── features
│   ├── auth
│   ├── product
│   ├── order
│   └── profile
│
├── navigation
│
└── MainActivity
```

---

# 3. Core Module

`core` chứa các thành phần **dùng chung cho toàn bộ project**.

Các feature **được phép phụ thuộc vào core**, nhưng **core không phụ thuộc feature**.

```
core
│
├── network
├── database
├── model
├── utils
└── ui
```

---

# 4. Core Layer Details

## 4.1 network

Chứa cấu hình network.

```
core/network
│
├── RetrofitProvider
├── ApiClient
├── NetworkInterceptor
└── ApiService
```

Responsibilities:

* Cấu hình Retrofit
* OkHttp interceptors
* Logging
* Authentication headers

---

## 4.2 database

Chứa Room database configuration.

```
core/database
│
├── AppDatabase
├── Dao
└── Entities
```

Responsibilities:

* Database initialization
* DAO interfaces
* Local storage configuration

---

## 4.3 model

Các model dùng chung giữa nhiều feature.

```
core/model
│
├── User
├── ApiResponse
└── ErrorResponse
```

---

## 4.4 utils

Các utility class.

```
core/utils
│
├── Result
├── Extensions
├── DateUtils
└── Constants
```

---

## 4.5 ui

Các base class cho UI.

```
core/ui
│
├── BaseFragment
├── BaseActivity
└── BaseViewModel
```

---

# 5. Feature Modules

Mỗi feature chứa **toàn bộ logic của chính nó**.

```
features
│
├── auth
├── product
├── order
└── profile
```

---

# 6. Feature Structure

Ví dụ `product` feature:

```
features/product
│
├── ui
│   ├── ProductFragment
│   ├── ProductDetailFragment
│   └── ProductAdapter
│
├── viewmodel
│   └── ProductViewModel
│
├── repository
│   └── ProductRepository
│
├── datasource
│   ├── ProductRemoteDataSource
│   └── ProductLocalDataSource
│
└── model
    ├── ProductRequest
    └── ProductResponse
```

---

# 7. MVVM Data Flow

```
View (Fragment / Activity)
        │
        ▼
ViewModel
        │
        ▼
Repository
        │
        ▼
DataSource
        │
        ▼
API / Database
```

---

# 8. Responsibilities

## View

Location:

```
features/{feature}/ui
```

Responsibilities:

* Render UI
* Handle user interaction
* Observe ViewModel state

View **must not contain business logic**.

---

## ViewModel

Location:

```
features/{feature}/viewmodel
```

Responsibilities:

* Handle business logic
* Call repository
* Manage UI state

ViewModel **must not access Retrofit or Database directly**.

---

## Repository

Location:

```
features/{feature}/repository
```

Responsibilities:

* Abstract data sources
* Decide whether to fetch from API or local storage

---

## DataSource

Location:

```
features/{feature}/datasource
```

Responsibilities:

* RemoteDataSource → API
* LocalDataSource → Database

---

# 9. Dependency Rules

Allowed dependency flow:

```
View → ViewModel → Repository → DataSource
```

Forbidden:

```
View → Repository
View → Database
ViewModel → Retrofit
Feature → Feature
```

---

# 10. Feature Independence

Feature **must not depend on other features**.

Wrong:

```
product → auth
```

Correct:

```
product → core
auth → core
```

---

# 11. Developer Workflow

Mỗi developer phụ trách **một feature**.

Example:

| Developer | Feature |
| --------- | ------- |
| Dev A     | Auth    |
| Dev B     | Product |
| Dev C     | Profile |
| Dev D     | Order   |

Developer implement:

* UI
* ViewModel
* Repository
* API integration

---

# 12. Naming Convention

### View

```
LoginFragment
ProductListFragment
ProfileActivity
```

---

### ViewModel

```
LoginViewModel
ProductViewModel
ProfileViewModel
```

---

### Repository

```
AuthRepository
ProductRepository
OrderRepository
```

---

### DataSource

```
AuthRemoteDataSource
ProductRemoteDataSource
ProductLocalDataSource
```

---

# 13. Git Guidelines

1. Mỗi feature nên được phát triển trên **separate branch**.

Example:

```
feature/auth
feature/product
feature/profile
```

2. Pull request phải được review trước khi merge.

---

# 14. Future Improvements

Kiến trúc này có thể mở rộng bằng:

* **Hilt (Dependency Injection)**
* **Multi-module architecture**
* **Clean Architecture**
* **StateFlow / UIState**

---

# 15. Summary

Kiến trúc này giúp:

* Developer làm việc độc lập
* Giảm Git conflict
* Dễ mở rộng project
* Code rõ ràng và dễ maintain

Recommended cho **team từ 3 developer trở lên**.
