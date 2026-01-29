# ⏱️ Stopwatch Plugin (1.21.x)

마인크래프트 **1.21.8** 버전에서 작동하는 **공유형 스톱워치** 플러그인입니다. 특정 태그를 가진 플레이어들이 실시간으로 동일한 시간을 확인하고 제어할 수 있도록 설계되었으며, 타 플러그인 개발자를 위한 전용 API를 제공합니다.

---

## ✨ 핵심 기능

- **실시간 시간 공유:** 특정 태그를 가진 모든 플레이어에게 액션바(Action Bar)를 통해 동일한 시간이 표시됩니다.
- **밀리초 단위 표시:** `00:00:00.000` 형식으로 정밀하게 시간을 측정합니다.
- **지능형 컨트롤 (시계 아이템 사용):**
  - **우클릭:** 스톱워치 시작 및 일시정지.
  - **좌클릭:** - 시간이 `0`일 때: UI 표시/숨기기 토글.
    - 시간이 멈춰있고 `0`이 아닐 때: 스톱워치 리셋.
    - **참고:** 스톱워치가 작동 중일 때는 리셋 방지를 위해 좌클릭이 작동하지 않습니다.

--- 

## 🎮 인게임 사용법

1. **태그 부여:** `/tag <닉네임> add stopwatch` (기본 설정 태그)
2. **아이템 준비:** 일반 **시계(Clock)** 아이템을 손에 듭니다.
3. **조작법:**
   - **UI 활성화:** 시계를 들고 바닥을 조준한 채 **좌클릭** 하세요.
   - **타이머 작동:** **우클릭**으로 시작하거나 멈출 수 있습니다.
   - **초기화:** 정지 상태에서 **좌클릭**을 하면 시간이 리셋됩니다.
4. **색상 변경:** `/swcolor <색상코드>` 명령어로 타이머 색상을 즉시 변경할 수 있습니다. (예: `/swcolor &a&l`)

---

## 💻 개발자용 API (Developer API)

다른 플러그인 개발자가 이 플러그인을 참조하여 데이터를 가져올 수 있습니다.

### 1. 의존성 추가
자신의 플러그인의 `plugin.yml` 파일에 다음 내용을 추가하세요.

```yaml
name: ...
version: ...
main: ...
depend: [Stopwatch]
```

### 2. Java에서 데이터 사용 예시
StopwatchAPI 클래스를 통해 실시간 데이터에 접근하세요.

```java
public void example() {
    // 1. 전체 밀리초 값
    long totalMs = StopwatchAPI.getTotalMillis();

    // 2. 개별 단위 시간 (시간, 분, 초, 밀리초)
    long h = StopwatchAPI.getHours();
    long m = StopwatchAPI.getMinutes();
    long s = StopwatchAPI.getSeconds();
    long ms = StopwatchAPI.getMilliseconds();

    // 3. 포맷된 문자열 (00:00:00.000)
    String timeString = StopwatchAPI.getFormattedTime();

    // 4. 상태 확인
    boolean running = StopwatchAPI.isRunning();
}
```
---

## ⚙️ 설정 (config.yml)

```yaml
# 스톱워치를 공유하고 제어할 수 있는 플레이어의 태그를 지정합니다.
target-tag: "stopwatch"

# 타이머의 기본 색상 및 스타일을 지정합니다.
timer-color: "&f&l"
```
## 🛠 빌드 정보

- **Java**: 21
- **API**: Spigot 1.21.1-R0.1-SNAPSHOT
- **Build Tool**: Maven


