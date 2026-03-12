# Git Branching Guide

## 1. Mục đích

Tài liệu này mô tả cách quản lý **branch trong Git** cho project nhằm:

* Tránh xung đột code
* Dễ kiểm soát thay đổi
* Đảm bảo `main` luôn ổn định
* Hỗ trợ nhiều người phát triển cùng lúc

---

# 2. Cấu trúc Branch

Project sử dụng **3 loại branch chính**

```
main
  ↑
dev
  ↑
feature / personal branches
```

### main

* Là **phiên bản sản phẩm cuối cùng**
* Chỉ chứa **code đã ổn định và hoàn chỉnh**
* Không commit trực tiếp vào `main`

---

### dev

* Là **branch tổng hợp code từ các thành viên**
* Dùng để **test integration**
* Sau khi ổn định mới merge vào `main`

---

### feature / personal branch

Mỗi thành viên tạo branch riêng để phát triển.

Có thể đặt tên theo:

```
feature/<tên-feature>
```

hoặc

```
<tên-thành-viên>/<feature>
```

Ví dụ:

```
feature/login
feature/payment
khoa/login-ui
an/product-api
minh/fix-cart-bug
```

---

# 3. Quy trình làm việc

## Bước 1: Clone project

```
git clone <repository-url>
```

---

## Bước 2: Chuyển sang branch dev

```
git checkout dev
```

---

## Bước 3: Tạo branch mới để làm việc

Tạo branch theo feature hoặc tên cá nhân:

```
git checkout -b feature/login
```

hoặc

```
git checkout -b khoa/login-ui
```

---

## Bước 4: Commit code

```
git add .
git commit -m "Add login UI"
```

---

## Bước 5: Push branch lên remote

```
git push origin feature/login
```

---

## Bước 6: Tạo Pull Request

Sau khi hoàn thành feature:

```
feature branch → dev
```

Trên GitHub:

```
Create Pull Request
base: dev
compare: feature/login
```

---

## Bước 7: Merge vào dev

Sau khi review:

```
feature/login → dev
```

---

## Bước 8: Release lên main

Khi `dev` đã ổn định:

```
dev → main
```

---

# 4. Quy tắc quan trọng

### Không commit trực tiếp vào main

```
❌ git push origin main
```

---

### Luôn pull dev trước khi làm việc

```
git checkout dev
git pull origin dev
```

---

### Tạo branch mới từ dev

```
git checkout dev
git checkout -b feature/new-feature
```

---

### Sau khi merge xong có thể xóa branch

```
git branch -d feature/login
```

Remote:

```
git push origin --delete feature/login
```

---

# 5. Ví dụ workflow 4 người

```
main
 ↑
dev
 ↑
 ├─ khoa/login
 ├─ an/product-api
 ├─ minh/cart-ui
 └─ linh/payment
```

Workflow:

```
khoa/login   → dev
an/product   → dev
minh/cart    → dev
linh/payment → dev
```

Sau khi test:

```
dev → main
```

---

# 6. Best Practices

### Commit message rõ ràng

```
Add login API
Fix product filter bug
Update cart UI
```

---

### Branch name nên ngắn gọn

```
feature/login
feature/cart
fix/payment-bug
```

---

### Mỗi branch chỉ làm **một feature**

Tránh:

```
feature/login-and-payment
```

---

# 7. Tổng kết

Workflow chuẩn:

```
main  (production)
  ↑
dev   (integration)
  ↑
feature branches (developers)
```

Luồng phát triển:

```
Developer → Feature Branch → Dev → Main
```
