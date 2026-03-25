# HelloDoc - Hệ thống Diễn đàn Cộng đồng Hỗ trợ Người Khuyết Tật

<p align="center">
  <img src="https://img.shields.io/badge/status-active-brightgreen" />
  <img src="https://img.shields.io/badge/platform-Android%20%7C%20Web%20Admin%20%7C%20API-blue" />
  <img src="https://img.shields.io/badge/tech-NestJS%20%7C%20Nuxt%20%7C%20Kotlin-orange" />
</p>

<p align="center">
  Nền tảng y tế toàn diện kết hợp diễn đàn cộng đồng, đặt lịch khám bệnh và công nghệ hỗ trợ người khuyết tật.
</p>

---

## 📖 Giới thiệu

HelloDoc là hệ thống diễn đàn cộng đồng và đặt lịch khám bệnh được phát triển với mục tiêu:

- **Thu hẹp khoảng cách số**: Hỗ trợ người khiếm thị, khiếm thính và khiếm thanh tiếp cận thông tin y tế
- **Kết nối cộng đồng**: Tạo không gian chia sẻ kinh nghiệm và tư vấn sức khỏe
- **Đặt lịch thông minh**: Hệ thống quản lý lịch khám hiệu quả
- **AI hỗ trợ**: Tích hợp trí tuệ nhân tạo để phân tích triệu chứng và gợi ý

**Nhóm thực hiện:**
- Mai Nguyễn Đăng Khoa (2251120423)
- Vũ Nguyễn Phương (2251120437)  
- Lê Nguyễn Minh Phúc (2251120040)

---

## ⭐ Demo Ứng dụng
<p align="center">
  <a href="https://ibb.co/B5pYFk3s">
    <img src="https://i.ibb.co/ks7PvZBc/Screenshot-2025-12-30-211100.png"
         alt="HelloDoc Demo"
         width="600">
  </a>
</p>

## ⭐ Demo Admin
<p align="center">
  <a href="https://ibb.co/FbymzF7H">
    <img src="https://i.ibb.co/ynZdqrs4/z7380911785310-8154f636a38c51a041b3b85ed0b5c55e.jpg" alt="z7380911785310-8154f636a38c51a041b3b85ed0b5c55e" width="600">
  </a>
</p>

<p align="center">
  <a href="https://ibb.co/Hf6hKW64"><img src="https://i.ibb.co/S4bf6hby/z7380911850008-b73c15a349f89645f18242f6b039462d.jpg" alt="z7380911850008-b73c15a349f89645f18242f6b039462d" width="600"></a>
</p>

## 🎯 Tính năng chính

### 1. Chức năng chung
- ✅ Đăng ký / Đăng nhập với xác thực an toàn
- ✅ Quản lý thông tin cá nhân
- ✅ Đăng bài viết và tương tác cộng đồng
- ✅ Bình luận và báo cáo vi phạm
- ✅ Đặt lịch khám bệnh
- ✅ Quản lý chuyên khoa và tìm kiếm bác sĩ
- ✅ Trợ lý ảo AI (Gemini API)
- ✅ Quản lý tin tức y tế

### 2. Hỗ trợ người khiếm thị
- 🔊 Điều hướng bằng cử chỉ và phản hồi âm thanh
- 🔊 Text-to-Speech đọc nội dung
- 🔊 Thao tác: vuốt, nhấn, nhấn giữ

### 3. Hỗ trợ người khiếm thanh  
- 💬 Gợi ý từ ngữ thông minh với NLP (Underthesea)
- 💬 Xây dựng câu nhanh từ ngữ cảnh
- 💬 Tích hợp Graph Database (Neo4j) cho quan hệ ngữ nghĩa

### 4. Hỗ trợ người khiếm thính
- 🤟 Chuyển đổi giọng nói sang ngôn ngữ ký hiệu 3D
- 🤟 Nhận dạng giọng nói tiếng Việt (PhoWhisper)
- 🤟 Trích xuất chuyển động với MediaPipe
- 🤟 Hiển thị nhân vật 3D bằng SceneView

---


### Stack công nghệ

| Thành phần | Công nghệ | Mục đích |
|------------|-----------|----------|
| **Backend API** | NestJS (Node.js), TypeScript | REST API, Microservices |
| **Web Admin** | Nuxt.js, Vue 3, Tailwind CSS | Dashboard quản trị |
| **Mobile App** | Kotlin, Jetpack Compose, ExoPlayer | Ứng dụng Android |
| **Database** | MongoDB, Qdrant, Neo4j, Redis, RoomDB | Polyglot Persistence |
| **AI/ML** | Gemini API, Hugging Face, MediaPipe | NLP, Computer Vision, ASR |
| **Auth** | Firebase Auth, JWT | Xác thực & phân quyền |
| **Storage** | Cloudinary | Quản lý media |
| **Real-time** | WebSocket | Cập nhật trạng thái trực tuyến |

---

## 🗄️ Cơ sở dữ liệu

### MongoDB - Cơ sở dữ liệu chính
- Lưu trữ: Users, Posts, Comments, Appointments,...
- Schema linh hoạt, hỗ trợ mở rộng

### Qdrant - Vector Database  
- Lưu trữ embeddings 384 chiều
- Tìm kiếm ngữ nghĩa với HNSW algorithm
- Cosine similarity cho content recommendation

### Neo4j - Graph Database
- Mô hình hóa quan hệ y khoa (triệu chứng → bệnh → điều trị)
- Gợi ý từ đồng nghĩa với Cypher query
- Hỗ trợ word suggestion

### Redis - Cache & Real-time
- Session management
- Cache kết quả tìm kiếm
- Rate limiting

### RoomDB - Local Storage (Android)
- Offline data access
- Sync với server khi online

---

## 🤖 Mô hình AI/ML

### 1. Natural Language Processing
- **Underthesea**: Word segmentation, POS tagging cho tiếng Việt
- **Sentence Transformers** (MiniLM-L6-V2): Text embeddings
- **BAAI/bge-m3**: Multilingual embeddings

### 2. Speech Recognition
- **PhoWhisper**: ASR cho tiếng Việt
- Nhận dạng giọng nói với độ chính xác cao
- Xử lý nhiễu nền

### 3. Computer Vision
- **MediaPipe**: Pose estimation, hand tracking
- Trích xuất landmarks 3D (21 điểm bàn tay, 33 điểm cơ thể)
- Real-time processing

### 4. Generative AI
- **Gemini API**: Chatbot, image analysis
- Multimodal understanding
- Content generation

---

## 🚀 Cài đặt & Triển khai

### Yêu cầu hệ thống

- **Node.js** 18+ (cho Backend & Web Admin)
- **JDK 21** (bundled với Android Studio)
- **Android Studio** 
- **MongoDB** 8.0+
- **Redis** 7.0+

### 1. Clone repository

```bash
git clone https://github.com/MaiKhoa0101/HealthCareSystem
```

### 2. Cài đặt Frontend (Kotlin)

```bash
//Sửa lại ip trong file retrofit, network_security_config.xml
```

## 📱 Demo & Screenshots

### Mobile App - Chức năng chính

<!-- Thêm screenshots thực tế -->

**Diễn đàn cộng đồng**
- Đăng bài viết với text/image/video
- Bình luận
- Bài viết liên quan với Vector Search
  
<p align="center">
  <a href="https://imgbb.com/"><img src="https://i.ibb.co/d0fcTPZD/Screenshot-2025-12-30-214929.png" alt="Screenshot 2025 12 30 214929" width="600"></a>
</p>

**Đặt lịch khám**
- Chọn chuyên khoa → Chọn bác sĩ → Chọn thời gian
- Quét QR code thanh toán
- Lịch sử lịch khám

<p align="center">
  <a href="https://ibb.co/fdxTK6f4"><img src="https://i.ibb.co/pjLksVcJ/Screenshot-2025-12-30-222719.png" alt="Screenshot-2025-12-30-222719" width="600"></a>
</p>

### Tính năng hỗ trợ người khuyết tật

**Người khiếm thị**
- Navigation bằng gesture (swipe/tap/long-press)
- Text-to-Speech feedback
- Hướng dẫn bước-by-bước
  
<p align="center">
  <a href="https://ibb.co/rGJ1BJXs"><img src="https://i.ibb.co/DDnSBnjp/Screenshot-2025-12-30-223412.png" alt="Screenshot-2025-12-30-223412" width="600"></a>
</p>

**Người khiếm thanh**
- Gợi ý từ ngữ thông minh
- Word completion từ Neo4j graph

<p align="center">
  <a href="https://imgbb.com/"><img src="https://i.ibb.co/4wsBNdND/Screenshot-2025-12-30-224303.png" alt="Screenshot 2025 12 30 224303" width="600"></a>
</p>

**Người khiếm thính**
- Video → Sign language 3D animation
- Real-time gesture rendering
  
<p align="center">
  <a href="https://imgbb.com/"><img src="https://i.ibb.co/3Y4S9MJY/Screenshot-2025-12-30-224648.png" alt="Screenshot 2025 12 30 224648" width="600"></a>
</p>

---

## 🧪 Kiểm thử & Đánh giá

### Kết quả đạt được

| Chỉ tiêu | Kết quả | Đánh giá |
|----------|---------|----------|
| Mức độ hoàn thiện | Đầy đủ chức năng chính | ✅ Đạt |
| Tốc độ phản hồi | < 3 giây | ✅ Đạt |
| Độ chính xác AI | 60-70% (gợi ý từ), 50-60% (sign language) | ⚠️ Đạt (cần cải thiện) |
| Tính bảo mật | Mã hóa AES, JWT | ✅ Tốt |
| Tính nhân văn | Thu hẹp khoảng cách số | ⭐ Xuất sắc |

---

### Kế hoạch tương lai 🚀
- [ ] iOS app (React Native/Flutter)
- [ ] Mở rộng dataset y khoa Việt Nam
- [ ] Tích hợp thanh toán (Momo/ZaloPay)
- [ ] Cải tiến mô hình 3D scenceView
- [ ] Hệ sinh thái giáo dục cho người khuyết tật

---

## 📧 Liên hệ

**Nhóm thực hiện:**
- Mai Nguyễn Đăng Khoa - maikhoa2015@gmail.com
- Vũ Nguyễn Phương - pvunguyen84@gmail.com
- Lê Nguyễn Minh Phúc - lenguyenminhphuc0706@gmail.com

---

<p align="center">
  <i>Được phát triển với ❤️ bởi nhóm HelloDoc</i>
</p>

<p align="center">
  <i>"Thu hẹp khoảng cách số, nâng cao chất lượng cuộc sống"</i>
</p>
