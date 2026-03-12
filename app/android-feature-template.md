# Android Feature Template

Tài liệu này mô tả **template chuẩn khi tạo một feature mới** trong project Android sử dụng **MVVM + Feature-based Architecture**.

Mục tiêu:

* Giữ **cấu trúc project đồng nhất**
* Giúp developer **tạo feature mới nhanh**
* Đảm bảo **tuân thủ kiến trúc của project**

---

# 1. Khi nào cần tạo Feature mới

Một **feature** là một phần chức năng độc lập của ứng dụng.

Ví dụ:

* Authentication
* Product browsing
* Order management
* User profile
* Notifications

Mỗi feature nên có:

* UI
* ViewModel
* Repository
* DataSource
* Model

---

# 2. Cấu trúc Feature

Khi tạo feature mới, sử dụng cấu trúc sau:

```
features/{feature_name}
│
├── ui
│   ├── {Feature}Fragment
│   ├── {Feature}Activity
│   └── {Feature}Adapter
│
├── viewmodel
│   └── {Feature}ViewModel
│
├── repository
│   └── {Feature}Repository
│
├── datasource
│   ├── {Feature}RemoteDataSource
│   └── {Feature}LocalDataSource
│
└── model
    ├── {Feature}Request
    └── {Feature}Response
```

---

# 3. Ví dụ: Product Feature

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

# 4. Code Template

## Fragment (View)

```
class ProductFragment : Fragment() {

    private val viewModel: ProductViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeViewModel()
        viewModel.loadProducts()
    }

    private fun observeViewModel() {
        viewModel.products.observe(viewLifecycleOwner) {
            // update UI
        }
    }
}
```

Responsibilities:

* Render UI
* Handle user interaction
* Observe ViewModel state

Fragment **không chứa business logic**.

---

# 5. ViewModel Template

```
class ProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    val products = MutableLiveData<List<Product>>()

    fun loadProducts() {
        viewModelScope.launch {
            products.value = repository.getProducts()
        }
    }
}
```

Responsibilities:

* Business logic
* Manage UI state
* Call repository

ViewModel **không được gọi API trực tiếp**.

---

# 6. Repository Template

```
class ProductRepository(
    private val remoteDataSource: ProductRemoteDataSource
) {

    suspend fun getProducts(): List<Product> {
        return remoteDataSource.getProducts()
    }
}
```

Responsibilities:

* Abstraction layer cho data
* Decide source (API / database)

---

# 7. RemoteDataSource Template

```
class ProductRemoteDataSource(
    private val api: ProductApi
) {

    suspend fun getProducts(): List<Product> {
        return api.getProducts()
    }
}
```

Responsibilities:

* API communication
* Network request handling

---

# 8. Naming Convention

### Feature Folder

```
features/product
features/auth
features/profile
```

---

### View

```
ProductFragment
ProductDetailFragment
LoginActivity
```

---

### ViewModel

```
ProductViewModel
AuthViewModel
ProfileViewModel
```

---

### Repository

```
ProductRepository
AuthRepository
ProfileRepository
```

---

### DataSource

```
ProductRemoteDataSource
ProductLocalDataSource
```

---

# 9. Dependency Rules

Luồng dependency:

```
View → ViewModel → Repository → DataSource
```

Không được:

```
View → Repository
ViewModel → Retrofit
Feature → Feature
```

Feature chỉ được phụ thuộc:

```
Feature → Core
```

---

# 10. Steps để tạo Feature mới

### Bước 1

Tạo folder:

```
features/{feature_name}
```

---

### Bước 2

Tạo các package:

```
ui
viewmodel
repository
datasource
model
```

---

### Bước 3

Tạo class cơ bản:

```
{Feature}Fragment
{Feature}ViewModel
{Feature}Repository
{Feature}RemoteDataSource
```

---

### Bước 4

Connect UI với ViewModel.

---

### Bước 5

Implement API logic trong DataSource.

---

# 11. Best Practices

1. Feature phải **độc lập**.
2. Không được gọi trực tiếp **Retrofit trong ViewModel**.
3. Không viết **business logic trong Fragment**.
4. Repository là nơi quản lý data flow.
5. Model dùng chung nên đặt trong **core/model**.

---

# 12. Feature Checklist

Trước khi merge PR, kiểm tra:

* [ ] Feature có cấu trúc đúng
* [ ] Không vi phạm dependency rule
* [ ] Không có business logic trong View
* [ ] Repository được sử dụng đúng
* [ ] API gọi qua DataSource

---

# 13. Example Feature Creation

Tạo feature `order`:

```
features/order
│
├── ui
│   └── OrderFragment
│
├── viewmodel
│   └── OrderViewModel
│
├── repository
│   └── OrderRepository
│
├── datasource
│   └── OrderRemoteDataSource
│
└── model
    └── OrderResponse
```

---

# 14. Summary

Feature template giúp:

* Developer tạo feature nhanh
* Project structure đồng nhất
* Giảm lỗi kiến trúc
* Code dễ maintain
