🕸️ Graph Algorithms Visualizer
**(Ứng dụng Mô phỏng Thuật toán Đồ thị)**
=> Một công cụ trực quan hóa mạnh mẽ giúp sinh viên và giảng viên hiểu rõ cơ chế hoạt động của các thuật toán đồ thị phổ biến thông qua các hiệu ứng hình ảnh động (Animation) và tương tác.
---
## 🚀 Giới thiệu
Dự án này được phát triển bằng ngôn ngữ Java và thư viện JavaFX, áp dụng mô hình kiến trúc MVC và các mẫu thiết kế hiện đại. Ứng dụng cho phép người dùng vẽ đồ thị, nhập dữ liệu từ file, và chạy mô phỏng các thuật toán từ cơ bản đến nâng cao.
## ✨ Tính năng nổi bật
### 1. Quản lý Đồ thị
* Vẽ đồ thị tương tác: Hỗ trợ nhập số đỉnh và danh sách cạnh, hoặc tạo ngẫu nhiên.
* File I/O: Lưu đồ thị ra file `.txt` và mở lại dễ dàng để tái sử dụng.
* Thao tác trực quan:
* 🖱️ Zoom & Pan: Phóng to/thu nhỏ và di chuyển vùng nhìn.
* 🖐️ Drag & Drop: Kéo thả các đỉnh để thay đổi bố cục.
* Layout thông minh: Hỗ trợ rải đỉnh theo hình tròn hoặc ngẫu nhiên.
### 2. Kho thuật toán đa dạng
Hệ thống hỗ trợ mô phỏng chi tiết các thuật toán sau:
* Duyệt đồ thị:
** Breadth-First Search (BFS)
** Depth-First Search (DFS)

* Tìm đường đi ngắn nhất:
** Dijkstra
** A* Search
** Greedy Best-First Search (GBFS)

* Cây khung nhỏ nhất (MST):
** Kruskal

* Nâng cao:
** Max Flow (Edmonds-Karp): Mô phỏng luồng cực đại trên mạng.
** Tarjan: Tìm các thành phần liên thông mạnh (SCC).

### 3. Điều khiển mô phỏng
* 📝 Log chi tiết: Hiển thị từng bước chạy (thăm đỉnh, duyệt cạnh,...).
* 🎨 Màu sắc trực quan: Phân biệt rõ ràng trạng thái đỉnh (Đang xét, Đã thăm, Kết quả).
---
## 📥 Cài đặt và Chạy
1. Clone hoặc Tải project:
git clone https://github.com/your-username/graph-visualizer.git

2. Mở trong IntelliJ IDEA:
* File -> Open -> Chọn thư mục project.
* Đợi IDE index và tải thư viện.

3. Cấu hình chạy (Nếu cần):
* Nếu gặp lỗi JavaFX runtime, thêm VM Options:
--module-path "/path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml

4. Chạy ứng dụng:
* Tìm file `Main.java` và nhấn nút Run.
---
## 📖 Hướng dẫn sử dụng
### 1. Cấu hình ban đầu
Khi khởi động, chọn loại đồ thị bạn muốn làm việc:
* ☑️ Directed: Đồ thị có hướng (Dùng cho Max Flow, Tarjan...).
* ☑️ Weighted: Đồ thị có trọng số (Dùng cho Dijkstra, MST...).
### 2. Nhập dữ liệu
Bạn có 2 cách để tạo đồ thị:
* Cách 1 (Thủ công): Nhập số đỉnh, nhập danh sách cạnh vào ô text (Ví dụ: `1 2 10` nghĩa là cạnh từ 1 đến 2 trọng số 10) -> Bấm "Cập nhật Đồ thị".
* Cách 2 (File): Bấm "Mở File" trên thanh công cụ và chọn file `.txt` có sẵn.
### 3. Chạy thuật toán
1. Chọn thuật toán từ ComboBox bên trái.
2. Nhập ID đỉnh Bắt đầu / Đích.
3. Bấm "Run Algorithm".
4. Kéo thanh trượt để điều chỉnh tốc độ mong muốn.
---
## 📄 Cấu trúc File dữ liệu (.txt)
File đồ thị mẫu có định dạng đơn giản như sau:
* Dòng 1: Số lượng đỉnh ().
* Các dòng tiếp theo: Danh sách cạnh theo định dạng `u v w` (Đỉnh nguồn, Đỉnh đích, Trọng số).
Ví dụ:
5
1 2 10
1 3 5
2 4 1
3 4 9
4 5 2
---
## 📂 Cấu trúc Project
Project được tổ chức theo mô hình phân lớp rõ ràng:
src
└─── com.project1.graphvisualizer
    ├─── model                 # Chứa các lớp thực thể dữ liệu
    │    ├─── Vertex.java
    │    ├─── Edge.java
    │    └─── Graph.java
    │
    ├─── algorithm             # Chứa logic xử lý các thuật toán
    │    ├─── AlgoFactory.java
    │    ├─── GraphAlgorithm.java (Interface)
    │    └─── impl             # Các cài đặt thuật toán cụ thể
    │         ├─── connectivity (Tarjan)
    │         ├─── flow (MaxFlow)
    │         ├─── shortestpath (Dijkstra, A*, GBFS)
    │         ├─── traversal (BFS, DFS)
    │         └─── tree (Kruskal)
    │
    └─── ui                    # Chứa các thành phần giao diện JavaFX
         ├─── MainApp.java
         ├─── animation        # Xử lý hiệu ứng động
         ├─── components       # Các node đồ họa (VertexNode, EdgeView)
         ├─── controls         # Các bảng điều khiển nhập liệu
         ├─── formatter        # Định dạng kết quả hiển thị (Strategy Pattern)
         └─── layout           # Thuật toán sắp xếp vị trí đỉnh (Circle, Random)
