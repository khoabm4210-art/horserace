# 🚀 IMPLEMENTATION GUIDE – CORE BUSINESS SERVICES

Dựa trên `PROMPT_BACKEND_SpringBoot_V3.md` + trạng thái hiện tại, dưới đây là **danh sách các dịch vụ cần implement** theo thứ tự ưu tiên.

## ✅ Đã hoàn thành
- [x] HorseService (CRUD + approve/reject/disqualify)
- [x] JockeyService (cần verify)
- [x] UserService (cần verify)
- [x] AuthService + JwtTokenProvider
- [x] SecurityConfig + JwtAuthenticationFilter

## ⏳ Cần implement – Phase 1 (Tuần này)

### 1. **RegistrationService** ← CRITICAL
**Mục đích:** Quản lý đăng ký tham gia cuộc đua
- `createRegistration(request, ownerId)` → validate horse + jockey + race
- `approveRegistration(id)` → check điều kiện + ghi RequestHistory
- `rejectRegistration(id, reason)` → lưu lý do từ chối
- `getMyRegistrations(ownerId, page, size, status)` → danh sách đăng ký của owner
- `getAllRegistrations(page, size, raceId, status)` → cho ORGANIZER/ADMIN

**Validation rules:**
- Horse.status = APPROVED
- Jockey.status = APPROVED
- Race.status = OPEN
- registeredCount < race.maxHorses
- (horse_id, race_id) unique

**Workflow:**
```
PENDING → [ORGANIZER approve] → APPROVED → Lane được phân công
PENDING → [ORGANIZER reject] → REJECTED → ghi RequestHistory
```

---

### 2. **LaneService** ← CRITICAL
**Mục đích:** Phân công làn chạy cho mỗi registration
- `assignLane(request)` → tạo Lane record
- `updateLane(id, request)` → thay đổi lane_number
- `deleteLane(id)` → xóa (chỉ khi race chưa ONGOING)
- `getLanesByRace(raceId)` → danh sách lane của 1 cuộc đua

**Validation:**
- lane_number unique trong race (1-30)
- registration_id phải APPROVED
- race.status = OPEN hoặc ONGOING

---

### 3. **ResultService** ← CRITICAL (Logic phức tạp nhất)
**Mục đích:** Nhập kết quả + tính điểm + cập nhật ranking
- `entryResult(request)` → tạo RaceResult + RaceResultDetail
- `updateResult(raceId, request)` → sửa kết quả trước publish
- `publishResult(raceId)` → **LOGIC TÍNH ĐIỂM + cập nhật ranking**
- `getResult(raceId)` → chi tiết kết quả

**Key logic (publish):**
```
1. Lấy PointRule của season
2. Duyệt từng detail → tính points theo position
3. Nếu position không có rule → dùng position=99 (fallback)
4. Upsert horse_rankings + jockey_rankings
5. Gửi broadcast notification
6. races.status = COMPLETED
```

---

### 4. **RankingService** ← IMPORTANT
**Mục đích:** Quản lý bảng xếp hạng ngựa/nài
- `upsertHorseRanking(seasonId, horseId, points, isWin)` → upsert (không tạo duplicate)
- `upsertJockeyRanking(seasonId, jockeyId, points, isWin)` → upsert
- `getHorseRankings(seasonId, page, size)` → sorted by totalPoints DESC
- `getJockeyRankings(seasonId, page, size)` → sorted by totalPoints DESC

**Upsert logic (prevent duplicate):**
```sql
INSERT INTO horse_rankings (season_id, horse_id, total_points, total_races, total_wins, updated_at)
VALUES (?, ?, ?, 1, ?, NOW())
ON DUPLICATE KEY UPDATE
  total_points = total_points + VALUES(total_points),
  total_races = total_races + 1,
  total_wins = total_wins + VALUES(total_wins),
  updated_at = NOW();
```

---

## ⏳ Cần implement – Phase 2 (Tiếp theo)

### 5. **FileUploadService + StorageService** ← Cloud-ready
- `upload(file, targetType, fileType, targetId)` → delegate StorageService
- `delete(fileId)` → soft delete hoặc xóa thực
- `getUrl(fileId)` → redirect public URL

**StorageService interface:**
- LocalStorageService (default)
- S3StorageService (stub)
- MinioStorageService (stub)

---

### 6. **NotificationService + WebSocketGateway** ← Realtime
- `sendToUser(userId, notification)` → WebSocket /user/queue/notifications
- `broadcast(notification)` → WebSocket /topic/notifications
- `createNotification(userId, title, message, type)` → lưu DB + gửi realtime

**Trigger từ:**
- Duyệt/từ chối horse → sendToUser(ownerId)
- Duyệt/từ chối jockey → sendToUser(ownerId)
- Duyệt/từ chối registration → sendToUser(ownerId)
- Publish result → broadcast()

---

### 7. **SeasonService + PointRuleService** ← Important
- `createSeason(request)` → tạo + seed PointRule mặc định
- `updateSeason(id, request)`
- `closeSeason(id)` → status = CLOSED
- `getPointRules(seasonId)` → trả list PointRule
- `updatePointRules(seasonId, rules)` → update bulk

**Seed mặc định khi tạo Season:**
```
position=1 → 10 points
position=2 → 7 points
position=3 → 5 points
position=4 → 3 points
position=5 → 2 points
position=99 → 1 point (fallback)
```

---

### 8. **RaceService** ← Important
- `createRace(request, createdBy)` → tạo + status=OPEN
- `updateRace(id, request)` → chỉ khi OPEN
- `deleteRace(id)` → soft delete (chỉ khi OPEN)
- `publishRace(id)` → status vẫn OPEN nhưng is_published=1
- `getRaceDetail(id, includeRegistrations, includeLanes)` → return full data
- `getRacesBySeasonAndStatus(seasonId, status, page, size)`

---

### 9. **DashboardService** ← UI-driven
- `getStatsForAdmin()` → tổng users, horses, races, results
- `getStatsForOrganizer()` → races opened, pending registrations
- `getStatsForHorseOwner(ownerId)` → my horses, my registrations
- `getUpcomingRaces(limit)` → 5 race sắp tới
- `getRecentResults(limit)` → 5 result mới nhất (published)

---

### 10. **AuditLogService** ← Compliance
- `createAuditLog(userId, action, targetType, targetId, description, ...)` → ghi log
- `getAuditLogs(userId, action, fromDate, toDate, page, size)` → filter audit
- Auto-log qua AOP + @Auditable annotation

---

## 📌 PRIORITY RECOMMENDATION

**Tuần 1 – Build foundation:**
1. RegistrationService
2. LaneService
3. ResultService (logic tính điểm)
4. RankingService

**Tuần 2 – Add features:**
5. SeasonService + PointRuleService
6. RaceService
7. FileUploadService + StorageService
8. NotificationService + WebSocketGateway

**Tuần 3 �� Polish:**
9. DashboardService
10. AuditLogService
11. Testing + Integration

---

## 🧪 TEST FLOW ĐỂ VERIFY TÍCH HỢP

```
1. Login as horse_owner
   → Tạo Horse (status=PENDING)
   → Tạo Jockey (status=PENDING)

2. Login as organizer
   → Approve Horse (status=APPROVED)
   → Approve Jockey (status=APPROVED)
   → Tạo Season (với mặc định PointRules)
   → Tạo Race (status=OPEN)

3. Login as horse_owner lại
   → Register (Race + Horse + Jockey) → status=PENDING
   → Nhận notification WebSocket

4. Login as organizer
   → Approve Registration → status=APPROVED
   → Assign Lane
   → Entry Result (finish positions)
   → Publish Result
     ✓ Tính điểm đúng theo PointRule
     ✓ Upsert rankings
     ✓ Broadcast notification

5. Check rankings
   → GET /api/v1/rankings/horses?seasonId=1
   → Danh sách sorted by totalPoints DESC
```

---

## 📋 CODE TEMPLATE REFERENCES

Xem các files template trong `backend/CODE_TEMPLATES/` directory:
- `RegistrationService.java.template`
- `LaneService.java.template`
- `ResultService.java.template`
- `RankingService.java.template`
- `SeasonService.java.template`
- `RaceService.java.template`
- `FileUploadService.java.template`
- `StorageService.java.template`
- `NotificationService.java.template`
- `DashboardService.java.template`
- `AuditLogService.java.template`

---

Bắt đầu từ **RegistrationService** vì nó là linh hồn của hệ thống! 🎯
